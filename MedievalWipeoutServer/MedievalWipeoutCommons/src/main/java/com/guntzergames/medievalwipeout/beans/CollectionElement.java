package com.guntzergames.medievalwipeout.beans;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "COLLECTION_ELEMENT")
public class CollectionElement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private int id;
	
	@Column(name = "DRAWABLE_RESOURCE_NAME")
	protected String drawableResourceName;
	
	@ManyToOne(targetEntity = CardModel.class)
	@JoinColumn(name = "CARD_MODEL_KEY")
	private CardModel cardModel;
	
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
	
	public CollectionElement() {

	}

	public CollectionElement(CardModel model) {
		this.drawableResourceName = model.getDrawableResourceName();
		this.attack = model.getAttack();
		this.lifePoints = model.getLifePoints();
		this.name = model.getName();
		this.goldCost = model.getGoldCost();
		this.faithCost = model.getFaithCost();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CardModel getCardModel() {
		return cardModel;
	}

	public void setCardModel(CardModel cardModel) {
		this.cardModel = cardModel;
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

	@Override
	public String toString() {
		return String.format("%s: Attack = %s, Life Points = %s", name, attack, lifePoints);
	}

}