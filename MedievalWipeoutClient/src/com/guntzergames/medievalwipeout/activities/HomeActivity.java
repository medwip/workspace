package com.guntzergames.medievalwipeout.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.beans.DeckTemplate;
import com.guntzergames.medievalwipeout.enums.GameState;
import com.guntzergames.medievalwipeout.interfaces.Constants;
import com.guntzergames.medievalwipeout.interfaces.GameWebClientCallbackable;
import com.guntzergames.medievalwipeout.services.HomeGameCheckerThread;
import com.guntzergames.medievalwipeout.views.GameView;
import com.guntzergames.medievalwipeout.webclients.GameWebClient;

public class HomeActivity extends FragmentActivity implements GameWebClientCallbackable {

	private static final String TAG = "HomeActivity";

	private GridLayout layout = null;
	private GameView game;
	private long gameId, selectedDeckTemplateId;
	private GraphUser user = null;
	private Button createGameButton, resumeGameButton, editDeckButton;
	private TextView debugTextView = null;
	private ProgressBar loader;
	private int gameCheckAttempts;
	private HomeGameCheckerThread gameCheckerThread = null;
	private LoginFragment loginFragment;
	private GameWebClient gameWebClient;
	private boolean httpRequestBeingExecuted = false;
	private List<DeckTemplate> deckTemplates = new ArrayList<DeckTemplate>();
	private ListView deckTemplateListView;
	private DeckTemplate selectedDeckTemplate;
	private Account account;

	final private Handler checkGameHandler = new Handler() {

		@Override
		public void handleMessage(Message message) {
			super.handleMessage(message);
			debugTextView.setText("gameCheckAttempts: " + gameCheckAttempts++);
			loader.setVisibility(View.VISIBLE);
			loader.setProgress(gameCheckAttempts * 10);
		}
	};

	public static final int MAIN_ACTIVITY = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		layout = (GridLayout) LinearLayout.inflate(this, R.layout.activity_home, null);

		setContentView(layout);
		gameWebClient = new GameWebClient(Constants.SERVER_IP_ADDRESS, this);

		// New intent
		Intent intent = getIntent();

		resumeGameButton = (Button) layout.findViewById(R.id.resumeGame);
		editDeckButton = (Button) layout.findViewById(R.id.editDeck);
		debugTextView = (TextView) layout.findViewById(R.id.debug);
		loader = (ProgressBar) layout.findViewById(R.id.progressBar);
		deckTemplateListView = (ListView) layout.findViewById(R.id.deckTemplatesList);

		gameId = intent.getLongExtra(Constants.GAME_ID, 0);
		int gameState = intent.getIntExtra(Constants.GAME_STATE, Constants.GAME_NOT_STARTED);
		if (gameId > 0 && gameState != Constants.GAME_STOPPED) {
			getGame(gameId);
		}
		debugTextView.setText("Game: " + game);

		if (gameState == Constants.GAME_IN_PROGRESS) {
			Toast.makeText(this, String.format("Returned to home page from game %s", gameId), Toast.LENGTH_SHORT).show();
			resumeGameButton.setEnabled(true);
		} else if (gameState == Constants.GAME_STOPPED) {
			Toast.makeText(this, String.format("Stopped game %s", gameId), Toast.LENGTH_SHORT).show();
			resumeGameButton.setEnabled(false);
		} else if (gameState == Constants.GAME_NOT_STARTED) {
			resumeGameButton.setEnabled(false);
		} else {
			Toast.makeText(this, String.format("Unknown state... %s", gameId), Toast.LENGTH_SHORT).show();
		}

		if (savedInstanceState == null) {
			// Add the fragment on initial activity setup
			loginFragment = new LoginFragment(this);
			getSupportFragmentManager().beginTransaction().add(android.R.id.content, loginFragment).commit();
		} else {
			// Or set the fragment from restored state info
			loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
			loginFragment.setHomeActivity(this);
		}

		createGameButton = (Button) layout.findViewById(R.id.startGame);
		createGameButton.setEnabled(false);
		editDeckButton.setEnabled(false);

		createGameButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					createGame();
					createGameButton.setEnabled(false);
				} catch (Exception e) {
					displayError(e.getMessage());
				}
			}
		}

		);

		resumeGameButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startGameActivity(game);
			}
		}

		);
		
		editDeckButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startEditDeckActivity();
			}
		});

		Button preferencesButton = (Button) layout.findViewById(R.id.preferences);
		preferencesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, ApplicationPreferenceActivity.class);
				startActivity(intent);
			}
		}

		);

		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (!(networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected())) {
			Toast.makeText(this, String.format("No access to Internet"), Toast.LENGTH_LONG).show();
			createGameButton.setEnabled(false);
		}

		gameCheckerThread = new HomeGameCheckerThread(this);
		gameCheckerThread.start();

		deckTemplateListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onDeckSelected(id);
			}

		});

	}

	public GraphUser getUser() {
		return user;
	}
	
	@Override
	public String getFacebookUserId() {
		if (user == null) return null;
		return user.getId();
	}

	public void setUser(GraphUser user) {
		Log.d(TAG, String.format("User connected: %s", user.getName()));
		TextView welcome = (TextView) layout.findViewById(R.id.welcome);
		welcome.setText("Welcome, " + user.getName() + " [id=" + user.getId() + "]!");
		this.user = user;
		gameWebClient.getAccount(user.getId());
	}

	public void setDebugText(String debugText) {
		debugTextView.setText(debugText);
	}

	public void createGame() {
		if (gameWebClient != null) {
			gameWebClient.joinGame(user != null ? user.getId() : "No user", selectedDeckTemplate.getId());
		} else {
			displayError("web client is null");
		}
	}

	private void displayError(String str) {
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}

	public void getGame(long gameId) {
		gameWebClient.getGame(gameId);
	}

	public void onDeckSelected(long id) {

		Log.i(TAG, String.format("id: %d", id));
		selectedDeckTemplateId = id;
		selectedDeckTemplate = deckTemplates.get((int) id);
		createGameButton.setEnabled(true);
		editDeckButton.setEnabled(true);

	}
	
	@Override
	public void onDeleteGame() {
		onError(String.format("onDeleteGame not supported in class %s", HomeActivity.class.getSimpleName()));
	}

	@Override
	public void onGetGame(GameView game) {
		this.game = game;
		debugTextView.setText("onGetGame: " + game);
	}

	public void onGetAccount(Account account) {

		this.account = account;
		updateDeckTemplates();

	}

	public void onCheckGame(GameView game) {
		this.game = game;
		if (game.getGameState() == GameState.WAITING_FOR_JOINER) {
			Message message = checkGameHandler.obtainMessage();
			checkGameHandler.sendMessage(message);
		} else {
			onGameJoined(game);
		}
	}
	
	@Override
	public void onError(String err) {
		setDebugText(err);
	}

	@Override
	public void onGameJoined(GameView game) {

		this.game = game;

		if (game != null) {

			switch (game.getGameState()) {

			case WAITING_FOR_JOINER:
				gameCheckerThread.setCheckActivated(true);
				break;

			case STARTED:
				gameCheckerThread.setCheckActivated(false);
				startGameActivity(game);
				break;

			default:
				Toast.makeText(this, String.format("Unsupported game state %s for game %s", game.getGameState(), game.getId()), Toast.LENGTH_LONG).show();
				break;

			}

		} else {
			Toast.makeText(this, "Game is null", Toast.LENGTH_LONG).show();
		}
	}

	public void onWaitingForGameCreation() {
		debugTextView.setText("Looking for an opponent...");
	}

	public void updateDeckTemplates() {

		Log.d(TAG, String.format("updateDeckTemplates"));

		List<HashMap<String, String>> listElements = new ArrayList<HashMap<String, String>>();
		deckTemplates = account.getDeckTemplates();

		HashMap<String, String> element;

		for (DeckTemplate deckTemplate : deckTemplates) {

			element = new HashMap<String, String>();
			element.put("id", deckTemplate.getId() + "");
			element.put("libel", deckTemplate.getDeckLibel());
			listElements.add(element);

		}

		ListAdapter adapter = new SimpleAdapter(this, listElements, android.R.layout.simple_list_item_2, new String[] { "id", "libel" }, new int[] { android.R.id.text1,
				android.R.id.text2 });

		deckTemplateListView.setAdapter(adapter);

	}

	public void startGameActivity(GameView game) {
		this.game = game;
		Intent intent = new Intent(HomeActivity.this, GameActivity.class);
		intent.putExtra(Constants.GAME_ID, game.getId());
		intent.putExtra(Constants.GAME_COMMAND, "GAME_START");
		Log.i("startGameActivity", user.getName());
		intent.putExtra(Constants.FACEBOOK_USER_ID, user.getId());
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// startActivityForResult(intent, MAIN_ACTIVITY);
		startActivity(intent);
	}

	public void startEditDeckActivity() {
		Intent intent = new Intent(HomeActivity.this, DeckTemplateActivity.class);
		intent.putExtra(Constants.DECK_TEMPLATE_ID, selectedDeckTemplateId);
		Log.i(TAG, "startEditDeckActivity");
		intent.putExtra(Constants.FACEBOOK_USER_ID, user.getId());
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("Ici Request code", String.format("%s", requestCode));
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		Log.d("Request code", String.format("%s", requestCode));
		if (requestCode == MAIN_ACTIVITY) {
			Log.d("resultCode", String.format("%s", resultCode));
			if (/* resultCode == RESULT_OK */true) {
				Toast.makeText(this, String.format("Gamed stopped"), Toast.LENGTH_SHORT).show();
				createGameButton.setEnabled(true);
				resumeGameButton.setEnabled(true);
				debugTextView.setText(String.format("requestCode: %s, resultCode: %s", requestCode, resultCode));
			}
		}
	}

	public GameView getGame() {
		return game;
	}

	public void setGame(GameView game) {
		this.game = game;
	}

	public boolean isHttpRequestBeingExecuted() {
		return httpRequestBeingExecuted;
	}

	@Override
	public void setHttpRequestBeingExecuted(boolean httpRequestBeingExecuted) {
		this.httpRequestBeingExecuted = httpRequestBeingExecuted;
	}

}