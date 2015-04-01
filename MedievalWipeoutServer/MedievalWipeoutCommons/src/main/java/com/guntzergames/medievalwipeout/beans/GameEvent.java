package com.guntzergames.medievalwipeout.beans;

import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class GameEvent {

	public enum PlayerType {
		PLAYER, OPPONENT;
	}
	
	private PlayerType playerType;
	
	public GameEvent() {
		super();
	}
	
	public GameEvent(PlayerType playerType) {
		this.playerType = playerType;
	}

	public PlayerType getPlayerType() {
		return playerType;
	}

	public void setPlayerType(PlayerType playerType) {
		this.playerType = playerType;
	}
	
}
