package com.guntzergames.medievalwipeout.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestQueries {

	private static EntityManagerFactory emf;
	private static EntityManager em;

	@BeforeClass
	public static void setUpClass() throws Exception {
		
		try {
			emf = Persistence.createEntityManagerFactory("mwpu");
			em = emf.createEntityManager();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAccount() {

	}

}
