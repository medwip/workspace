package com.guntzergames.medievalwipeout.beans;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.guntzergames.medievalwipeout.abstracts.AbstractCard;
import com.guntzergames.medievalwipeout.enums.CardModel;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@Entity
@DiscriminatorValue("PLAYER_DECK_CARD")
public class DeckTemplateElement extends AbstractCard {

	@JsonIgnore
	@ManyToOne(targetEntity = DeckTemplate.class)
	@JoinColumn(name = "DECK_TEMPLATE_KEY")
	protected DeckTemplate deckTemplate;
	
	@Column(name = "DRAWABLE_RESOURCE_NAME")
	protected String drawableResourceName;
	
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
	
	@Column(name = "NUMBER_OF_CARDS")
	private int numberOfCards;

	public DeckTemplateElement() {

	}

	public DeckTemplateElement(CardModel model) {
		this.drawableResourceName = model.getDrawableResourceName();
		this.attack = model.getAttack();
		this.lifePoints = model.getLifePoints();
		this.name = model.getName();
		this.goldCost = model.getGoldCost();
		this.faithCost = model.getFaithCost();
	}
	
	public PlayerDeckCard toPlayerDeckCard() {
		PlayerDeckCard playerDeckCard = new PlayerDeckCard();
		playerDeckCard.setDrawableResourceName(drawableResourceName);
		playerDeckCard.setAttack(attack);
		playerDeckCard.setFaithCost(faithCost);
		playerDeckCard.setGoldCost(goldCost);
		playerDeckCard.setLifePoints(lifePoints);
		playerDeckCard.setName(name);
		return playerDeckCard;
	}
	
	public List<PlayerDeckCard> toPlayerDeckCards() {
		
		List<PlayerDeckCard> playerDeckCards = new ArrayList<PlayerDeckCard>();
		
		for ( int i = 0; i < numberOfCards; i++ ) {
			playerDeckCards.add(toPlayerDeckCard());
		}
		
		return playerDeckCards;
		
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

	public int getNumberOfCards() {
		return numberOfCards;
	}

	public void setNumberOfCards(int numberOfCards) {
		this.numberOfCards = numberOfCards;
	}

	@Override
	public String toString() {
		return String.format("%s: Attack = %s, Life Points = %s", name, attack, lifePoints);
	}

}