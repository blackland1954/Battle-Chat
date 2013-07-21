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
import com.ninetwozero.battlechat.datatypes.User;

import java.util.List;

public class UserListAdapter extends AbstractListAdapter<User> {
	private final int HEADING = 0;
	private final int ITEM = 1;
	
	public UserListAdapter(Context context) {
		super(context);
	}
	
	public UserListAdapter(Context context, List<User> items) {
		super(context, items);
	}
	
	@Override
	public boolean isEnabled(int position) {
		return getItem(position).getId() > 0;
	}
	
	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}
	
	@Override
	public int getItemViewType(int position) {
		return mItems.get(position).getId() == 0? HEADING : ITEM;
	
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final User user = getItem(position);
		if( getItemViewType(position) == HEADING ) {
			if( convertView == null ) {
				convertView = mLayoutInflater.inflate(R.layout.list_item_heading, null);
			}
			setText(convertView, R.id.title, user.getUsername());
			
		} else {
			if( convertView == null ) {
				convertView = mLayoutInflater.inflate(R.layout.list_item_user, null);
			}
		
			setText(convertView, R.id.username, user.getUsername(), getColorForStatus(user.isPlaying(), user.isOnline(), user.isAway()));
			setText(convertView, R.id.online_status, user.getOnlineStatus(), getColorForStatus(user.isPlaying(), user.isOnline(), user.isAway()));
			convertView.setTag(user);
		}
		return convertView;
	}

	private int getColorForStatus(boolean playing, boolean online, boolean away) {
		if( playing ) {
			return R.color.blue;
		} else if( online ) {
			return R.color.green;
		} else if( away ) {
            return R.color.darkgreen;
        } else {
            return R.color.grey;
        }
	}

}
