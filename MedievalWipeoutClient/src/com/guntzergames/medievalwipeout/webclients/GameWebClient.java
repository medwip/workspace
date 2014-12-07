package com.guntzergames.medievalwipeout.webclients;

import java.io.IOException;

import org.apache.http.Header;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.util.Log;

import com.guntzergames.medievalwipeout.activities.GameActivity;
import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.beans.CardModelList;
import com.guntzergames.medievalwipeout.interfaces.GameWebClientCallbackable;
import com.guntzergames.medievalwipeout.services.MainGameCheckerThread;
import com.guntzergames.medievalwipeout.views.GameView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class GameWebClient {

	AsyncHttpClient client = null;
	GameWebClientCallbackable callbackable = null;
	String ip = null;

	public GameWebClient(String ip, GameWebClientCallbackable callbackable) {
		client = new AsyncHttpClient();
		this.callbackable = callbackable;
		this.ip = ip;
	}

	public void joinGame(String facebookUserId, long deckId) {

		client.get("http://" + ip + ":8080/MedievalWipeout/rest/game/join/" + facebookUserId + "/" + deckId, null, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				GameView game = GameView.fromJson(response);
				callbackable.onGameJoined(game);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				callbackable.onError(arg3.getMessage());
			}

		});
	}

	public GameWebClientCallbackable getCallbackable() {
		return callbackable;
	}

	public void setCallbackable(GameWebClientCallbackable callbackable) {
		this.callbackable = callbackable;
	}

	public void getGame(long gameId) {

		client.get("http://" + ip + ":8080/MedievalWipeout/rest/game/get/" + gameId + "/" + callbackable.getFacebookUserId(), null, new AsyncHttpResponseHandler() {
			@Override
			public void onFinish() {
				super.onFinish();
				callbackable.setHttpRequestBeingExecuted(false);
			}

			@Override
			public void onStart() {
				super.onStart();
				callbackable.setHttpRequestBeingExecuted(true);
			}

			@Override
			public void onSuccess(String response) {

				ObjectMapper mapper = new ObjectMapper();
				GameView game = null;
				try {
					game = mapper.readValue(response, GameView.class);
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				callbackable.onGetGame(game);
			}
		});
	}

	public void getGame(long gameId, final MainGameCheckerThread mainGameCheckerThread) {

		client.get("http://" + ip + ":8080/MedievalWipeout/rest/game/get/" + gameId + "/" + mainGameCheckerThread.getFacebookUserId(), null, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {

				ObjectMapper mapper = new ObjectMapper();
				GameView game = null;
				try {
					game = mapper.readValue(response, GameView.class);
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mainGameCheckerThread.onGetGame(game);
			}
		});
	}
	
	public void getAccount(String facebookUserId) {
		
		client.get("http://" + ip + ":8080/MedievalWipeout/rest/account/get/" + facebookUserId, null, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				ObjectMapper mapper = new ObjectMapper();
				Account account = null;
				try {
					account = mapper.readValue(response, Account.class);
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				callbackable.onGetAccount(account);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				callbackable.onError(arg3.getMessage());
			}

		});

	}

	public void addDeckTemplateElement(long deckTemplateId, long collectionElementId) {
		
		if ( callbackable.getFacebookUserId() != null ) {
			
			client.get("http://" + ip + ":8080/MedievalWipeout/rest/account/addDeckTemplateElement/"
			+ callbackable.getFacebookUserId() + "/" + deckTemplateId + "/" + collectionElementId,
			null, new AsyncHttpResponseHandler() {
				
				@Override
				public void onFinish() {
					super.onFinish();
					callbackable.setHttpRequestBeingExecuted(false);
				}

				@Override
				public void onStart() {
					super.onStart();
					callbackable.setHttpRequestBeingExecuted(true);
				}

				@Override
				public void onSuccess(String response) {
					ObjectMapper mapper = new ObjectMapper();
					Account account = null;
					try {
						account = mapper.readValue(response, Account.class);
					} catch (JsonParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					callbackable.onGetAccount(account);
				}
			});
		}
		
	}

	public void nextPhase(long gameId) {

		client.get("http://" + ip + ":8080/MedievalWipeout/rest/game/nextPhase/" + gameId + "/" + callbackable.getFacebookUserId(), null, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {

				ObjectMapper mapper = new ObjectMapper();
				GameView game = null;
				try {
					game = mapper.readValue(response, GameView.class);
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				callbackable.onGetGame(game);
			}
		});
	}

	public void playCard(long gameId, String destinationLayout, long cardId) {

		client.get("http://" + ip + ":8080/MedievalWipeout/rest/game/play/" + callbackable.getFacebookUserId() + "/" + gameId + "/" + destinationLayout + "/" + cardId, null,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						Log.i("GameWebClient", "Success");
						GameView game = GameView.fromJson(response);
						callbackable.onGetGame(game);
					}
				});
	}

	public void deleteGame(long gameId) {

		client.get("http://" + ip + ":8080/MedievalWipeout/rest/game/delete/" + gameId, null, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				callbackable.onDeleteGame();
			}
		});

	}

	public void drawInitialHand(long gameId, GameActivity mainActivity) {

		client.get("http://" + ip + ":8080/MedievalWipeout/rest/game/drawInitialHand/" + gameId + "/" + mainActivity.getFacebookUserId(), null, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {

				ObjectMapper mapper = new ObjectMapper();
				GameView game = null;
				try {
					game = mapper.readValue(response, GameView.class);
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				callbackable.onGetGame(game);
			}
		});

	}

	public void getCardModels() {

		client.get("http://" + ip + ":8080/MedievalWipeout/rest/account/getCardModels", null, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {

				ObjectMapper mapper = new ObjectMapper();
				CardModelList cardModelList = null;
				try {
					cardModelList = mapper.readValue(response, CardModelList.class);
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				callbackable.onGetCardModels(cardModelList.getCardModels());
			}
		});

	}

	public void checkGame(long gameId) {

		if ( callbackable.getFacebookUserId() != null ) {
			
			client.get("http://" + ip + ":8080/MedievalWipeout/rest/game/get/" + gameId + "/" + callbackable.getFacebookUserId(), null, new AsyncHttpResponseHandler() {
				
				@Override
				public void onFinish() {
					super.onFinish();
					callbackable.setHttpRequestBeingExecuted(false);
				}

				@Override
				public void onStart() {
					super.onStart();
					callbackable.setHttpRequestBeingExecuted(true);
				}

				@Override
				public void onSuccess(String response) {
					GameView game = GameView.fromJson(response);
					callbackable.onCheckGame(game);
				}
			});
		}
		
	}
	
}
