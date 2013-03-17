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

package com.ninetwozero.battlechat.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.abstractions.AbstractListFragment;
import com.ninetwozero.battlechat.activities.ChatActivity;
import com.ninetwozero.battlechat.activities.MainActivity;
import com.ninetwozero.battlechat.adapters.UserListAdapter;
import com.ninetwozero.battlechat.comparators.UserComparator;
import com.ninetwozero.battlechat.datatypes.User;
import com.ninetwozero.battlechat.http.BattleChatClient;
import com.ninetwozero.battlechat.http.HttpUris;
import com.ninetwozero.battlechat.interfaces.FragmentCallback;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactListFragment extends AbstractListFragment implements FragmentCallback {

    public final static String TAG = "ContactListFragment";

    private Bundle mSavedInstanceState;
    private ReloadTask mReloadTask;
    private boolean mIsDoublePane;
    private int mSelectedPosition = 0;

    public ContactListFragment() {}

    @Override
    public void onResume() {
        super.onResume();
        setupFromSavedInstance(mSavedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_list_contacts, container, false);
        setupListView(view);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        final UserListAdapter adapter = (UserListAdapter) getListView().getAdapter();
        final ArrayList<User> friends = (ArrayList<User>) adapter.getItems();
        out.putParcelableArrayList("friends", friends);

        super.onSaveInstanceState(out);
    }

    private void setupFromSavedInstance(Bundle in) {
        if( in == null ){
            return;
        }
        final List<User> friends = in.getParcelableArrayList("friends");
        final UserListAdapter adapter = (UserListAdapter) getListView().getAdapter();
        adapter.setItems(friends);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        User user = (User) view.getTag();
        if( user != null ) {
            if( mIsDoublePane ) {
                setSelectedRow(position);
                ((MainActivity) getSherlockActivity()).openChat(user);
            } else {
                startActivity( new Intent(getSherlockActivity(), ChatActivity.class).putExtra(ChatFragment.EXTRA_USER, user) );
            }
        }
    }

    private void setSelectedRow(int position) {
        mSelectedPosition = position;

    }

    private void setupListView(View v) {
        final ListView listView = (ListView) v.findViewById(android.R.id.list);
        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        listView.setAdapter(new UserListAdapter(getSherlockActivity()));
    }

    public void reload(boolean show) {
        if( mReloadTask == null ) {
            mReloadTask = new ReloadTask(show);
            mReloadTask.execute();
        }
    }

    private class ReloadTask extends AsyncTask<Void, Void, Boolean> {
        private String mMessage;
        private List<User> mItems;
        private boolean mShow;

        public ReloadTask(boolean show) {
            mShow = show;
        }

        @Override
        protected void onPreExecute() {
            if( getListView().getCount() == 0 || mShow ) {
                toggleLoading(true);
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                JSONObject result = BattleChatClient.post(
                    HttpUris.Chat.FRIENDS,
                    new BasicNameValuePair("post-check-sum", BattleChat.getSession().getChecksum())
                );

                if( result.has("error") ) {
                    mMessage = result.getString("error");
                    return false;
                }

                mItems = getUsersFromJson(result);
                return true;
            } catch( Exception ex ) {
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if( result ) {
                ((UserListAdapter)getListView().getAdapter()).setItems(mItems);
            } else {
                showToast(mMessage);
                //logoutFromWebsite();
            }
            toggleLoading(false);
            mReloadTask = null;
        }

        private List<User> getUsersFromJson(JSONObject result) throws JSONException {
            JSONArray friends = result.getJSONArray("friendscomcenter");
            JSONObject friend;
            JSONObject presence;
            List<User> users = new ArrayList<User>();

            int numFriends = friends.length();
            int numPlaying = 0;
            int numOnline = 0;
            int numOffline = 0;

            if( numFriends > 0 ) {
                User user = null;
                for( int i = 0; i < numFriends; i++ ) {
                    friend = friends.optJSONObject(i);
                    presence = friend.getJSONObject("presence");

                    if( presence.getBoolean("isPlaying") ) {
                        user = new User(
                                Long.parseLong(friend.getString("userId")),
                                friend.getString("username"),
                                User.PLAYING
                        );
                        numPlaying++;
                    } else if( presence.getBoolean("isOnline") ) {
                        user = new User(
                                Long.parseLong(friend.getString("userId")),
                                friend.getString("username"),
                                User.ONLINE
                        );
                        numOnline++;
                    } else {
                        user = new User(
                                Long.parseLong(friend.getString("userId")),
                                friend.getString("username"),
                                User.OFFLINE
                        );
                        numOffline++;
                    }
                    users.add(user);
                }

                if (numPlaying > 0) {
                    users.add(new User(0, getString(R.string.label_playing), User.PLAYING));
                }

                if (numOnline > 0) {
                    users.add(new User(0, getString(R.string.label_online), User.ONLINE));
                }

                if (numOffline > 0) {
                    users.add(new User(0, getString(R.string.label_offline), User.OFFLINE));
                }
                Collections.sort(users, new UserComparator());
            }
            return users;
        }

        private void toggleLoading(boolean isLoading) {
            final View view = getSherlockActivity().findViewById(R.id.status);
            view.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    public void setDoublePane(boolean value) {
        mIsDoublePane = value;
    }
}
