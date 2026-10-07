package com.voxelsandbox.rendersystem.cpu.target;

import com.voxelsandbox.rendersystem.core.target.IRenderTarget;
import java.util.Arrays;

/** In-memory ARGB target; each frame starts with transparent pixels. */
public final class CpuRenderTarget implements IRenderTarget {
    private final int width;
    private final int height;
    private final int[] pixels;
    private boolean active;

    public CpuRenderTarget(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Target dimensions must be positive");
        }
        this.width = width;
        this.height = height;
        this.pixels = new int[Math.multiplyExact(width, height)];
    }

    @Override public int getWidth() { return width; }
    @Override public int getHeight() { return height; }

    @Override public void beginFrame() {
        if (active) throw new IllegalStateException("A frame is already active");
        Arrays.fill(pixels, 0);
        active = true;
    }

    @Override public void drawPixel(int x, int y, int argb) {
        setPixel(x, y, argb);
    }

    public void setPixel(int x, int y, int argb) {
        if (!active) throw new IllegalStateException("No active frame");
        pixels[index(x, y)] = argb;
    }

    public int getPixel(int x, int y) { return pixels[index(x, y)]; }

    private int index(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException("Pixel outside render target: " + x + ", " + y);
        }
        return y * width + x;
    }

    @Override public void endFrame() {
        if (!active) throw new IllegalStateException("No active frame");
        active = false;
    }
}
