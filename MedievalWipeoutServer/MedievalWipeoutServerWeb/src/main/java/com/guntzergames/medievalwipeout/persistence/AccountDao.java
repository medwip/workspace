package com.guntzergames.medievalwipeout.persistence;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.beans.CardModel;
import com.guntzergames.medievalwipeout.beans.CollectionElement;

@Stateless
public class AccountDao {

	@PersistenceContext(unitName = "mwpu")
    private EntityManager em;
	
	public Account findByFacebookUserId(String facebookUserId) {
		
		try {
			return em.createNamedQuery(Account.NQ_FIND_BY_FACEBOOK_USER_ID, Account.class)
						.setParameter("facebookUserId", facebookUserId).getSingleResult();
		}
		catch ( NoResultException e ) {
			return null;
		}
		
	}
	
	public void addAccount(Account account) {
		
		System.out.println("Account: " + account);
		if ( !account.isBotAccount() ) {
			System.out.println("Persisting new account: " + account);
			account = em.merge(account);
		}
		
	}
	
	public void addCollectionElement(CollectionElement collectionElement) {
		
		collectionElement = em.merge(collectionElement);
		
	}
	
	public List<CardModel> findAllCardModels() {
		
		return em.createNamedQuery(CardModel.NQ_FIND_ALL, CardModel.class)
				.getResultList();
		
	}
	
	public List<CollectionElement> findCollectionELementsByAccount(Account account) {
		
		return em.createNamedQuery(CollectionElement.NQ_FIND_BY_ACCOUNT, CollectionElement.class)
				.setParameter("account", account)
				.getResultList();
		
	}
	
}
