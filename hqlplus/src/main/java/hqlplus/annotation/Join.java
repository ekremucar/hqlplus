package hqlplus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Join {
	String collection();

    String alias();
	
	public enum JoinType {
		//INNER_JOIN,
		LEFT_OUTER_JOIN,
		//RIGHT_OUTER_JOIN,
		//FULL_JOIN
		
		;
	}
	
	JoinType joinType() default JoinType.LEFT_OUTER_JOIN;
	

}
