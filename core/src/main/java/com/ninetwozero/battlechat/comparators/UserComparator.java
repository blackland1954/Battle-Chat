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

package com.ninetwozero.battlechat.comparators;

import com.ninetwozero.battlechat.datatypes.User;

import java.util.Comparator;

public class UserComparator implements Comparator<User> {
    public int compare(User u1, User u2) {
    	if( u1.isPlaying() && !u2.isPlaying() || (u1.isOnline() || u1.isAway()) && u2.isOffline() ) {
    		return -1;
    	} else if( u1.isOffline() && (u2.isOnline() || u2.isAway()) || !u1.isPlaying() && u2.isPlaying() ) {
    		return 1;
    	} else if( u1.getId() == u2.getId() ) {
    		return 0;
    	} else {
    		if( u1.getId() == 0 ) {
    			return -1;
    		} else if( u2.getId() == 0 ) {
    			return 1; 
    		} else {
        		return u1.getUsername().compareToIgnoreCase(u2.getUsername());
    		}
    	}
    }
}
