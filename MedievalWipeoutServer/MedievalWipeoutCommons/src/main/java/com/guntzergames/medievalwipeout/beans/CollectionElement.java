package com.guntzergames.medievalwipeout.beans;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.guntzergames.medievalwipeout.interfaces.ICard;

@Entity
@Table(name = "COLLECTION_ELEMENT")
@NamedQueries({ 
	@NamedQuery(name = CollectionElement.NQ_FIND_BY_ACCOUNT, query = "SELECT c FROM CollectionElement c WHERE c.account = :account"),
	@NamedQuery(name = CollectionElement.NQ_FIND_BY_ACCOUNT_AND_CARD_MODEL, query = "SELECT c FROM CollectionElement c WHERE c.account = :account AND c.cardModel = :cardModel")	
})
public class CollectionElement implements ICard {
	
	public static final String NQ_FIND_BY_ACCOUNT = "NQ_FIND_COLLECTION_ELEMENTS_BY_ACCOUNT";
	public static final String NQ_FIND_BY_ACCOUNT_AND_CARD_MODEL = "NQ_FIND_COLLECTION_ELEMENTS_BY_ACCOUNT_AND_CARD_MODEL";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private long id;
	
	@Column(name = "DRAWABLE_RESOURCE_NAME")
	protected String drawableResourceName;
	
	@ManyToOne(targetEntity = CardModel.class)
	@JoinColumn(name = "CARD_MODEL_KEY")
	private CardModel cardModel;
	
	@JsonIgnore
	@ManyToOne(targetEntity = Account.class)
	@JoinColumn(name = "ACCOUNT_KEY")
	private Account account;
	
	@Column(name = "PLAYER_DECK_CARD_NAME")
	protected String name;
	
	@Column(name = "ATTACK")
	protected int attack;
	
	@Column(name = "LIFE_POINTS")
	protected int lifePoints;
	
	@Column(name = "GOLD_COST")
	protected int goldCost;
	
	@Column(name = "FAITH_COST")
	protected int faithCost;
	
	@Column(name = "DEFENSOR")
	private boolean defensor;

	@Column(name = "ARCHER")
	private boolean archer;

	@Column(name = "NUMBER_OF_CARDS")
	private int numberOfCards;
	
	public CollectionElement() {

	}

	public CollectionElement(CardModel model) {
		this.drawableResourceName = model.getDrawableResourceName();
		this.attack = model.getAttack();
		this.lifePoints = model.getLifePoints();
		this.name = model.getName();
		this.goldCost = model.getGoldCost();
		this.faithCost = model.getFaithCost();
		this.archer = model.isArcher();
		this.defensor = model.isDefensor();
		this.cardModel = model;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public CardModel getCardModel() {
		return cardModel;
	}

	public void setCardModel(CardModel cardModel) {
		this.cardModel = cardModel;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getDrawableResourceName() {
		return drawableResourceName;
	}

	public void setDrawableResourceName(String drawableResourceName) {
		this.drawableResourceName = drawableResourceName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public int getLifePoints() {
		return lifePoints;
	}

	public void setLifePoints(int lifePoints) {
		this.lifePoints = lifePoints;
	}

	public int getGoldCost() {
		return goldCost;
	}

	public void setGoldCost(int goldCost) {
		this.goldCost = goldCost;
	}

	public int getFaithCost() {
		return faithCost;
	}

	public void setFaithCost(int faithCost) {
		this.faithCost = faithCost;
	}

	public boolean isDefensor() {
		return defensor;
	}

	public void setDefensor(boolean defensor) {
		this.defensor = defensor;
	}

	public boolean isArcher() {
		return archer;
	}

	public void setArcher(boolean archer) {
		this.archer = archer;
	}

	public int getNumberOfCards() {
		return numberOfCards;
	}

	public void setNumberOfCards(int numberOfCards) {
		this.numberOfCards = numberOfCards;
	}
	
	public void incrementNumberOfCards() {
		numberOfCards++;
	}

	@Override
	public String toString() {
		return String.format("%s: Attack = %s, Life Points = %s", name, attack, lifePoints);
	}

}