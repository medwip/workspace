package com.guntzergames.medievalwipeout.beans;

import java.util.ArrayList;
import java.util.List;

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
	private List<Player> players = new ArrayList<Player>();
	private Player activePlayer;

	private int numberOfPlayers;

	private GameState gameState = GameState.WAITING_FOR_JOINER;
	private Phase phase;
	private ResourceDeck resourceDeck = new ResourceDeck();
	private ResourceDeckCard resourceCard1, resourceCard2;
	private int turn;

	private int trade;
	private int defense;
	private int faith;

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

	public List<Player> selectOpponents(Player player) throws PlayerNotInGameException {

		List<Player> opponents = new ArrayList<Player>();
		
		for ( Player currentPlayer : players ) {
			if ( !currentPlayer.hasSameAccount(player) ) {
				opponents.add(currentPlayer);
			}
		}
		
		return opponents;
	}

	public Player selectPlayer(String userName) throws PlayerNotInGameException {

		for ( Player player : players ) {
			if ( player.getAccount().getFacebookUserId().equals(userName) ) {
				return player;
			}
		}
		
		throw new PlayerNotInGameException();
	}

	public Player selectOpponent(String userName) throws PlayerNotInGameException {

		if (creator.getAccount().getFacebookUserId().equals(userName)) {
			return joiner;
		}

		if (joiner != null && joiner.getAccount().getFacebookUserId().equals(userName)) {
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

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}
	
	public void addPlayer(Player player) {
		players.add(player);
	}
	
	public boolean isPlayerRegistered(String facebookUserId) {
		boolean found = false;
		
		for ( Player currentPlayer : players ) {
			if ( currentPlayer.getAccount().getFacebookUserId().equals(facebookUserId) ) {
				found = true;
				break;
			}
		}
		
		return found;
	}

	public Player getActivePlayer() {
		return activePlayer;
	}

	public void setActivePlayer(Player activePlayer) {
		this.activePlayer = activePlayer;
	}

	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public void setNumberOfPlayers(int numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
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
		gameClientView.setOpponents(selectOpponents(player));
		if (player.getAccount().getFacebookUserId().equals(getActivePlayer().getAccount().getFacebookUserId())) {
			gameClientView.setActivePlayer(true);
		} else {
			gameClientView.setActivePlayer(false);
		}

		gameClientView.setResourceCard1(resourceCard1);
		gameClientView.setResourceCard2(resourceCard2);

		gameClientView.setPlayerHand(player.getPlayerHand());
		gameClientView.setPlayerFieldAttack(player.getPlayerFieldAttack());
		gameClientView.setPlayerFieldDefense(player.getPlayerFieldDefense());

		gameClientView.setGameState(gameState);
		gameClientView.setTurn(turn);
		gameClientView.setPhase(phase);
		gameClientView.setGold(player.getGold());
		gameClientView.setTrade(trade);
		gameClientView.setDefense(defense);
		gameClientView.setFaith(faith);

		return gameClientView;

	}

	public void nextTurn() {

		turn += 1;
		resetPlayable();
		adjustResources();
		selectNextActivePlayer();

	}

	private void resetPlayable(PlayerField playerField) {

		for (PlayerFieldCard card : playerField.getCards()) {
			card.setPlayed(false);
		}

	}

	public void resetPlayable() {

		for ( Player player : getPlayers() ) {
			resetPlayable(player.getPlayerFieldAttack());
			resetPlayable(player.getPlayerFieldDefense());
		}
		
	}

	public void adjustResources() {

		for ( Player player : getPlayers() ) {
			player.adjustResources();
		}

	}
	
	public void selectNextActivePlayer() {
		
		int i = 0;
		
		for ( Player player : getPlayers() ) {
			if ( player == activePlayer ) {
				if ( i < players.size() - 1 ) {
					activePlayer = players.get(i + 1);
				}
				else {
					activePlayer = players.get(0);
				}
				break;
			}
			i++;
		}
		
	}

	public int getTrade() {
		return trade;
	}

	public void setTrade(int trade) {
		this.trade = trade;
	}

	public void addTrade(int trade) {
		this.trade += trade;
	}

	public int getDefense() {
		return defense;
	}

	public void setDefense(int defense) {
		this.defense = defense;
	}

	public void addDefense(int defense) {
		this.defense += defense;
	}

	public int getFaith() {
		return faith;
	}

	public void setFaith(int faith) {
		this.faith = faith;
	}

	public void addFaith(int faith) {
		this.faith += faith;
	}

}
