package com.voxelsandox.rendersystem.cpu.target;

import com.voxelsandbox.rendersystem.cpu.target.CpuRenderTarget;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CpuRenderTargetTest {
    @Test void framesClearPixelsAndEnforceLifecycle() {
        var target = new CpuRenderTarget(2, 3);
        assertThrows(IllegalStateException.class, () -> target.drawPixel(0, 0, -1));
        assertThrows(IllegalStateException.class, target::endFrame);
        target.beginFrame();
        assertThrows(IllegalStateException.class, target::beginFrame);
        target.drawPixel(1, 2, 0xFF123456);
        target.endFrame();
        assertEquals(0xFF123456, target.getPixel(1, 2));
        target.beginFrame();
        assertEquals(0, target.getPixel(1, 2));
        target.endFrame();
    }

    @Test void rejectsInvalidDimensionsAndCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> new CpuRenderTarget(0, 1));
        assertThrows(IllegalArgumentException.class, () -> new CpuRenderTarget(1, -1));
        assertThrows(ArithmeticException.class, () -> new CpuRenderTarget(Integer.MAX_VALUE, 2));
        var target = new CpuRenderTarget(2, 3);
        target.beginFrame();
        for (int[] point : new int[][]{{-1, 0}, {2, 0}, {0, -1}, {0, 3}}) {
            assertThrows(IndexOutOfBoundsException.class, () -> target.setPixel(point[0], point[1], -1));
            assertThrows(IndexOutOfBoundsException.class, () -> target.getPixel(point[0], point[1]));
        }
        target.endFrame();
    }
}
