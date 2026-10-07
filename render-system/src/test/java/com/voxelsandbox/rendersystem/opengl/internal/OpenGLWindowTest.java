package com.voxelsandbox.rendersystem.opengl.internal;

import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;

class OpenGLWindowTest {
    private static final class Driver implements OpenGLWindow.Driver {
        final List<String> calls = new ArrayList<>();
        String failAt;
        long createdHandle = 7;
        private void call(String name) {
            calls.add(name);
            if (name.equals(failAt)) throw new IllegalStateException(name);
        }
        public void initialize() { call("init"); }
        public long create(int w,int h,String title,boolean visible) { call("create"); return createdHandle; }
        public void makeCurrent(long handle) { call("current:"+handle); }
        public void createCapabilities() { call("capabilities"); }
        public void clearCapabilities() { call("clear"); }
        public void destroy(long handle) { call("destroy:"+handle); }
        public void terminate() { call("terminate"); }
    }
    @Test void lifecycleIsIdempotentAndCanRestart() {
        var driver = new Driver();
        var window = new OpenGLWindow(driver);
        window.close();
        assertThrows(IllegalStateException.class,window::handle);
        window.initialize(10,10,"test",false);
        window.initialize(10,10,"test",false);
        assertEquals(7,window.handle());
        window.close();
        window.close();
        assertFalse(window.isInitialized());
        assertEquals(List.of("init","create","current:7","capabilities","clear","current:0","destroy:7","terminate"),driver.calls);
        window.initialize(10,10,"test",true);
        window.close();
        assertEquals(2,Collections.frequency(driver.calls,"terminate"));
    }
    @Test void partialInitializationReleasesResourcesAndAllowsRetry() {
        for (String point : List.of("create","current:7","capabilities")) {
            var driver = new Driver(); driver.failAt = point;
            var window = new OpenGLWindow(driver);
            assertThrows(IllegalStateException.class,()->window.initialize(10,10,"test",false));
            assertFalse(window.isInitialized());
            assertTrue(driver.calls.contains("terminate"));
            if (!point.equals("create")) assertTrue(driver.calls.contains("destroy:7"));
            driver.failAt = null;
            window.initialize(10,10,"test",false);
            window.close();
        }
    }
    @Test void missingWindowAndInvalidDimensionsFailCleanly() {
        var driver = new Driver(); driver.createdHandle = 0;
        var window = new OpenGLWindow(driver);
        assertThrows(IllegalArgumentException.class,()->window.initialize(0,10,"test",false));
        assertTrue(driver.calls.isEmpty());
        assertThrows(IllegalStateException.class,()->window.initialize(10,10,"test",false));
        assertEquals(List.of("init","create","terminate"),driver.calls);
    }
    @Test void cleanupContinuesIfClearingCapabilitiesFails() {
        var driver = new Driver();
        var window = new OpenGLWindow(driver);
        window.initialize(10,10,"test",false);
        driver.failAt = "clear";
        assertThrows(IllegalStateException.class,window::close);
        assertTrue(driver.calls.containsAll(List.of("current:0","destroy:7","terminate")));
        assertFalse(window.isInitialized());
    }
    @Test void foreignThreadCannotCloseNativeOwner() throws Exception {
        var driver = new Driver();
        var window = new OpenGLWindow(driver);
        window.initialize(10,10,"test",false);
        var error = new AtomicReference<Throwable>();
        var thread = new Thread(()-> { try { window.close(); } catch (Throwable e) { error.set(e); } });
        thread.start(); thread.join();
        assertInstanceOf(IllegalStateException.class,error.get());
        assertTrue(window.isInitialized());
        window.close();
    }
}
