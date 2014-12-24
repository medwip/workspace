package com.guntzergames.medievalwipeout.listeners;

import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;

import com.guntzergames.medievalwipeout.activities.GameActivity;
import com.guntzergames.medievalwipeout.activities.R;
import com.guntzergames.medievalwipeout.layouts.CardLayout;
import com.guntzergames.medievalwipeout.webclients.GameWebClient;

public class GameDragListener implements OnDragListener {

	GameActivity gameActivity;
	String userName;

	public GameDragListener(GameActivity gameActivity, String userName) {
		this.gameActivity = gameActivity;
		this.userName = userName;
	}
	
	private boolean isPossibleDest(View dest) {
		return dest.getId() == R.id.playerFieldAttack || dest.getId() == R.id.playerFieldDefense || 
				dest.getId() == R.id.opponentFieldDefense || dest.getId() == R.id.playerHand ||
				dest.getId() == R.id.opponentFieldAttackCard0 || dest.getId() == R.id.opponentFieldAttackCard1 ||
				dest.getId() == R.id.opponentFieldAttackCard2 || dest.getId() == R.id.opponentFieldAttackCard3 ||
				dest.getId() == R.id.opponentFieldAttackCard4;
	}
	
	private boolean isDefenseDest(View dest) {
		return dest.getId() == R.id.playerFieldDefense || dest.getId() == R.id.opponentFieldDefense;
	}
	
	private int getFrameBorderId(View dest) {
		return isDefenseDest(dest) ? R.drawable.frame_border_defense : R.drawable.frame_border;
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
			if ( cardLayout.getPossibleTarget(dest.getId()) != null ) {
				dest.setBackgroundDrawable(gameActivity.getResources().getDrawable(R.drawable.frame_border_highlight));
			}
			break;
			
		case DragEvent.ACTION_DRAG_EXITED:
			if ( isPossibleDest(dest) ) {
				dest.setBackgroundDrawable(gameActivity.getResources().getDrawable(getFrameBorderId(dest)));
			}
			break;
			
		case DragEvent.ACTION_DROP:
			
			Log.i("PlayerFieldDragListener", String.format("ici, view = %h alors que field id = %h", dest.getId(), R.id.playerField));
			gameActivity.hideCardLayoutDetail();
			
			if ( isPossibleDest(dest) ) {
				dest.setBackgroundDrawable(gameActivity.getResources().getDrawable(getFrameBorderId(dest)));
			}
			
			String source = cardLayout.getPossibleSource(((View)cardLayout.getParent()).getId());
			Log.i("TEST", String.format("source=%s, cardLayout.getId()=%s, R.id.playerFieldDefense=%s", source, ((View)cardLayout.getParent()).getId(), R.id.playerFieldDefense));
			String target = cardLayout.getPossibleTarget(dest.getId());
			
			if ( target != null ) {
				gameWebClient.playCard(gameActivity.getGameId(), source, target, cardLayout.getSeqNum());
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
