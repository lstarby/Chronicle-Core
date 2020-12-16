/*
 * Copyright 2016-2020 chronicle.software
 *
 * https://chronicle.software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openhft.chronicle.core;

import net.openhft.chronicle.core.internal.InternalJvm;
import net.openhft.chronicle.core.onoes.ExceptionHandler;
import net.openhft.chronicle.core.onoes.ExceptionKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.misc.SignalHandler;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import static net.openhft.chronicle.core.util.ObjectUtils.requireNonNull;

/**
 * Utility class to access information in the JVM.
 */
public final class Jvm {

    public static final String JAVA_CLASS_PATH = "java.class.path";
    public static final String SYSTEM_PROPERTIES = "system.properties";

    private Jvm() {
    }


    // Todo: Depricate this method and use a static initializer of InternalJvm.
    public static void init() {
        // force static initialisation
        InternalJvm.init();
    }

    /**
     * Returns the compile threshold for the JVM or else an
     * estimate thereof (e.g. 10_000).
     * <p>
     * The compile threshold can be explicitly set using the command
     * line parameter "-XX:CompileThreshold="
     *
     * @return the compile threshold for the JVM or else an
     *         estimate thereof (e.g. 10_000)
     */
    public static int compileThreshold() {
        return InternalJvm.compileThreshold();
    }

    /**
     * Returns the major Java version (e.g. 8, 11 or 17)
     * @return the major Java version (e.g. 8, 11 or 17)
     */
    public static int majorVersion() {
        return InternalJvm.majorVersion();
    }

    // I think this provides little or no extra value over
    // majorVersion() > majorVersion
    public static boolean isMajorVersionGreaterThan(int majorVersion) {
        return majorVersion() > majorVersion;
    }

    /**
     * Returns if the major Java version is higher than 9.
     *
     * @return if the major Java version is higher than 9
     */
    @Deprecated // For removal in x.22. Use majorVersion() > 9 instead.
    public static boolean isJava9Plus() {
        return InternalJvm.isJava9Plus();
    }

    /**
     * Returns if the major Java version is higher than 12.
     *
     * @return if the major Java version is higher than 12
     */
    @Deprecated // For removal in x.22. Use majorVersion() > 12 instead.
    public static boolean isJava12Plus() {
        return InternalJvm.isJava12Plus();
    }

    /**
     * Returns if the major Java version is higher than 14.
     *
     * @return if the major Java version is higher than 14
     */
    @Deprecated // For removal in x.22. Use majorVersion() > 12 instead.
    public static boolean isJava14Plus() {
        return InternalJvm.isJava14Plus();
    }

    @Deprecated // for removal in x.20. Use processId() instead.
    public static int getProcessId() {
        return InternalJvm.getProcessId();
    }

    /**
     * Returns the current process id or, if the process id cannot be determined,
     * a non-negative random number less than 2^16.
     *
     * @return the current process id or, if the process id cannot be determined,
     *         a non-negative random number less than 2^16
     */
    // Todo: Discuss the rational behind the random number. Alternately, 0 could be returned or perhaps -1
    public static int processId() {
        return InternalJvm.getProcessId();
    }

    /**
     * Cast any Throwable (e.g. a checked exception) to a RuntimeException.
     *
     * @param throwable to cast
     * @param <T>       the type of the Throwable
     * @return this method will never return a Throwable instance, it will just throw it.
     * @throws T the throwable as an unchecked throwable
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> RuntimeException rethrow(@NotNull final Throwable throwable) throws T {
        throw (T) throwable; // rely on vacuous cast
    }

    /**
     * Append the provided {@code StackTraceElements} to the provided {@code stringBuilder} trimming some internal methods.
     *
     * @param stringBuilder      to append to
     * @param stackTraceElements stack trace elements
     */
    public static void trimStackTrace(@NotNull final StringBuilder stringBuilder, @NotNull final StackTraceElement... stackTraceElements) {
        requireNonNull(stringBuilder);
        requireNonNull(stackTraceElements);
        InternalJvm.trimStackTrace(stringBuilder, stackTraceElements);
    }

    /**
     * Returns if the JVM is running in debug mode.
     *
     * @return if the JVM is running in debug mode
     */
    @SuppressWarnings("SameReturnValue")
    public static boolean isDebug() {
        return InternalJvm.isDebug();
    }

    /**
     * Returns if the JVM is running in flight recorder mode.
     *
     * @return if the JVM is running in flight recorder mode
     */
    @SuppressWarnings("SameReturnValue")
    public static boolean isFlightRecorder() {
        return InternalJvm.isFlightRecorder();
    }

