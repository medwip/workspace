package com.guntzergames.medievalwipeout.activities;

import java.util.LinkedList;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.guntzergames.medievalwipeout.abstracts.AbstractCard;
import com.guntzergames.medievalwipeout.abstracts.AbstractCardList;
import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.beans.GameEvent;
import com.guntzergames.medievalwipeout.beans.GameEventPlayCard;
import com.guntzergames.medievalwipeout.beans.GameEventPlayCard.PlayerType;
import com.guntzergames.medievalwipeout.beans.CardModel;
import com.guntzergames.medievalwipeout.beans.Player;
import com.guntzergames.medievalwipeout.beans.PlayerDeckCard;
import com.guntzergames.medievalwipeout.beans.PlayerFieldCard;
import com.guntzergames.medievalwipeout.beans.PlayerHandCard;
import com.guntzergames.medievalwipeout.beans.ResourceDeckCard;
import com.guntzergames.medievalwipeout.enums.CardLocation;
import com.guntzergames.medievalwipeout.enums.Phase;
import com.guntzergames.medievalwipeout.interfaces.Constants;
import com.guntzergames.medievalwipeout.interfaces.GameWebClientCallbackable;
import com.guntzergames.medievalwipeout.layouts.CardLayout;
import com.guntzergames.medievalwipeout.listeners.GameAnimationListener;
import com.guntzergames.medievalwipeout.listeners.GameDragListener;
import com.guntzergames.medievalwipeout.services.MainGameCheckerThread;
import com.guntzergames.medievalwipeout.views.GameView;
import com.guntzergames.medievalwipeout.webclients.GameWebClient;

public class GameActivity extends ActionBarActivity implements GameWebClientCallbackable {

	private RelativeLayout layout = null, playerChoicesLayout;
	private long gameId;
	private String gameCommand;
	private GameView game;
	private Player player, opponent;
	private String facebookUserId;
	private MainGameCheckerThread mainGameCheckerThread;
	private boolean beingModified = false, httpRequestBeingExecuted = false;;
	private CardLayout cardLayoutDetail = null;
	private CardDetailListener cardDetailListener;
	private GameAnimationListener gameAnimationListener;
	private LinearLayout playerHandLayout, playerFieldLayout, opponentFieldLayout;
	private int httpCallsDone = 0, httpCallsAborted = 0, touchEvents = 0;
	private CardLayout resourceCard1Layout, resourceCard2Layout, playerDeckCard1Layout, playerDeckCard2Layout, gameEventLayout;
	private TextView gameInfos;
	private GameWebClient gameWebClient;

	final private Handler checkGameHandler = new Handler() {

		@Override
		public void handleMessage(Message message) {
			super.handleMessage(message);
			GameView game = (GameView) message.obj;
			if (!beingModified) {
				onGetGame(game);
			} else {
				++httpCallsAborted;
			}
		}
	};

	public long getGameId() {
		return gameId;
	}

	public boolean isBeingModified() {
		return beingModified;
	}

	public void setBeingModified(boolean beingModified) {
		// this.beingModified = false;
		this.beingModified = beingModified;
		ImageView beingModifiedImageView = (ImageView) layout.findViewById(R.id.beingModified);
		beingModifiedImageView.setImageDrawable(getResources().getDrawable(beingModified ? R.drawable.red : R.drawable.green));
	}

	private class CardDetailListener implements OnTouchListener {

		private AbstractCard card;
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			setBeingModified(true);

			CardLayout cardLayout = ((CardLayout) v);
			card = cardLayout.getCard();

			Log.d("CardDetailListener", String.format("onTouch event detected: %s for event %s", event.getAction(), v.getClass().getName()));

