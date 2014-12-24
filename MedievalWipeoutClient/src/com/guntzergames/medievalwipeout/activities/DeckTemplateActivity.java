package com.guntzergames.medievalwipeout.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.guntzergames.medievalwipeout.adapters.CollectionElementAdapter;
import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.beans.CollectionElement;
import com.guntzergames.medievalwipeout.beans.DeckTemplate;
import com.guntzergames.medievalwipeout.beans.DeckTemplateElement;
import com.guntzergames.medievalwipeout.enums.CardLocation;
import com.guntzergames.medievalwipeout.interfaces.Constants;
import com.guntzergames.medievalwipeout.layouts.CardLayout;

public class DeckTemplateActivity extends ApplicationActivity {

	private long deckTemplateId;
	private String facebookUserId;
	private LinearLayout layout;
	private Button homeButton, newDeckButton;
	private EditText newDeckLibelEditText;
	private ListView cardModelListView;
	private Spinner deckTemplateSpinner;
	private GridLayout cardGridView;
	private List<CollectionElement> collectionElements;
	private DeckTemplate selectedDeckTemplate;
	private Account account;

	private void init() {

		deckTemplateId = getIntent().getExtras().getLong(Constants.DECK_TEMPLATE_ID);
		facebookUserId = getIntent().getExtras().getString(Constants.FACEBOOK_USER_ID);

		Toast.makeText(this, String.format("Editing template ID=%s, facebookUserId=%s", deckTemplateId, facebookUserId), Toast.LENGTH_SHORT).show();

		layout = (LinearLayout) LinearLayout.inflate(this, R.layout.activity_deck, null);

		homeButton = (Button) layout.findViewById(R.id.homeButton);
		newDeckButton = (Button) layout.findViewById(R.id.newDeckButton);
		cardModelListView = (ListView) layout.findViewById(R.id.cardModelList);
		deckTemplateSpinner = (Spinner) layout.findViewById(R.id.deckTemplateSpinner);
		cardGridView = (GridLayout) layout.findViewById(R.id.cardGrid);
		newDeckLibelEditText = (EditText) layout.findViewById(R.id.newDeckLibel);
		
		setContentView(layout);

	}
	
	public Activity getActivity() {
		return this;
	}

	public void returnHome() {
		Intent intent = new Intent(DeckTemplateActivity.this, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		init();

		homeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				returnHome();
			}
		});
		
		newDeckButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				createNewDeck();
			}
		});

		deckTemplateSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedDeckTemplate = account.getDeckTemplates().get(position);
//				Toast.makeText(getActivity(), "Click on ID " + selectedDeckTemplate.getId(), Toast.LENGTH_LONG).show();
				updateDeckTemplateListElements();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
			
		});
		
		cardModelListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if ( !isHttpRequestBeingExecuted() ) {
					gameWebClient.addDeckTemplateElement(selectedDeckTemplate.getId(), collectionElements.get(position).getId());
				}
				else {
					Toast.makeText(getActivity(), "HTTP being executed...", Toast.LENGTH_SHORT).show();
				}
			}
			
		});

		gameWebClient.getAccount(facebookUserId);

	}

	private void updateDeckTemplateListView() {

		List<String> elems = new ArrayList<String>();
		for (DeckTemplate deckTemplate : account.getDeckTemplates()) {
			elems.add(deckTemplate.getDeckLibel());
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, elems);
		deckTemplateSpinner.setAdapter(adapter);
		
	}
	
	private void updateDeckTemplateListElements() {
		
		for ( int i = 0; i < 8; i++ ) {
			
			CardLayout cardLayout = (CardLayout) layout.findViewById(CardLayout.getGridCardFromId(i++));
			cardLayout.reset();
			
		}
		
		int i = 0;
		
		for ( DeckTemplateElement deckTemplateElement : selectedDeckTemplate.getCards() ) {
			
			CardLayout cardLayout = (CardLayout) layout.findViewById(CardLayout.getGridCardFromId(i++));
			cardLayout.setup(this, deckTemplateElement, 0, CardLocation.GRID);
//			cardGridView.addView(cardLayout, new GridLayout.LayoutParams(
//                    GridLayout.spec(1, GridLayout.CENTER),
//                    GridLayout.spec(1, GridLayout.CENTER)));
//			Toast.makeText(getActivity(), "deckTemplateElement " + deckTemplateElement, Toast.LENGTH_LONG).show();
			
		}
		
	}

	private void updateCardModelListView() {

		Log.i("DeckTemplateActivity", String.format("Number of elements: %s", collectionElements.size()));
		CollectionElementAdapter adapter = new CollectionElementAdapter(this, collectionElements, this.getResources());
		cardModelListView.setAdapter(adapter);

	}
	
	private void createNewDeck() {
		
		newDeckLibelEditText.setVisibility(View.VISIBLE);
		
	}

	@Override
	public void onError(String err) {
		super.onError(err);
	}

	@Override
	public String getFacebookUserId() {
		return facebookUserId;
	}

	@Override
	public void onGetAccount(Account account) {

		this.account = account;
		collectionElements = account.getCollectionElements();
		Log.i("DeckTemplateActivity", String.format("1 - Number of elements: %s", collectionElements.size()));

		updateCardModelListView();
		updateDeckTemplateListView();

	}

}
