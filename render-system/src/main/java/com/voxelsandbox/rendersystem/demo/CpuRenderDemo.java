package com.voxelsandbox.rendersystem.demo;

import com.voxelsandbox.engine.world.World;
import com.voxelsandbox.engine.world.chunk.ChunkPosition;
import com.voxelsandbox.engine.world.generation.FlatWorldGenerator;
import com.voxelsandbox.engine.world.type.VoxelType;
import com.voxelsandbox.rendersystem.core.camera.PerspectiveCamera3D;
import com.voxelsandbox.rendersystem.core.camera.ICamera3D;
import com.voxelsandbox.rendersystem.core.math.CpuVec3f;
import com.voxelsandbox.rendersystem.cpu.target.CpuRenderTarget;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.io.IOException;

/** Portable, display-free reference demo. No native graphics calls are made. */
public final class CpuRenderDemo {
    private CpuRenderDemo() {}
    public static CpuRenderTarget render(int width,int height) {
        return createRenderer().render(defaultCamera(width,height),width,height);
    }
    public static CpuWorldRenderer createRenderer() {
        var world = new World(42,new FlatWorldGenerator());
        for (int x = -3; x <= 3; x++) for (int z = -3; z <= 3; z++)
            for (int y = 0; y <= 3; y++) world.loadChunk(new ChunkPosition(x,y,z));
        for (int x = -4; x <= 4; x++) for (int z = -4; z <= 4; z++)
            for (int y = 16; y < 16 + 3 * (5 - Math.max(Math.abs(x),Math.abs(z))); y++)
                world.setVoxel(x,y,z,VoxelType.SOLID);
        return new CpuWorldRenderer(world);
    }
    public static ICamera3D defaultCamera(int width,int height) {
        return new PerspectiveCamera3D(new CpuVec3f(29,34,38),
                new CpuVec3f(-29,-15,-38).normalize(),new CpuVec3f(0,1,0),
                (float)Math.toRadians(60),(float)width/height,.1f,120f);
    }
    public static void writePng(CpuRenderTarget target,Path path) throws IOException {
        var image = new BufferedImage(target.getWidth(),target.getHeight(),BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < target.getHeight(); y++) for (int x = 0; x < target.getWidth(); x++)
            image.setRGB(x,y,target.getPixel(x,y));
        var output = path.toAbsolutePath();
        Files.createDirectories(output.getParent());
        if (!ImageIO.write(image,"png",output.toFile())) throw new IOException("PNG writer unavailable");
    }
    public static void main(String[] args) throws IOException {
        if (args.length > 1) throw new IllegalArgumentException("Usage: CpuRenderDemo [output.png]");
        var output = Path.of(args.length == 1 ? args[0] : "build/demo/voxel-world.png");
        writePng(render(320,200),output);
        System.out.println("CPU world image: " + output.toAbsolutePath());
    }
}
