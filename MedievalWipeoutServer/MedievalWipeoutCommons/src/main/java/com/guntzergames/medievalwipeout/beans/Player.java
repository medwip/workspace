package com.guntzergames.medievalwipeout.beans;

import java.util.LinkedList;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
@Table(name = "PLAYER")
public class Player {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private long id;
	
	@ManyToOne(targetEntity = DeckTemplate.class)
	@JoinColumn(name = "DECK_TEMPLATE_KEY")
	private DeckTemplate deckTemplate;
	
	@JsonIgnore
	@ManyToOne(targetEntity = Game.class, cascade=CascadeType.ALL)
	@JoinColumn(name = "GAME_KEY")
	private Game game;
	
	@Transient
	private PlayerDeck playerDeck = new PlayerDeck();
	@Transient
	private PlayerHand playerHand = new PlayerHand();
	@Transient
	private PlayerField playerFieldAttack = new PlayerField(), playerFieldDefense = new PlayerField();
	@Transient
	private PlayerDiscard playerDiscard = new PlayerDiscard();
	@Transient
	private PlayerDeckCard playerDeckCard1, playerDeckCard2;
	@Transient
	private LinkedList<GameEvent> events = new LinkedList<GameEvent>();
	@Transient
	private int lifePoints;
	@Transient
	private int trade;
	@Transient
	private int gold;
	@Transient
	private int defense;
	@Transient
	private int currentDefense;
	@Transient
	private int faith;

	public Account getAccount() {
		return deckTemplate != null ? deckTemplate.getAccount() : null;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
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

	public PlayerField getPlayerFieldAttack() {
		return playerFieldAttack;
	}

	public void setPlayerFieldAttack(PlayerField playerFieldAttack) {
		this.playerFieldAttack = playerFieldAttack;
	}

	public PlayerField getPlayerFieldDefense() {
		return playerFieldDefense;
	}

	public void setPlayerFieldDefense(PlayerField playerFieldDefense) {
		this.playerFieldDefense = playerFieldDefense;
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
	
	public boolean hasSameAccount(Player player) {
		return player != null && getAccount().getFacebookUserId().equals(player.getAccount().getFacebookUserId());
	}

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
		return String.format("Id: %s, account: %s", id, getAccount() != null ? getAccount().getFacebookUserId() : "null");
	}
	
}
