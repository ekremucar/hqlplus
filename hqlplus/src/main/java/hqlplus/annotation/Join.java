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

		
		INNER_JOIN("INNER JOIN"),
		LEFT_OUTER_JOIN("LEFT JOIN"),
		RIGHT_OUTER_JOIN("RIGHT OUTER JOIN");
		//FULL_JOIN
		
		
		private JoinType(String joinWord){
			this.joinWord = joinWord;
		}
		
		private String joinWord;
		
		public String getJoinWord() {
			return joinWord;
		}
		
		public void setJoinWord(String joinWord) {
			this.joinWord = joinWord;
		}
		;
	}
	
	JoinType joinType() default JoinType.LEFT_OUTER_JOIN;
	

}
