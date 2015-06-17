package hqlplus;

import hqlplus.annotation.ClazzInfo;
import hqlplus.annotation.Disjunction;
import hqlplus.annotation.Elements;
import hqlplus.annotation.FieldInfo;
import hqlplus.annotation.Join;
import hqlplus.annotation.Joins;
import hqlplus.annotation.SelectMore;
import hqlplus.enumeration.CompareMethod;
import hqlplus.enumeration.JoinType;
import hqlplus.enumeration.MatchMode;
import hqlplus.paging.OrderBy;
import hqlplus.paging.Pager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;


public class HqlBuilderImpl<D extends HqlParameter> implements HqlBuilder<D> {

	@Override
	public HqlQuery buildQuery(D param, Pager pager) {
		try {
			Class<D> obj = (Class<D>)param.getClass();
			
			if(obj.equals(HqlParameter.class)){
				throw new RuntimeException("Not implemeted yet.");
			}
			
			String clazzName = null;

            SelectAlias selectAlias = null;
            if (obj.isAnnotationPresent(ClazzInfo.class)) {
                ClazzInfo annotation = obj.getAnnotation(ClazzInfo.class);
                //clazzName = annotation.clazzName();
                clazzName = annotation.clazz().getName().substring(annotation.clazz().getName().lastIndexOf(".") + 1);
                selectAlias = new SelectAlias(clazzName, annotation.alias());
            }

			List<String> whereClauses = new ArrayList<String>();
			Map<String, Object> values = new HashMap<String, Object>();
			
			for(Field field : obj.getDeclaredFields()){
				if(field.isAnnotationPresent(FieldInfo.class)){
					FieldInfo annotation = field.getAnnotation(FieldInfo.class);

					FieldInfoReturnee processFieldInfo = processFieldInfo(annotation, field, param, selectAlias);
					if(processFieldInfo != null && StringUtils.isNotEmpty(processFieldInfo.getClause())) {
						values.putAll(processFieldInfo.getValues());
						whereClauses.add(processFieldInfo.getClause());
					}
				}
				
				if(field.isAnnotationPresent(Elements.class)){
					processElements(whereClauses, field);
				}
				
				if(field.isAnnotationPresent(Disjunction.class)){
					Disjunction annotation = field.getAnnotation(Disjunction.class);
					List<FieldInfoReturnee> orList = new ArrayList<>();
					
					for(FieldInfo fieldInfo : annotation.value()){
						FieldInfoReturnee fieldInfoReturnee = processFieldInfo(fieldInfo, field, param, selectAlias);
						if(fieldInfoReturnee != null){
							orList.add(fieldInfoReturnee);
						}

					}
					
					if(orList.size() > 0){
						String clause = " ( "; 
						
						int i = 0;
						for(FieldInfoReturnee returnee : orList){
							i++;
							clause = clause + returnee.getClause();
							
							if(orList.size() != i){
								clause = clause + " OR ";
							}
							values.putAll(returnee.getValues());
						}
						
						clause = clause + " ) ";
						
						whereClauses.add(clause);
					}
				}
				
					
			}
			
			List<SelectAlias> moreSelects = new ArrayList<SelectAlias>();
			if (obj.isAnnotationPresent(SelectMore.class)) {
				SelectMore annotation = obj.getAnnotation(SelectMore.class);
				//clazzName = annotation.clazzName();
                clazzName = annotation.clazz().getName().substring(annotation.clazz().getName().lastIndexOf(".") + 1);
				moreSelects.add(new SelectAlias(clazzName, annotation.alias()));
			}

			StringBuilder selectBuilder = new StringBuilder();
			
			select(selectAlias, moreSelects, selectBuilder);
			join(obj, selectBuilder, true, selectAlias.getAlias());
			where(whereClauses, selectBuilder);
			order(pager, selectBuilder, selectAlias.getAlias());

			StringBuilder countBuilder = new StringBuilder();
			count(selectAlias, moreSelects, countBuilder);
			join(obj, countBuilder, false, selectAlias.getAlias());
			where(whereClauses, countBuilder);

	        return new HqlQuery(selectBuilder.toString(), countBuilder.toString(), values);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private void processElements(List<String> whereClauses, Field field) {
		Elements annotation = field.getAnnotation(Elements.class);
		String clause = annotation.alias() + " IN elements(" + annotation.inAliasField() + ") ";
		whereClauses.add( clause );
	}

	private FieldInfoReturnee processFieldInfo(FieldInfo annotation, Field field, D param, SelectAlias selectAlias) throws IllegalAccessException {
		field.setAccessible(true);
		String fieldName = field.getName();

		if(!annotation.fieldName().equals("")){
			fieldName = annotation.fieldName();
		}
		
		Object fieldValue = field.get(param);
		
		CompareMethod compareMethod = annotation.compareMethod();
		
		String alias = selectAlias.getAlias();
		if (StringUtils.isNotEmpty(annotation.alias())) {
			alias = annotation.alias();
		}

		Locale locale = param.getLocale();

		MatchMode matchMode = annotation.matchMode();

		boolean isMap = fieldValue != null && fieldValue instanceof Map;
		boolean isCollection = fieldValue != null && fieldValue instanceof Collection;
		boolean isString = fieldValue != null && fieldValue instanceof String;
		
		int mapSize = 0;
		int collectionSize = 0;
		int stringSize = 0;
		
		if(fieldValue != null){
			if(isMap){
				mapSize = ((Map)fieldValue).size();
			}
			if(isCollection){
				collectionSize = ((Collection)fieldValue).size();
			}
			if(isString){
				stringSize = ((String)fieldValue).length();
			}
		}
		
		if(fieldValue != null 
				&& !(isString && stringSize == 0) 
				&& !(isMap && mapSize == 0) 
				&& !(isCollection && collectionSize == 0)){
			FieldInfoReturnee returnee = new FieldInfoReturnee();
			if(isMap){
				Map<String, String> map = (Map<String, String>)fieldValue;
		        if (map.size() > 0) {
		            for (Map.Entry<String, String> attribute : map.entrySet()) {
		                if (!StringUtils.isEmpty(attribute.getKey())
		                        && !StringUtils.isEmpty(attribute.getValue())) {
		                	
		                	String paramNameKey = alias.concat(attribute.getKey()).concat("KEY");
		                	String paramNameVal = alias.concat(attribute.getKey()).concat("VAL");
		                	
		                	if(compareMethod == CompareMethod.LIKE){
		                		returnee.getValues().put(paramNameKey, attribute.getKey());
		                		returnee.getValues().put(paramNameVal, matchMode.toMatchString(attribute.getValue()));
		                		
		                		String clause = alias+".key = :"+paramNameKey + " AND " + nlsLike(alias+".value", paramNameVal, locale) ;
		                		
		                		returnee.setClause( clause );
		                	} else {
		                		throw new RuntimeException("Not implemented.");
		                	}
		                }
		            }
		        }
			} else {
		        String paramName = alias.concat(fieldName);

                paramName = StringUtils.replace(paramName, ".", "");

				if(compareMethod == CompareMethod.EQ){
					returnee.getValues().put(paramName, fieldValue);
					
					String clause = alias + "." + fieldName + " = :"+paramName;
					returnee.setClause( clause );
				} else if(compareMethod == CompareMethod.GT){
					returnee.getValues().put(paramName, fieldValue);
					
					String clause = alias + "." + fieldName + " > :"+paramName;
					returnee.setClause( clause );
				} else if(compareMethod == CompareMethod.GE){
                    returnee.getValues().put(paramName, fieldValue);

                    String clause = alias + "." + fieldName + " >= :"+paramName;
                    returnee.setClause( clause );
                } else if(compareMethod == CompareMethod.LT){
                    returnee.getValues().put(paramName, fieldValue);

                    String clause = alias + "." + fieldName + " < :"+paramName;
                    returnee.setClause( clause );
                } else if(compareMethod == CompareMethod.LE){
                    returnee.getValues().put(paramName, fieldValue);

                    String clause = alias + "." + fieldName + " <= :"+paramName;
                    returnee.setClause( clause );
                } else if(compareMethod == CompareMethod.IN){
					returnee.getValues().put(paramName, fieldValue);
					
					String clause = alias + "." + fieldName + " IN (:"+paramName + ")";
					returnee.setClause( clause );
				} else if(compareMethod == CompareMethod.LIKE){
					returnee.getValues().put(paramName, matchMode.toMatchString(fieldValue.toString()));
					
					String clause = nlsLike(alias + "." + fieldName, paramName, locale );
					returnee.setClause( clause );
				}
			}
			return returnee;
		}
		
		return null;
	}

//	private Query query(Map<String, Object> values, StringBuilder queryBuilder) {
//		Query query = getSession().createQuery(queryBuilder.toString());
//		
//		for (Map.Entry<String, Object> entry : values.entrySet()) {
//		    if (entry.getValue() instanceof Collection) {
//		        query.setParameterList(entry.getKey(), (Collection) entry.getValue());
//		    } else {
//		        query.setParameter(entry.getKey(), entry.getValue());
//		    }
//		}
//		return query;
//	}

	private void order(Pager pager, StringBuilder queryBuilder, String selectAlias) {
		queryBuilder.append(" ORDER BY ");
		
		OrderBy orderBy = pager.getOrderBy();
		//queryBuilder.append(selectAlias);
		
		if (!StringUtils.isEmpty(orderBy.getName())) {
		    if (orderBy.isAsc()) {
		    	queryBuilder.append(orderBy.getName());
		    } else {
		    	queryBuilder.append(orderBy.getName());
		    }
		} else {
			queryBuilder.append(selectAlias).append(".id");
		}
		
	    if (orderBy.isAsc()) {
	    	queryBuilder.append(" ASC");
	    } else {
	    	queryBuilder.append(" DESC");
	    }
	}

	private void select(SelectAlias selectAlias, List<SelectAlias> moreSelects, StringBuilder queryBuilder) {
		queryBuilder.append("SELECT distinct ")
		.append(selectAlias.getAlias());
		
		queryBuilder.append(" FROM ")
		.append(selectAlias.getClazz()).append(" ")
		.append(selectAlias.getAlias());
		

		for(SelectAlias a : moreSelects){
			queryBuilder.append(" , ")
			.append(a.getClazz()).append(" ")
			.append(a.getAlias());
		}
	}
	
	private void count(SelectAlias selectAlias, List<SelectAlias> moreSelects, StringBuilder queryBuilder) {
		queryBuilder.append("SELECT count( distinct ")
		.append(selectAlias.getAlias())
		.append(")");
		
		queryBuilder.append(" FROM ")
		.append(selectAlias.getClazz()).append(" ")
		.append(selectAlias.getAlias());
		

		for(SelectAlias a : moreSelects){
			queryBuilder.append(" , ")
			.append(a.getClazz()).append(" ")
			.append(a.getAlias());
		}
	}

	private void join(Class<D> obj, StringBuilder queryBuilder, boolean fetch, String selectAlias) {
		if (obj.isAnnotationPresent(Join.class)) {
			Join annotation = obj.getAnnotation(Join.class);
			joinAnnotation(queryBuilder, selectAlias, annotation, fetch);
		}

		if (obj.isAnnotationPresent(Joins.class)) {
			Joins annotation = obj.getAnnotation(Joins.class);
			for(Join join : annotation.value()){
				joinAnnotation(queryBuilder, selectAlias, join, fetch);
			}
		}
	}

	private void where(List<String> whereClauses, StringBuilder queryBuilder) {
		if ( whereClauses.size() > 0) {
			int i = 0;
			queryBuilder.append(" WHERE ");
			for(String s : whereClauses){
				i++;
				queryBuilder.append(s);
				if(whereClauses.size() != i){
					queryBuilder.append(" AND ");
				}
			}
		}
	}

    protected String nlsLike(String field, String param, Locale locale){
    	//TODO implement more
    	if(locale.getLanguage().equals("tr")){
    		String nls = ", 'NLS_SORT=XTURKISH'";
    		return "NLS_LOWER ("+field+nls+") like NLS_LOWER (:"+param+nls+")";
    	}
    	return "LOWER ("+field+ ") like LOWER (:"+param+")";
    }
    
	private void joinAnnotation(StringBuilder queryBuilder, String selectAlias, Join annotation, boolean fetch) {
		JoinType joinType = annotation.joinType();
		String collection = annotation.collection();
		
		queryBuilder.append(" ").append(joinType.getJoinWord()).append(" ");
		
		if(fetch){
			queryBuilder.append(" FETCH ");
		}
		
		queryBuilder.append(" ").append(collection).append(" AS ").append(annotation.alias());
	}

	class SelectAlias {
		private String clazz;
		private String alias;
		
		public SelectAlias(String clazz, String alias){
			this.clazz = clazz;
			this.alias = alias;
		}
		
		public String getClazz() {
			return clazz;
		}
		public void setClazz(String clazz) {
			this.clazz = clazz;
		}
		public String getAlias() {
			return alias;
		}
		public void setAlias(String alias) {
			this.alias = alias;
		}
	}
	
	class FieldInfoReturnee {
		private String clause;
		private Map<String, Object> values = new HashMap<String, Object>();
		
		public String getClause() {
			return clause;
		}
		public void setClause(String clause) {
			this.clause = clause;
		}

		public Map<String, Object> getValues() {
			return values;
		}

		public void setValues(Map<String, Object> values) {
			this.values = values;
		}
	}
	
}
