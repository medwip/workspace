package com.guntzergames.medievalwipeout.tests;

import java.io.BufferedReader;
import java.io.FileReader;

import org.junit.Assert;
import org.junit.Test;

import com.guntzergames.medievalwipeout.beans.CardModel;
import com.guntzergames.medievalwipeout.beans.GameEvent.PlayerType;
import com.guntzergames.medievalwipeout.beans.GameEventPlayCard;
import com.guntzergames.medievalwipeout.beans.Player;
import com.guntzergames.medievalwipeout.beans.PlayerDeckCard;
import com.guntzergames.medievalwipeout.views.GameView;

public class TestJson {

	@Test
	public void testJson() throws Exception {

		BufferedReader b = new BufferedReader(new FileReader("./src/test/resources/json/game.json"));
		String all = "";
		String str = "";
		while ((str = b.readLine()) != null) {
			all += str;
		}
		b.close();
		GameView gameView = GameView.fromJson(all);
		Player player = gameView.getPlayer();
		GameEventPlayCard gameEventPlayCard = new GameEventPlayCard(PlayerType.PLAYER);
		gameEventPlayCard.setSource(new PlayerDeckCard(CardModel.DEFAULT_CARD));
		gameEventPlayCard.setDestination(new PlayerDeckCard(CardModel.DEFAULT_CARD));
		player.getEvents().add(gameEventPlayCard);
		Assert.assertNotNull(gameView);
		String json = gameView.toJson();
		System.out.println(String.format("JSON: %s", json));
		Assert.assertNotNull(GameView.fromJson(json));
		
	}
	
}