    /**
     * Returns if the JVM is running in code coverage mode.
     *
     * @return if the JVM is running in code coverage mode
     */
    public static boolean isCodeCoverage() {
        return InternalJvm.isCodeCoverage();
    }

    /**
     * Silently pause for the provided {@code durationMs} milliseconds.
     * <p>
     * If the provided {@code durationMs} is positive, then the
     * current thread sleeps.
     * <p>
     * If the provided {@code durationMs} is zero, then the
     * current thread yields.
     *
     * @param durationMs to sleep for.
     */
    public static void pause(long durationMs) {
        InternalJvm.pause(durationMs);
    }

    /**
     * Pause in a busy loop for a very short time.
     */
    public static void nanoPause() {
        InternalJvm.nanoPause();
    }

    /**
     * Pause in a busy loop for the provided {@code durationUs} microseconds.
     *
     * This method is designed to be used when the time to be waited is very small,
     * typically under a millisecond (@{code durationUs < 1_000}).
     *
     * @param durationUs Time in durationUs
     */
    public static void busyWaitMicros(long durationUs) {
        InternalJvm.busyWaitMicros(durationUs);
    }

    /**
     * Pauses the current thread in a busy loop for the provided {@code durationNs} nanoseconds.
     *
     * This method is designed to be used when the time to be waited is very small,
     * typically under a millisecond (@{code durationNs < 1_000_000}).
     *
     * @param durationNs nanosecond precision counter value to await.
     */
    public static void busyWaitUntil(long durationNs) {
        InternalJvm.busyWaitUntil(durationNs);
    }

    /**
     * Returns the Field for the provided {@code clazz} and the provided {@code fieldName} or
     * throws an Exception if no such Field exists.
     *
     * @param clazz to get the field for
     * @param fieldName of the field
     * @return the Field.
     * @throws AssertionError if no such Field exists
     */

    // Todo: Should not throw an AssertionError but rather a RuntimeException

    @NotNull
    public static Field getField(@NotNull final Class<?> clazz, @NotNull final String fieldName) {
        requireNonNull(clazz);
        requireNonNull(fieldName);
        return InternalJvm.getField(clazz, fieldName);
    }

    /**
     * Returns the Field for the provided {@code clazz} and the provided {@code fieldName} or {@code null}
     * if no such Field exists.
     *
     * @param clazz to get the field for
     * @param fieldName of the field
     * @return the Field.
     * @throws AssertionError if no such Field exists
     */
    @Nullable
    public static Field getFieldOrNull(@NotNull final Class<?> clazz, @NotNull final String fieldName) {
        requireNonNull(clazz);
        requireNonNull(fieldName);
        return InternalJvm.getFieldOrNull(clazz, fieldName);
    }

    // The two methods above could be replaced with Optional<Field> fieldOf(@NotNull final Class<?> clazz, @NotNull final String fieldName)
    // as the methods are not performant anyway.

    /**
     * Returns the Method for the provided {@code clazz}, {@code methodName} and
     * {@code argTypes} or throws an Exception.
     *
     * if it exists or throws {@link AssertionError}.
     * <P>
     * Default methods are not detected unless the class explicitly overrides it
     *
     * @param clazz class
     * @param methodName  methodName
     * @param argTypes argument types
     * @return method
     * @throws AssertionError if no such Method exists
     */

    // Todo: Should not throw an AssertionError but rather a RuntimeException

    @NotNull
    public static Method getMethod(@NotNull final Class<?> clazz,
                                   @NotNull final String methodName,
                                   @NotNull final Class<?>... argTypes) {
        return InternalJvm.getMethod(clazz, methodName, argTypes);
    }

    /**
     * Set the accessible flag for the provided {@code accessibleObject} indicating that
     * the reflected object should suppress Java language access checking when it is used.
     * <p>
     * The setting of the accessible flag might be subject to security manager approval.
     *
     * @param accessibleObject to modify
     * @throws SecurityException – if the request is denied.
     * @see  SecurityManager#checkPermission, RuntimePermission
     */
    public static void setAccessible(@NotNull final AccessibleObject accessibleObject) {
        requireNonNull(accessibleObject);
        InternalJvm.setAccessible(accessibleObject);
    }

