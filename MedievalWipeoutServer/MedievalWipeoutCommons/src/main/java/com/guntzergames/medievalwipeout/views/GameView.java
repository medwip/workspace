package com.guntzergames.medievalwipeout.views;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import com.guntzergames.medievalwipeout.beans.Player;
import com.guntzergames.medievalwipeout.beans.PlayerField;
import com.guntzergames.medievalwipeout.beans.PlayerHand;
import com.guntzergames.medievalwipeout.beans.ResourceDeckCard;
import com.guntzergames.medievalwipeout.enums.GameState;
import com.guntzergames.medievalwipeout.enums.Phase;

public class GameView {
	
	private long id;
	
	private Player player;
	private Player opponent;
	private boolean creator, joiner;
	
	private ResourceDeckCard resourceCard1, resourceCard2;
	
	private PlayerHand playerHand;
	private PlayerField playerField;
	
	private int gold, trade, defense, faith, turn;
	
	private Phase phase;
	private GameState gameState;
	
	public GameView() {
		super();
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getOpponent() {
		return opponent;
	}

	public void setOpponent(Player opponent) {
		this.opponent = opponent;
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

	public PlayerHand getPlayerHand() {
		return playerHand;
	}

	public void setPlayerHand(PlayerHand playerHand) {
		this.playerHand = playerHand;
	}

	public PlayerField getPlayerField() {
		return playerField;
	}

	public void setPlayerField(PlayerField playerField) {
		this.playerField = playerField;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
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

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public Phase getPhase() {
		return phase;
	}

	public void setPhase(Phase phase) {
		this.phase = phase;
	}

	public int getTrade() {
		return trade;
	}

	public void setTrade(int trade) {
		this.trade = trade;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public boolean isCreator() {
		return creator;
	}

	public void setCreator(boolean creator) {
		this.creator = creator;
	}

	public boolean isJoiner() {
		return joiner;
	}

	public void setJoiner(boolean joiner) {
		this.joiner = joiner;
	}
	
	public static GameView fromJson(String json) {
		
		ObjectMapper mapper = new ObjectMapper();
    	GameView gameView = null;
    	try {
			gameView = mapper.readValue(json, GameView.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return gameView;
		
	}
	
	public String toJson() {

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
		mapper.enable(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			mapper.writeValue(out, this);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String json = new String(out.toByteArray());
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	
	}

	@Override
	public String toString() {
		return String.format("Game %s, state=%s, turn=%s, phase=%s", id, gameState, turn, phase);
	}

}
