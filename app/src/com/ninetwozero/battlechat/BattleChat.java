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

package com.ninetwozero.battlechat;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.ninetwozero.battlechat.activities.LoginActivity;
import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.datatypes.User;
import com.ninetwozero.battlechat.misc.Keys;

import org.apache.http.cookie.Cookie;

public class BattleChat extends Application {
    public static final String COOKIE_NAME = "beaker.session.id";
    public static final String COOKIE_DOMAIN = "battlelog.battlefield.com";

    private static BattleChat mInstance;
    private static Session mSession;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static Context getContext() {
        return mInstance;
    }

    public static Session getSession() {
        return mSession;
    }

    public static void setSession(Session session) {
        mSession = session;
    }

    public static void reloadSession(final SharedPreferences sharedPreferences) {
        mSession = new Session(sharedPreferences);
    }

    public static void reloadSession(User user, Cookie cookie, String email, String checksum) {
        mSession = new Session(user, cookie, email, checksum);
    }

    public static boolean hasSession() {
        return mSession != null;
    }

    public static void saveToSharedPreferences(Context c) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putLong(Keys.Session.USER_ID, mSession.getUser().getId());
        editor.putString(Keys.Session.USERNAME, mSession.getUser().getUsername());
        editor.putString(Keys.Session.EMAIL, mSession.getEmail());
        editor.putString(Keys.Session.COOKIE_NAME, mSession.getCookie().getName());
        editor.putString(Keys.Session.COOKIE_VALUE, mSession.getCookie().getValue());
        editor.putString(Keys.Session.CHECKSUM, mSession.getChecksum());

        editor.commit();
    }

    public static void clearSession(Context c) {
        BattleChat.mSession = null;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = preferences.edit();

        editor.remove(Keys.Session.USER_ID);
        editor.remove(Keys.Session.USERNAME);
        editor.remove(Keys.Session.COOKIE_NAME);
        editor.remove(Keys.Session.COOKIE_VALUE);
        editor.remove(Keys.Session.CHECKSUM);

        editor.commit();
    }

    public static void showLoggedInNotification(final Context c) {
        if (c == null) {
            return;
        }

        final String subtitle = String.format(
                c.getString(R.string.text_notification_subtitle_ok),
                BattleChat.getSession().getUsername()
        );

        NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.cancel(R.string.service_name);

        Notification notification = new NotificationCompat.Builder(c)
                .setContentTitle(c.getString(R.string.text_notification_title))
                .setContentText(subtitle)
                .setSmallIcon(R.drawable.ic_launcher)
                .setOngoing(true)
                .setContentIntent(PendingIntent.getActivity(c, 0, new Intent(c, LoginActivity.class), 0))
                .build();
        mNotificationManager.notify(R.string.service_name, notification);
    }

    public static void showLoggedOutNotification(final Context c) {
        NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.cancel(R.string.service_name);

        Notification notification = new NotificationCompat.Builder(c)
                .setContentTitle(c.getString(R.string.text_notification_title))
                .setContentText(c.getString(R.string.text_notification_subtitle_fail))
                .setSmallIcon(R.drawable.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(c, 0, new Intent(c, LoginActivity.class), 0))
                .build();
        mNotificationManager.notify(R.string.service_name, notification);
    }

    public static void clearNotification(final Context c) {
        NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.cancel(R.string.service_name);
    }

    public static boolean hasStoredCookie(final SharedPreferences preferences) {
        return !"".equals(preferences.getString(Keys.Session.COOKIE_VALUE, ""));
    }

    public static boolean isConnectedToNetwork() {
        ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo network = manager.getActiveNetworkInfo();
        return network != null && network.isConnected();
    }
}
