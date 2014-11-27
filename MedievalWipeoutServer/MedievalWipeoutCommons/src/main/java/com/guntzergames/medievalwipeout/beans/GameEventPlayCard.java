package com.guntzergames.medievalwipeout.beans;

public class GameEventPlayCard extends GameEvent {

	public enum PlayerType {
		PLAYER, OPPONENT;
	}
	
	private PlayerDeckCard source;
	private PlayerDeckCard destination;
	private PlayerType playerType;
	private int sourceIndex, destinationIndex;
	
	public GameEventPlayCard() {
		super();
	}
	
	public GameEventPlayCard(GameEventPlayCard.PlayerType playerType) {
		this();
		this.playerType = playerType;
	}
	
	public PlayerDeckCard getSource() {
		return source;
	}
	public void setSource(PlayerDeckCard source) {
		this.source = source;
	}
	public PlayerDeckCard getDestination() {
		return destination;
	}
	public void setDestination(PlayerDeckCard destination) {
		this.destination = destination;
	}
	public PlayerType getPlayerType() {
		return playerType;
	}
	public void setPlayerType(PlayerType playerType) {
		this.playerType = playerType;
	}
	
	public int getSourceIndex() {
		return sourceIndex;
	}

	public void setSourceIndex(int sourceIndex) {
		this.sourceIndex = sourceIndex;
	}

	public int getDestinationIndex() {
		return destinationIndex;
	}

	public void setDestinationIndex(int destinationIndex) {
		this.destinationIndex = destinationIndex;
	}

	public GameEventPlayCard duplicate() {
		GameEventPlayCard event = new GameEventPlayCard();
		event.setPlayerType(playerType == PlayerType.PLAYER ? PlayerType.OPPONENT : PlayerType.PLAYER);
		event.setSource(source);
		event.setDestination(destination);
		return event;
	}
	
	@Override
	public String toString() {
		return String.format("%s source=[index=%s][%s], destination=[index=%s][%s]", playerType, sourceIndex, source, destinationIndex, destination);
	}
	
}
