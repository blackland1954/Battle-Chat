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

package com.ninetwozero.battlechat.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.abstractions.AbstractListAdapter;
import com.ninetwozero.battlechat.datatypes.Message;
import com.ninetwozero.battlechat.utils.DateUtils;

public class MessageListAdapter extends AbstractListAdapter<Message> {

    private String mOtherUser;

    public MessageListAdapter(Context context, String other) {
        super(context);
        mOtherUser = other;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Message message = getItem(position);
        final boolean fromOtherUser = mOtherUser.equals(message.getUsername());
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_message, null);
        }

        setText(convertView, R.id.username, message.getUsername(), fromOtherUser ? R.color.orange : R.color.blue);
        setText(convertView, R.id.message, message.getMessage());
        setText(convertView, R.id.timestamp, DateUtils.getRelativeTimeString(mContext, message.getTimestamp()));
        return convertView;
    }
}
