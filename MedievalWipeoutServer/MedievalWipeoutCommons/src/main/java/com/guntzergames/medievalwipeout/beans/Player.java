package com.guntzergames.medievalwipeout.beans;

import java.util.LinkedList;

public class Player {

	private Account account;
	private PlayerDeck playerDeck = new PlayerDeck();
	private PlayerHand playerHand = new PlayerHand();
	private PlayerField playerField = new PlayerField();
	private PlayerDiscard playerDiscard = new PlayerDiscard();
	private DeckTemplate deckTemplate;
	private PlayerDeckCard playerDeckCard1, playerDeckCard2;
	private LinkedList<GameEvent> events = new LinkedList<GameEvent>();
	private int lifePoints;
	private int trade;
	private int gold;
	private int defense;
	private int currentDefense;
	private int faith;

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public PlayerDeck getplayerDeck() {
		return playerDeck;
	}

	public void setPlayerDeck(PlayerDeck deck) {
		this.playerDeck = deck;
	}

	public PlayerHand getPlayerHand() {
		return playerHand;
	}

	public void setPlayerHand(PlayerHand hand) {
		this.playerHand = hand;
	}

	public PlayerDiscard getPlayerDiscard() {
		return playerDiscard;
	}

	public void setPlayerDiscard(PlayerDiscard discard) {
		this.playerDiscard = discard;
	}
	
	public DeckTemplate getDeckTemplate() {
		return deckTemplate;
	}

	public void setDeckTemplate(DeckTemplate deckTemplate) {
		this.deckTemplate = deckTemplate;
	}

	public int getLifePoints() {
		return lifePoints;
	}

	public void setLifePoints(int lifePoints) {
		this.lifePoints = lifePoints;
	}

	public void removeLifePoints(int lifePoints) {
		this.lifePoints -= lifePoints;
	}

	public PlayerField getPlayerField() {
		return playerField;
	}

	public void setPlayerField(PlayerField playerField) {
		this.playerField = playerField;
	}

	public int getTrade() {
		return trade;
	}

	public void setTrade(int trade) {
		this.trade = trade;
	}

	public void addTrade(int trade) {
		this.trade += trade;
	}

	public int getDefense() {
		return defense;
	}

	public void setDefense(int defense) {
		this.defense = defense;
	}

	public void addDefense(int defense) {
		this.defense += defense;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public void addGold(int gold) {
		this.gold += gold;
	}

	public int getCurrentDefense() {
		return currentDefense;
	}

	public void setCurrentDefense(int currentDefense) {
		this.currentDefense = currentDefense;
	}
	
	public void removeCurrentDefense(int currentDefense) {
		this.currentDefense -= currentDefense;
	}

	public int getFaith() {
		return faith;
	}

	public void setFaith(int faith) {
		this.faith = faith;
	}

	public void addFaith(int faith) {
		this.faith += faith;
	}

	public PlayerDeckCard getPlayerDeckCard1() {
		return playerDeckCard1;
	}

	public void setPlayerDeckCard1(PlayerDeckCard playerDeckCard1) {
		this.playerDeckCard1 = playerDeckCard1;
	}

	public PlayerDeckCard getPlayerDeckCard2() {
		return playerDeckCard2;
	}

	public void setPlayerDeckCard2(PlayerDeckCard playerDeckCard2) {
		this.playerDeckCard2 = playerDeckCard2;
	}
	
	public LinkedList<GameEvent> getEvents() {
		return events;
	}

//	@JsonIgnore
	public void setEvents(LinkedList<GameEvent> events) {
		this.events = events;
	}

	public void updatePlayableHandCards() {
		for ( PlayerHandCard card : getPlayerHand().getCards() ) {
			card.setPlayableFromPlayer(this);
		}
	}
	
	public void adjustResources() {
		
		gold += trade;
		currentDefense = defense;
		
	}

	@Override
	public String toString() {
		return String.format("Account: %s", account.getFacebookUserId());
	}
	
}