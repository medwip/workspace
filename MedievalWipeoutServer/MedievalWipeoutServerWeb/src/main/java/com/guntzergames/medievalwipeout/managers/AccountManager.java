package com.guntzergames.medievalwipeout.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.beans.CardModel;
import com.guntzergames.medievalwipeout.beans.CollectionElement;
import com.guntzergames.medievalwipeout.beans.DeckTemplate;
import com.guntzergames.medievalwipeout.beans.DeckTemplateElement;
import com.guntzergames.medievalwipeout.beans.Player;
import com.guntzergames.medievalwipeout.persistence.AccountDao;
import com.guntzergames.medievalwipeout.singletons.BotAccountSingleton;

@Stateless
public class AccountManager {
	
	@EJB
	private BotAccountSingleton botAccountSingleton;
	
	@EJB
	private AccountDao accountDao;
	
	public Account getAccount(String facebookUserId, boolean botAccount) {
		
		Account account = null;
		if ( !botAccount ) {
			account = accountDao.findByFacebookUserId(facebookUserId);
		}
		
		if ( account == null ) {
			account = new Account();
			account.setFacebookUserId(facebookUserId);
			account.setBotAccount(botAccount);
			account.setLevel(1);
			account = accountDao.mergeAccount(account);
			System.out.println("Account before drawPacket " + account);
			drawPacket(10, account);
			account = accountDao.mergeAccount(account);
		}
		
		System.out.println("Account: " + account);
		System.out.println("Templates: " + account.getDeckTemplates());
		
		DeckTemplate deckTemplate = null;
		
		if ( account.isBotAccount() ) {
			deckTemplate = new DeckTemplate();
			List<DeckTemplateElement> cards = new ArrayList<DeckTemplateElement>();
			// TODO
			/*
			cards.add(new DeckTemplateElement(CardModel.GOBLIN_PIRAT));
			cards.add(new DeckTemplateElement(CardModel.TEMPLAR));
			cards.add(new DeckTemplateElement(CardModel.ASTRAL_SPIDER));
			cards.add(new DeckTemplateElement(CardModel.GNOME_ENGINEER));
			cards.add(new DeckTemplateElement(CardModel.GOBLIN_PIRAT));
			cards.add(new DeckTemplateElement(CardModel.GOBLIN_PIRAT));
			cards.add(new DeckTemplateElement(CardModel.GNOME_ENGINEER));
			cards.add(new DeckTemplateElement(CardModel.TEMPLAR));
			cards.add(new DeckTemplateElement(CardModel.ASTRAL_SPIDER));
			cards.add(new DeckTemplateElement(CardModel.ASTRAL_SPIDER));*/
			deckTemplate.setCards(cards);
			List<DeckTemplate> deckTemplates = new ArrayList<DeckTemplate>();
			deckTemplates.add(deckTemplate);
			account.setDeckTemplates(deckTemplates);
		}
		
		return account;
	}
	
	public List<CollectionElement> drawPacket(int numberOfCards, Account account) {
		
		List<CollectionElement> collectionElements = new ArrayList<CollectionElement>();
		
		List<CardModel> cardModels = findCardModelsForAccount(account);
		Random generator = new Random();
		int len = cardModels.size();
		System.out.println("len = " + len);
		
		for ( int i = 0; i < numberOfCards; i++ ) {
			CardModel cardModel = cardModels.get(generator.nextInt(len));
			System.out.println("Account before addCollectionElement " + account);
			CollectionElement collectionElement = accountDao.addCollectionElement(account, cardModel);
			collectionElements.add(collectionElement);
		}
		
		return collectionElements;
		
	}
	
	public void addDeckTemplateElement(CollectionElement collectionElement, DeckTemplate deckTemplate) {
		accountDao.addDeckTemplateElement(collectionElement, deckTemplate);
	}
	
	public Player getBotPlayer(long i) {
		return botAccountSingleton.getBotPlayer(i);
	}

	public List<CardModel> findAllCardModels() {
		return accountDao.findAllCardModels();
	}
	
	public DeckTemplate findDeckTemplateById(long id) {
		return accountDao.findDeckTemplateById(id);
	}
	
	public CollectionElement findCollectionElementById(long id) {
		return accountDao.findCollectionElementById(id);
	}
	
	public List<CardModel> findCardModelsForAccount(Account account) {
		return accountDao.findCardModelsForAccount(account);
	}
	
}
