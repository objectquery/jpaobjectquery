package org.objectquery.jpaquerybuilder;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.objectquery.builder.AbstractObjectQuery;
import org.objectquery.builder.GroupType;

public class JPQLObjectQuery<T> extends AbstractObjectQuery<T> {

	public JPQLObjectQuery(Class<T> clazz) {
		super(new JPQLQueryBuilder(GroupType.AND), clazz);
	}

	@SuppressWarnings("rawtypes")
	public List execute(EntityManager entityManager) {
		Query qu = entityManager.createQuery(getQuery());
		Map<String, Object> pars = ((JPQLQueryBuilder) getBuilder()).getParamenters();
		for (Map.Entry<String, Object> ent : pars.entrySet()) {
			qu.setParameter(ent.getKey(), ent.getValue());
		}
		return qu.getResultList();
	}

	@Override
	public JPQLQueryBuilder getBuilder() {
		return (JPQLQueryBuilder) super.getBuilder();
	}

	public String getQuery() {
		return getBuilder().buildQuery(getTargetClass());
	}

}
