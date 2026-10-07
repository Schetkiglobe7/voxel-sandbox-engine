package com.voxelsandbox.rendersystem.demo.camera;

import com.voxelsandbox.rendersystem.core.camera.ICamera3D;
import com.voxelsandbox.rendersystem.core.camera.PerspectiveCamera3D;
import com.voxelsandbox.rendersystem.core.math.*;
import java.util.Objects;

/** Demo-only free camera; explicit input/time keep movement testable without GLFW. */
public final class PreviewCameraController {
    private static final float SPEED = 12f;
    private static final float LOOK_SPEED = (float)Math.toRadians(90);
    private static final double MOUSE_SPEED = .003;
    private static final float PITCH_LIMIT = (float)Math.toRadians(89);
    private static final CpuVec3f START = new CpuVec3f(29,34,38);
    private static final float START_YAW = (float)Math.atan2(-29,38);
    private static final float START_PITCH = (float)Math.atan2(-15,Math.hypot(29,38));
    private Vec3f position = START;
    private float yaw = START_YAW;
    private float pitch = START_PITCH;

    /** Movement is horizontal relative to yaw; Q/E independently change world Y. */
    public boolean update(CameraInput input, double seconds) {
        Objects.requireNonNull(input);
        if (!Double.isFinite(seconds) || seconds < 0) throw new IllegalArgumentException("Invalid camera timestep");
        Vec3f oldPosition = position;
        float oldYaw = yaw, oldPitch = pitch;
        if (input.reset()) {
            position = START; yaw = START_YAW; pitch = START_PITCH;
        } else {
            float dt = (float)Math.min(seconds,.1); // Avoid a jump after a slow render or focus change.
            yaw = wrap(yaw + input.yaw()*LOOK_SPEED*dt + input.pointerX()*MOUSE_SPEED);
            pitch = (float)Math.max(-PITCH_LIMIT,Math.min(PITCH_LIMIT,
                    pitch + input.pitch()*LOOK_SPEED*dt - input.pointerY()*MOUSE_SPEED));
            float sin = (float)Math.sin(yaw), cos = (float)Math.cos(yaw);
            var direction = new CpuVec3f(input.forward()*sin + input.right()*cos,
                    input.up(),-input.forward()*cos + input.right()*sin);
            if (direction.lengthSquared() > 0 && dt > 0) {
                position = position.add(direction.normalize().mul(SPEED*(input.fast() ? 3 : 1)*dt));
            }
        }
        return !position.equals(oldPosition) || yaw != oldYaw || pitch != oldPitch;
    }
    private static float wrap(double angle) { return (float)Math.IEEEremainder(angle,2*Math.PI); }
    public ICamera3D snapshot(int width,int height) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("Invalid viewport");
        float cosPitch = (float)Math.cos(pitch);
        var forward = new CpuVec3f((float)Math.sin(yaw)*cosPitch,(float)Math.sin(pitch),
                -(float)Math.cos(yaw)*cosPitch).normalize();
        return new PerspectiveCamera3D(position,forward,new CpuVec3f(0,1,0),
                (float)Math.toRadians(60),(float)width/height,.1f,120f);
    }
}
