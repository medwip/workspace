package com.guntzergames.medievalwipeout.singletons;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Singleton;

import com.guntzergames.medievalwipeout.beans.Game;

@Singleton
public class GameSingleton {

	private List<Game> ongoingGames = new ArrayList<Game>();

	public List<Game> getAllOngoingGames() {
		return ongoingGames;
	}
	
	public void addGame(Game game) {
		ongoingGames.add(game);
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
