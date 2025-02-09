/*
 * Copyright 2016-2020 chronicle.software
 *
 * https://chronicle.software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openhft.chronicle.core.threads;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;

@FunctionalInterface
public interface EventHandler extends VanillaEventHandler {
    /**
     * This method is called once when it is added to an eventLoop, which might be before the EventLoop has started
     * This could be called in any thread.
     *
     * @param eventLoop the handler has been added to.
     */
    default void eventLoop(EventLoop eventLoop) {
    }

    /**
     * This handler has been added to an EventLoop or when the EventLoop starts.
     * This is always called in the EventLoop thread.
     * This is called after the first called to eventLoop() to make it clearer.
     */
    default void loopStarted() {
    }

    /**
     * Notify handler that the event handler's action method
     * will not be called again. This is an appropriate place to perform cleanup.
     * Event loop implementations call this once only, from the event loop's execution thread.
     * <p>This is called either when the event loop is terminating, or if this EventHandler is being
     * removed from the event loop.
     * <p>If this implements {@link Closeable} then the event loop will call close (once only) on this after
     * loopFinished has been called.
     * <p>If this implements {@link Closeable} and something other than the event loop
     * calls close then it is expected that this will throw {@link InvalidEventHandlerException} next time
     * {@link #action()} is called. This will then allow the event loop to call loopFinished (and close) on this.
     * If this use case is required it is strongly recommend that this event handler guards against close
     * being called more than once (and ignores subsequent calls).
     * <p>Exceptions thrown by loopFinished or close are caught and logged (at debug level)
     * and cleanup continues.
     */
    default void loopFinished() {
    }

    @NotNull
    default HandlerPriority priority() {
        return HandlerPriority.MEDIUM;
    }
}

