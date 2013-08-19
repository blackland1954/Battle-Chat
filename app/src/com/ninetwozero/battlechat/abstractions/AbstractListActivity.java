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

package com.ninetwozero.battlechat.abstractions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.activities.LoginActivity;
import com.ninetwozero.battlechat.http.BattleChatClient;
import com.ninetwozero.battlechat.misc.Keys;

public class AbstractListActivity extends SherlockListActivity {
    protected SharedPreferences mSharedPreferences;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (BattleChat.hasStoredCookie(mSharedPreferences)) {
            setupBattleChatClient();
            showNotification();
        } else {
            sendToLoginScreen();
        }
    }

    final protected void sendToLoginScreen() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void showToast(final int resource) {
        showToast(getString(resource));
    }

    public void showToast(final String text) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }

        mToast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void setupBattleChatClient() {
        if (!BattleChat.hasSession() && BattleChat.hasStoredCookie(mSharedPreferences)) {
            BattleChat.reloadSession(mSharedPreferences);
        }
        BattleChatClient.setCookie(BattleChat.getSession().getCookie());
    }

    protected void showNotification() {
        if (mSharedPreferences.getBoolean(Keys.Settings.PERSISTENT_NOTIFICATION, true)) {
            BattleChat.showLoggedInNotification(getApplicationContext());
        } else {
            BattleChat.clearNotification(getApplicationContext());
        }
    }
}
