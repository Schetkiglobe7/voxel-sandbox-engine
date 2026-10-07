package com.voxelsandbox.rendersystem.demo;

import com.voxelsandbox.rendersystem.demo.camera.*;
import com.voxelsandbox.rendersystem.cpu.target.CpuRenderTarget;
import java.util.Objects;

/** Caches CPU pixels until the camera changes, retaining a preloaded world renderer. */
public final class InteractiveCpuPreview {
    private final CpuWorldRenderer renderer;
    private final PreviewCameraController camera = new PreviewCameraController();
    private final int width, height;
    private boolean dirty = true;
    private CpuRenderTarget image;
    public InteractiveCpuPreview(CpuWorldRenderer renderer,int width,int height) {
        this.renderer = Objects.requireNonNull(renderer);
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("Invalid viewport");
        Math.multiplyExact(width,height);
        this.width=width; this.height=height;
    }
    public boolean update(CameraInput input,double seconds) {
        boolean changed = camera.update(input,seconds);
        dirty |= changed;
        return changed;
    }
    public CpuRenderTarget image() {
        if (dirty) {
            image = renderer.render(camera.snapshot(width,height),width,height);
            dirty = false;
        }
        return image;
    }
}