    /**
     * Returns the value of the provided {@code fieldName} extracted from the provided {@code target}.
     * <p>
     * The provided {@code fieldName} can denote fields of arbitrary depth (e.g. foo.bar.baz, whereby
     * the foo value will be extracted from the provided {@code target} and then the bar value
     * will be extracted from the foo value and so on).
     * @param target used for extraction
     * @param fieldName denoting the field(s) to extract
     * @param <V> return type
     * @return the value of the provided {@code fieldName} extracted from the provided {@code target}
     */
    @Nullable
    public static <V> V getValue(@NotNull final Object target, @NotNull final String fieldName) {
        requireNonNull(target);
        requireNonNull(fieldName);
        return InternalJvm.getValue(target, fieldName);
    }

    /**
     * Log the stack trace of the thread holding a lock.
     *
     * @param lock to log
     * @return the lock.toString plus a stack trace.
     */
    @NotNull
    public static String lockWithStack(@NotNull final ReentrantLock lock) {
        requireNonNull(lock);
        return InternalJvm.lockWithStack(lock);
    }

    /**
     * Returns the accumulated amount of memory in bytes used by direct ByteBuffers
     * or 0 if the value cannot be determined.
     *<p>
     * (i.e. ever allocated via ByteBuffer.allocateDirect())
     *
     * @return the accumulated amount of memory in bytes used by direct ByteBuffers
     *         or 0 if the value cannot be determined
     */
    public static long usedDirectMemory() {
        return InternalJvm.usedDirectMemory();
    }

    /**
     * Returns the accumulated amount of memory used in bytes by UnsafeMemory.allocate().
     *
     * @return the accumulated amount of memory used in bytes by UnsafeMemory.allocate()
     */
    public static long usedNativeMemory() {
        return InternalJvm.usedNativeMemory();
    }

    /**
     * Returns the maximum direct memory in bytes that can ever be allocated or 0 if the
     * value cannot be determined.
     * (i.e. ever allocated via ByteBuffer.allocateDirect())
     *
     * @return the maximum direct memory in bytes that can ever be allocated or 0 if the
     *         value cannot be determined
     */
    public static long maxDirectMemory() {
        return InternalJvm.maxDirectMemory();
    }

    /**
     * Returns if the JVM runs in 64 bit mode.
     *
     * @return if the JVM runs in 64 bit mode
     */
    public static boolean is64bit() {
        return InternalJvm.is64bit();
    }

    // Todo: BEGIN: Break out Exception handlers to a separate class

    public static void resetExceptionHandlers() {
        InternalJvm.resetExceptionHandlers();
    }

    public static void disableDebugHandler() {
        InternalJvm.disableDebugHandler();
    }

    @NotNull
    public static Map<ExceptionKey, Integer> recordExceptions() {
        return InternalJvm.recordExceptions();
    }

    @NotNull
    public static Map<ExceptionKey, Integer> recordExceptions(boolean debug) {
        return InternalJvm.recordExceptions(debug);
    }

    @NotNull
    public static Map<ExceptionKey, Integer> recordExceptions(boolean debug, boolean exceptionsOnly) {
        return InternalJvm.recordExceptions(debug, exceptionsOnly);
    }

    @NotNull
    public static Map<ExceptionKey, Integer> recordExceptions(boolean debug,
                                                              boolean exceptionsOnly,
                                                              boolean logToSlf4j) {
        return InternalJvm.recordExceptions(debug, exceptionsOnly, logToSlf4j);
    }

    public static boolean hasException(@NotNull final Map<ExceptionKey, Integer> exceptions) {
        requireNonNull(exceptions);
        return InternalJvm.hasException(exceptions);
    }

    @Deprecated(/* to be removed in x.22 */)
    public static void setExceptionsHandlers(@Nullable final ExceptionHandler fatal,
                                             @Nullable final ExceptionHandler warn,
                                             @Nullable final ExceptionHandler debug) {
        InternalJvm.setExceptionHandlers(fatal, warn, debug);
    }

    public static void setExceptionHandlers(@Nullable final ExceptionHandler fatal,
                                            @Nullable final ExceptionHandler warn,
                                            @Nullable final ExceptionHandler debug) {
        InternalJvm.setExceptionHandlers(fatal, warn, debug);
    }

    public static void setExceptionHandlers(@Nullable final ExceptionHandler fatal,
                                            @Nullable final ExceptionHandler warn,
                                            @Nullable final ExceptionHandler debug,
                                            @Nullable final ExceptionHandler perf) {
        InternalJvm.setExceptionHandlers(fatal, warn, debug, perf);
    }

    public static void setThreadLocalExceptionHandlers(@Nullable final ExceptionHandler fatal,
                                                       @Nullable final ExceptionHandler warn,
                                                       @Nullable final ExceptionHandler debug) {
        InternalJvm.setThreadLocalExceptionHandlers(fatal, warn, debug);
    }

