package hqlplus.paging;

import java.util.List;

public class Pagination<E> {

	private List<E> result;
	private Long count;
	
	public Pagination() {
	}
	
	public Pagination(List<E> result, long count) {
		this.result = result;
		this.count = count;
	}
	
	public List<E> getResult() {
		return result;
	}
	public void setResult(List<E> result) {
		this.result = result;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
}
