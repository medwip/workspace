package com.guntzergames.medievalwipeout.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.beans.CardModel;
import com.guntzergames.medievalwipeout.beans.CollectionElement;
import com.guntzergames.medievalwipeout.interfaces.Constants;

public class DeckTemplateActivity extends ApplicationActivity {

	private long deckTemplateId;
	private String facebookUserId;
	private LinearLayout layout;
	private Button homeButton;
	private ListView cardModelListView;
	private List<String> cardModelNames;
	private Account account;

	private void init() {

		deckTemplateId = getIntent().getExtras().getLong(Constants.DECK_TEMPLATE_ID);
		facebookUserId = getIntent().getExtras().getString(Constants.FACEBOOK_USER_ID);

		Toast.makeText(this, String.format("Editing template ID=%s, facebookUserId=%s", deckTemplateId, facebookUserId), Toast.LENGTH_SHORT).show();

		layout = (LinearLayout) LinearLayout.inflate(this, R.layout.activity_deck, null);

		homeButton = (Button) layout.findViewById(R.id.homeButton);
		cardModelListView = (ListView) layout.findViewById(R.id.cardModelList);
		
		// To call getCollectionElements...
		gameWebClient.getCardModels();

		setContentView(layout);

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

	}

	private void updateCardModelListView() {

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_list_item_1, cardModelNames);
		cardModelListView.setAdapter(adapter);

	}

	@Override
	public void onError(String err) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getFacebookUserId() {
		return facebookUserId;
	}

	@Override
	public void onGetCardModels(List<CardModel> cardModels) {

		cardModelNames = new ArrayList<String>();

		for (CardModel cardModel : cardModels) {
			cardModelNames.add(cardModel.getName());
		}
		
		updateCardModelListView();

	}

	@Override
	public void onGetAccount(Account account) {
		
		cardModelNames = new ArrayList<String>();

		for (CollectionElement collectionElement : account.getCollectionElements()) {
			cardModelNames.add(collectionElement.getName());
		}
		
		updateCardModelListView();
		
	}
	
}
