package com.guntzergames.medievalwipeout.persistence;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import com.guntzergames.medievalwipeout.beans.Account;

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
	
}
