package com.guntzergames.medievalwipeout.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.guntzergames.medievalwipeout.interfaces.Constants;

public class DeckTemplateActivity extends Activity {
	
	private long deckTemplateId;
	private String facebookUserId;
	private GridLayout layout;
	private Button homeButton;
	private ListView cardModelListView;
	private List<String> cardModelNames = new ArrayList<String>();
	
	private void init() {

		deckTemplateId = getIntent().getExtras().getLong(Constants.DECK_TEMPLATE_ID);
		facebookUserId = getIntent().getExtras().getString(Constants.FACEBOOK_USER_ID);

		Toast.makeText(this, String.format("Editing template ID=%s, facebookUserId=%s", deckTemplateId, facebookUserId), Toast.LENGTH_SHORT).show();

		layout = (GridLayout) GridLayout.inflate(this, R.layout.activity_deck, null);
		
		homeButton = (Button) layout.findViewById(R.id.homeButton);
		cardModelListView = (ListView) layout.findViewById(R.id.cardModelList);

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

}
