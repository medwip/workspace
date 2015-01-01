package com.guntzergames.medievalwipeout.interfaces;

import java.util.List;

import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.beans.CardModel;
import com.guntzergames.medievalwipeout.views.GameView;

public interface GameWebClientCallbackable {

	public boolean isHttpRequestBeingExecuted();
	public void setHttpRequestBeingExecuted(boolean httpRequestBeingExecuted);
	public int getCurrentRequestPriority();
	public void setCurrentRequestPriority(int currentRequestPriority);
	
	public String getFacebookUserId();
	
	public void onError(String err);
	
	public void onGetGame(GameView gameView);
	public void onCheckGame(GameView gameView);
	public void onGameJoined(GameView gameView);
	public void onDeleteGame();
	
	public void onGetAccount(Account account);
	public void onGetCardModels(List<CardModel> cardModels);
	
}
