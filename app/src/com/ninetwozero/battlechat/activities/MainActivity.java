/*
	This file is part of BattleChat

	BattleChat is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	BattleChat is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
*/

package com.ninetwozero.battlechat.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.abstractions.AbstractListActivity;
import com.ninetwozero.battlechat.adapters.UserListAdapter;
import com.ninetwozero.battlechat.comparators.UserComparator;
import com.ninetwozero.battlechat.datatypes.User;
import com.ninetwozero.battlechat.http.BattleChatClient;
import com.ninetwozero.battlechat.http.HttpUris;
import com.ninetwozero.battlechat.services.BattleChatService;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AbstractListActivity {

	public final static String TAG = "MainActivity";
	
	private ReloadTask mReloadTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupListView();
		setupFromSavedInstance(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		reload(false);
		showNotification();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle out) {
		final UserListAdapter adapter = (UserListAdapter) getListView().getAdapter();
		final ArrayList<User> friends = (ArrayList<User>) adapter.getItems();
		out.putParcelableArrayList("friends", friends);
		
		super.onSaveInstanceState(out);
	}

	private void setupFromSavedInstance(Bundle in) {
		if( in == null ){
			return;
		}
		final List<User> friends = in.getParcelableArrayList("friends");
		final UserListAdapter adapter = (UserListAdapter) getListView().getAdapter();
		adapter.setItems(friends);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId() ) {
			case R.id.menu_about:
				startActivity( new Intent(this, AboutActivity.class) );
				return true;
			case R.id.menu_reload:
				reload(true);
				return true;
			case R.id.menu_settings:
				startActivity( new Intent(this, SettingsActivity.class));
				return true;
			case R.id.menu_exit:
    			logoutFromWebsite();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void logoutFromWebsite() {
		new LogoutTask().execute();
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id) {
		User user = (User) view.getTag();
		if( user != null ) {
			startActivity( new Intent(this, ChatActivity.class).putExtra(ChatActivity.EXTRA_USER, user) );			
		}
	}

	private void setupListView() {
		final ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
		listView.setAdapter(new UserListAdapter(getApplicationContext()));
	}

	private void reload(boolean show) {
		if( mReloadTask == null ) {
			mReloadTask = new ReloadTask(show);
			mReloadTask.execute();
		}
	}
	
	private class ReloadTask extends AsyncTask<Void, Void, Boolean> {
		private String mMessage;
		private List<User> mItems;
		private boolean mShow;
		
		public ReloadTask(boolean show) {
			mShow = show;
		}
		
		@Override
		protected void onPreExecute() {
			if( getListView().getCount() == 0 || mShow ) {
				toggleLoading(true);
			}
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				JSONObject result = BattleChatClient.post(
					HttpUris.Chat.FRIENDS, 
					new BasicNameValuePair("post-check-sum", BattleChat.getSession().getChecksum())
				);
				
				if( result.has("error") ) {
					mMessage = result.getString("error");
					return false;
				} 

				mItems = getUsersFromJson(result);
				return true;
			} catch( Exception ex ) {
				ex.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if( result ) {
				((UserListAdapter)getListView().getAdapter()).setItems(mItems);
			} else {
				showToast(mMessage);
				logoutFromWebsite();
			}
			toggleLoading(false);
			mReloadTask = null;
		}
		
		private List<User> getUsersFromJson(JSONObject result) throws JSONException {
			JSONArray friends = result.getJSONArray("friendscomcenter");
			JSONObject friend;
            JSONObject presence;
			int presenceState;
			List<User> users = new ArrayList<User>();
			
			int numFriends = friends.length();
			int numPlaying = 0;
			int numOnline = 0;
			int numOffline = 0;

			if( numFriends > 0 ) {
				for( int i = 0; i < numFriends; i++ ) {
					friend = friends.optJSONObject(i);
					presence = friend.getJSONObject("presence");
                    presenceState = getPresenceStateFromJSON(presence);

                    switch( presenceState ) {
                        case User.PLAYING_MP:
                            numPlaying++;
                            break;
                        case User.ONLINE_WEB:
                        case User.AWAY_WEB:
                            numOnline++;
                            break;
                        case User.OFFLINE:
                            numOffline++;
                            break;
                        default:
                            numOffline++;
                            break;
                    }

                    users.add(
                        new User(
                            Long.parseLong(friend.getString("userId")),
                            friend.getString("username"),
                            presenceState
                        )
                    );
				}

				if (numPlaying > 0) {
					users.add(new User(0, getString(R.string.label_playing), User.PLAYING_MP));
				}

				if (numOnline > 0) {
					users.add(new User(0, getString(R.string.label_online), User.ONLINE_WEB));
				}

				if (numOffline > 0) {
					users.add(new User(0, getString(R.string.label_offline), User.OFFLINE));
				}
				Collections.sort(users,	new UserComparator());
			}
			return users;
		}

        private int getPresenceStateFromJSON(final JSONObject presence) {
            if( presence.has("isPlaying") ) {
                return User.PLAYING_MP;
            } else if( presence.has("isAway") ) {
                return User.AWAY_WEB;
            } else if( presence.has("isOnline") ) {
                return User.ONLINE_WEB;
            } else {
                return User.OFFLINE;
            }
        }
    }
	
	private class LogoutTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				final Cookie cookie = BattleChat.getSession().getCookie();
				Jsoup.connect(HttpUris.LOGOUT).cookie(cookie.getName(), cookie.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			BattleChat.clearSession(getApplicationContext());
			BattleChat.clearNotification(getApplicationContext());
			BattleChatService.unschedule(getApplicationContext());
			sendToLoginScreen();
		}
	}
	
	private void toggleLoading(boolean isLoading) {
		final View view = findViewById(R.id.status);
		view.setVisibility(isLoading ? View.VISIBLE : View.GONE);
	}
}
