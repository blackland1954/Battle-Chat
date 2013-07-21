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

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    public final static int OFFLINE = 0;

    public static final int ONLINE_WEB = 1;
    public static final int ONLINE_TABLET = 2;
    public static final int ONLINE_MOBILE = 4;
    public static final int ONLINE_GAME = 8;
    public static final int ONLINE_ORIGIN = 16;

    public static final int PLAYING_MP = 256;
    public static final int PLAYING_COOP = 512;
    public static final int PLAYING_ORIGIN = 1024;

    public static final int AWAY_WEB = 65536;
    public static final int AWAY_ORIGIN = 131072;

    public static final int INVISIBLE_WEB = 6777216;
    public static final int INVISIBLE_TABLET = 33554432;
    public static final int INVISIBLE_MOBILE = 67108864;

    public static final long GROUP_WEB = 4294967296L;
    public static final long GROUP_ORIGIN = 8589934592L;

    private long mId;
	private String mUsername;
	private int mState;
	
	public User(Parcel in) {
		mId = in.readLong();
		mUsername = in.readString();
		mState = in.readInt();
	}
	
	public User(long id, String name) {
		mId = id;
		mUsername = name;
		mState = ONLINE_WEB;
	}
	
	public User(long id, String name, int status) {
		mId = id;
		mUsername = name;
		mState = status;
	}
	
	public long getId() {
		return mId;
	}
	
	public String getUsername() {
		return mUsername;
	}
	
	public boolean isPlaying() {
		return mState == PLAYING_MP;
	}
	
	public boolean isOnline() {
		return mState == ONLINE_WEB;
	}

    public boolean isAway() {
        return mState == AWAY_WEB;
    }
	
	public boolean isOffline() {
		return mState == OFFLINE;
	}
	
	public String getOnlineStatus() {
        switch(mState) {
            case OFFLINE:
                return "OFFLINE";
            case ONLINE_WEB:
                return "ONLINE";
            case PLAYING_MP:
                return "PLAYING";
            case AWAY_WEB:
                return "AWAY";
            default:
                return "UNKNOWN (" + mState + ")";
        }
    }

	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(mId);
		out.writeString(mUsername);
		out.writeLong(mState);
	}
	
	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		public User createFromParcel(Parcel in) {
			return new User(in);
		}
		public User[] newArray(int size) {
			return new User[size];
		}
	};
	
	@Override
	public String toString() {
		return "User [mId=" + mId + ", mUsername=" + mUsername + ", mState="
				+ mState + "]";
	}

}
