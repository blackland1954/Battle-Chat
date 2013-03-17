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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockListFragment;

abstract public class AbstractListFragment extends SherlockListFragment {
	private Toast mToast;
    protected SharedPreferences mSharedPreferences;
    protected Bundle mSavedInstanceState;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setRetainInstance(true);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
        mSavedInstanceState = icicle;
    }

	public void showToast(final int resource) {
		showToast(getString(resource));
	}
	
	public void showToast(final String text) {
		if( mToast != null ) { 
			mToast.cancel();
			mToast = null;
		}
		
		mToast = Toast.makeText(getSherlockActivity(), text, Toast.LENGTH_SHORT);
		mToast.show();
	}
}
