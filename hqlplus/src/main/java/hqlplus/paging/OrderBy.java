package hqlplus.paging;

public class OrderBy {
	
	private String name;
	private boolean asc = true;
	
	public OrderBy() {
	}
	
	public OrderBy(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public boolean isAsc() {
		return asc;
	}

	public void setAsc(boolean asc) {
		this.asc = asc;
	}
	
	public static OrderBy asc(String columnName){
		OrderBy orderBy = new OrderBy();
		orderBy.setName(columnName);
		orderBy.setAsc(true);
		return orderBy;
	}
	
	public static OrderBy desc(String columnName){
		OrderBy orderBy = new OrderBy();
		orderBy.setName(columnName);
		orderBy.setAsc(false);
		return orderBy;
	}
	
	
}
