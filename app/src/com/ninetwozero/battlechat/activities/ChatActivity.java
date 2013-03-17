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
import android.os.Bundle;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.abstractions.AbstractListActivity;
import com.ninetwozero.battlechat.fragments.ChatFragment;


public class ChatActivity extends AbstractListActivity {

	public static final String TAG = "ChatActivity";
    private ChatFragment mChatFragment;
	private long mChatId = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
        setupChatFragment(getIntent());
	}

    @Override
    public void onResume() {
        super.onResume();
        reload(true);
    }

    private void setupChatFragment(final Intent in) {
        mChatFragment = (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_chat);
        if( in.hasExtra(ChatFragment.EXTRA_USER) ){
            mChatFragment.setData(in);
        } else {
            finish();
        }
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_chat, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if( item.getItemId() == R.id.menu_reload ) {
			reload(true);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void reload(boolean show) {
           mChatFragment.reload(show);
	}
}
