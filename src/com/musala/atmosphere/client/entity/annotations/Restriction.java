package com.musala.atmosphere.client.entity.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that will be used for denoting restrictions on a group of devices that supports a certain implementation
 * for a functionality defined as an entity. All criteria used for group separation might be added to this interface.
 * 
 * @author filareta.yordanova
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Restriction {
    String manufacturer();
}
