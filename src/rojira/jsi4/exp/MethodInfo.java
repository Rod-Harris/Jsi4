package rojira.jsi4.exp;

import java.lang.annotation.*;

@Retention( RetentionPolicy.RUNTIME )

@Target( ElementType.METHOD )

@Documented

/**
 * <p> Include this annotation to mark methods as examples
 */
public @interface MethodInfo
{
	@SuppressWarnings("rawtypes")
	Class[] min();

	@SuppressWarnings("rawtypes")
	Class[] mout();
	
	@SuppressWarnings("rawtypes")
	Class[] merr();
}
