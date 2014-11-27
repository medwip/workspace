package com.guntzergames.medievalwipeout.abstracts;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

public abstract class AbstractCardList<T extends AbstractCard> {

	private List<T> cards = new ArrayList<T>();

	public List<T> getCards() {
		return cards;
	}

	public void setCards(List<T> cards) {
		this.cards = cards;
	}

	public T pop() {
		T card = cards.get(cards.size() - 1);
		cards.remove(cards.size() - 1);
		return card;
	}
	
	@JsonIgnore
	public T getLast() {
		if ( !cards.isEmpty() ) {
			return cards.get(cards.size() - 1);
		}
		return null;
	}
	
	@JsonIgnore
	public int getLastIndex() {
		return cards.size() - 1;
	}

	public void addCard(T card) {
		cards.add(card);
	}

}
