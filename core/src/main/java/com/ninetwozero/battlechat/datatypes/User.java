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
	public final static int ONLINE = 1;
	public final static int PLAYING = 264;
	
	private long mId;
	private String mUsername;
	private int mStatus;
	
	public User(Parcel in) {
		mId = in.readLong();
		mUsername = in.readString();
		mStatus = in.readInt();
	}
	
	public User(long id, String name) {
		mId = id;
		mUsername = name;
		mStatus = ONLINE;
	}
	
	public User(long id, String name, int status) {
		mId = id;
		mUsername = name;
		mStatus = status;
	}
	
	public long getId() {
		return mId;
	}
	
	public String getUsername() {
		return mUsername;
	}
	
	public boolean isPlaying() {
		return mStatus == PLAYING;
	}
	
	public boolean isOnline() {
		return mStatus >= ONLINE && mStatus != PLAYING;
	}
	
	public boolean isOffline() {
		return mStatus == OFFLINE;
	}
	
	public String getOnlineStatus() {
		switch(mStatus) {
			case OFFLINE:
				return "OFFLINE";
			case ONLINE:
				return "ONLINE";
			case PLAYING:
				return "PLAYING";
			default:
				return "ONLINE"; // 1 <= x < 264 = online
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
		out.writeInt(mStatus);
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
		return "User [mId=" + mId + ", mUsername=" + mUsername + ", mStatus="
				+ mStatus + "]";
	}

}
