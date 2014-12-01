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

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@Entity
@DiscriminatorValue("PLAYER_DECK_CARD")
public class DeckTemplateElement extends AbstractCard {

	@JsonIgnore
	@ManyToOne(targetEntity = DeckTemplate.class)
	@JoinColumn(name = "DECK_TEMPLATE_KEY")
	protected DeckTemplate deckTemplate;
	
	@ManyToOne(targetEntity = CollectionElement.class)
	@JoinColumn(name = "COLLECTION_ELEMENT_KEY")
	private CollectionElement collectionElement;
	
	@Column(name = "NUMBER_OF_CARDS")
	private int numberOfCards;

	public DeckTemplateElement() {

	}

	public PlayerDeckCard toPlayerDeckCard() {
		PlayerDeckCard playerDeckCard = new PlayerDeckCard();
		playerDeckCard.setDrawableResourceName(getDrawableResourceName());
		playerDeckCard.setAttack(getAttack());
		playerDeckCard.setFaithCost(getFaithCost());
		playerDeckCard.setGoldCost(getGoldCost());
		playerDeckCard.setLifePoints(getLifePoints());
		playerDeckCard.setName(getName());
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

	public CollectionElement getCollectionElement() {
		return collectionElement;
	}

	public void setCollectionElement(CollectionElement collectionElement) {
		this.collectionElement = collectionElement;
	}

	@JsonIgnore
	public String getDrawableResourceName() {
		return collectionElement.getDrawableResourceName();
	}

	@JsonIgnore
	public String getName() {
		return collectionElement.getName();
	}

	@JsonIgnore
	public int getAttack() {
		return collectionElement.getAttack();
	}

	@JsonIgnore
	public int getLifePoints() {
		return collectionElement.getLifePoints();
	}

	@JsonIgnore
	public int getGoldCost() {
		return collectionElement.getGoldCost();
	}

	@JsonIgnore
	public int getFaithCost() {
		return collectionElement.getFaithCost();
	}

	public int getNumberOfCards() {
		return numberOfCards;
	}

	public void setNumberOfCards(int numberOfCards) {
		this.numberOfCards = numberOfCards;
	}

	@Override
	public String toString() {
		return String.format("%s: Attack = %s, Life Points = %s", getName(), getAttack(), getLifePoints());
	}

}