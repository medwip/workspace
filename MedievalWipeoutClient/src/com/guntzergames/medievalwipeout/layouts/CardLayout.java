package com.guntzergames.medievalwipeout.layouts;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guntzergames.medievalwipeout.abstracts.AbstractCard;
import com.guntzergames.medievalwipeout.activities.R;
import com.guntzergames.medievalwipeout.beans.CollectionElement;
import com.guntzergames.medievalwipeout.beans.DeckTemplateElement;
import com.guntzergames.medievalwipeout.beans.PlayerDeckCard;
import com.guntzergames.medievalwipeout.beans.PlayerFieldCard;
import com.guntzergames.medievalwipeout.beans.PlayerHandCard;
import com.guntzergames.medievalwipeout.beans.ResourceDeckCard;
import com.guntzergames.medievalwipeout.enums.CardLocation;
import com.guntzergames.medievalwipeout.interfaces.ICard;

public class CardLayout extends RelativeLayout {
	
	private static final String TAG = "CardLayout";

	private static LayoutInflater layoutInflater;
	
	private ICard card;
	private Context context;
	private boolean detailShown = false;
	private int seqNum;
	private CardLocation cardLocation;
	
	private ImageView image;

	private LinearLayout rootView;
	private ElementLayout nameLayout, defenseLayout, faithLayout, numberOfCardsLayout;
	private TextView attack, lifePoints, name, trade, defense, faith;

	public void hide() {
		this.setVisibility(View.INVISIBLE);
	}

	public void show() {
		if ( this.getVisibility() == View.INVISIBLE ) this.setVisibility(View.VISIBLE);
	}

	public CardLayout(Context context) {
		super(context);
		this.context = context;
	}

