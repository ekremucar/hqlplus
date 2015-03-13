package hqlplus.annotation;

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
    
	enum CompareMethod {
		   EQ, GT, IN, LIKE
	}
	
	enum MatchMode {
		EXACT {
			public String toMatchString(String pattern) {
				return pattern;
			}
		},
		
		START {
			public String toMatchString(String pattern) {
				return pattern + '%';
			}
		},
		
		END {
			public String toMatchString(String pattern) {
				return '%' + pattern;
			}
		},

		ANYWHERE {
			public String toMatchString(String pattern) {
				return '%' + pattern + '%';
			}
		};

		public abstract String toMatchString(String pattern);

	}
	
	CompareMethod compareMethod() default CompareMethod.EQ;
	
	MatchMode matchMode() default MatchMode.ANYWHERE;

}
