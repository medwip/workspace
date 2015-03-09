package com.guntzergames.medievalwipeout.beans;

import java.util.List;

import com.guntzergames.medievalwipeout.utils.JsonUtils;
import com.guntzergames.medievalwipeout.views.GameView;

public class GameViewList {
	
	private List<GameView> gameViews;
	
	public GameViewList() {
		super();
	}
	
	public GameViewList(List<GameView> gameViews) {
		setGameViews(gameViews);
	}

	public List<GameView> getGameViews() {
		return gameViews;
	}

	public void setGameViews(List<GameView> gameViews) {
		this.gameViews = gameViews;
	}

	public static GameViewList fromJson(String json) {
		return JsonUtils.fromJson(GameViewList.class, json);
	}

}
