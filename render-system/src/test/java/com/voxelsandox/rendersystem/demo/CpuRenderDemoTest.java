package com.voxelsandox.rendersystem.demo;

import com.voxelsandbox.rendersystem.demo.CpuRenderDemo;
import com.voxelsandbox.rendersystem.core.pipeline.stage.output.CpuPixelOutputStage;
import com.voxelsandbox.rendersystem.adapter.EngineVoxelWorldAdapter;
import com.voxelsandbox.engine.world.World;
import com.voxelsandbox.engine.world.generation.FlatWorldGenerator;
import com.voxelsandbox.engine.world.chunk.ChunkPosition;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.*;

class CpuRenderDemoTest {
    @TempDir Path temp;
    @Test void rendersDeterministicWorldAndRoundTripsPng() throws Exception {
        var first = CpuRenderDemo.render(33,21);
        var second = CpuRenderDemo.render(33,21);
        var colors = new HashSet<Integer>();
        for (int y=0;y<21;y++) for (int x=0;x<33;x++) {
            assertEquals(first.getPixel(x,y),second.getPixel(x,y));
            colors.add(first.getPixel(x,y));
        }
        assertTrue(colors.contains(CpuPixelOutputStage.SKY));
        assertTrue(colors.size() > 3,"Scene must contain shaded voxel surfaces");
        var output = temp.resolve("nested/world.png");
        CpuRenderDemo.writePng(first,output);
        var image = ImageIO.read(output.toFile());
        assertEquals(33,image.getWidth());
        assertEquals(21,image.getHeight());
        for (int y=0;y<21;y++) for (int x=0;x<33;x++) assertEquals(first.getPixel(x,y),image.getRGB(x,y));
    }
    @Test void adapterHandlesNegativeCoordinatesWithoutLoadingChunks() {
        var world = new World(42,new FlatWorldGenerator());
        world.loadChunk(new ChunkPosition(-1,0,-1));
        var adapter = new EngineVoxelWorldAdapter(world);
        assertTrue(adapter.isChunkLoaded(-1,0,-16));
        assertTrue(adapter.isSolid(-1,0,-16));
        assertFalse(adapter.isChunkLoaded(-17,0,-16));
        assertFalse(adapter.isSolid(-17,0,-16));
        assertEquals(1,world.getChunks().size());
    }
}
