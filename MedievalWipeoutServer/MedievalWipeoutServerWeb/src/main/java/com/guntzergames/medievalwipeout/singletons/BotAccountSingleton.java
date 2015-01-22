package com.guntzergames.medievalwipeout.singletons;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.beans.Player;
import com.guntzergames.medievalwipeout.managers.AccountManager;

@Singleton
public class BotAccountSingleton {

	@EJB
	private AccountManager accountManager;
	
	private Map<AtomicLong, Player> botPlayers = new HashMap<AtomicLong, Player>();

	public Player getBotPlayer(long i) {
		AtomicLong ai = new AtomicLong(i);
		if ( !botPlayers.containsKey(ai) ) {
			Account botAccount = accountManager.getAccount("BOT " + i, true);
			botAccount.setBotAccount(true);
			Player botPlayer = new Player();
//			botPlayer.setAccount(botAccount);
			botPlayers.put(ai, botPlayer);
		}
		return botPlayers.get(ai);
	}

}
