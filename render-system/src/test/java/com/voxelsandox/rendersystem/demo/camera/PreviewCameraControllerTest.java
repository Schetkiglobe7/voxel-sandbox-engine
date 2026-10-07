package com.voxelsandox.rendersystem.demo.camera;

import com.voxelsandbox.rendersystem.demo.camera.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PreviewCameraControllerTest {
    private CameraInput move(float forward,float right,float up) { return new CameraInput(forward,right,up,0,0,0,0,false,false); }
    @Test void idleAndZeroTimeDoNotMoveCamera() {
        var camera = new PreviewCameraController();
        var initial = camera.snapshot(320,200);
        assertFalse(camera.update(CameraInput.NONE,.1));
        assertFalse(camera.update(move(1,1,1),0));
        assertEquals(initial.getPosition(),camera.snapshot(320,200).getPosition());
        assertEquals(initial.getForward(),camera.snapshot(320,200).getForward());
    }
    @Test void diagonalMovementIsNormalizedAndVerticalMotionIndependentOfPitch() {
        var straight = new PreviewCameraController();
        var diagonal = new PreviewCameraController();
        var initial = straight.snapshot(320,200).getPosition();
        straight.update(move(1,0,0),.1);
        diagonal.update(move(1,1,1),.1);
        assertEquals(1.2f,straight.snapshot(320,200).getPosition().sub(initial).length(),.00001f);
        assertEquals(1.2f,diagonal.snapshot(320,200).getPosition().sub(initial).length(),.00001f);
        assertEquals(initial.y(),straight.snapshot(320,200).getPosition().y());
        var vertical = new PreviewCameraController();
        vertical.update(move(0,0,1),.1);
        assertEquals(initial.x(),vertical.snapshot(320,200).getPosition().x());
        assertEquals(initial.z(),vertical.snapshot(320,200).getPosition().z());
        assertEquals(initial.y()+1.2f,vertical.snapshot(320,200).getPosition().y(),.00001f);
    }
    @Test void movementUsesElapsedTimeAndClampsLongStalls() {
        var one = new PreviewCameraController(); var split = new PreviewCameraController();
        one.update(move(1,0,0),.1);
        split.update(move(1,0,0),.05); split.update(move(1,0,0),.05);
        assertEquals(0,one.snapshot(320,200).getPosition().sub(split.snapshot(320,200).getPosition()).length(),.00001f);
        var stalled = new PreviewCameraController(); stalled.update(move(1,0,0),10);
        assertEquals(one.snapshot(320,200).getPosition(),stalled.snapshot(320,200).getPosition());
    }
    @Test void fastMovementTriplesSpeedAndResetRestoresOriginalPose() {
        var camera = new PreviewCameraController(); var initial = camera.snapshot(320,200);
        camera.update(new CameraInput(1,0,0,1,1,0,0,true,false),.1);
        assertEquals(3.6f,camera.snapshot(320,200).getPosition().sub(initial.getPosition()).length(),.00001f);
        assertTrue(camera.update(new CameraInput(0,0,0,0,0,0,0,false,true),0));
        assertEquals(initial.getPosition(),camera.snapshot(320,200).getPosition());
        assertEquals(initial.getForward(),camera.snapshot(320,200).getForward());
        assertFalse(camera.update(new CameraInput(0,0,0,0,0,0,0,false,true),.1));
    }
    @Test void pitchLimitsKeepTheCameraBasisAndRaysFinite() {
        var camera = new PreviewCameraController();
        camera.update(new CameraInput(0,0,0,0,0,100000,-100000,false,false),0);
        var snapshot = camera.snapshot(320,200);
        assertEquals(1,snapshot.getForward().length(),.00001f);
        assertTrue(snapshot.getForward().y() < 1);
        assertTrue(Float.isFinite(snapshot.getRight().x()));
        assertEquals(1,snapshot.generateRay(160,100,320,200).direction().length(),.00001f);
    }
    @Test void invalidInputsAndTimestepsAreRejected() {
        var camera = new PreviewCameraController();
        for (double time : new double[]{-1,Double.NaN,Double.POSITIVE_INFINITY})
            assertThrows(IllegalArgumentException.class,()->camera.update(CameraInput.NONE,time));
        assertThrows(IllegalArgumentException.class,()->new CameraInput(2,0,0,0,0,0,0,false,false));
        assertThrows(IllegalArgumentException.class,()->new CameraInput(0,0,0,0,0,Double.NaN,0,false,false));
    }
    @Test void dragReanchorsAfterReleaseOrLostFocus() {
        var drag = new PointerDrag();
        assertEquals(new PointerDrag.Delta(0,0),drag.sample(true,true,100,100));
        assertEquals(new PointerDrag.Delta(20,-10),drag.sample(true,true,120,90));
        drag.sample(false,true,500,500);
        assertEquals(new PointerDrag.Delta(0,0),drag.sample(true,true,1000,1000));
        drag.sample(true,false,1000,1000);
        assertEquals(new PointerDrag.Delta(0,0),drag.sample(true,true,10,10));
    }
}
