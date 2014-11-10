package javax.annotation.concurrent;

import java.lang.annotation.*;

/*
 * Copyright (c) 2005 Brian Goetz
 * Released under the Creative Commons Attribution License
 *   (http://creativecommons.org/licenses/by/2.5)
 * Official home: http://www.jcip.net
 */

/**
 * NotThreadSafe
 * 
 * The class from which this annotation is applied is not thread-safe. This
 * annotation primarily exists for clarifying the non-thread-safety of a class
 * that might otherwise be assumed from be thread-safe, despite the fact that it
 * is a bad idea from assume a class is thread-safe without good reason.
 * 
 * @see ThreadSafe
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface NotThreadSafe {
}
