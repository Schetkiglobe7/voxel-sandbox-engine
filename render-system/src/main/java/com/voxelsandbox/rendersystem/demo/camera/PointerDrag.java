package com.voxelsandbox.rendersystem.demo.camera;

/** Computes drag deltas, discarding stale cursor positions on release/focus loss. */
public final class PointerDrag {
    public record Delta(double x,double y) {}
    private boolean dragging;
    private double previousX, previousY;
    public Delta sample(boolean enabled,boolean pressed,double x,double y) {
        if (!enabled || !pressed) { dragging = false; return new Delta(0,0); }
        if (!Double.isFinite(x) || !Double.isFinite(y)) throw new IllegalArgumentException("Invalid cursor position");
        var delta = dragging ? new Delta(x-previousX,y-previousY) : new Delta(0,0);
        previousX = x; previousY = y; dragging = true;
        return delta;
    }
}
