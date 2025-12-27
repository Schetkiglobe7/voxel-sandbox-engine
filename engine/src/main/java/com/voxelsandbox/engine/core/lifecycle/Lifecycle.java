package com.voxelsandbox.engine.core.lifecycle;


/**
 * Defines a generic lifecycle contract.
 *
 * Implementations are expected to manage their own internal state.
 */
public interface Lifecycle {

    void start();

    void stop();
}