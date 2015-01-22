package com.guntzergames.medievalwipeout.singletons;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import com.guntzergames.medievalwipeout.beans.Game;
import com.guntzergames.medievalwipeout.persistence.GameDao;

@Singleton
public class GameSingleton {
	
	@EJB
	private GameDao gameDao;

	private List<Game> ongoingGames = new ArrayList<Game>();

	public List<Game> getAllOngoingGames() {
		return ongoingGames;
	}
	
	public Game addGame(Game game) {
		ongoingGames.add(game);
		return game;
	}
	
	public void deleteGame(long gameId) {
		for ( int i = 0; i < ongoingGames.size(); i++ ) {
			long currentId = ongoingGames.get(i).getId();
			if ( gameId == currentId ) {
				ongoingGames.remove(i);
				break;
			}
		}
	}
	
}
