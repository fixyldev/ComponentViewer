package dev.fixyl.componentviewer.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation is a type-hint to signal that some value, either
 * as a method's return value, parameter or otherwise, can be {@code null}.
 * <p>
 * It is purely a type-hint for humans and therefore distinct from all other
 * {@code @Nullable} annotations which may be used for null-analysis.
 * Therfore, this annotation doesn't have a {@code @NotNullPermitted}
 * counterpart, as seen with {@code @NonNull} annotations, since {@code null}
 * is seen as invalid by default.
 */
@Retention(CLASS)
@Target({ FIELD, LOCAL_VARIABLE, METHOD, PARAMETER, TYPE_USE })
public @interface NullPermitted {}
