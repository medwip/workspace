package com.guntzergames.medievalwipeout.beans;

public class PlayerFieldCard extends PlayerDeckCard {
	
	private boolean played = false;
	private int currentLifePoints;
	
	public PlayerFieldCard() {
		
	}

	public PlayerFieldCard(PlayerDeckCard playerDeckCard) {
		super(playerDeckCard);
		this.currentLifePoints = this.lifePoints;
		this.played = false;
	}

	public boolean isPlayed() {
		return played;
	}

	public void setPlayed(boolean played) {
		this.played = played;
	}

	public int getCurrentLifePoints() {
		return currentLifePoints;
	}

	public void setCurrentLifePoints(int currentLifePoints) {
		this.currentLifePoints = currentLifePoints;
	}
	
}
