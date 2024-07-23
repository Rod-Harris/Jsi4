package rojira.jsi4.util.system;

import java.lang.annotation.*;

@Retention( RetentionPolicy.RUNTIME )

@Target( ElementType.METHOD )

@Documented

/**
 * <p> Include this annotation to mark methods as examples
 */
public @interface Example
{
	String value();
	
	String description() default "";
	
	boolean throws_exception() default false;
}