    public static void setThreadLocalExceptionHandlers(@Nullable final ExceptionHandler fatal,
                                                       @Nullable final ExceptionHandler warn,
                                                       @Nullable final ExceptionHandler debug,
                                                       @Nullable final ExceptionHandler perf) {
        InternalJvm.setThreadLocalExceptionHandlers(fatal, warn, debug, perf);
    }

    @NotNull
    public static ExceptionHandler fatal() {
        return InternalJvm.fatal();
    }

    @NotNull
    public static ExceptionHandler warn() {
        return InternalJvm.warn();
    }

    @NotNull
    public static ExceptionHandler startup() {
        // TODO, add a startup level?
        return InternalJvm.startup();
    }

    @NotNull
    public static ExceptionHandler perf() {
        return InternalJvm.perf();
    }

    @NotNull
    public static ExceptionHandler debug() {
        return InternalJvm.debug();
    }

    public static void dumpException(@NotNull final Map<ExceptionKey, Integer> exceptions) {
        requireNonNull(exceptions);
        InternalJvm.dumpException(exceptions);
    }

    public static boolean isDebugEnabled(@NotNull final Class<?> clazz) {
        requireNonNull(clazz);
        return InternalJvm.isDebugEnabled(clazz);
    }

    // Todo: END: Break out Exception handlers to a separate class

    /**
     * Adds the provided {@code signalHandler} to an internal chain of handlers that will be invoked
     * upon detecting system signals (e.g. HUP, INT, TERM).
     * <p>
     * Not all signals are available on all operating systems.
     *
     * @param signalHandler to call on a signal
     */
    public static void signalHandler(@NotNull final SignalHandler signalHandler) {
        requireNonNull(signalHandler);
        InternalJvm.signalHandler(signalHandler);
    }

    /**
     * Inserts a low-cost Java safe-point in the code path.
     */
    public static void safepoint() {
        // Todo: Non-compiled performance evaluation with delegation
        InternalJvm.safepoint();
    }

    @Deprecated(/* to be removed in x.22 */)
    public static void optionalSafepoint() {
        InternalJvm.optionalSafepoint();
    }

    // todo: Deprecate this method too as optionalSafepoint() is @Deprecated?
    public static boolean areOptionalSafepointsEnabled() {
        return InternalJvm.areOptionalSafepointsEnabled();
    }

    /**
     * Returns if there is a class name that ends with the provided {@code endsWith} string
     * when examining the current stack trace of depth at most up to the provided {@code maxDepth}.
     *
     * @param endsWith to test against the current stack trace
     * @param maxDepth to examine
     * @return if there is a class name that ends with the provided {@code endsWith} string
     *         when examining the current stack trace of depth at most up to the provided {@code maxDepth}
     */
    public static boolean stackTraceEndsWith(@NotNull final String endsWith, int maxDepth) {
        requireNonNull(endsWith);
        return InternalJvm.stackTraceEndsWith(endsWith, maxDepth);
    }

    /**
     * Returns if the JVM runs on a CPU using the ARM architecture.
     *
     * @return if the JVM runs on a CPU using the ARM architecture
     */
    public static boolean isArm() {
        return InternalJvm.isArm();
    }

    // Todo: Remove throws IllegalArgumentException

    /**
     * Acquires and returns the ClassMetrics for the provided {@code clazz}.
     *
     * @param clazz for which ClassMetrics shall be acquired
     * @return the ClassMetrics for the provided {@code clazz}
     * @throws IllegalArgumentException if no ClassMetrics can be acquired
     * @see ClassMetrics
     */
    @NotNull
    public static ClassMetrics classMetrics(Class<?> clazz) {
        requireNonNull(clazz);
        return InternalJvm.classMetrics(clazz);
    }

    /**
     * Returns the user's home directory (e.g. "/home/alice") or "."
     * if the user's home director cannot be determined.
     *
     * @return the user's home directory (e.g. "/home/alice") or "."
     *         if the user's home director cannot be determined
     */
    @NotNull
    public static String userHome() {
        return InternalJvm.userHome();
    }

    // Todo: document this method
    public static boolean dontChain(@NotNull final Class<?> tClass) {
        requireNonNull(tClass);
        return InternalJvm.dontChain(tClass);
    }

    /**
     * Returns if certain chronicle resources (such as memory regions) are traced.
     * <p>
     * Tracing resources incurs slightly less performance but provides a means
     * of detecting proper release of resources.
     *
     * @return if certain chronicle resources (such as memory regions) are traced
     */
    public static boolean isResourceTracing() {
        return InternalJvm.isResourceTracing();
    }

