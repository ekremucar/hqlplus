package hqlplus;

import hqlplus.paging.Pager;

public interface HqlBuilder<D extends HqlParameter> {
	HqlQuery buildQuery(D param, Pager pager);
}