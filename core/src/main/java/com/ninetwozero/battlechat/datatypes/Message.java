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

public class Message implements Parcelable {

    private String mContent;
    private String mUsername;
    private long mTimestamp;

    public Message(Parcel in) {
        mContent = in.readString();
        mUsername = in.readString();
        mTimestamp = in.readLong();
    }

    public Message(String content, String user, long timestamp) {
        mContent = content;
        mUsername = user;
        mTimestamp = timestamp;
    }

    public String getMessage() {
        return mContent;
    }

    public String getUsername() {
        return mUsername;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mContent);
        out.writeString(mUsername);
        out.writeLong(mTimestamp);
    }

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
