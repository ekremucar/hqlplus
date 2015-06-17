package hqlplus.enumeration;

public enum  JoinType {
    INNER_JOIN("INNER JOIN"),
    LEFT_OUTER_JOIN("LEFT JOIN"),
    RIGHT_OUTER_JOIN("RIGHT OUTER JOIN");

    private String joinWord;

    private JoinType(String joinWord){
        this.joinWord = joinWord;
    }
    public String getJoinWord() {
        return joinWord;
    }

    public void setJoinWord(String joinWord) {
        this.joinWord = joinWord;
    }
}
