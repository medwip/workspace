package com.guntzergames.medievalwipeout.activities;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.beans.CardModel;
import com.guntzergames.medievalwipeout.interfaces.Constants;
import com.guntzergames.medievalwipeout.interfaces.GameWebClientCallbackable;
import com.guntzergames.medievalwipeout.views.GameView;
import com.guntzergames.medievalwipeout.webclients.GameWebClient;

public class ApplicationActivity extends FragmentActivity implements GameWebClientCallbackable {

	protected GameWebClient gameWebClient;
	protected GameView gameView;
	protected boolean httpRequestBeingExecuted = false;
	
	public GameWebClient getGameWebClient() {
		return gameWebClient;
	}

	public void setGameWebClient(GameWebClient gameWebClient) {
		this.gameWebClient = gameWebClient;
	}

	public GameView getGameView() {
		return gameView;
	}

	public void setGameView(GameView gameView) {
		this.gameView = gameView;
	}

	public boolean isHttpRequestBeingExecuted() {
		return httpRequestBeingExecuted;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		gameWebClient = new GameWebClient(Constants.SERVER_IP_ADDRESS, this);
		
	}
	
	public void getGame(long gameId) {
		gameWebClient.getGame(gameId);
	}

	@Override
	public void onError(String err) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHttpRequestBeingExecuted(boolean httpRequestBeingExecuted) {
		this.httpRequestBeingExecuted = httpRequestBeingExecuted;
	}

	@Override
	public String getFacebookUserId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onGetGame(GameView gameView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCheckGame(GameView gameView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGameJoined(GameView gameView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeleteGame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetAccount(Account account) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetCardModels(List<CardModel> cardModels) {
		// TODO Auto-generated method stub
		
	}

}
