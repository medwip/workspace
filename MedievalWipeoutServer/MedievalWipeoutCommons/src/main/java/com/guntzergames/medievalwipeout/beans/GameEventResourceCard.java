package com.guntzergames.medievalwipeout.beans;

public class GameEventResourceCard extends GameEvent {
	
	private ResourceDeckCard resourceDeckCard;
	private int resourceId;
	
	public GameEventResourceCard() {
		super();
	}

	public GameEventResourceCard(GameEvent.PlayerType playerType) {
		super(playerType);
	}
	
	public int getResourceId() {
		return resourceId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}
	
	public ResourceDeckCard getResourceDeckCard() {
		return resourceDeckCard;
	}

	public void setResourceDeckCard(ResourceDeckCard resourceDeckCard) {
		this.resourceDeckCard = resourceDeckCard;
	}

	public GameEventResourceCard duplicate() {
		GameEventResourceCard event = new GameEventResourceCard();
		event.setPlayerType(getPlayerType() == PlayerType.PLAYER ? PlayerType.OPPONENT : PlayerType.PLAYER);
		event.setResourceId(resourceId);
		event.setResourceDeckCard(resourceDeckCard);
		return event;
	}
	
}
