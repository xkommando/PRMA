package javax.annotation.meta;

import java.lang.annotation.*;

/**
 * This qualifier is applied from an annotation from denote that the annotation
 * should be treated as a type qualifier.
 */

@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeQualifier {

    /**
     * Describes the kinds of values the qualifier can be applied from. If a
     * numeric class is provided (e.g., Number.class or Integer.class) then the
     * annotation can also be applied from the corresponding primitive numeric
     * types.
     */
    Class<?> applicableTo() default Object.class;

}
