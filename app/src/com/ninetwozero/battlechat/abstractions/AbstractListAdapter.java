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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractListAdapter<T> extends BaseAdapter {

    protected Context mContext;
    protected List<T> mItems;
    protected final LayoutInflater mLayoutInflater;

    public AbstractListAdapter(Context context) {
        mContext = context;
        mItems = new ArrayList<T>();
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public final int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public final T getItem(int position) {
        return mItems.get(position);
    }

    public final List<T> getItems() {
        return mItems;
    }

    public final void setItems(List<T> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public final void setText(View container, int resourceId, Object text) {
        setText(container, resourceId, text, -1);
    }

    public final void setText(View container, int resourceId, Object text, int colorResource) {
        TextView textView = (TextView) container.findViewById(resourceId);
        textView.setText(String.valueOf(text));
        if (colorResource > -1) {
            textView.setTextColor(mContext.getResources().getColor(colorResource));
        }
    }

    @Override
    public abstract long getItemId(int position);

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);
}
