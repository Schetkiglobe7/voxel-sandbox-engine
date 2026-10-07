package com.voxelsandox.rendersystem.demo;

import com.voxelsandbox.rendersystem.demo.*;
import com.voxelsandbox.rendersystem.demo.camera.CameraInput;
import com.voxelsandbox.engine.world.World;
import com.voxelsandbox.engine.world.chunk.ChunkPosition;
import com.voxelsandbox.engine.world.generation.FlatWorldGenerator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InteractiveCpuPreviewTest {
    @Test void rerendersOnlyOnPoseChangeAndResetRestoresPixels() {
        var preview = new InteractiveCpuPreview(CpuRenderDemo.createRenderer(),65,41);
        var initial = preview.image();
        assertFalse(preview.update(CameraInput.NONE,.1));
        assertSame(initial,preview.image());
        assertTrue(preview.update(new CameraInput(1,1,0,1,0,0,0,false,false),.1));
        var moved = preview.image();
        assertNotSame(initial,moved);
        int differences = 0;
        for (int y=0;y<41;y++) for (int x=0;x<65;x++) if (initial.getPixel(x,y) != moved.getPixel(x,y)) differences++;
        assertTrue(differences > 0);
        assertSame(moved,preview.image());
        preview.update(new CameraInput(0,0,0,0,0,0,0,false,true),0);
        var reset = preview.image();
        for (int y=0;y<41;y++) for (int x=0;x<65;x++) assertEquals(initial.getPixel(x,y),reset.getPixel(x,y));
    }
    @Test void repeatedFramesPreserveWorldChunkInstancesAndVoxelState() {
        var world = new World(42,new FlatWorldGenerator());
        var chunk = world.loadChunk(new ChunkPosition(1,0,2));
        var chunks = world.getChunks();
        var voxel = world.getVoxel(16,0,32);
        var preview = new InteractiveCpuPreview(new CpuWorldRenderer(world),17,11);
        preview.image();
        preview.update(new CameraInput(1,0,1,0,0,0,0,false,false),.1);
        preview.image();
        assertEquals(chunks,world.getChunks());
        assertSame(chunk,world.getChunkIfPresent(new ChunkPosition(1,0,2)));
        assertEquals(voxel,world.getVoxel(16,0,32));
    }
}
