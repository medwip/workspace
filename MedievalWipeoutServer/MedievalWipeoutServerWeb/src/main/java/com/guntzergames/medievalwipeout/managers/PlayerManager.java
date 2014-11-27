package com.guntzergames.medievalwipeout.managers;

import javax.ejb.Stateless;

import com.guntzergames.medievalwipeout.beans.Game;
import com.guntzergames.medievalwipeout.beans.Player;
import com.guntzergames.medievalwipeout.exceptions.PlayerNotInGameException;

@Stateless
public class PlayerManager {
	
	public Player selectPlayer(Game game, String facebookUserId) throws PlayerNotInGameException {
		
		if ( game.getCreator().getAccount().getFacebookUserId().equals(facebookUserId) ) return game.getCreator();
		if ( game.getJoiner().getAccount().getFacebookUserId().equals(facebookUserId) ) return game.getJoiner();
		
		throw new PlayerNotInGameException();
		
	}
	
}
