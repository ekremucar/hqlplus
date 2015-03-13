package hqlplus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//add one more alias to select

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SelectMore {
	String clazzName();
	String alias();
}
