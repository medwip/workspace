package com.guntzergames.medievalwipeout.webclients;

import org.apache.http.Header;

import android.util.Log;

import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.beans.CardModelList;
import com.guntzergames.medievalwipeout.interfaces.GameWebClientCallbackable;
import com.guntzergames.medievalwipeout.views.GameView;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class OnGetGameWebAsyncResponse extends AsyncHttpResponseHandler {

	private GameWebClientCallbackable callbackable;
	private ResponseType responseType;

	public enum ResponseType {
		GET_GAME, JOIN_GAME, CHECK_GAME, DELETE_GAME, GET_ACCOUNT, GET_CARD_MODELS
	};

	public OnGetGameWebAsyncResponse(GameWebClientCallbackable callbackable) {
		this.callbackable = callbackable;
	}

	public GameWebClientCallbackable getCallbackable() {
		return callbackable;
	}

	public void setCallbackable(GameWebClientCallbackable callbackable) {
		this.callbackable = callbackable;
	}

	public ResponseType getResponseType() {
		return responseType;
	}

	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}

	@Override
	public void onStart() {
		super.onStart();
		callbackable.setHttpRequestBeingExecuted(true);
	}

	@Override
	public void onFinish() {
		super.onFinish();
		callbackable.setHttpRequestBeingExecuted(false);
	}

	@Override
	public void onSuccess(String response) {
		Log.i("GameWebClient", "Success");
		switch (responseType) {
		case GET_ACCOUNT:
			callbackable.onGetAccount(Account.fromJson(response));
			break;
		case GET_GAME:
			callbackable.onGetGame(GameView.fromJson(response));
			break;
		case JOIN_GAME:
			callbackable.onGameJoined(GameView.fromJson(response));
			break;
		case DELETE_GAME:
			callbackable.onDeleteGame();
			break;
		case CHECK_GAME:
			callbackable.onCheckGame(GameView.fromJson(response));
			break;
		case GET_CARD_MODELS:
			callbackable.onGetCardModels(CardModelList.fromJson(response).getCardModels());
			break;
		default:
			break;
		
		}
	}

	@Override
	public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		callbackable.onError(arg3.getMessage());
	}

}
