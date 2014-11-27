package com.guntzergames.medievalwipeout.beans;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.guntzergames.medievalwipeout.enums.GameState;
import com.guntzergames.medievalwipeout.enums.Phase;
import com.guntzergames.medievalwipeout.exceptions.PlayerNotInGameException;
import com.guntzergames.medievalwipeout.views.GameView;

@Entity
@Table(name = "GAME")
public class Game {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
	private long id;
	
	private Player creator;
	private Player joiner;
	private GameState gameState = GameState.WAITING_FOR_JOINER;
	private Phase phase;
	private ResourceDeck resourceDeck = new ResourceDeck();
	private ResourceDeckCard resourceCard1, resourceCard2;
	private int turn;

	public Player getCreator() {
		return creator;
	}

	public void setCreator(Player creator) {
		this.creator = creator;
	}

	public Player getJoiner() {
		return joiner;
	}

	public void setJoiner(Player joiner) {
		this.joiner = joiner;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}
	
	public Player selectPlayer(Player player) throws PlayerNotInGameException {
		
		return selectPlayer(player.getAccount().getFacebookUserId());
	}

	public Player selectOpponent(Player player) throws PlayerNotInGameException {
		
		return selectOpponent(player.getAccount().getFacebookUserId());
	}

	public Player selectPlayer(String userName) throws PlayerNotInGameException {
		
		if ( creator.getAccount().getFacebookUserId().equals(userName) ) {
			return creator;
		}
		
		if ( joiner != null && joiner.getAccount().getFacebookUserId().equals(userName) ) {
			return joiner;
		}
		
		throw new PlayerNotInGameException();
	}

	public Player selectOpponent(String userName) throws PlayerNotInGameException {
		
		if ( creator.getAccount().getFacebookUserId().equals(userName) ) {
			return joiner;
		}
		
		if ( joiner != null && joiner.getAccount().getFacebookUserId().equals(userName) ) {
			return creator;
		}
		
		throw new PlayerNotInGameException();
	}

	@Override
	public String toString() {
		return String.format("Game %s, state=%s, turn=%s, phase=%s", id, gameState, turn, phase);
	}

	public Phase getPhase() {
		return phase;
	}

	public void setPhase(Phase phase) {
		this.phase = phase;
	}

	public ResourceDeck getResourceDeck() {
		return resourceDeck;
	}

	public void setResourceDeck(ResourceDeck resourceDeck) {
		this.resourceDeck = resourceDeck;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

		public ResourceDeckCard getResourceCard1() {
		return resourceCard1;
	}

	public void setResourceCard1(ResourceDeckCard resourceCard1) {
		this.resourceCard1 = resourceCard1;
	}

	public ResourceDeckCard getResourceCard2() {
		return resourceCard2;
	}

	public void setResourceCard2(ResourceDeckCard resourceCard2) {
		this.resourceCard2 = resourceCard2;
	}

	public GameView buildGameView(Player player) throws PlayerNotInGameException {
		
		GameView gameClientView = new GameView();
		gameClientView.setId(id);
		
		gameClientView.setPlayer(selectPlayer(player));
		gameClientView.setOpponent(selectOpponent(player));
		if ( player.getAccount().getFacebookUserId().equals(creator.getAccount().getFacebookUserId()) ) {
			gameClientView.setCreator(true);
			gameClientView.setJoiner(false);
		}
		else {
			gameClientView.setCreator(false);
			gameClientView.setJoiner(true);
		}
		
		gameClientView.setResourceCard1(resourceCard1);
		gameClientView.setResourceCard2(resourceCard2);
		
		gameClientView.setPlayerHand(player.getPlayerHand());
		gameClientView.setPlayerField(player.getPlayerField());
		
		gameClientView.setGameState(gameState);
		gameClientView.setTurn(turn);
		gameClientView.setPhase(phase);
		gameClientView.setGold(player.getGold());
		gameClientView.setTrade(player.getTrade());
		gameClientView.setDefense(player.getDefense());
		gameClientView.setFaith(player.getFaith());
		
		return gameClientView;
		
	}
	
	public void nextTurn() {
		
		turn += 1;
		resetPlayable();
		adjustResources();
		
	}
	
	public void resetPlayable() {
		
		for ( PlayerFieldCard card : creator.getPlayerField().getCards() ) {
			card.setPlayed(false);
		}
		
		for ( PlayerFieldCard card : joiner.getPlayerField().getCards() ) {
			card.setPlayed(false);
		}
		
	}
	
	public void adjustResources() {
		
		creator.adjustResources();
		joiner.adjustResources();
		
	}
	
}
