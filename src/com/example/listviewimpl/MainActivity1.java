package com.example.listviewimpl;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity1 extends Activity {

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	private Handler handler;
	private List<String> createTestData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main1);
		// setContentView(R.layout.activity_main1);

		final ListView listView = (ListView) findViewById(R.id.my_listview);
		createTestData = createTestData();
		final CustomAdapter customAdapter = new CustomAdapter(this, createTestData);
		final CustomAdapter1 customAdapter1 = new CustomAdapter1();
		listView.setAdapter(customAdapter);

		// item Click
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String itemName = (String) customAdapter.getItem(position);
				Toast.makeText(getBaseContext(), "点击   " + itemName, Toast.LENGTH_SHORT).show();
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				String itemName = (String) customAdapter.getItem(position);
				Toast.makeText(getBaseContext(), "长按  " + itemName, Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		handler = new Handler();
		// handler.postDelayed(new Runnable() {
		// int count = 0;
		//
		// @Override
		// public void run() {
		// count++;
		// if (count < 30) {
		// createTestData.addAll(createTestData());
		// customAdapter.notifyDataSetChanged();
		// handler.postDelayed(this, 2000);
		// }
		// }
		// }, 2000);

		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				System.out.println(firstVisibleItem);
				System.out.println(visibleItemCount + "===");
				System.out.println(totalItemCount + "----");
			}
		});

	}

	// ===========================================================
	// Private Methods
	// ===========================================================

	/**
	 * ListView数据创建
	 */
	private List<String> createTestData() {
		List<String> data = new ArrayList<String>();
		for (int i = 0; i < 300; i++) {
			data.add("Love World " + i);
		}
		return data;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class CustomAdapter extends BaseAdapter {

		private List<String> mData;
		private LayoutInflater mInflater;

		public CustomAdapter(Context context, List<String> data) {
			mData = data;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if (mData == null || mData.size() <= 0) {
				return 0;
			}
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			if (mData == null || mData.size() <= 0 || position < 0 || position >= mData.size()) {
				return null;
			}
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position % 4 == 1) {
				TextView textView = new TextView(MainActivity1.this);
				textView.setText("ooooxxxx");
				textView.setBackgroundColor(Color.GREEN);
				return textView;
			}
			if (convertView == null || convertView instanceof TextView) {
				convertView = mInflater.inflate(R.layout.list_item, null);
			}

			TextView name = (TextView) convertView.findViewById(R.id.tv_name);
			name.setText((CharSequence) getItem(position));

			return convertView;
		}

	}

	private class CustomAdapter1 extends BaseAdapter {

		@Override
		public int getCount() {
			return 20;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// if (position % 4 == 1){
			// TextView textView = new TextView(MainActivity.this);
			// textView.setText("ooooxxxx");
			// textView.setBackgroundColor(Color.GREEN);
			// return textView;
			// }

			if (convertView == null) {
				TextView textView = new TextView(MainActivity1.this);
				textView.setText("ooooxxxx");
				textView.setBackgroundColor(Color.GREEN);
				convertView = textView;
			}

			return convertView;
		}

	}

}