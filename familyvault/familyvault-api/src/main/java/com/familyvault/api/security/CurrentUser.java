package com.familyvault.api.security;

import java.lang.annotation.*;

/**
 * Annotation to inject the current authenticated user's ID.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
}
