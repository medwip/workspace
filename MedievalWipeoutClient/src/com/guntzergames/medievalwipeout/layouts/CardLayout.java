package com.guntzergames.medievalwipeout.layouts;

import java.lang.reflect.Field;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guntzergames.medievalwipeout.abstracts.AbstractCard;
import com.guntzergames.medievalwipeout.activities.R;
import com.guntzergames.medievalwipeout.beans.PlayerDeckCard;
import com.guntzergames.medievalwipeout.beans.PlayerFieldCard;
import com.guntzergames.medievalwipeout.beans.PlayerHandCard;
import com.guntzergames.medievalwipeout.beans.ResourceDeckCard;
import com.guntzergames.medievalwipeout.enums.CardLocation;
import com.guntzergames.medievalwipeout.interfaces.Constants;

public class CardLayout extends RelativeLayout {

	private AbstractCard card;
	private Context context;
	private boolean detailShown = false;
	private boolean calledFromHand = false;
	private int seqNum;
	private CardLocation cardLocation;
	private TextView cardNameInHand, cardAttackTextView, cardLifePointsTextView, resourceCardTextView;
	
	private ImageView image;

	public void hide() {
		this.setVisibility(View.INVISIBLE);
	}

	public void show() {
		if ( this.getVisibility() == View.INVISIBLE ) this.setVisibility(View.VISIBLE);
	}

	public CardLayout(Context context) {
		super(context);
	}

	public CardLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CardLayout(Context context, PlayerDeckCard card, int seqNum, CardLocation cardLocation) {
		super(context);
		this.card = card;
		this.context = context;
		this.seqNum = seqNum;
		this.cardLocation = cardLocation;
		init();
	}
	
	public void init(Context context, AbstractCard card, int seqNum, boolean calledFromHand, CardLocation cardLocation) {
		this.context = context;
		this.card = card;
		this.calledFromHand = calledFromHand;
		this.seqNum = seqNum;
		this.cardLocation = cardLocation;
		init();
		setup();
	}

	public void setup(Context context, AbstractCard card, int seqNum, boolean calledFromHand, CardLocation cardLocation) {
		this.context = context;
		this.card = card;
		this.calledFromHand = calledFromHand;
		this.seqNum = seqNum;
		this.cardLocation = cardLocation;
		setup();
	}
	
