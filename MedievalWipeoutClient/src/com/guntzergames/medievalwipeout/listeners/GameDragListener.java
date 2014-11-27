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
			if ( dest.getId() == R.id.playerField || dest.getId() == R.id.opponentField || dest.getId() == R.id.playerHand ) {
				dest.setBackgroundDrawable(gameActivity.getResources().getDrawable(R.drawable.frame_border));
			}
			break;
			
		case DragEvent.ACTION_DROP:
			
			Log.i("PlayerFieldDragListener", String.format("ici, view = %h alors que field id = %h", dest.getId(), R.id.playerField));
			gameActivity.hideCardLayoutDetail();
			
			if ( dest.getId() == R.id.playerField || dest.getId() == R.id.opponentField || dest.getId() == R.id.playerHand ) {
				dest.setBackgroundDrawable(gameActivity.getResources().getDrawable(R.drawable.frame_border));
			}
			
			String target = cardLayout.getPossibleTarget(dest.getId());
			
			if ( target != null ) {
				gameWebClient.playCard(gameActivity.getGameId(), target, cardLayout.getSeqNum(), gameActivity);
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
