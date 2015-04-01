package com.guntzergames.medievalwipeout.beans;

public class GameEventPlayCard extends GameEvent {

	public enum EventType {
		ATTACK_ATTACK_CARD, ATTACK_DEFENSE_FIELD;
	}
	
	private PlayerDeckCard source;
	private PlayerDeckCard destination;
	private EventType eventType;
	private int sourceIndex, destinationIndex;
	
	public GameEventPlayCard() {
		super();
	}
	
	public GameEventPlayCard(GameEvent.PlayerType playerType) {
		super(playerType);
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
	
	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
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
		event.setPlayerType(getPlayerType() == PlayerType.PLAYER ? PlayerType.OPPONENT : PlayerType.PLAYER);
		event.setSource(source);
		event.setDestination(destination);
		event.setEventType(eventType);
		return event;
	}
	
	@Override
	public String toString() {
		return String.format("%s source=[index=%s][%s], destination=[index=%s][%s]", getPlayerType(), sourceIndex, source, destinationIndex, destination);
	}
	
}