	public static int getResourceFromName(String name) throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
		Class<R.drawable> drawableClass = R.drawable.class;
		Field field = drawableClass.getField(name);
		return field.getInt(null);
	}

	public static int getCardFromId(String type, int id) {
		Class<R.id> idClass = R.id.class;
		Field field;
		try {
			field = idClass.getField(String.format("%sCard%s", type, id));
			return field.getInt(null);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void reset() {
//		this.removeAllViews();
	}
	
	public void setup() {
		
		if (card instanceof PlayerDeckCard) {
			
			PlayerDeckCard playerDeckCard = (PlayerDeckCard)card;

			if ( detailShown ) {
				try {
					image.setImageDrawable(getResources().getDrawable(getResourceFromName(playerDeckCard.getDrawableResourceName() + "_large")));
				} catch (Exception e) {
					Log.i("CardLayout", "Large image not found");
					try {
						image.setImageDrawable(getResources().getDrawable(getResourceFromName(playerDeckCard.getDrawableResourceName())));
					} catch (Exception e2) {
						e2.printStackTrace();
						image.setImageDrawable(getResources().getDrawable(R.drawable.card_unknown));
					}
				}
			}
			else {
				try {
					image.setImageDrawable(getResources().getDrawable(getResourceFromName(playerDeckCard.getDrawableResourceName())));
				} catch (Exception e) {
					e.printStackTrace();
					image.setImageDrawable(getResources().getDrawable(R.drawable.card_unknown));
				}
			}
			
			cardNameInHand.setText(String.format("[%d] %s", seqNum, playerDeckCard.getName()));
			cardAttackTextView.setText(String.format("%s", playerDeckCard.getAttack()));
			
			Log.d("CardLayout", String.format("detailShown: %s", detailShown));
			if (detailShown) {
				cardLifePointsTextView.setText(String.format("%s", playerDeckCard.getLifePoints()));
			}
			if ( card instanceof PlayerFieldCard && !detailShown ) {
				PlayerFieldCard playerFieldCard = (PlayerFieldCard)card;
				if ( !playerFieldCard.isPlayed() ) {
					this.setBackgroundDrawable(getResources().getDrawable(R.drawable.card_border_highlight));
				}
				else {
					this.setBackgroundDrawable(getResources().getDrawable(R.drawable.card_border));
				}
			}
			if ( card instanceof PlayerHandCard && !detailShown ) {
				PlayerHandCard playerHandCard = (PlayerHandCard)card;
				if ( playerHandCard.isPlayable() ) {
					this.setBackgroundDrawable(getResources().getDrawable(R.drawable.card_border_highlight));
				}
				else {
					this.setBackgroundDrawable(getResources().getDrawable(R.drawable.card_border));
				}
			}
			
		}
		
		if ( card instanceof ResourceDeckCard ) {
			
			ResourceDeckCard resourceDeckCard = (ResourceDeckCard)card;
			try {
				image.setImageDrawable(getResources().getDrawable(R.drawable.gold));
			} catch (Exception e) {
				e.printStackTrace();
				image.setImageDrawable(getResources().getDrawable(R.drawable.card_unknown));
			}
			
			resourceCardTextView.setText(String.format("Trade: %s\nDefense: %s\nFaith: %s", resourceDeckCard.getTrade(), resourceDeckCard.getDefense(), resourceDeckCard.getFaith()));
			
		}
		
	}
	
	public String getPossibleTarget(int dest) {
		
		if ( cardLocation == CardLocation.MODAL && card instanceof ResourceDeckCard && dest == R.id.playerField ) {
			return "playerField";
		}
		if ( cardLocation == CardLocation.MODAL && card instanceof PlayerDeckCard && dest == R.id.playerHand ) {
			return "playerHand";
		}
		if ( cardLocation == CardLocation.HAND && calledFromHand && card instanceof PlayerDeckCard && dest == R.id.playerField ) {
			return "playerField";
		}
		if ( cardLocation == CardLocation.FIELD && !calledFromHand && card instanceof PlayerDeckCard && dest == R.id.opponentField ) {
			return "opponentField";
		}
		
		return null;
		
	}

	private void init() {
		reset();
		resourceCardTextView = new TextView(context);
		image = new ImageView(context);
		cardNameInHand = new TextView(context);
		cardAttackTextView = new TextView(context);
		cardLifePointsTextView = new TextView(context);

		if (card instanceof PlayerDeckCard) {
			
			LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			image.setLayoutParams(layoutParams);
			image.setId(Constants.CARD_LAYOUT_IMAGE_ID);
			this.addView(image);
			
			cardNameInHand.setTextAppearance(context, R.style.CardStyle);
			cardNameInHand.setId(Constants.CARD_LAYOUT_CARD_NAME_ID);
			layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			layoutParams.addRule(RelativeLayout.BELOW, Constants.CARD_LAYOUT_IMAGE_ID);
			cardNameInHand.setLayoutParams(layoutParams);
			this.addView(cardNameInHand);
			
			cardAttackTextView.setTextAppearance(context, R.style.CardStyle_Attack);
			cardAttackTextView.setId(Constants.CARD_LAYOUT_ATTACK_ID);
			layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParams.addRule(RelativeLayout.BELOW, Constants.CARD_LAYOUT_CARD_NAME_ID);
			cardAttackTextView.setLayoutParams(layoutParams);
			this.addView(cardAttackTextView);
			
			if (detailShown) {
				cardLifePointsTextView.setTextAppearance(context, R.style.CardStyle_LifePoints);
				cardLifePointsTextView.setId(Constants.CARD_LAYOUT_LIFE_POINTS_ID);
				layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutParams.addRule(RelativeLayout.BELOW, Constants.CARD_LAYOUT_CARD_NAME_ID);
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				cardLifePointsTextView.setLayoutParams(layoutParams);
				this.addView(cardLifePointsTextView);
			}
			
		}
		
		if ( card instanceof ResourceDeckCard ) {
			
			ResourceDeckCard resourceDeckCard = (ResourceDeckCard)card;
			image = new ImageView(context);
			try {
				image.setImageDrawable(getResources().getDrawable(R.drawable.gold));
			} catch (Exception e) {
				e.printStackTrace();
				image.setImageDrawable(getResources().getDrawable(R.drawable.card_unknown));
			}
			LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			image.setLayoutParams(layoutParams);
			image.setId(Constants.CARD_LAYOUT_IMAGE_ID);
			this.addView(image);
			
			resourceCardTextView.setText(String.format("Trade: %s\nDefense: %s\nFaith: %s", resourceDeckCard.getTrade(), resourceDeckCard.getDefense(), resourceDeckCard.getFaith()));
			resourceCardTextView.setTextAppearance(context, R.style.CardStyle_Attack);
			resourceCardTextView.setId(Constants.CARD_LAYOUT_ATTACK_ID);
			layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParams.addRule(RelativeLayout.BELOW, Constants.CARD_LAYOUT_IMAGE_ID);
			resourceCardTextView.setLayoutParams(layoutParams);
			this.addView(resourceCardTextView);
			
		}
		
	}

	public PlayerDeckCard getPlayerDeckCard() {
		return (PlayerDeckCard)card;
	}

	public boolean isDetailShown() {
		return detailShown;
	}

	public void setDetailShown(boolean detailShown) {
		this.detailShown = detailShown;
	}

	public boolean isCalledFromHand() {
		return calledFromHand;
	}

	public void setCalledFromHand(boolean calledFromHand) {
		this.calledFromHand = calledFromHand;
	}

	public AbstractCard getCard() {
		return card;
	}

	public void setCard(AbstractCard card) {
		this.card = card;
	}

	public int getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}

	public CardLocation getCardLocation() {
		return cardLocation;
	}

	public void setCardLocation(CardLocation cardLocation) {
		this.cardLocation = cardLocation;
	}

}
