package hqlplus.annotation;

import hqlplus.enumeration.JoinType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Join {
	String collection();

    String alias();
	
	JoinType joinType() default JoinType.LEFT_OUTER_JOIN;
}
