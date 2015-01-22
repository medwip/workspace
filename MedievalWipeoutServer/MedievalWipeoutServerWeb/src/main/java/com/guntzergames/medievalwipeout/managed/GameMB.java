package com.guntzergames.medievalwipeout.managed;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.guntzergames.medievalwipeout.beans.Game;
import com.guntzergames.medievalwipeout.beans.Player;
import com.guntzergames.medievalwipeout.exceptions.PlayerNotInGameException;
import com.guntzergames.medievalwipeout.managers.AccountManager;
import com.guntzergames.medievalwipeout.managers.GameManager;

@ManagedBean(name = "gameMB")
@SessionScoped
public class GameMB  {

	@EJB
	private GameManager gameManager;
	@EJB
	private AccountManager accountManager;
	
	public List<Game> getAllGames() {
		return gameManager.getAllGames();
	}
	
	public void deleteGame(long gameId) {
		gameManager.deleteGame(gameId);
	}
	
	private Player getBotPlayer(long gameId) {
		return accountManager.getBotPlayer(gameId);
	}
	
	public void joinGame(long gameId) throws PlayerNotInGameException {
		gameManager.joinGame(getBotPlayer(gameId));
	}
	
	public String getPlayers(long gameId) {
		String ret = "";
		Game game = gameManager.getGame(gameId);
		
		for ( Player player : game.getPlayers() ) {
			ret += player + ", ";
		}
		
		return ret;
	}
	
	public void refresh() {
	}
	
}
