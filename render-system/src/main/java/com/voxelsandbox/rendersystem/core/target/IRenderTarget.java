package com.voxelsandbox.rendersystem.core.target;

/** Backend-independent pixel output for a render frame. */
public interface IRenderTarget {
    int getWidth();
    int getHeight();
    void beginFrame();
    void drawPixel(int x, int y, int argb);
    void endFrame();
}
