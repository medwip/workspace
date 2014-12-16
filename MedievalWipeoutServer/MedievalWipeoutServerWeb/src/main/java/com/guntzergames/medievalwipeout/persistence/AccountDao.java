package com.guntzergames.medievalwipeout.persistence;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.beans.CardModel;
import com.guntzergames.medievalwipeout.beans.CollectionElement;
import com.guntzergames.medievalwipeout.beans.DeckTemplate;
import com.guntzergames.medievalwipeout.beans.DeckTemplateElement;

@Stateless
public class AccountDao {

	@PersistenceContext(unitName = "mwpu")
	private EntityManager em;

	public Account findByFacebookUserId(String facebookUserId) {

		try {
			return em.createNamedQuery(Account.NQ_FIND_BY_FACEBOOK_USER_ID, Account.class).setParameter("facebookUserId", facebookUserId).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}

	}

	public Account mergeAccount(Account account) {

		System.out.println("Account: " + account);
		if (!account.isBotAccount()) {
			System.out.println("Persisting new account: " + account);
			account = em.merge(account);
			System.out.println("After persisting new account: " + account);
		}
		
		return account;

	}
	
	public void mergeDeckTemplateElement(DeckTemplateElement deckTemplateElement) {
		em.merge(deckTemplateElement);
	}

	public void flush() {
		em.flush();
	}
	
	public void mergeCollectionElement(CollectionElement collectionElement) {
		collectionElement = em.merge(collectionElement);
	}
	
	public CollectionElement addCollectionElement(Account account, CardModel cardModel) {
		
		CollectionElement collectionElement = findCollectionElementsByAccountAndCardModel(account, cardModel);
		
		if ( collectionElement == null ) {
			collectionElement = new CollectionElement(cardModel);
			collectionElement.setAccount(account);
			collectionElement.setNumberOfCards(1);
		}
		else {
			collectionElement.incrementNumberOfCards();
		}
		
		mergeCollectionElement(collectionElement);
		return collectionElement;
		
	}

	public List<CardModel> findAllCardModels() {

		return em.createNamedQuery(CardModel.NQ_FIND_ALL, CardModel.class).getResultList();

	}

	public List<CollectionElement> findCollectionElementsByAccount(Account account) {

		return em.createNamedQuery(CollectionElement.NQ_FIND_BY_ACCOUNT, CollectionElement.class).setParameter("account", account).getResultList();

	}
	
	public CollectionElement findCollectionElementsByAccountAndCardModel(Account account, CardModel cardModel) {

		try {
			return em.createNamedQuery(CollectionElement.NQ_FIND_BY_ACCOUNT_AND_CARD_MODEL, CollectionElement.class)
					.setParameter("account", account)
					.setParameter("cardModel", cardModel)
					.getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}
	
	public List<CardModel> findCardModelsForAccount(Account account) {
		
		List<CardModel> cardModels = em.createNamedQuery(CardModel.NQ_FIND_BY_REQUIRED_LEVEL, CardModel.class).setParameter("requiredLevel", account.getLevel()).getResultList();
		return cardModels;
		
	}

	public DeckTemplateElement findDeckTemplateElementByCollectionElement(CollectionElement collectionElement, DeckTemplate deckTemplate) {

		DeckTemplateElement deckTemplateElement = null;

		try {
			deckTemplateElement = em.createNamedQuery(DeckTemplateElement.NQ_FIND_BY_COLLECTION_ELEMENT_AND_DECK_TEMPLATE, DeckTemplateElement.class)
					.setParameter("collectionElement", collectionElement)
					.setParameter("deckTemplate", deckTemplate)
					.getSingleResult();
		} catch (NoResultException e) {
			deckTemplateElement = null;
		}

		return deckTemplateElement;

	}

	public void addDeckTemplateElement(CollectionElement collectionElement, DeckTemplate deckTemplate) {

		System.out.println(String.format("Before merge, number of elements=%s", deckTemplate.getAccount().getCollectionElements().size()));
		DeckTemplateElement deckTemplateElement = findDeckTemplateElementByCollectionElement(collectionElement, deckTemplate);

		if (deckTemplateElement == null) {
			deckTemplateElement = new DeckTemplateElement();
			deckTemplateElement.setCollectionElement(collectionElement);
			deckTemplateElement.setDeckTemplate(deckTemplate);
			deckTemplateElement.setNumberOfCards(0);
		}
		deckTemplateElement.incrementNumberOfCards();
		mergeDeckTemplateElement(deckTemplateElement);
		System.out.println(String.format("After merge, number of elements=%s", deckTemplate.getAccount().getCollectionElements().size()));

	}
	
	public DeckTemplate findDeckTemplateById(long id) {
		return em.find(DeckTemplate.class, id);
	}
	
	public CollectionElement findCollectionElementById(long id) {
		return em.find(CollectionElement.class, id);
	}

}
