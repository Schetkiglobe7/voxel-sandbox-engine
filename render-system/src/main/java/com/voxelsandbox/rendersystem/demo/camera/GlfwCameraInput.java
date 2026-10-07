package com.voxelsandbox.rendersystem.demo.camera;

import org.lwjgl.system.MemoryStack;
import static org.lwjgl.glfw.GLFW.*;

/** GLFW polling adapter. No native callbacks or cursor capture need lifecycle cleanup. */
public final class GlfwCameraInput {
    private final PointerDrag drag = new PointerDrag();
    public CameraInput sample(long window,boolean enabled) {
        if (!enabled) {
            drag.sample(false,false,0,0);
            return CameraInput.NONE;
        }
        try (var stack = MemoryStack.stackPush()) {
            var x = stack.mallocDouble(1); var y = stack.mallocDouble(1);
            glfwGetCursorPos(window,x,y);
            var pointer = drag.sample(true,glfwGetMouseButton(window,GLFW_MOUSE_BUTTON_RIGHT) == GLFW_PRESS,x.get(0),y.get(0));
            return new CameraInput(axis(window,GLFW_KEY_W,GLFW_KEY_S),axis(window,GLFW_KEY_D,GLFW_KEY_A),
                    axis(window,GLFW_KEY_E,GLFW_KEY_Q),axis(window,GLFW_KEY_RIGHT,GLFW_KEY_LEFT),
                    axis(window,GLFW_KEY_UP,GLFW_KEY_DOWN),pointer.x(),pointer.y(),
                    down(window,GLFW_KEY_LEFT_SHIFT) || down(window,GLFW_KEY_RIGHT_SHIFT),down(window,GLFW_KEY_R));
        }
    }
    private static float axis(long window,int positive,int negative) { return (down(window,positive)?1:0)-(down(window,negative)?1:0); }
    private static boolean down(long window,int key) { return glfwGetKey(window,key) == GLFW_PRESS; }
}
