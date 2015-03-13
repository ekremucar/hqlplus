package hqlplus.test;

import hqlplus.HqlBuilderImpl;
import hqlplus.HqlQuery;
import hqlplus.paging.OrderBy;
import hqlplus.paging.Pager;
import hqlplus.test.parameters.BookParameter;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

public class HqlTest {

	@Test
	public void test() {
		HqlBuilderImpl<BookParameter> builder = new HqlBuilderImpl<BookParameter>();
		BookParameter searchParameter = new BookParameter(Locale.forLanguageTag("tr"));
		Pager pager = new Pager(0, 10, new OrderBy("name"));
		HqlQuery hqlQuery = builder.buildQuery(searchParameter, pager);
		Assert.assertEquals("SELECT distinct book FROM Book book ORDER BY name ASC", hqlQuery.getSelectHql());
	}
	
	@Test
	public void testSearchByNameLike() {
		HqlBuilderImpl<BookParameter> builder = new HqlBuilderImpl<BookParameter>();
		BookParameter searchParameter = new BookParameter(Locale.forLanguageTag("tr"));
		searchParameter.setName("ekrem");
		Pager pager = new Pager(0, 10, OrderBy.desc("name"));
		HqlQuery hqlQuery = builder.buildQuery(searchParameter, pager);
		
		Assert.assertEquals("SELECT distinct book FROM Book book WHERE NLS_LOWER (book.name, 'NLS_SORT=XTURKISH') like NLS_LOWER (:bookname, 'NLS_SORT=XTURKISH') ORDER BY name DESC", hqlQuery.getSelectHql());
	}

}
