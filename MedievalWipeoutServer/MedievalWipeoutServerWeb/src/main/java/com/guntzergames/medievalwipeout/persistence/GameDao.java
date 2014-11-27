package com.guntzergames.medievalwipeout.persistence;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.beans.DeckTemplate;

@Stateless
public class GameDao {

	@PersistenceContext(unitName = "mwpu")
    private EntityManager em;
	
	public DeckTemplate findDeckTemplateById(long deckId) {
		
		return em.find(DeckTemplate.class, deckId);
		
	}
	
	public List<DeckTemplate> findDeckTemplatesByAccount(Account account) {
		
		return em.createNamedQuery(DeckTemplate.NQ_FIND_BY_ACCOUNT, DeckTemplate.class)
				.setParameter("account", account)
				.getResultList();
		
	}
	
}