	public CardLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	public void setup(Context context, ICard card, int seqNum, CardLocation cardLocation) {
		this.context = context;
		this.card = card;
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
	
	public static int getGridCardFromId(int id) {
		int columnCount = 4;
		Class<R.id> idClass = R.id.class;
		Field field;
		try {
			field = idClass.getField(String.format("card%s_%s", id / columnCount, id % columnCount));
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
		this.removeAllViews();
	}
	
	private Drawable getActiveDrawable() {
		
		Drawable background = this.getBackground();
		
		if ( background instanceof LayerDrawable ) {
			return ((LayerDrawable)background).getDrawable(1);
		}
		else {
			return null;
		}
		
	}

	private Drawable getHighlightDrawable() {
		
		Drawable background = this.getBackground();
		
		if ( background instanceof LayerDrawable ) {
			return ((LayerDrawable)background).getDrawable(2);
		}
		else {
			return null;
		}
		
	}

	public void activeCardLayout() {
		
		Drawable activeDrawable = getActiveDrawable();
		if ( activeDrawable != null ) activeDrawable.setAlpha(255);
		
	}

	public void unactiveCardLayout() {
		
		Drawable activeDrawable = getActiveDrawable();
		if ( activeDrawable != null ) activeDrawable.setAlpha(0);
		
	}

	public void setup() {
		
		init();
		
		if (card instanceof PlayerDeckCard) {
			
			PlayerDeckCard playerDeckCard = (PlayerDeckCard)card;

			if ( detailShown ) {
				try {
					image.setImageDrawable(getResources().getDrawable(getResourceFromName("card_" + playerDeckCard.getDrawableResourceName() + "_large")));
				} catch (Exception e) {
					Log.i("CardLayout", "Large image not found");
					try {
						image.setImageDrawable(getResources().getDrawable(getResourceFromName(("card_" + playerDeckCard.getDrawableResourceName()))));
					} catch (Exception e2) {
						e2.printStackTrace();
						image.setImageDrawable(getResources().getDrawable(R.drawable.card_unknown));
					}
				}
			}
			else {
				try {
					image.setImageDrawable(getResources().getDrawable(getResourceFromName("card_" + playerDeckCard.getDrawableResourceName())));
				} catch (Exception e) {
					e.printStackTrace();
					image.setImageDrawable(getResources().getDrawable(R.drawable.card_unknown));
				}
			}
			
			name.setText(playerDeckCard.getName());
			attack.setText(String.format("%s", playerDeckCard.getAttack()));
//			attackLayout.setup(getResources().getString(R.string.attack), playerDeckCard.getAttack());
			
			Log.d("CardLayout", String.format("detailShown: %s", detailShown));
			
			if ( playerDeckCard instanceof PlayerFieldCard ) {
				PlayerFieldCard playerFieldCard = (PlayerFieldCard)playerDeckCard;
				lifePoints.setText(String.format("%s/%s", playerFieldCard.getCurrentLifePoints(), playerDeckCard.getLifePoints()));
//				lifePointsLayout.setup(getResources().getString(R.string.life_points),
//						String.format("%s (%s)", playerFieldCard.getCurrentLifePoints(), playerDeckCard.getLifePoints()));
			}
			else {
				lifePoints.setText(String.format("%s", playerDeckCard.getLifePoints()));
//				lifePointsLayout.setup(getResources().getString(R.string.life_points), playerDeckCard.getLifePoints());
			}
			
			if ( card instanceof PlayerFieldCard && !detailShown ) {
				PlayerFieldCard playerFieldCard = (PlayerFieldCard)card;
				if ( !playerFieldCard.isPlayed() ) {
					activeCardLayout();
//					this.setBackgroundDrawable(getResources().getDrawable(R.drawable.card_border_active_highlightable));
				}
				else {
					unactiveCardLayout();
//					this.setBackgroundDrawable(getResources().getDrawable(R.drawable.card_border_highlightable));
				}
			}
			if ( card instanceof PlayerHandCard && !detailShown ) {
				PlayerHandCard playerHandCard = (PlayerHandCard)card;
				if ( playerHandCard.isPlayable() ) {
					activeCardLayout();
				}
				else {
					unactiveCardLayout();
				}
			}
			
		}
		
		if (card instanceof DeckTemplateElement) {
			
			DeckTemplateElement deckTemplateElement = (DeckTemplateElement)card;
			numberOfCardsLayout.setVisibility(View.VISIBLE);

			if ( detailShown ) {
				try {
					image.setImageDrawable(getResources().getDrawable(getResourceFromName("card_" + deckTemplateElement.getDrawableResourceName() + "_large")));
				} catch (Exception e) {
					Log.i("CardLayout", "Large image not found");
					try {
						image.setImageDrawable(getResources().getDrawable(getResourceFromName(("card_" + deckTemplateElement.getDrawableResourceName()))));
					} catch (Exception e2) {
						e2.printStackTrace();
						image.setImageDrawable(getResources().getDrawable(R.drawable.card_unknown));
					}
				}
			}
			else {
				try {
					image.setImageDrawable(getResources().getDrawable(getResourceFromName("card_" + deckTemplateElement.getDrawableResourceName())));
				} catch (Exception e) {
					e.printStackTrace();
					image.setImageDrawable(getResources().getDrawable(R.drawable.card_unknown));
				}
			}
			
			name.setText(deckTemplateElement.getName());
			attack.setText(String.format("%s", deckTemplateElement.getAttack()));
			numberOfCardsLayout.setup(getResources().getString(R.string.number_of_cards), String.format("%s", deckTemplateElement.getNumberOfCards()));
			lifePoints.setText(String.format("%s", deckTemplateElement.getLifePoints()));
			
			Log.d("CardLayout", String.format("detailShown: %s", detailShown));
			if ( card instanceof PlayerFieldCard && !detailShown ) {
				PlayerFieldCard playerFieldCard = (PlayerFieldCard)card;
				if ( !playerFieldCard.isPlayed() ) {
					activeCardLayout();
				}
				else {
					unactiveCardLayout();
				}
			}
			if ( card instanceof PlayerHandCard && !detailShown ) {
				PlayerHandCard playerHandCard = (PlayerHandCard)card;
				if ( playerHandCard.isPlayable() ) {
					activeCardLayout();
				}
				else {
					unactiveCardLayout();
				}
			}
			
		}
		
		if (card instanceof CollectionElement) {
			
			CollectionElement collectionElement = (CollectionElement)card;

			if ( detailShown ) {
				try {
					image.setImageDrawable(getResources().getDrawable(getResourceFromName("card_" + collectionElement.getDrawableResourceName() + "_large")));
				} catch (Exception e) {
					Log.i("CardLayout", "Large image not found");
					try {
						image.setImageDrawable(getResources().getDrawable(getResourceFromName(("card_" + collectionElement.getDrawableResourceName()))));
					} catch (Exception e2) {
						e2.printStackTrace();
						image.setImageDrawable(getResources().getDrawable(R.drawable.card_unknown));
					}
				}
			}
			else {
				try {
					image.setImageDrawable(getResources().getDrawable(getResourceFromName("card_" + collectionElement.getDrawableResourceName())));
				} catch (Exception e) {
					e.printStackTrace();
					image.setImageDrawable(getResources().getDrawable(R.drawable.card_unknown));
				}
			}
			
			name.setText(collectionElement.getName());
			attack.setText(String.format("%s", collectionElement.getAttack()));
			lifePoints.setText(String.format("%s", collectionElement.getLifePoints()));
			
			Log.d("CardLayout", String.format("detailShown: %s", detailShown));
			
		}
		
		if ( card instanceof ResourceDeckCard ) {
			
			ResourceDeckCard resourceDeckCard = (ResourceDeckCard)card;
			try {
				image.setImageDrawable(getResources().getDrawable(R.drawable.gold));
			} catch (Exception e) {
				e.printStackTrace();
				image.setImageDrawable(getResources().getDrawable(R.drawable.card_unknown));
			}
			
			trade.setVisibility(View.VISIBLE);
			defense.setVisibility(View.VISIBLE);
			faith.setVisibility(View.VISIBLE);
			
			trade.setText(String.format("%s", resourceDeckCard.getTrade()));
			defense.setText(String.format("%s", resourceDeckCard.getDefense()));
			faith.setText(String.format("%s", resourceDeckCard.getFaith()));
			
		}
		
	}
	
	public String getPossibleSource(int id) {
		
		if ( id == R.id.playerField ) {
			return "playerField";
		}
		else if ( id == R.id.playerHand ) {
			return "playerHand";
		}
		else if ( id == R.id.playerFieldAttack ) {
			return "playerFieldAttack";
		}
//		else if ( id == R.id.playerFieldDefense ) {
//			return "playerFieldDefense";
//		}
		else if ( id == R.id.opponentField ) {
			return "opponentField";
		}
		else {
			return null;
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

	public ICard getCard() {
		return card;
	}

	public void setCard(ICard card) {
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
	
	private void init() {
		
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if ( card instanceof ResourceDeckCard ) {
			rootView = (LinearLayout)layoutInflater.inflate(R.layout.card_resource, null);
			
			reset();
			image = (ImageView)rootView.findViewById(R.id.cardLayoutImage);
			name = (TextView)rootView.findViewById(R.id.cardLayoutName);
			trade = (TextView)rootView.findViewById(R.id.cardLayoutTrade);
			defense = (TextView)rootView.findViewById(R.id.cardLayoutDefense);
			faith = (TextView)rootView.findViewById(R.id.cardLayoutFaith);
		}
		else {
			rootView = (LinearLayout)layoutInflater.inflate(R.layout.card_creature, null);
			
			reset();
			image = (ImageView)rootView.findViewById(R.id.cardLayoutImage);
			name = (TextView)rootView.findViewById(R.id.cardName);
			attack = (TextView)rootView.findViewById(R.id.cardAttackText);
			lifePoints = (TextView)rootView.findViewById(R.id.cardLifePointsText);
//			currentLifePoints = (TextView)rootView.findViewById(R.id.cardLayoutCurrentLifePoints);
			numberOfCardsLayout = (ElementLayout)rootView.findViewById(R.id.numberOfCardsLayout);
		}
		
		Drawable highlightDrawable = getHighlightDrawable();
		if ( highlightDrawable != null ) {
			highlightDrawable.setAlpha(0);
		}
		
		this.addView(rootView);			
		
	}
	
}
