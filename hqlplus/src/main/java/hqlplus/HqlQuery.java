package hqlplus;

import java.util.Map;

public class HqlQuery {
	private String selectHql;
	private String countHql;
	private Map<String, Object> parameterValues;

	public HqlQuery(String selectHql, String countHql, Map<String, Object> values){
		this.selectHql = selectHql;
		this.countHql = countHql;
		this.parameterValues = values;
	}

	public String getSelectHql() {
		return selectHql;
	}

	public void setSelectHql(String selectHql) {
		this.selectHql = selectHql;
	}

	public String getCountHql() {
		return countHql;
	}

	public void setCountHql(String countHql) {
		this.countHql = countHql;
	}

	public Map<String, Object> getParameterValues() {
		return parameterValues;
	}

	public void setParameterValues(Map<String, Object> parameterValues) {
		this.parameterValues = parameterValues;
	}
}
