package com.guntzergames.medievalwipeout.layouts;

import java.lang.reflect.Field;

import android.content.Context;
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
import com.guntzergames.medievalwipeout.beans.DeckTemplateElement;
import com.guntzergames.medievalwipeout.beans.PlayerDeckCard;
import com.guntzergames.medievalwipeout.beans.PlayerFieldCard;
import com.guntzergames.medievalwipeout.beans.PlayerHandCard;
import com.guntzergames.medievalwipeout.beans.ResourceDeckCard;
import com.guntzergames.medievalwipeout.enums.CardLocation;

public class CardLayout extends RelativeLayout {

	private static LayoutInflater layoutInflater;
	
	private AbstractCard card;
	private Context context;
	private boolean detailShown = false;
	private int seqNum;
	private CardLocation cardLocation;
	private LinearLayout rootView;
	private TextView name, attack, lifePoints, resource, numberOfCards;
	
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
	
	public void init(Context context, AbstractCard card, int seqNum, CardLocation cardLocation) {
		this.context = context;
		this.card = card;
		this.seqNum = seqNum;
		this.cardLocation = cardLocation;
		init();
//		setup();
	}

	public void setup(Context context, AbstractCard card, int seqNum, CardLocation cardLocation) {
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
	
	public void setup() {
		
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
			
			name.setText(String.format("[%d] %s", seqNum, playerDeckCard.getName()));
			attack.setText(String.format("%s", playerDeckCard.getAttack()));
			
			if ( card instanceof PlayerFieldCard ) {
				attack.setText(String.format("%s %s", ((PlayerFieldCard) card).getLocation() , playerDeckCard.getAttack()));
			}
			
			Log.d("CardLayout", String.format("detailShown: %s", detailShown));
			if (detailShown) {
				lifePoints.setText(String.format("%s", playerDeckCard.getLifePoints()));
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
		
		if (card instanceof DeckTemplateElement) {
			
			DeckTemplateElement deckTemplateElement = (DeckTemplateElement)card;

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
			
			name.setText(String.format("[%d] %s", seqNum, deckTemplateElement.getName()));
			attack.setText(String.format("%s", deckTemplateElement.getAttack()));
			numberOfCards.setText(String.format("%s", deckTemplateElement.getNumberOfCards()));
			
			Log.d("CardLayout", String.format("detailShown: %s", detailShown));
			if (detailShown) {
				lifePoints.setText(String.format("%s", deckTemplateElement.getLifePoints()));
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
			
			resource.setText(String.format("Trade: %s\nDefense: %s\nFaith: %s", resourceDeckCard.getTrade(), resourceDeckCard.getDefense(), resourceDeckCard.getFaith()));
			
		}
		
	}
	
	public String getPossibleTarget(int dest) {
		
		if ( cardLocation == CardLocation.MODAL && card instanceof ResourceDeckCard && dest == R.id.playerHand ) {
			return "playerHand";
		}
		if ( cardLocation == CardLocation.MODAL && card instanceof PlayerDeckCard && dest == R.id.playerHand ) {
			return "playerHand";
		}
		if ( cardLocation == CardLocation.HAND && card instanceof PlayerDeckCard && dest == R.id.playerFieldAttack ) {
			return "playerFieldAttack";
		}
		if ( cardLocation == CardLocation.HAND && card instanceof PlayerDeckCard && dest == R.id.playerFieldDefense ) {
			return "playerFieldDefense";
		}
		if ( cardLocation == CardLocation.FIELD_ATTACK && card instanceof PlayerDeckCard && dest == R.id.opponentField ) {
			return "opponentField";
		}
		if ( cardLocation == CardLocation.FIELD_DEFENSE && card instanceof PlayerDeckCard && dest == R.id.opponentField ) {
			return "opponentField";
		}
		
		return null;
		
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
		else if ( id == R.id.playerFieldDefense ) {
			return "playerFieldDefense";
		}
		else if ( id == R.id.opponentField ) {
			return "opponentField";
		}
		else {
			return null;
		}
		
	}

	private void init() {
		
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rootView = (LinearLayout)layoutInflater.inflate(R.layout.card, null);
		
		reset();
		resource = new TextView(context);
		image = (ImageView)rootView.findViewById(R.id.cardLayoutImage);
		name = (TextView)rootView.findViewById(R.id.cardLayoutName);
		attack = (TextView)rootView.findViewById(R.id.cardLayoutAttack);
		lifePoints = (TextView)rootView.findViewById(R.id.cardLayoutLifePoints);
		numberOfCards = (TextView)rootView.findViewById(R.id.cardLayoutNumberOfCards);
		resource = (TextView)rootView.findViewById(R.id.cardLayoutResource);
		
		this.addView(rootView);

		if (card instanceof PlayerDeckCard) {
			
			if (!detailShown) {
				lifePoints.setVisibility(View.INVISIBLE);
			}
			
		}
		
		if (card instanceof DeckTemplateElement) {
			
			numberOfCards.setVisibility(View.VISIBLE);
			
		}
		
		if ( card instanceof ResourceDeckCard ) {
			
			resource.setVisibility(View.VISIBLE);
			
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
