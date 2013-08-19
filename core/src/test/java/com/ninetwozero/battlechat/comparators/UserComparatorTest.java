package com.ninetwozero.battlechat.comparators;

import com.ninetwozero.battlechat.datatypes.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

// TODO: We have to remove the Parcelable parts of User in able to run this at the moment (as regular JUNIT).
// FIXME: When Junit supports individual bars per assertion, I'll refactor the ten methods into one with a loop

public class UserComparatorTest {
    
	private static List<User> mUsersAutomatic;
	private static List<User> mUsersManual;
	
	public UserComparatorTest() {		
		User user0 = new User(0, "Playing", User.PLAYING_MP);
		User user1 = new User(100, "Jarl Ank", User.PLAYING_MP);
		User user2 = new User(101, "Jarl Pank", User.PLAYING_MP);
        User user3 = new User(0, "Online", User.ONLINE_WEB);
        User user4 = new User(200, "Darl Ank", User.ONLINE_WEB);
        User user5 = new User(201, "Jarl Stank", User.AWAY_WEB);
        User user6 = new User(202, "Sarl Pank", User.ONLINE_WEB);
        User user7 = new User(203, "Zarl Zank", User.AWAY_WEB);
		User user8 = new User(0, "Offline", User.OFFLINE);
		User user9 = new User(300, "Amk", User.OFFLINE);
		User user10 = new User(301, "Ank", User.OFFLINE);

		mUsersAutomatic = new ArrayList<User>();
		mUsersManual = new ArrayList<User>();
		
		mUsersAutomatic.add(user2);
		mUsersAutomatic.add(user7);
		mUsersAutomatic.add(user0);
        mUsersAutomatic.add(user10);
		mUsersAutomatic.add(user1);
		mUsersAutomatic.add(user5);
		mUsersAutomatic.add(user8);
        mUsersAutomatic.add(user9);
		mUsersAutomatic.add(user3);
		mUsersAutomatic.add(user6);
		mUsersAutomatic.add(user4);

		mUsersManual.add(user0);
		mUsersManual.add(user1);
		mUsersManual.add(user2);
		mUsersManual.add(user3);
		mUsersManual.add(user4);
		mUsersManual.add(user5);
		mUsersManual.add(user6);
		mUsersManual.add(user7);
        mUsersManual.add(user8);
        mUsersManual.add(user9);
        mUsersManual.add(user10);
		
		Collections.sort(mUsersAutomatic, new UserComparator());
	}
	
	@Test
	public void testThatAlgorithmWorks() {
        for( int i = 0, max = mUsersAutomatic.size(); i < max; i++ ) {
            Assert.assertEquals(mUsersManual.get(i), mUsersAutomatic.get(i));
        }
	}
}
