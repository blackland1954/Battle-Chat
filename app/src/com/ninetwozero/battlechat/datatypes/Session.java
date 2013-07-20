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

package com.ninetwozero.battlechat.datatypes;

import android.content.SharedPreferences;

import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.http.CookieFactory;
import com.ninetwozero.battlechat.misc.Keys;

import org.apache.http.cookie.Cookie;

public class Session {

	private User mUser;
	private Cookie mCookie;
    private String mEmail;
	private String mChecksum;
	
	public Session(SharedPreferences sharedPreferences) {
		mUser = new User(
			sharedPreferences.getLong(Keys.Session.USER_ID, 0),
			sharedPreferences.getString(Keys.Session.USERNAME, "N/A"),
			User.ONLINE_WEB
		);
		mCookie = CookieFactory.build(
			sharedPreferences.getString(Keys.Session.COOKIE_NAME, BattleChat.COOKIE_NAME),
			sharedPreferences.getString(Keys.Session.COOKIE_VALUE, "")
		);
        mEmail = sharedPreferences.getString(Keys.Session.EMAIL, "");
		mChecksum = sharedPreferences.getString(Keys.Session.CHECKSUM, "");
	}
	
	public Session(User user, Cookie cookie, String email, String checksum) {
		mUser = user;
		mCookie = cookie;
        mEmail = email;
		mChecksum = checksum;
	}
	
	public User getUser() {
		return mUser;
	}
	
	public String getUsername() {
		return mUser.getUsername();
	}

    public String getEmail() {
        return mEmail;
    }
	
	public Cookie getCookie() {
		return mCookie;
	}
		
	public String getChecksum() {
		return mChecksum;
	}

	@Override
	public String toString() {
		return "Session [mUser=" + mUser + ", mCookie=" + mCookie
				+ ", mChecksum=" + mChecksum + "]";
	}
}
