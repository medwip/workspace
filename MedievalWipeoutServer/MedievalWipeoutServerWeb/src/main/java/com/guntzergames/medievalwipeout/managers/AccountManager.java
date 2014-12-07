package com.guntzergames.medievalwipeout.managers;

import java.util.ArrayList;
import java.util.List;

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
			accountDao.addAccount(account);
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
	
}
