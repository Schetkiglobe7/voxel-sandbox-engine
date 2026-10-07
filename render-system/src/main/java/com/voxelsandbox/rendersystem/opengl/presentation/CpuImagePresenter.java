package com.voxelsandbox.rendersystem.opengl.presentation;

import com.voxelsandbox.rendersystem.cpu.target.CpuRenderTarget;
import org.lwjgl.system.MemoryUtil;
import java.nio.ByteBuffer;
import static org.lwjgl.opengl.GL33C.*;

/** Displays a CPU image as a nearest-filtered OpenGL texture. No voxel rendering runs on the GPU. */
public final class CpuImagePresenter implements AutoCloseable {
    private int texture;
    private int program;
    private int vao;
    private ByteBuffer pixels;
    private final int width;
    private final int height;

    public CpuImagePresenter(CpuRenderTarget image) {
        width = image.getWidth();
        height = image.getHeight();
        try {
            program = createProgram();
            vao = glGenVertexArrays();
            texture = glGenTextures();
            glBindTexture(GL_TEXTURE_2D,texture);
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_CLAMP_TO_EDGE);
            pixels = MemoryUtil.memAlloc(Math.multiplyExact(Math.multiplyExact(width,height),4));
            glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA8,width,height,0,GL_RGBA,GL_UNSIGNED_BYTE,(ByteBuffer)null);
            update(image);
        } catch (RuntimeException | Error failure) {
            close();
            throw failure;
        }
    }

    /** Reuses the existing texture and staging buffer; dimensions remain fixed for this presenter. */
    public void update(CpuRenderTarget image) {
        if (pixels == null || texture == 0) throw new IllegalStateException("Presenter is closed");
        if (image.getWidth() != width || image.getHeight() != height) throw new IllegalArgumentException("Image dimensions changed");
        pixels.clear();
        // CPU images are top-down; the shader samples with the same orientation.
        for (int y=0;y<height;y++) for (int x=0;x<width;x++) {
            int argb = image.getPixel(x,y);
            pixels.put((byte)(argb>>>16)).put((byte)(argb>>>8)).put((byte)argb).put((byte)(argb>>>24));
        }
        pixels.flip();
        glBindTexture(GL_TEXTURE_2D,texture);
        glTexSubImage2D(GL_TEXTURE_2D,0,0,0,width,height,GL_RGBA,GL_UNSIGNED_BYTE,pixels);
    }

    private static int compile(int type,String source) {
        int shader = glCreateShader(type);
        glShaderSource(shader,source);
        glCompileShader(shader);
        if (glGetShaderi(shader,GL_COMPILE_STATUS) == GL_FALSE) {
            String message = glGetShaderInfoLog(shader);
            glDeleteShader(shader);
            throw new IllegalStateException("Presentation shader failed: " + message);
        }
        return shader;
    }
    private static int createProgram() {
        int vertex = compile(GL_VERTEX_SHADER,"""
                #version 330 core
                out vec2 uv;
                void main() {
                    vec2 p = vec2((gl_VertexID << 1) & 2, gl_VertexID & 2);
                    uv = vec2(p.x, 1.0 - p.y);
                    gl_Position = vec4(p * 2.0 - 1.0, 0.0, 1.0);
                }
                """);
        int fragment = 0, result = 0;
        try {
            fragment = compile(GL_FRAGMENT_SHADER,"""
                    #version 330 core
                    in vec2 uv;
                    out vec4 color;
                    uniform sampler2D image;
                    void main() { color = texture(image, uv); }
                    """);
            result = glCreateProgram();
            glAttachShader(result,vertex);
            glAttachShader(result,fragment);
            glLinkProgram(result);
            if (glGetProgrami(result,GL_LINK_STATUS) == GL_FALSE)
                throw new IllegalStateException("Presentation program failed: " + glGetProgramInfoLog(result));
            return result;
        } catch (RuntimeException | Error failure) {
            if (result != 0) glDeleteProgram(result);
            throw failure;
        } finally {
            glDeleteShader(vertex);
            if (fragment != 0) glDeleteShader(fragment);
        }
    }

    public void draw(int framebufferWidth,int framebufferHeight) {
        glViewport(0,0,framebufferWidth,framebufferHeight);
        glClearColor(.08f,.10f,.13f,1);
        glClear(GL_COLOR_BUFFER_BIT);
        if (framebufferWidth <= 0 || framebufferHeight <= 0) return;
        // Preserve image proportions when resizing or on high-DPI displays.
        double scale = Math.min((double)framebufferWidth/width,(double)framebufferHeight/height);
        int w = Math.max(1,(int)(width*scale)), h = Math.max(1,(int)(height*scale));
        glViewport((framebufferWidth-w)/2,(framebufferHeight-h)/2,w,h);
        glUseProgram(program);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D,texture);
        glUniform1i(glGetUniformLocation(program,"image"),0);
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES,0,3);
    }
    @Override public void close() {
        if (pixels != null) { MemoryUtil.memFree(pixels); pixels=null; }
        if (texture != 0) { glDeleteTextures(texture); texture=0; }
        if (vao != 0) { glDeleteVertexArrays(vao); vao=0; }
        if (program != 0) { glDeleteProgram(program); program=0; }
    }
}
