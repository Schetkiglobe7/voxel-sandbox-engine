package com.voxelsandbox.rendersystem.demo;

import com.voxelsandbox.rendersystem.cpu.target.CpuRenderTarget;
import com.voxelsandbox.rendersystem.opengl.internal.OpenGLContextBootstrap;
import com.voxelsandbox.rendersystem.opengl.internal.OpenGLWindow;
import com.voxelsandbox.rendersystem.opengl.presentation.CpuImagePresenter;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;
import java.nio.file.Path;

/** Static CPU world preview with a native window; --smoke validates real OpenGL without waiting for input. */
public final class OpenGLPreviewDemo {
    private OpenGLPreviewDemo() {}
    public static void main(String[] args) throws Exception {
        if (args.length > 1 || (args.length == 1 && !args[0].equals("--smoke")))
            throw new IllegalArgumentException("Usage: OpenGLPreviewDemo [--smoke]");
        boolean smoke = args.length == 1;
        if (smoke) verifyContextLifecycle();
        var image = CpuRenderDemo.render(320,200);
        try (var window = new OpenGLWindow()) {
            window.initialize(smoke ? 320 : 960,smoke ? 200 : 600,"Voxel Sandbox — CPU world preview",!smoke);
            glfwSwapInterval(smoke ? 0 : 1);
            System.out.println("OpenGL " + glGetString(GL_VERSION) + " / " + glGetString(GL_RENDERER));
            try (var presenter = new CpuImagePresenter(image); var stack = MemoryStack.stackPush()) {
                var width = stack.mallocInt(1);
                var height = stack.mallocInt(1);
                do {
                    glfwPollEvents();
                    if (glfwGetKey(window.handle(),GLFW_KEY_ESCAPE) == GLFW_PRESS)
                        glfwSetWindowShouldClose(window.handle(),true);
                    glfwGetFramebufferSize(window.handle(),width,height);
                    presenter.draw(width.get(0),height.get(0));
                    if (smoke) {
                        verifyPixels(image,width.get(0),height.get(0));
                        glfwSetWindowSize(window.handle(),401,257);
                        glfwPollEvents();
                        // GLX/Mesa may resize its drawable buffers only on the next swap.
                        // Present once, then redraw before inspecting the resized back buffer.
                        glfwSwapBuffers(window.handle());
                        glfwPollEvents();
                        glfwGetFramebufferSize(window.handle(),width,height);
                        presenter.draw(width.get(0),height.get(0));
                        verifyPixels(image,width.get(0),height.get(0));
                    }
                    glfwSwapBuffers(window.handle());
                    if (smoke) break;
                } while (!glfwWindowShouldClose(window.handle()));
            }
        }
        if (smoke) System.out.println("OpenGL smoke OK: lifecycle, texture presentation, resized pixel readback and cleanup");
    }
    private static void verifyContextLifecycle() {
        try (var context = new OpenGLContextBootstrap()) {
            context.shutdown();
            context.initialize();
            try (var competing = new OpenGLContextBootstrap()) {
                try {
                    competing.initialize();
                    throw new AssertionError("A second GLFW owner was accepted");
                } catch (IllegalStateException expected) {
                    if (glGetString(GL_VERSION) == null) throw new IllegalStateException("Competing owner damaged active context");
                }
            }
            context.initialize();
            if (!context.isInitialized()) throw new IllegalStateException("Context failed to initialize");
            context.shutdown();
            context.shutdown();
            if (context.isInitialized()) throw new IllegalStateException("Context failed to shut down");
            context.initialize();
        }
    }
    private static void verifyPixels(CpuRenderTarget image,int width,int height) throws Exception {
        if (width <= 0 || height <= 0) throw new IllegalStateException("Smoke test framebuffer unavailable");
        var pixels = MemoryUtil.memAlloc(Math.multiplyExact(Math.multiplyExact(width,height),4));
        try {
            glReadBuffer(GL_BACK);
            glReadPixels(0,0,width,height,GL_RGBA,GL_UNSIGNED_BYTE,pixels);
            int error = glGetError();
            if (error != GL_NO_ERROR) throw new IllegalStateException("OpenGL error: " + error);
            var capture = new CpuRenderTarget(width,height);
            capture.beginFrame();
            for (int y=0;y<height;y++) for (int x=0;x<width;x++) {
                int i = ((height-1-y)*width+x)*4;
                int argb = 0xFF000000 | ((pixels.get(i)&255)<<16) | ((pixels.get(i+1)&255)<<8) | (pixels.get(i+2)&255);
                capture.drawPixel(x,y,argb);
            }
            capture.endFrame();
            double scale = Math.min((double)width/image.getWidth(),(double)height/image.getHeight());
            int w = (int)(image.getWidth()*scale), h = (int)(image.getHeight()*scale);
            int left = (width-w)/2, top = height-(height-h)/2-h;
            for (int row=0;row<5;row++) for (int col=0;col<8;col++) {
                int x = w*(2*col+1)/16, y = h*(2*row+1)/10;
                int sx = (int)((x+.5)*image.getWidth()/w), sy = (int)((y+.5)*image.getHeight()/h);
                if (capture.getPixel(left+x,top+y) != image.getPixel(sx,sy))
                    throw new IllegalStateException("GPU presentation pixel mismatch at sample " + col + "," + row
                            + " in " + width + "x" + height + ": expected "
                            + Integer.toHexString(image.getPixel(sx,sy)) + ", got "
                            + Integer.toHexString(capture.getPixel(left+x,top+y)));
            }
            CpuRenderDemo.writePng(capture,Path.of("build/demo/opengl-world.png"));
        } finally { MemoryUtil.memFree(pixels); }
    }
}
