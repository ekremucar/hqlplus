package hqlplus.paging;


public class Pager {
	int firstResult;
	int maxResult;
	OrderBy orderBy;
	
	public Pager(int firstResult, int maxResult, OrderBy orderBy){
		this.firstResult = firstResult;
		this.maxResult = maxResult;
		this.orderBy = orderBy;
	}
	
	public int getFirstResult() {
		return firstResult;
	}
	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}
	public int getMaxResult() {
		return maxResult;
	}
	public void setMaxResult(int maxResult) {
		this.maxResult = maxResult;
	}
	public OrderBy getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(OrderBy orderBy) {
		this.orderBy = orderBy;
	}
}
