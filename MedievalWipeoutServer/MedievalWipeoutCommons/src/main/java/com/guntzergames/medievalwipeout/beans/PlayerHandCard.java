package com.guntzergames.medievalwipeout.beans;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("PLAYER_HAND_CARD")
public class PlayerHandCard extends PlayerDeckCard {
	
	private boolean playable = false;

	public PlayerHandCard() {
		
	}

	public PlayerHandCard(PlayerDeckCard playerDeckCard) {
		super(playerDeckCard);
		this.playable = false;
	}
	
	public PlayerHandCard(PlayerDeckCard playerDeckCard, Player player) {
		this(playerDeckCard);
		setPlayableFromPlayer(player);
	}

	public boolean isPlayable() {
		return playable;
	}

	public void setPlayable(boolean playable) {
		this.playable = playable;
	}
	
	public void setPlayableFromPlayer(Player player) {
		System.out.println(String.format("goldCost=%s, faithCost=%s, player.getGold()=%s, player.getFaith()=%s", 
				goldCost, faithCost, player.getGold(), player.getFaith()));
		if ( player.getGold() >= goldCost 
			&& player.getFaith() >= faithCost
		) {
			playable = true;
		}
		else {
			playable = false;
		}
	}
	
}
