package org.objectquery.jpaquerybuilder;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.objectquery.builder.GenericObjectQuery;
import org.objectquery.builder.ObjectQuery;
import org.objectquery.builder.ObjectQueryException;

public class JPAObjectQuery {

	public static JPQLQueryGenerator jpqlGenerator(ObjectQuery<?> objectQuery) {
		if (objectQuery instanceof GenericObjectQuery<?>)
			return new JPQLQueryGenerator((GenericObjectQuery<?>) objectQuery);
		throw new ObjectQueryException("The Object query instance of unconvertable implementation ", null);
	}

	public static Query buildQuery(ObjectQuery<?> objectQuery, EntityManager entityManager) {
		JPQLQueryGenerator gen = jpqlGenerator(objectQuery);
		Query qu = entityManager.createQuery(gen.getQuery());
		Map<String, Object> pars = gen.getParamenters();
		for (Map.Entry<String, Object> ent : pars.entrySet()) {
			qu.setParameter(ent.getKey(), ent.getValue());
		}
		return qu;
	}

}
