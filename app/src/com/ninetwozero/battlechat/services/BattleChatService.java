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

package com.ninetwozero.battlechat.services;

import org.apache.http.cookie.Cookie;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.datatypes.User;
import com.ninetwozero.battlechat.http.HttpUris;
import com.ninetwozero.battlechat.http.LoginHtmlParser;
import com.ninetwozero.battlechat.misc.Keys;
import com.ninetwozero.battlechat.utils.DateUtils;

public class BattleChatService extends Service {
		private static final String TAG = "BattlelogSessionService";

	    private final IBinder mBinder = new LocalBinder();
	    
	    private SessionReloadTask mSessionReloadTask;
		private SharedPreferences mSharedPreferences;

	    @Override
	    public void onCreate() {
	    	mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    }

	    @Override
	    public int onStartCommand(Intent intent, int flags, int startId) {
	    	reloadSession();
	    	load();
		    return Service.START_NOT_STICKY;	
	    }
	    
		@Override
	    public IBinder onBind(Intent intent) {
	        return mBinder;
	    }
	    
		private void load() {
			if( mSessionReloadTask == null && BattleChat.isConnectedToNetwork() ) {
				mSessionReloadTask = new SessionReloadTask();
				mSessionReloadTask.execute();
			}
		}

		public static final PendingIntent getPendingIntent(Context c) {
			return PendingIntent.getService(c, 0, getIntent(c), PendingIntent.FLAG_CANCEL_CURRENT);
		}
		
		public static final PendingIntent getPendingIntent(Context c, Intent data) {
			return PendingIntent.getService(c, 0, data, PendingIntent.FLAG_CANCEL_CURRENT);
		}
	    
	    public static final Intent getIntent(Context c) {
	    	return new Intent(c, BattleChatService.class);
	    }
	    
	    public class SessionReloadTask extends AsyncTask<Void, Void, Boolean> {
	    	private User mUser;
	    	private Cookie mCookie;
            private String mEmail;
	    	private String mChecksum;
	    	
	    	@Override
	    	protected Boolean doInBackground(Void... params) {
	    		try {
	    			Log.i(TAG, "Talking to the website...");
	    			Connection.Response response = Jsoup.connect(HttpUris.MAIN).cookie(
	    				BattleChat.getSession().getCookie().getName(), 
	    				BattleChat.getSession().getCookie().getValue()
					).execute();

	    			LoginHtmlParser parser = new LoginHtmlParser(response.parse());
	    			if( parser.isLoggedIn() ) {
		    			mUser = new User(parser.getUserId(), parser.getUsername(), User.ONLINE_MOBILE);
		    			mCookie = BattleChat.getSession().getCookie();
		    			mEmail = BattleChat.getSession().getEmail();
                        mChecksum = parser.getChecksum();
		    			return true;
	    			}
	    		} catch( Exception ex ) {
	    			ex.printStackTrace();
	    		}	    			
	    		return false;
	    	}
	    	
	    	@Override
	    	protected void onPostExecute(Boolean hasActiveSession) {
	    		if(hasActiveSession) {
		    		Log.i(TAG, "Our sesssion is intact, keep rolling!");
	    			BattleChat.reloadSession(mUser, mCookie, mEmail, mChecksum);
	    		} else {
		    		Log.i(TAG, "Our sesssion isn't intact. Removing the stored information.");
		    		BattleChatService.unschedule(getApplicationContext());
	    			BattleChat.clearSession(getApplicationContext());
	    		}
	    		showNotification(hasActiveSession);
	    		stopSelf();
	    	}

			private void showNotification(boolean hasActiveSession) {
				if( mSharedPreferences.getBoolean(Keys.Settings.PERSISTENT_NOTIFICATION, true) ) {
					if( hasActiveSession ) {
						BattleChat.showLoggedInNotification(getApplicationContext());
					} else {
						BattleChat.showLoggedOutNotification(getApplicationContext());
					}
				}
			}
	    }

	    public class LocalBinder extends Binder {
	        BattleChatService getService() {
	            return BattleChatService.this;
	        }
	    }
	    
	    private void reloadSession() {
			Log.i(TAG, "Grabbing the session details from SharedPreferences...");
	    	if( !BattleChat.hasSession() ) {
	    		Log.i(TAG, "We don't have a session. Reloading it...");
				BattleChat.reloadSession(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
			}
		}

		public static void scheduleRun(Context c) {
			AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
			alarmManager.setInexactRepeating(
				AlarmManager.ELAPSED_REALTIME, 
				DateUtils.HOUR_IN_SECONDS * 1000, 
				DateUtils.HOUR_IN_SECONDS * 1000, 
				BattleChatService.getPendingIntent(c.getApplicationContext())
			);
		}

		public static void unschedule(Context c) {
			AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(BattleChatService.getPendingIntent(c.getApplicationContext()));
		}
	}