    /**
     * Returns if a System Property with the provided {@code systemPropertyKey}
     * either exists, is set to "yes" or is set to "true".
     * <p>
     * This provides a more permissive boolean System systemPropertyKey flag where
     * {@code -Dflag} {@code -Dflag=true} {@code -Dflag=yes} are all accepted.
     *
     * @param systemPropertyKey name to lookup
     * @return if a System Property with the provided {@code systemPropertyKey}
     *         either exists, is set to "yes" or is set to "true"
     */
    public static boolean getBoolean(@NotNull final String systemPropertyKey) {
        requireNonNull(systemPropertyKey);
        return InternalJvm.getBoolean(systemPropertyKey);
    }

    /**
     * Returns if a System Property with the provided {@code systemPropertyKey}
     * either exists, is set to "yes" or is set to "true" or, if it does not exist,
     * returns the provided {@code defaultValue}.
     * <p>
     * This provides a more permissive boolean System systemPropertyKey flag where
     * {@code -Dflag} {@code -Dflag=true} {@code -Dflag=yes} are all accepted.
     *
     * @param systemPropertyKey     name to lookup
     * @param defaultValue value to be used if unknown
     * @return if a System Property with the provided {@code systemPropertyKey}
     *         either exists, is set to "yes" or is set to "true" or, if it does not exist,
     *         returns the provided {@code defaultValue}.
     */
    public static boolean getBoolean(@NotNull final String systemPropertyKey, final boolean defaultValue) {
        requireNonNull(systemPropertyKey);
        return InternalJvm.getBoolean(systemPropertyKey, defaultValue);
    }

    /**
     * Returns the native address of the provided {@code byteBuffer}.
     * <p>
     * <em>Use with caution!</em>. Native address should always be carefully
     * guarded to prevent unspecified results or even JVM crashes.
     *
     * @param byteBuffer from which to extract the native address
     * @return the native address of the provided {@code byteBuffer}
     */
    public static long address(@NotNull final ByteBuffer byteBuffer) {
        requireNonNull(byteBuffer);
        return InternalJvm.address(byteBuffer);
    }

    /**
     * Returns the array byte base offset used by this JVM.
     * <p>
     * The value is the number of bytes that precedes the actual
     * memory layout of a {@code byte[] } array in a java array object.
     * <p>
     * <em>Use with caution!</em>. Native address should always be carefully
     * guarded to prevent unspecified results or even JVM crashes.
     *
     * @return the array byte base offset used by this JVM
     */
    public static int arrayByteBaseOffset() {
        return InternalJvm.arrayByteBaseOffset();
    }

    /**
     * Employs a best-effort of preventing the provided {@code fc } from being automatically closed
     * whenever the current thread gets interrupted.
     * <p>
     * If the effort failed, the provided {@code clazz} is used for logging purposes.
     *
     * @param clazz to use for logging should the effort fail.
     * @param fc to prevent from automatically closing upon interrupt.
     */
    public static void doNotCloseOnInterrupt(@NotNull final Class<?> clazz, @NotNull final FileChannel fc) {
        requireNonNull(clazz);
        requireNonNull(fc);
        InternalJvm.doNotCloseOnInterrupt(clazz, fc);
    }

    /**
     * Ensures that all the jars and other resources are added to the class path of the classloader
     * associated by the provided {@code clazz}.
     *
     * @param clazz to use as a template.
     */
    public static void addToClassPath(@NotNull final Class<?> clazz) {
        requireNonNull(clazz);
        InternalJvm.addToClassPath(clazz);
    }

    /**
     * Returns the System Property associated with the provided {@code systemPropertyKey}
     * parsed as a {@code double} or, if no such parsable System Property exists,
     * returns the provided {@code defaultValue}.
     *
     * @param systemPropertyKey  to lookup in the System Properties
     * @param defaultValue       to be used if no parsable key association exists
     * @return the System Property associated with the provided {@code systemPropertyKey}
     *         parsed as a {@code double} or, if no such parsable System Property exists,
     *         returns the provided {@code defaultValue}
     */
    public static double getDouble(@NotNull final String systemPropertyKey, double defaultValue) {
        requireNonNull(systemPropertyKey);
        return InternalJvm.getDouble(systemPropertyKey, defaultValue);
    }

    // Todo: Move to OS
    /**
     * Returns if a process with the provided {@code pid} process id is alive.
     *
     * @param pid the process id (pid) of the process to check
     * @return if a process with the provided {@code pid} process id is alive
     */
    public static boolean isProcessAlive(long pid) {
        return InternalJvm.isProcessAlive(pid);
    }

}