			if (event.getAction() == MotionEvent.ACTION_DOWN) {

				cardLayoutDetail.setDetailShown(true);
				cardLayoutDetail.setup(getMainActivity(), card, cardLayout.getSeqNum(), false, cardLayout.getCardLocation());
				cardLayoutDetail.show();
				// v.setBackgroundColor(getResources().getColor(R.color.com_facebook_blue));
				Log.i("CardDetailListener", String.format("Showing %s, touchEvents: %s", card, ++touchEvents));

				v.performClick();

				if ((cardLayout.getCard() instanceof ResourceDeckCard) && game.getPhase() == getDuringPlayerResourceDrawPhase()) {
					Log.i("MainActivity", "Drag initiated during player resource draw");
					ClipData data = ClipData.newPlainText("", "");
					DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(cardLayout);
					cardLayout.startDrag(data, shadowBuilder, cardLayout, 0);
				}

				if ((cardLayout.getCard() instanceof PlayerDeckCard) && game.getPhase() == getDuringPlayerDeckDrawPhase()) {
					Log.i("MainActivity", "Drag initiated during player deck draw");
					ClipData data = ClipData.newPlainText("", "");
					DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(cardLayout);
					cardLayout.startDrag(data, shadowBuilder, cardLayout, 0);
				}

				if (cardLayout.isCalledFromHand() && (cardLayout.getCard() instanceof PlayerHandCard) && game.getPhase() == getDuringPlayerPlayPhase()
						&& ((PlayerHandCard) card).isPlayable()) {
					Log.i("MainActivity", "Drag initiated during player play from hand");
					ClipData data = ClipData.newPlainText("", "");
					DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
					v.startDrag(data, shadowBuilder, v, 0);
				}

				if (!cardLayout.isCalledFromHand() && (cardLayout.getCard() instanceof PlayerFieldCard) && game.getPhase() == getDuringPlayerPlayPhase()
						&& !((PlayerFieldCard) card).isPlayed()) {
					Log.i("MainActivity", "Drag initiated during player play from field");
					ClipData data = ClipData.newPlainText("", "");
					DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
					v.startDrag(data, shadowBuilder, v, 0);
				}

				return true;

			}

			if (event.getAction() == MotionEvent.ACTION_UP) {

				hideCardLayoutDetail();
				// v.setBackgroundColor(getResources().getColor(R.color.red));
				Log.d("CardDetailListener", "Hiding " + card);
				setBeingModified(false);
				return true;

			}

