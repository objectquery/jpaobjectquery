package org.objectquery.jpaobjectquery;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectquery.ObjectQuery;
import org.objectquery.generic.GenericObjectQuery;
import org.objectquery.generic.ObjectQueryException;
import org.objectquery.generic.ProjectionType;
import org.objectquery.jpaobjectquery.domain.Dog;
import org.objectquery.jpaobjectquery.domain.Person;

public class TestPersistentSubQuery {

	private EntityManager entityManager;

	@Before
	public void beforeTest() {
		entityManager = PersistentTestHelper.getFactory().createEntityManager();
		entityManager.getTransaction().begin();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSubquerySimple() {
		ObjectQuery<Person> query = new GenericObjectQuery<Person>(Person.class);

		ObjectQuery<Person> subQuery = query.subQuery(Person.class);
		subQuery.eq(subQuery.target().getName(), "tomdud");
		query.eq(query.target().getDud(), subQuery);

		List<Person> res = JPAObjectQuery.buildQuery(query, entityManager).getResultList();
		Assert.assertEquals(1, res.size());
		Assert.assertEquals(res.get(0).getName(), "tom");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testBackReferenceSubquery() {
		GenericObjectQuery<Person> query = new GenericObjectQuery<Person>(Person.class);
		Person target = query.target();
		ObjectQuery<Person> subQuery = query.subQuery(Person.class);
		subQuery.eq(subQuery.target().getDog().getName(), target.getDog().getName());
		subQuery.notEq(subQuery.target(), target);
		query.eq(query.target().getDud(), subQuery);

		List<Person> res = JPAObjectQuery.buildQuery(query, entityManager).getResultList();
		Assert.assertEquals(1, res.size());
		Assert.assertEquals(res.get(0).getName(), "tom");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testDoubleSubQuery() {

		GenericObjectQuery<Person> query = new GenericObjectQuery<Person>(Person.class);
		Person target = query.target();
		ObjectQuery<Person> subQuery = query.subQuery(Person.class);
		query.eq(target.getDud(), subQuery);
		subQuery.eq(subQuery.target().getDog().getName(), target.getDog().getName());
		ObjectQuery<Dog> doubSubQuery = subQuery.subQuery(Dog.class);
		subQuery.eq(subQuery.target().getDog(), doubSubQuery);

		doubSubQuery.notEq(doubSubQuery.target().getOwner(), subQuery.target());
		doubSubQuery.notEq(doubSubQuery.target().getOwner(), query.target().getMom());

		List<Person> res = JPAObjectQuery.buildQuery(query, entityManager).getResultList();
		Assert.assertEquals(1, res.size());
		Assert.assertEquals(res.get(0).getName(), "tom");

	}

	@Test
	@SuppressWarnings("unchecked")
	public void testMultipleReferenceSubquery() {
		GenericObjectQuery<Person> query = new GenericObjectQuery<Person>(Person.class);
		Person target = query.target();
		ObjectQuery<Person> subQuery = query.subQuery(Person.class);
		subQuery.eq(subQuery.target().getName(), "tomdud");
		ObjectQuery<Person> subQuery1 = query.subQuery(Person.class);
		subQuery1.eq(subQuery1.target().getName(), "tommum");
		query.eq(target.getDud(), subQuery);
		query.eq(target.getMom(), subQuery1);

		List<Person> res = JPAObjectQuery.buildQuery(query, entityManager).getResultList();
		Assert.assertEquals(1, res.size());
		Assert.assertEquals(res.get(0).getName(), "tom");

	}

	@SuppressWarnings("unchecked")
	@Test(expected = ObjectQueryException.class)
	public void testProjectionSubquery() {
		GenericObjectQuery<Person> query = new GenericObjectQuery<Person>(Person.class);
		Person target = query.target();
		ObjectQuery<Person> subQuery = query.subQuery(Person.class);
		subQuery.eq(subQuery.target().getDog().getOwner(), target.getDud());
		query.prj(subQuery);

		List<Person> res = JPAObjectQuery.buildQuery(query, entityManager).getResultList();
		Assert.assertEquals(3, res.size());
		Assert.assertEquals(res.get(0), null);
		Assert.assertEquals(res.get(1), null);
		Assert.assertEquals(res.get(1), null);

	}

	@Test(expected = ObjectQueryException.class)
	public void testOrderSubquery() {
		GenericObjectQuery<Person> query = new GenericObjectQuery<Person>(Person.class);
		Person target = query.target();
		ObjectQuery<Person> subQuery = query.subQuery(Person.class);
		subQuery.eq(subQuery.target().getDog().getOwner(), target.getDud());
		query.order(subQuery);

		JPAObjectQuery.buildQuery(query, entityManager).getResultList();
	}

	@Test(expected = ObjectQueryException.class)
	public void testHavingSubquery() {
		GenericObjectQuery<Person> query = new GenericObjectQuery<Person>(Person.class);
		Person target = query.target();
		ObjectQuery<Person> subQuery = query.subQuery(Person.class);
		subQuery.eq(subQuery.target().getDog().getOwner(), target.getDud());
		query.having(subQuery, ProjectionType.COUNT).eq(3D);

		JPAObjectQuery.buildQuery(query, entityManager).getResultList();
	}

}