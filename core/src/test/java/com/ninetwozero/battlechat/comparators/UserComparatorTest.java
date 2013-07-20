package com.ninetwozero.battlechat.comparators;

import com.ninetwozero.battlechat.datatypes.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

// TODO: We have to remove the Parcelable parts of User in able to run this at the moment (as regular JUNIT).
// FIXME: When Junit supports individual bars per assertion, I'll refactor the ten methods into one with a loop

public class UserComparatorTest {
    
	private static List<User> mUsersAutomatic;
	private static List<User> mUsersManual;
	
	public UserComparatorTest() {		
		User user0 = new User(0, "Playing", User.PLAYING_MP);
		User user1 = new User(100, "Jarl Ank", User.PLAYING_COOP);
		User user2 = new User(101, "Jarl Pank", User.PLAYING_ORIGIN);
		User user3 = new User(0, "Online", User.ONLINE_WEB);
		User user5 = new User(201, "Sarl Ank", User.ONLINE_GAME);
		User user4 = new User(200, "Jarl Pank", User.ONLINE_MOBILE);
		User user6 = new User(0, "Offline", User.OFFLINE);
		User user7 = new User(300, "Amk", User.OFFLINE);
		User user8 = new User(301, "Ank", User.OFFLINE);

		mUsersAutomatic = new ArrayList<User>();
		mUsersManual = new ArrayList<User>();
		
		mUsersAutomatic.add(user2);
		mUsersAutomatic.add(user7);
		mUsersAutomatic.add(user0);
		mUsersAutomatic.add(user1);
		mUsersAutomatic.add(user5);
		mUsersAutomatic.add(user8);
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
		
		Collections.sort(mUsersAutomatic, new UserComparator());
	}
	
	@Test
	public void testThatAlgorithmWorksOnIndex0() throws Exception {
		assertEquals(mUsersManual.get(0), mUsersAutomatic.get(0));
	}
	
	@Test
	public void testThatAlgorithmWorksOnIndex1() throws Exception {
		assertEquals(mUsersManual.get(1), mUsersAutomatic.get(1));
	}
	

	@Test
	public void testThatAlgorithmWorksOnIndex2() throws Exception {
		assertEquals(mUsersManual.get(2), mUsersAutomatic.get(2));
	}
	

	@Test
	public void testThatAlgorithmWorksOnIndex3() throws Exception {
		assertEquals(mUsersManual.get(3), mUsersAutomatic.get(3));
	}
	

	@Test
	public void testThatAlgorithmWorksOnIndex4() throws Exception {
		assertEquals(mUsersManual.get(4), mUsersAutomatic.get(4));
	}
	

	@Test
	public void testThatAlgorithmWorksOnIndex5() throws Exception {
		assertEquals(mUsersManual.get(5), mUsersAutomatic.get(5));
	}
	

	@Test
	public void testThatAlgorithmWorksOnIndex6() throws Exception {
		assertEquals(mUsersManual.get(6), mUsersAutomatic.get(6));
	}
	

	@Test
	public void testThatAlgorithmWorksOnIndex7() throws Exception {
		assertEquals(mUsersManual.get(7), mUsersAutomatic.get(7));
	}

	@Test
	public void testThatAlgorithmWorksOnIndex8() throws Exception {
		assertEquals(mUsersManual.get(8), mUsersAutomatic.get(8));
	}
	
}
