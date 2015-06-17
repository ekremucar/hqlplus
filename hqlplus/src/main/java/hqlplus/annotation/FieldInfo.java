package hqlplus.annotation;

import hqlplus.enumeration.CompareMethod;
import hqlplus.enumeration.MatchMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldInfo {
	String fieldName() default "";

	boolean enabled() default true;

    String alias() default "";

	CompareMethod compareMethod() default CompareMethod.EQ;

	MatchMode matchMode() default MatchMode.ANYWHERE;
}
