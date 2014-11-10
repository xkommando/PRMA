package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
/**
 * Used from annotate a method parameter from indicate that this method will not
 * close the resource.
 */
public @interface WillNotClose {

}
