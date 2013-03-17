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
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.abstractions.AbstractListFragment;
import com.ninetwozero.battlechat.adapters.MessageListAdapter;
import com.ninetwozero.battlechat.datatypes.Message;
import com.ninetwozero.battlechat.datatypes.User;
import com.ninetwozero.battlechat.http.BattleChatClient;
import com.ninetwozero.battlechat.http.HttpHeaders;
import com.ninetwozero.battlechat.http.HttpUris;
import com.ninetwozero.battlechat.interfaces.FragmentCallback;
import com.ninetwozero.battlechat.misc.Keys;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChatFragment extends AbstractListFragment implements FragmentCallback {

    public static final String TAG = "ChatFragment";
    public static final String EXTRA_USER = "user";

    private User mUser;
    private long mChatId = 0;
    private boolean mFirstRun = true;
    private long mLatestMessageTimestamp = 0;

    private ReloadTask mReloadTask;
    private SendMessageTask mSendMessageTask;
    private Timer mTimer;
    private SoundPool mSoundPool;
    private int mSoundId = 0;

    public ChatFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        setupListView(view);
        Log.d(TAG, "HELLO222222222222 ==============================================================>");
        setupForm(view);
        Log.d(TAG, "HELLO333333333333 ==============================================================>");
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        final MessageListAdapter adapter = (MessageListAdapter) getListView().getAdapter();
        final ArrayList<Message> messages = (ArrayList<Message>) adapter.getItems();

        out.putLong("chatId", mChatId);
        out.putParcelable("user", mUser);
        out.putParcelableArrayList("messages", messages);
        out.putBoolean("firstRun", mFirstRun);

        super.onSaveInstanceState(out);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupFromSavedInstance(mSavedInstanceState);
        startTimer();
        setupSound();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new CloseTask().execute(mChatId);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
        stopSound();
    }

    private void setupFromSavedInstance(Bundle in) {
        if( in == null ){
            return;
        }
        final long chatId = in.getLong("chatId");
        final User user = in.getParcelable(ChatFragment.EXTRA_USER);
        final List<Message> friends = in.getParcelableArrayList("messages");
        final MessageListAdapter adapter = (MessageListAdapter) getListView().getAdapter();
        final boolean firstRun = in.getBoolean("firstRun");

        mChatId = chatId;
        mUser = user;
        adapter.setItems(friends);
        mFirstRun = firstRun;
    }

    private void stopTimer() {
        if( mTimer != null ) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public void reload(boolean show) {
        if( mReloadTask == null && mUser != null ) {
            mReloadTask = new ReloadTask(show);
            mReloadTask.execute();
        }
    }

    public void setData(Intent data) {
        long id = (mUser == null)? 0 : mUser.getId();
        mUser = data.getParcelableExtra(ChatFragment.EXTRA_USER);

        clearInput();
        setTitle(mUser);
        reload(mUser.getId() != id);
    }

    private void setupForm(final View view) {
        view.findViewById(R.id.button_send).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSend();
                }
            }
        );
    }

    private void setupListView(final View view) {
        final ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        listView.setAdapter(new MessageListAdapter(getSherlockActivity(), BattleChat.getSession().getUsername()));
    }

    private void startTimer() {
        mTimer = new Timer();
        mTimer.schedule(
            new TimerTask() {
                @Override
                public void run() {
                    getSherlockActivity().runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    reload(false);
                                }
                            }
                    );
                }
            },
            0,
            mSharedPreferences.getInt(Keys.Settings.CHAT_INTERVAL, 25)*1000 //--> ms
        );
    }

    private void onSend() {
        if( mSendMessageTask != null ) {
            showToast(R.string.msg_chat_send_multiple_error);
            return;
        }

        EditText field = (EditText) getView().findViewById(R.id.input_message);
        String message = field.getText().toString();
        if( message.length() == 0 ) {
            field.setError(getString(R.string.msg_chat_send_message_error));
            field.requestFocus();
            return;
        }

        mSendMessageTask = new SendMessageTask();
        mSendMessageTask.execute(message, String.valueOf(mChatId), BattleChat.getSession().getChecksum());
    }

    private void toggleButton(boolean enable) {
        final Button button = (Button) getView().findViewById(R.id.button_send);
        if( enable ) {
            button.setText(R.string.label_send);
            button.setEnabled(true);
        } else {
            button.setText(R.string.label_sending);
            button.setEnabled(false);
        }

    }

    private void clearInput() {
       ((TextView) getView().findViewById(R.id.input_message)).setText("");
    }

    private class ReloadTask extends AsyncTask<Void, Void, Boolean> {
        private List<Message> mMessages = new ArrayList<Message>();
        private boolean mShow = true;

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
                        HttpUris.Chat.MESSAGES.replace("{USER_ID}", String.valueOf(mUser.getId())),
                        new BasicNameValuePair("post-check-sum", BattleChat.getSession().getChecksum())
                );

                if( result.has("chatId") ) {
                    JSONObject chatObject = result.getJSONObject("chat");
                    mMessages = getMessagesFromJSON(chatObject);
                    mChatId = result.getLong("chatId");
                    return true;
                }
            } catch( Exception ex ) {
                ex.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if( result ) {
                if( shouldNotifyUser(mMessages) ) {
                    notifyWithSound();
                }
                ((MessageListAdapter) getListView().getAdapter()).setItems(mMessages);
                scrollToBottom();
            } else {
                showToast(R.string.msg_chat_reload_fail);
                getView().findViewById(R.id.button_send).setEnabled(false);
                stopTimer();
            }
            toggleLoading(false);
            mReloadTask = null;
        }

        public boolean shouldNotifyUser(List<Message> messages) {
            Message message = null;
            for (int curr = messages.size() - 1, min = (curr > 5 ? curr - 5 : 0); curr > min; curr--) {
                message = messages.get(curr);
                if( message.getTimestamp() < mLatestMessageTimestamp ) {
                    return false;
                }

                if( message.getUsername().equals(mUser.getUsername()) && mLatestMessageTimestamp < message.getTimestamp() ) {
                    mFirstRun = (mLatestMessageTimestamp  == 0);
                    mLatestMessageTimestamp  = message.getTimestamp();
                    return true;
                }
            }
            return false;
        }


        private List<Message> getMessagesFromJSON(JSONObject chatObject) throws JSONException {
            List<Message> results = new ArrayList<Message>();
            JSONArray messages = chatObject.getJSONArray("messages");
            JSONObject message = null;

            for( int i = 0, max = messages.length(); i < max; i++ ) {
                message = messages.getJSONObject(i);
                results.add(
                        new Message(
                                Long.parseLong(message.getString("chatId")),
                                message.getString("message"),
                                message.getString("fromUsername"),
                                message.getLong("timestamp")
                        )
                );
            }
            return results;
        }
    }

    private class SendMessageTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            showToast(R.string.msg_chat_sending_message);
            toggleButton(false);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                JSONObject result = BattleChatClient.post(
                        HttpUris.Chat.SEND,
                        HttpHeaders.Post.AJAX,
                        new BasicNameValuePair("message", params[0]),
                        new BasicNameValuePair("chatId", params[1]),
                        new BasicNameValuePair("post-check-sum", params[2])
                );
                return !result.has("error");
            } catch( Exception ex ) {
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if( result ) {
                showToast(R.string.msg_message_ok);
                clearInput();
                reload(false);
            } else {
                showToast(R.string.msg_message_fail);
            }
            mSendMessageTask = null;
            toggleButton(true);
        }
    }

    private void notifyWithSound() {
        if( mSharedPreferences.getBoolean(Keys.Settings.BEEP_ON_NEW, true) ) {
            playSound();
        }
    }

    public void scrollToBottom() {
        getListView().post(
            new Runnable() {
                @Override
                public void run() {
                    getListView().setSelection(getListView().getAdapter().getCount() - 1);
                }
            }
        );
    }

    private void toggleLoading(boolean isLoading) {
        final View view = getView().findViewById(R.id.status);
        view.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void setupSound() {
        mSoundPool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 0);
        mSoundId = mSoundPool.load(getSherlockActivity(), R.raw.notification, 1);
    }

    private void stopSound() {
        if( mSoundPool == null ) {
            return;
        }
        mSoundPool.release();
        mSoundPool = null;
        mSoundId = 0;
    }

    private void playSound() {
        if( mSoundPool == null ) {
            return;
        }
        mSoundPool.play(mSoundId, 1.0f, 1.0f, 1, 0, 1);
    }

    private class CloseTask extends AsyncTask<Long, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Long... params) {
            try {
                BattleChatClient.get(
                    HttpUris.Chat.CLOSE.replace("{CHAT_ID}", String.valueOf(params[0])),
                    HttpHeaders.Get.AJAX
                );
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    private void setTitle(final User user) {
        if( user == null ) {
            return;
        }
        getSherlockActivity().setTitle(String.format(getString(R.string.text_chat_title), user.getUsername()));
    }
}
