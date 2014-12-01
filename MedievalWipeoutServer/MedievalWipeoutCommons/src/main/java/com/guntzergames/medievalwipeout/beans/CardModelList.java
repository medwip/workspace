package com.guntzergames.medievalwipeout.beans;

import java.util.List;

public class CardModelList {

	private List<CardModel> cardModels;

	public CardModelList() {
		
	}
	
	public CardModelList(List<CardModel> cardModels) {
		setCardModels(cardModels);
	}

	public List<CardModel> getCardModels() {
		return cardModels;
	}
	
	public void setCardModels(List<CardModel> cardModels) {
		this.cardModels = cardModels;
	}
	
}
