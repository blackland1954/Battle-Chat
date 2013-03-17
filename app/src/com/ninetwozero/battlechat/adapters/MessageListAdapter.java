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

import java.util.List;

public class MessageListAdapter extends AbstractListAdapter<Message> {
	
	private String mCurrentUser;
	
	public MessageListAdapter(Context context, String currentUser) {
		super(context);
		mCurrentUser = currentUser;
	}

	public MessageListAdapter(Context context, List<Message> items, String currentUser) {
		super(context, items);
		mCurrentUser = currentUser;
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Message message = getItem(position);
		final boolean fromCurrentUser = mCurrentUser.equals(message.getUsername());
		if( convertView == null ) {
			convertView = mLayoutInflater.inflate(R.layout.list_item_message, null);
		}
		
		setText(convertView, R.id.username, message.getUsername(), fromCurrentUser? R.color.blue : R.color.orange);
		setText(convertView, R.id.message, message.getMessage());
		setText(convertView, R.id.timestamp, DateUtils.getRelativeTimeString(mContext, message.getTimestamp()));
		return convertView;
	}
}
