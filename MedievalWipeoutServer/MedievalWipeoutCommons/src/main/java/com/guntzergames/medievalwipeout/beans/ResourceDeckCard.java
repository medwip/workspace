package com.guntzergames.medievalwipeout.beans;

import com.guntzergames.medievalwipeout.abstracts.AbstractCard;

public class ResourceDeckCard extends AbstractCard {
	
	private int trade;
	private int defense;
	private int faith;
	
	public ResourceDeckCard() {
		
	}
	
	public ResourceDeckCard(int trade, int defense, int faith) {
		this.trade = trade;
		this.defense = defense;
		this.faith = faith;
	}

	public int getTrade() {
		return trade;
	}
	public void setTrade(int trade) {
		this.trade = trade;
	}
	public int getDefense() {
		return defense;
	}
	public void setDefense(int defense) {
		this.defense = defense;
	}
	public int getFaith() {
		return faith;
	}
	public void setFaith(int faith) {
		this.faith = faith;
	}

}