			setBeingModified(false);
			return false;

		}

	}

	private Phase getDuringPlayerPlayPhase() {

		if (game.isCreator())
			return Phase.DURING_CREATOR_PLAY;
		else
			return Phase.DURING_JOINER_PLAY;

	}

	private Phase getDuringPlayerResourceDrawPhase() {

		if (game.isCreator())
			return Phase.DURING_CREATOR_RESOURCE_DRAW;
		else
			return Phase.DURING_JOINER_RESOURCE_DRAW;

	}

	private Phase getDuringPlayerDeckDrawPhase() {

		if (game.isCreator())
			return Phase.DURING_CREATOR_DECK_DRAW;
		else
			return Phase.DURING_JOINER_DECK_DRAW;

	}

	protected GameActivity getMainActivity() {
		return this;
	}

	public void hideCardLayoutDetail() {
		cardLayoutDetail.hide();
	}

	public void onCardDrawn(PlayerDeckCard card) {
		if (card != null) {
			updateCardsDisplay();
		}
	}

	private void setupField(AbstractCardList<?> cardList, String layoutPrefix, CardLocation cardLocation) {

		int num = cardList.getCards().size();
		for (int i = 0; i < 10; i++) {

			CardLayout cardLayout = (CardLayout) layout.findViewById(CardLayout.getCardFromId(layoutPrefix, i));
			if (i < num) {
				AbstractCard card = cardList.getCards().get(i);
				cardLayout.setup(this, card, i, cardLocation == CardLocation.HAND ? true : false, cardLocation);
				cardLayout.show();
				Log.d("MainActivity", "Set view " + cardLayout + " visible " + cardLayout.getVisibility());
			} else {
				cardLayout.hide();
			}
		}

	}

	public void updateCardsDisplay() {
		
		player.updatePlayableHandCards();

		setupField(opponent.getPlayerField(), "opponentField", CardLocation.FIELD);
		setupField(player.getPlayerHand(), "playerHand", CardLocation.HAND);
		setupField(player.getPlayerField(), "playerField", CardLocation.FIELD);

		if (game.getPhase() == getDuringPlayerResourceDrawPhase()) {

			playerChoicesLayout.setVisibility(View.VISIBLE);
			resourceCard1Layout.setup(this, game.getResourceCard1(), 1, false, CardLocation.MODAL);
			resourceCard1Layout.show();
			resourceCard2Layout.setup(this, game.getResourceCard2(), 2, false, CardLocation.MODAL);
			resourceCard2Layout.show();

		} else {

			resourceCard1Layout.hide();
			resourceCard2Layout.hide();

		}

		if (game.getPhase() == getDuringPlayerDeckDrawPhase()) {

			playerChoicesLayout.setVisibility(View.VISIBLE);
			playerDeckCard1Layout.setup(this, player.getPlayerDeckCard1(), 1, false, CardLocation.MODAL);
			playerDeckCard1Layout.show();
			playerDeckCard2Layout.setup(this, player.getPlayerDeckCard2(), 2, false, CardLocation.MODAL);
			playerDeckCard2Layout.show();

		} else {

			playerDeckCard1Layout.hide();
			playerDeckCard2Layout.hide();

		}

		if (!(game.getPhase() == getDuringPlayerResourceDrawPhase() || game.getPhase() == getDuringPlayerDeckDrawPhase())) {

			playerChoicesLayout.setVisibility(View.INVISIBLE);

		}

	}

	public GameWebClient getGameWebClient() {
		return gameWebClient;
	}

	@Override
	protected void onResume() {

		super.onResume();

		Log.i("MainActivity", "onResume");
		mainGameCheckerThread.setPaused(false);
		mainGameCheckerThread.getGame(gameId);

	}

	@Override
	protected void onPause() {

		super.onPause();

		Log.i("MainActivity", "onPause");
		mainGameCheckerThread.setPaused(true);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		Log.d("MainActivity", "onCreate");
		gameWebClient = new GameWebClient(Constants.SERVER_IP_ADDRESS, this);

		init();

		cardLayoutDetail = (CardLayout) layout.findViewById(R.id.card_layout_detail);
		cardLayoutDetail.setDetailShown(true);
		cardLayoutDetail.init(this, new PlayerDeckCard(CardModel.DEFAULT_CARD), 0, false, CardLocation.HAND);
		hideCardLayoutDetail();
		gameEventLayout = (CardLayout) layout.findViewById(R.id.gameEvent);
		gameEventLayout.init(this, new PlayerDeckCard(CardModel.DEFAULT_CARD), 0, false, CardLocation.ANIMATION);
		gameEventLayout.setVisibility(View.INVISIBLE);

		playerHandLayout = (LinearLayout) layout.findViewById(R.id.playerHand);
		playerFieldLayout = (LinearLayout) layout.findViewById(R.id.playerField);
		opponentFieldLayout = (LinearLayout) layout.findViewById(R.id.opponentField);
		playerChoicesLayout = (RelativeLayout) layout.findViewById(R.id.playerChoices);

		cardDetailListener = new CardDetailListener();

		for (int num = 0; num < 9; num++) {

			CardLayout opponentCardInFieldLayout = (CardLayout) layout.findViewById(CardLayout.getCardFromId("opponentField", num));
			opponentCardInFieldLayout.setOnTouchListener(cardDetailListener);
			opponentCardInFieldLayout.init(this, new PlayerDeckCard(CardModel.DEFAULT_CARD), 0, false, CardLocation.FIELD);
			opponentCardInFieldLayout.hide();
			CardLayout cardInHandLayout = (CardLayout) layout.findViewById(CardLayout.getCardFromId("playerHand", num));
			cardInHandLayout.setCalledFromHand(true);
			cardInHandLayout.init(this, new PlayerDeckCard(CardModel.DEFAULT_CARD), 0, false, CardLocation.HAND);
			cardInHandLayout.hide();
			cardInHandLayout.setOnTouchListener(cardDetailListener);
			CardLayout cardInFieldLayout = (CardLayout) layout.findViewById(CardLayout.getCardFromId("playerField", num));
			cardInFieldLayout.setOnTouchListener(cardDetailListener);
			cardInFieldLayout.init(this, new PlayerDeckCard(CardModel.DEFAULT_CARD), 0, false, CardLocation.FIELD);
			cardInFieldLayout.hide();
		}

		Button stopGameButton = (Button) layout.findViewById(R.id.stopGameButton);
		stopGameButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("MainActivity", "Clicker on delete for game " + gameId);
				mainGameCheckerThread.setInterruptedSignalSent(true);
				gameWebClient.deleteGame(gameId);
			}
		});

		Button homeButton = (Button) layout.findViewById(R.id.homeButton);
		homeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				returnHome(Constants.GAME_IN_PROGRESS);
			}
		});

		Button getGameButton = (Button) layout.findViewById(R.id.getGameButton);
		getGameButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cardLayoutDetail.show();
				setBeingModified(false);
				getGame(gameId);
			}
		});

		Button nextPhaseButton = (Button) layout.findViewById(R.id.nextPhaseButton);
		nextPhaseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				nextPhase(gameId);
			}
		});

		GameDragListener gameDragListener = new GameDragListener(this, facebookUserId);
		gameAnimationListener = new GameAnimationListener(this);
		layout.setOnDragListener(gameDragListener);
		playerHandLayout.setOnDragListener(gameDragListener);
		playerFieldLayout.setOnDragListener(gameDragListener);
		opponentFieldLayout.setOnDragListener(gameDragListener);

		resourceCard1Layout = (CardLayout) layout.findViewById(CardLayout.getCardFromId("resource", 0));
		resourceCard1Layout.setOnTouchListener(cardDetailListener);
		resourceCard1Layout.init(this, new PlayerDeckCard(CardModel.DEFAULT_CARD), 0, false, CardLocation.MODAL);
		resourceCard2Layout = (CardLayout) layout.findViewById(CardLayout.getCardFromId("resource", 1));
		resourceCard2Layout.setOnTouchListener(cardDetailListener);
		resourceCard2Layout.init(this, new PlayerDeckCard(CardModel.DEFAULT_CARD), 0, false, CardLocation.MODAL);
		playerDeckCard1Layout = (CardLayout) layout.findViewById(CardLayout.getCardFromId("playerDeck", 0));
		playerDeckCard1Layout.setOnTouchListener(cardDetailListener);
		playerDeckCard1Layout.init(this, new PlayerDeckCard(CardModel.DEFAULT_CARD), 0, false, CardLocation.MODAL);
		playerDeckCard2Layout = (CardLayout) layout.findViewById(CardLayout.getCardFromId("playerDeck", 1));
		playerDeckCard2Layout.setOnTouchListener(cardDetailListener);
		playerDeckCard2Layout.init(this, new PlayerDeckCard(CardModel.DEFAULT_CARD), 0, false, CardLocation.MODAL);

		mainGameCheckerThread = new MainGameCheckerThread(checkGameHandler, gameId, this);
		mainGameCheckerThread.start();
		hideCardLayoutDetail();

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private void init() {

		gameId = getIntent().getExtras().getLong(Constants.GAME_ID);
		gameCommand = getIntent().getExtras().getString(Constants.GAME_COMMAND);
		facebookUserId = getIntent().getExtras().getString(Constants.FACEBOOK_USER_ID);

		Toast.makeText(this, String.format("Command %s for game ID: %s, userName=%s", gameCommand, gameId, facebookUserId), Toast.LENGTH_SHORT).show();

		layout = (RelativeLayout) LinearLayout.inflate(this, R.layout.activity_game, null);

		setContentView(layout);

		getGame(gameId);

	}

	public void returnHome(int status) {
		/*
		 * Intent result = new Intent(); setResult(RESULT_OK, result); finish();
		 */

		Intent intent = new Intent(GameActivity.this, HomeActivity.class);
		intent.putExtra(Constants.GAME_ID, gameId);
		intent.putExtra(Constants.GAME_STATE, status);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}
	
	public String getFacebookUserId() {
		return facebookUserId;
	}

	public void setFacebookUserId(String facebookUserId) {
		this.facebookUserId = facebookUserId;
	}

	public boolean isHttpRequestBeingExecuted() {
		return httpRequestBeingExecuted;
	}

	public void setHttpRequestBeingExecuted(boolean httpRequestBeingExecuted) {
		this.httpRequestBeingExecuted = httpRequestBeingExecuted;
	}

	public void getGame(long gameId) {
		gameWebClient.getGame(gameId);
	}

	public void nextPhase(long gameId) {
		gameWebClient.nextPhase(gameId);
	}

	private boolean displayEvents() {

		boolean handleUpdateDisplay = false;
		LinkedList<GameEvent> events = player.getEvents();
		String str = "Events... ";
		if (events != null && events.size() > 0) {
			for (GameEvent event : events) {
				if (event instanceof GameEventPlayCard) {
					GameEventPlayCard gameEventPlayCard = (GameEventPlayCard) event;
					PlayerDeckCard source = gameEventPlayCard.getSource();
					PlayerDeckCard destination = gameEventPlayCard.getDestination();
					PlayerType type = gameEventPlayCard.getPlayerType();

					if (type == PlayerType.PLAYER && source instanceof PlayerHandCard && destination instanceof PlayerFieldCard) {

						CardLayout sourceCardLayout = (CardLayout) layout.findViewById(CardLayout.getCardFromId("playerHand", gameEventPlayCard.getSourceIndex()));
						handleUpdateDisplay = true;
						animateCardEvent(sourceCardLayout.getCard(), playerHandLayout, playerFieldLayout);

					} else if (type == PlayerType.PLAYER && source instanceof PlayerFieldCard && destination instanceof PlayerFieldCard) {

						CardLayout sourceCardLayout = (CardLayout) layout.findViewById(CardLayout.getCardFromId("playerField", gameEventPlayCard.getSourceIndex()));
						handleUpdateDisplay = true;
						animateCardEvent(sourceCardLayout.getCard(), playerFieldLayout, opponentFieldLayout);

					} else if (type == PlayerType.OPPONENT && source instanceof PlayerHandCard && destination instanceof PlayerFieldCard) {

						handleUpdateDisplay = true;
						animateCardEvent(destination, gameInfos, opponentFieldLayout);

					} else if (type == PlayerType.OPPONENT && source instanceof PlayerFieldCard && destination instanceof PlayerFieldCard) {

						handleUpdateDisplay = true;
						animateCardEvent(destination, opponentFieldLayout, playerFieldLayout);

					} else {
						str += gameEventPlayCard.toString() + "\n";
					}

				}
			}
			Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
		}
		return handleUpdateDisplay;

	}

	private void animateCardEvent(AbstractCard card, View sourceLayout, View destinationLayout) {

		AnimationSet animationSet = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.card_animation);
		int[] sourceCoordinates = new int[2];
		sourceLayout.getLocationInWindow(sourceCoordinates);
		int[] destinationCoordinates = new int[2];
		destinationLayout.getLocationInWindow(destinationCoordinates);
		int[] gameEventLayoutCoordinates = new int[2];
		gameEventLayout.getLocationInWindow(gameEventLayoutCoordinates);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		TranslateAnimation translation = new TranslateAnimation(
		// Animation.ABSOLUTE, sourceCoordinates[0] -
		// gameEventLayoutCoordinates[0],
				Animation.ABSOLUTE, metrics.widthPixels / 2 - gameEventLayoutCoordinates[0], Animation.ABSOLUTE, destinationCoordinates[0] - gameEventLayoutCoordinates[0],
				// Animation.ABSOLUTE, metrics.widthPixels / 2 -
				// gameEventLayoutCoordinates[0],
				Animation.ABSOLUTE, sourceCoordinates[1] - gameEventLayoutCoordinates[1], Animation.ABSOLUTE, destinationCoordinates[1] - gameEventLayoutCoordinates[1]);
		animationSet.addAnimation(translation);
		animationSet.setFillAfter(false);
		animationSet.setFillEnabled(true);
		animationSet.setDuration(1000);

		animationSet.setAnimationListener(gameAnimationListener);
		gameEventLayout.setup(this, card, 0, false, CardLocation.ANIMATION);
		gameEventLayout.startAnimation(animationSet);

	}

	public CardLayout getGameEventLayout() {
		return gameEventLayout;
	}

	public void setGameEventLayout(CardLayout gameEventLayout) {
		this.gameEventLayout = gameEventLayout;
	}

	@Override
	public void onGetGame(GameView game) {

		Log.i("GameActivity", " ---> GET GAME <--- ");

		if (game == null) {
			Toast.makeText(this, String.format("Unable to get game"), Toast.LENGTH_LONG).show();
			return;
		}

		gameInfos = (TextView) layout.findViewById(R.id.gameInfos);
		TextView playerTradeValue = (TextView) layout.findViewById(R.id.playerTradeValue);
		TextView playerGoldValue = (TextView) layout.findViewById(R.id.playerGoldValue);
		TextView playerDefenseValue = (TextView) layout.findViewById(R.id.playerDefenseValue);
		TextView playerFaithValue = (TextView) layout.findViewById(R.id.playerFaithValue);
		TextView playerLifePointsValue = (TextView) layout.findViewById(R.id.playerLifePointsValue);
		TextView opponentTradeValue = (TextView) layout.findViewById(R.id.opponentTradeValue);
		TextView opponentGoldValue = (TextView) layout.findViewById(R.id.opponentGoldValue);
		TextView opponentDefenseValue = (TextView) layout.findViewById(R.id.opponentDefenseValue);
		TextView opponentFaithValue = (TextView) layout.findViewById(R.id.opponentFaithValue);
		TextView opponentLifePointsValue = (TextView) layout.findViewById(R.id.opponentLifePointsValue);

		this.game = game;
		player = game.getPlayer();
		opponent = game.getOpponent();

		Log.d("onGetGame", String.format("Entering onGetGame: state=%s", game.getGameState()));

		switch (game.getGameState()) {

		case STARTED:
			boolean handleDisplay = displayEvents();
			if (!handleDisplay)
				updateCardsDisplay();
			break;

		default:
			Log.e("onGetGame", String.format("Invalid game state: %s", game.getGameState()));
			break;

		}

		gameInfos.setText(String.format("%s [%s / %s]", game.toString(), ++httpCallsDone, httpCallsAborted));
		playerTradeValue.setText(String.format("%s", player.getTrade()));
		playerGoldValue.setText(String.format("%s", player.getGold()));
		playerDefenseValue.setText(String.format("%s (%s)", player.getCurrentDefense(), player.getDefense()));
		playerFaithValue.setText(String.format("%s", player.getFaith()));
		playerLifePointsValue.setText(String.format("%s", player.getLifePoints()));
		opponentTradeValue.setText(String.format("%s", opponent.getTrade()));
		opponentGoldValue.setText(String.format("%s", opponent.getGold()));
		opponentDefenseValue.setText(String.format("%s (%s)", opponent.getCurrentDefense(), opponent.getDefense()));
		opponentFaithValue.setText(String.format("%s", opponent.getFaith()));
		opponentLifePointsValue.setText(String.format("%s", opponent.getLifePoints()));

	}
	
	@Override
	public void onError(String err) {
		Toast.makeText(this, String.format("Error occured: %s", err), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onCheckGame(GameView gameView) {
	}

	@Override
	public void onGameJoined(GameView gameView) {
	}

	@Override
	public void onGetAccount(Account account) {
	}

	public void onDeleteGame() {
		returnHome(Constants.GAME_STOPPED);
		finish();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}