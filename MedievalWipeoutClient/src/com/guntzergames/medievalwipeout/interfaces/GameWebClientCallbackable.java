package com.guntzergames.medievalwipeout.interfaces;

import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.views.GameView;

public interface GameWebClientCallbackable {

	public void onError(String err);
	public void setHttpRequestBeingExecuted(boolean httpRequestBeingExecuted);
	
	public String getFacebookUserId();
	
	public void onGetGame(GameView gameView);
	public void onCheckGame(GameView gameView);
	public void onGameJoined(GameView gameView);
	public void onDeleteGame();
	
	public void onGetAccount(Account account);
	
}
