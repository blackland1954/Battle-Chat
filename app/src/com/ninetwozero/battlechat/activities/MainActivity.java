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
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.abstractions.AbstractListActivity;
import com.ninetwozero.battlechat.datatypes.User;
import com.ninetwozero.battlechat.fragments.ChatFragment;
import com.ninetwozero.battlechat.fragments.ContactListFragment;
import com.ninetwozero.battlechat.http.HttpUris;
import com.ninetwozero.battlechat.services.BattleChatService;
import org.apache.http.cookie.Cookie;
import org.jsoup.Jsoup;

public class MainActivity extends AbstractListActivity {

	public final static String TAG = "MainActivity";

    private ChatFragment mChatFragment;
    private ContactListFragment mContactListFragment;
    private boolean mIsDoublePane = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        setupChatFragment();
        setupContactListFragment();
    }

    private void setupContactListFragment() {
        mContactListFragment = (ContactListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_list_contacts);
        mContactListFragment.setDoublePane(mIsDoublePane);
    }

    private void setupChatFragment() {
        mChatFragment = (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_chat);
        mIsDoublePane = (mChatFragment != null);
    }


    @Override
    public void onResume() {
        super.onResume();
        reload(false);
        showNotification();
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

    private void reload(final boolean show) {
        mContactListFragment.reload(show);
        if(mIsDoublePane) {
            mChatFragment.reload(show);
        }
    }

	private void logoutFromWebsite() {
		new LogoutTask().execute();
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

    public void openChat(final User user) {
        mChatFragment.setData(new Intent().putExtra("user", user));
    }
}
