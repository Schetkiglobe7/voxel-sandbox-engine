package com.voxelsandbox.engine.core.lifecycle;


/**
 * Defines a generic lifecycle contract.
 * <p>
 *     Implementations are expected to manage their own internal state.
 * </p>
 */
public interface Lifecycle {

    void start();

    void stop();
}