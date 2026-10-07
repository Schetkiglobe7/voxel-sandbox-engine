package com.voxelsandbox.rendersystem.demo.camera;

/** Window-independent input snapshot. Axes are in [-1, 1]; pointer deltas are pixels. */
public record CameraInput(float forward, float right, float up, float yaw, float pitch,
                          double pointerX, double pointerY, boolean fast, boolean reset) {
    public static final CameraInput NONE = new CameraInput(0,0,0,0,0,0,0,false,false);
    public CameraInput {
        for (float axis : new float[]{forward,right,up,yaw,pitch}) {
            if (!Float.isFinite(axis) || Math.abs(axis) > 1) throw new IllegalArgumentException("Invalid camera axis");
        }
        if (!Double.isFinite(pointerX) || !Double.isFinite(pointerY)) throw new IllegalArgumentException("Invalid pointer delta");
    }
}
