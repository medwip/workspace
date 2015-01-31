package com.guntzergames.medievalwipeout.listeners;

import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;

import com.guntzergames.medievalwipeout.activities.GameActivity;
import com.guntzergames.medievalwipeout.activities.R;
import com.guntzergames.medievalwipeout.beans.PlayerDeckCard;
import com.guntzergames.medievalwipeout.beans.PlayerFieldCard;
import com.guntzergames.medievalwipeout.beans.ResourceDeckCard;
import com.guntzergames.medievalwipeout.enums.CardLocation;
import com.guntzergames.medievalwipeout.interfaces.ICard;
import com.guntzergames.medievalwipeout.layouts.CardLayout;
import com.guntzergames.medievalwipeout.webclients.GameWebClient;

public class GameDragListener implements OnDragListener {

	private static final String TAG = "GameActivity";

	private GameActivity gameActivity;

	public GameDragListener(GameActivity gameActivity) {
		this.gameActivity = gameActivity;
	}

	private boolean isPossibleDest(View dest) {
		return dest.getId() == R.id.playerFieldAttack || dest.getId() == R.id.playerFieldDefense || dest.getId() == R.id.opponentFieldDefense || dest.getId() == R.id.playerHand
				|| dest.getId() == R.id.opponentFieldAttackCard0 || dest.getId() == R.id.opponentFieldAttackCard1 || dest.getId() == R.id.opponentFieldAttackCard2
				|| dest.getId() == R.id.opponentFieldAttackCard3 || dest.getId() == R.id.opponentFieldAttackCard4;
	}

	public String getPossibleTarget(CardLayout cardLayout, View dest) {

		Log.i(TAG, "event=" + cardLayout);
		CardLocation cardLocation = cardLayout.getCardLocation();
		ICard card = cardLayout.getCard();

		if (cardLocation == CardLocation.MODAL && card instanceof ResourceDeckCard && dest.getId() == R.id.playerHand) {
			return "playerHand";
		}
		if (cardLocation == CardLocation.MODAL && card instanceof PlayerDeckCard && dest.getId() == R.id.playerHand) {
			return "playerHand";
		}
		if (cardLocation == CardLocation.HAND && card instanceof PlayerDeckCard && dest.getId() == R.id.playerFieldAttack) {
			return "playerFieldAttack";
		}
		if (cardLocation == CardLocation.HAND && card instanceof PlayerDeckCard && dest.getId() == R.id.playerFieldDefense) {
			return "playerFieldDefense";
		}
		// Select only player cards
		if (cardLocation == CardLocation.FIELD_ATTACK && card instanceof PlayerFieldCard) {
			
			if (((View) cardLayout.getParent()).getId() == R.id.playerFieldAttack) {
				if (dest.getId() == R.id.opponentFieldDefense) {
					return "opponentFieldDefense";
				}
				if (dest instanceof CardLayout) {
					if (((CardLayout) dest).getCardLocation() == CardLocation.FIELD_ATTACK) {
						return "opponentCardAttack";
					}
					if (((CardLayout) dest).getCardLocation() == CardLocation.FIELD_DEFENSE) {
						return "opponentCardDefense";
					}
				}
			}
		}
		/*
		 * if ( cardLocation == CardLocation.FIELD_DEFENSE && card instanceof
		 * PlayerDeckCard && dest == R.id.opponentField ) { return
		 * "opponentField"; }
		 */

		return null;

	}

	@Override
	public boolean onDrag(View dest, DragEvent event) {

		CardLayout cardLayout = (CardLayout) event.getLocalState();
		GameWebClient gameWebClient = gameActivity.getGameWebClient();

		switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED:
				gameActivity.setBeingModified(true);
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				if (getPossibleTarget(cardLayout, dest) != null) {
					gameActivity.startHighlightAnimation(dest);
				}
				break;

			case DragEvent.ACTION_DRAG_EXITED:
				if (isPossibleDest(dest)) {
					gameActivity.stopHightlightAnimation(dest);
				}
				break;

			case DragEvent.ACTION_DROP:

				Log.i("PlayerFieldDragListener", String.format("ici, view = %h alors que field id = %h", dest.getId(), R.id.playerField));
				gameActivity.hideCardLayoutDetail();

				if (isPossibleDest(dest)) {
					gameActivity.stopHightlightAnimation(dest);
				}

				String source = cardLayout.getPossibleSource(((View) cardLayout.getParent()).getId());
				String target = getPossibleTarget(cardLayout, dest);
				Log.i(TAG, String.format("source=%s, cardLayout.getId()=%s, target=%s, R.id.playerFieldDefense=%s", source, ((View) cardLayout.getParent()).getId(), target,
						R.id.playerFieldDefense));

				if (target != null) {
					int destinationCardId = (dest instanceof CardLayout) ? ((CardLayout) dest).getSeqNum() : -1;
					gameWebClient.playCard(gameActivity.getGameId(), source, cardLayout.getSeqNum(), target, destinationCardId);
				}

				break;

			case DragEvent.ACTION_DRAG_ENDED:
				gameActivity.setBeingModified(false);
				break;
			default:
				break;
		}

		return true;

	}

}
