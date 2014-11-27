package com.guntzergames.medievalwipeout.beans;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.guntzergames.medievalwipeout.abstracts.AbstractCard;
import com.guntzergames.medievalwipeout.enums.CardModel;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class PlayerDeckCard extends AbstractCard {

	@JsonIgnore
	protected DeckTemplate deckTemplate;
	
	protected String drawableResourceName;
	
	protected String name;
	
	protected int attack;
	
	protected int lifePoints;
	
	protected int goldCost;
	
	protected int faithCost;

	public PlayerDeckCard(int id, String drawableResourceName, String name, int attack, int lifePoints) {
		this.id = id;
		this.drawableResourceName = drawableResourceName;
		this.name = name;
		this.attack = attack;
		this.lifePoints = lifePoints;
	}

	public PlayerDeckCard() {

	}

	public PlayerDeckCard(CardModel model) {
		this.id = model.getId();
		this.drawableResourceName = model.getDrawableResourceName();
		this.attack = model.getAttack();
		this.lifePoints = model.getLifePoints();
		this.name = model.getName();
		this.goldCost = model.getGoldCost();
		this.faithCost = model.getFaithCost();
	}

	public PlayerDeckCard(PlayerDeckCard playerDeckCard) {
		this.id = playerDeckCard.getId();
		this.drawableResourceName = playerDeckCard.getDrawableResourceName();
		this.attack = playerDeckCard.getAttack();
		this.lifePoints = playerDeckCard.getLifePoints();
		this.name = playerDeckCard.getName();
		this.goldCost = playerDeckCard.getGoldCost();
		this.faithCost = playerDeckCard.getFaithCost();
	}

	public DeckTemplate getDeckTemplate() {
		return deckTemplate;
	}

	public void setDeckTemplate(DeckTemplate deckTemplate) {
		this.deckTemplate = deckTemplate;
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
