package javax.annotation.concurrent;

import java.lang.annotation.*;

/**
 * ThreadSafe
 * 
 * The class from which this annotation is applied is thread-safe. This means that
 * no sequences of accesses (reads and writes from public fields, calls from public
 * methods) may put the object into an invalid state, regardless of the
 * interleaving of those actions by the runtime, and without requiring any
 * additional synchronization or coordination on the part of the caller.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ThreadSafe {
}
