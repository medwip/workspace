package com.guntzergames.medievalwipeout.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.guntzergames.medievalwipeout.activities.R;
import com.guntzergames.medievalwipeout.beans.CollectionElement;
import com.guntzergames.medievalwipeout.layouts.CardLayout;

public class CollectionElementAdapter extends BaseAdapter implements OnClickListener {

	private static LayoutInflater inflater;

	private Activity activity;
	private List<CollectionElement> data;
	public Resources res;
	private CollectionElement tempValues;
	int i = 0;

	public static class ViewHolder {

		public TextView text;
		public TextView text1;
		public ImageView image;

	}

	public CollectionElementAdapter(Activity a, List<CollectionElement> d, Resources resLocal) {

		activity = a;
		data = d;
		res = resLocal;

		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public int getCount() {

		if (data.size() <= 0)
			return 1;
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	/****** Depends upon data size called for each row , Create each ListView row *****/
	public View getView(int position, View convertView, ViewGroup parent) {

		View vi = convertView;
		ViewHolder holder;

		if (convertView == null) {

			/****** Inflate tabitem.xml file for each row ( Defined below ) *******/
			vi = inflater.inflate(R.layout.collection_element_row, null);

			/****** View Holder Object to contain tabitem.xml file elements ******/

			holder = new ViewHolder();
			holder.text = (TextView) vi.findViewById(R.id.text);
			holder.text1 = (TextView) vi.findViewById(R.id.text1);
			holder.image = (ImageView) vi.findViewById(R.id.image);

			/************ Set holder with LayoutInflater ************/
			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();

		if (data.size() <= 0) {
			holder.text.setText("No Data");

		} else {
			/***** Get each Model object from Arraylist ********/
			tempValues = null;
			tempValues = (CollectionElement) data.get(position);

			/************ Set Model values in Holder elements ***********/

			holder.text.setText(tempValues.getName());
			holder.text1.setText(tempValues.getAttack() + "");
			try {
				holder.image.setImageDrawable(res.getDrawable(CardLayout.getResourceFromName("card_" + tempValues.getDrawableResourceName())));
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  // res.getIdentifier("com.androidexample.customlistview:drawable/" + tempValues.getImage(), null, null));

			vi.setOnClickListener(new OnItemClickListener(position));
		}
		return vi;
	}

	@Override
	public void onClick(View v) {
		Log.v("CustomAdapter", "=====Row button clicked=====");
	}

	/********* Called when Item click in ListView ************/
	private class OnItemClickListener implements OnClickListener {
		private int mPosition;

		OnItemClickListener(int position) {
			mPosition = position;
		}

		@Override
		public void onClick(View arg0) {

//			CustomListViewAndroidExample sct = (CustomListViewAndroidExample) activity;

			/****
			 * Call onItemClick Method inside CustomListViewAndroidExample Class
			 * ( See Below )
			 ****/

//			sct.onItemClick(mPosition);
		}
	}
}