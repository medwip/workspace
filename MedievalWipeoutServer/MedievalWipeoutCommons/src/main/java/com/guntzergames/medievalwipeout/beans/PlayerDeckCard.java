package com.guntzergames.medievalwipeout.beans;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.guntzergames.medievalwipeout.abstracts.AbstractCard;

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
	
	protected boolean archer, defensor;

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
		this.archer = model.isArcher();
		this.defensor = model.isDefensor();
	}

	public PlayerDeckCard(PlayerDeckCard playerDeckCard) {
		this.id = playerDeckCard.getId();
		this.drawableResourceName = playerDeckCard.getDrawableResourceName();
		this.attack = playerDeckCard.getAttack();
		this.lifePoints = playerDeckCard.getLifePoints();
		this.name = playerDeckCard.getName();
		this.goldCost = playerDeckCard.getGoldCost();
		this.faithCost = playerDeckCard.getFaithCost();
		this.archer = playerDeckCard.isArcher();
		this.defensor = playerDeckCard.isDefensor();
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

	public boolean isArcher() {
		return archer;
	}

	public void setArcher(boolean archer) {
		this.archer = archer;
	}

	public boolean isDefensor() {
		return defensor;
	}

	public void setDefensor(boolean defensor) {
		this.defensor = defensor;
	}

	@Override
	public String toString() {
		return String.format("[%s] %s: Attack = %s, Life Points = %s", this.getClass().getSimpleName(), name, attack, lifePoints);
	}

}
