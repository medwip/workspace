package com.guntzergames.medievalwipeout.webclients;

import java.io.IOException;

import org.apache.http.Header;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.util.Log;

import com.guntzergames.medievalwipeout.activities.GameActivity;
import com.guntzergames.medievalwipeout.activities.HomeActivity;
import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.services.MainGameCheckerThread;
import com.guntzergames.medievalwipeout.views.GameView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class GameWebClient {

	AsyncHttpClient client = null;
	HomeActivity homeActivity = null;
	GameActivity mainActivity = null;
	String ip = null;

	public GameWebClient(String ip, HomeActivity homeActivity) {
		client = new AsyncHttpClient();
		this.homeActivity = homeActivity;
		this.ip = ip;
	}

	public void joinGame(String facebookUserId, long deckId) {
		// RequestParams params = new RequestParams();

		client.get("http://" + ip + ":8080/MedievalWipeout/rest/game/join/" + facebookUserId + "/" + deckId, null, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				GameView game = GameView.fromJson(response);
				homeActivity.onGameJoined(game);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				homeActivity.setDebugText(arg3.getMessage());
			}

		});
	}

	public GameActivity getMainActivity() {
		return this.mainActivity;
	}

	public HomeActivity getHomeActivity() {
		return this.homeActivity;
	}

	public void getGame(long gameId, GameActivity mainActivity) {
		// RequestParams params = new RequestParams();

		this.mainActivity = mainActivity;

		client.get("http://" + ip + ":8080/MedievalWipeout/rest/game/get/" + gameId + "/" + mainActivity.getFacebookUserId(), null, new AsyncHttpResponseHandler() {
			@Override
			public void onFinish() {
				super.onFinish();
				getMainActivity().setHttpRequestBeingExecuted(false);
			}

			@Override
			public void onStart() {
				super.onStart();
				getMainActivity().setHttpRequestBeingExecuted(true);
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
				getMainActivity().onGetGame(game);
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
		
		this.homeActivity = homeActivity;
		
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
				homeActivity.onGetAccount(account);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				homeActivity.setDebugText(arg3.getMessage());
			}

		});

	}

	public void nextPhase(long gameId, GameActivity mainActivity) {

		this.mainActivity = mainActivity;

		client.get("http://" + ip + ":8080/MedievalWipeout/rest/game/nextPhase/" + gameId + "/" + mainActivity.getFacebookUserId(), null, new AsyncHttpResponseHandler() {
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
				getMainActivity().onGetGame(game);
			}
		});
	}

	public void playCard(long gameId, String destinationLayout, long cardId, GameActivity mainActivity) {
		this.mainActivity = mainActivity;

		client.get("http://" + ip + ":8080/MedievalWipeout/rest/game/play/" + mainActivity.getFacebookUserId() + "/" + gameId + "/" + destinationLayout + "/" + cardId, null,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						Log.i("GameWebClient", "Success");
						GameView game = GameView.fromJson(response);
						getMainActivity().onGetGame(game);
					}
				});
	}

	public void deleteGame(long gameId, GameActivity mainActivity) {

		this.mainActivity = mainActivity;

		client.get("http://" + ip + ":8080/MedievalWipeout/rest/game/delete/" + gameId, null, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				getMainActivity().onDeleteGame();
			}
		});

	}

	public void drawInitialHand(long gameId, GameActivity mainActivity) {

		this.mainActivity = mainActivity;

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
				getMainActivity().onGetGame(game);
			}
		});

	}

	public void checkGame(long gameId, HomeActivity homeActivity) {

		this.homeActivity = homeActivity;

		if ( homeActivity.getUser() != null ) {
			
			client.get("http://" + ip + ":8080/MedievalWipeout/rest/game/get/" + gameId + "/" + homeActivity.getUser().getId(), null, new AsyncHttpResponseHandler() {
				
				
				@Override
				public void onFinish() {
					super.onFinish();
					getHomeActivity().setHttpRequestBeingExecuted(false);
				}

				@Override
				public void onStart() {
					super.onStart();
					getHomeActivity().setHttpRequestBeingExecuted(true);
				}

				@Override
				public void onSuccess(String response) {
					GameView game = GameView.fromJson(response);
					getHomeActivity().onCheckGame(game);
				}
			});
		}
		
	}

	public void getGame(long gameId, HomeActivity homeActivity) {
		// RequestParams params = new RequestParams();

		this.homeActivity = homeActivity;

		client.get("http://" + ip + ":8080/MedievalWipeout/rest/game/get/" + gameId + "/" + homeActivity.getUser().getId(), null, new AsyncHttpResponseHandler() {
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
				getHomeActivity().onGetGame(game);
			}
		});
	}

}
