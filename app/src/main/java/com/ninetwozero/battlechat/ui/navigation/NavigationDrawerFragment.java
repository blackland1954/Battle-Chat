package com.ninetwozero.battlechat.ui.navigation;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.base.ui.BaseLoadingListFragment;
import com.ninetwozero.battlechat.comparators.UserComparator;
import com.ninetwozero.battlechat.datatypes.Session;
import com.ninetwozero.battlechat.datatypes.TriggerRefreshEvent;
import com.ninetwozero.battlechat.factories.UrlFactory;
import com.ninetwozero.battlechat.json.chat.ComCenterRequest;
import com.ninetwozero.battlechat.json.chat.User;
import com.ninetwozero.battlechat.misc.Keys;
import com.ninetwozero.battlechat.network.IntelLoader;
import com.ninetwozero.battlechat.network.Result;
import com.ninetwozero.battlechat.network.SimpleGetRequest;
import com.ninetwozero.battlechat.ui.chat.ChatFragment;
import com.ninetwozero.battlechat.utils.BusProvider;
import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.List;

public class NavigationDrawerFragment extends BaseLoadingListFragment {
    private static final int ID_LOADER = 2000;
    private static final String STATE_SELECTED_ID = "selectedId";
    private static final String KEY_DISPLAY_OVERLAY = "manual_reload";

    private boolean shouldReloadOnAttach = false;
    private boolean shouldReloadWithLoadingScreen = false;
    private int currentSelectedId = 0;
    private ListView listView;
    private NavigationDrawerCallbacks callbacks;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedState) {
        final View baseView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        initialize(baseView, savedState);
        return baseView;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        try {
            callbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }

        if (shouldReloadOnAttach) {
            shouldReloadWithLoadingScreen = false;
            shouldReloadOnAttach = false;
            reload(shouldReloadWithLoadingScreen);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        outState.putInt(STATE_SELECTED_ID, currentSelectedId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();

        // TODO: Load friends from DB && selectItemFromState(currentSelectedId); ?
        BusProvider.getInstance().register(this);
        reload(true);
    }

    @Subscribe
    public void onReceivedRefreshEvent(final TriggerRefreshEvent event) {
        reload(event.getType() == TriggerRefreshEvent.Type.MANUAL);
    }

    private void reload(final boolean shouldDisplayLoadingOverlay) {
        if (getActivity() == null) {
            shouldReloadOnAttach = true;
            shouldReloadWithLoadingScreen = shouldDisplayLoadingOverlay;
            return;
        }

        final Bundle arguments = getArguments() == null ? new Bundle() : getArguments();
        arguments.putBoolean(KEY_DISPLAY_OVERLAY, shouldDisplayLoadingOverlay);

        getLoaderManager().restartLoader(ID_LOADER, arguments, this);
    }

    private void initialize(final View view, final Bundle state) {
        setupDataFromState(state);
        setupRegularViews(view);
        setupListView(view);
    }

    private void setupDataFromState(final Bundle state) {
        if (state != null) {
            currentSelectedId = state.getInt(STATE_SELECTED_ID);
        } else {
            currentSelectedId = 0;
        }
    }

    private void setupRegularViews(final View view) {
        ((TextView) view.findViewById(R.id.login_name)).setText(Session.getUsername());
    }

    private void setupListView(final View view) {
        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    private void storePositionState(final int id) {
        currentSelectedId = id;
    }

    private void selectItemFromState(final int id) {
    }

    @Override
    public void onListItemClick(final ListView listView, final View view, final int position, final long id) {
        final User user = ((NavigationDrawerListAdapter) getListAdapter()).getItem(position);
        if (user == null) {
            return;
        }

        final SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        preferences.putString("recent_chat_userid", user.getId());
        preferences.putString("recent_chat_username", user.getUsername());
        preferences.apply();

        final FragmentManager manager = getFragmentManager();
        final Fragment fragment = manager.findFragmentByTag("ChatListFragment");
        if (fragment == null) {
            final Bundle data = new Bundle();
            data.putString(Keys.Profile.ID, user.getId());
            data.putString(Keys.Profile.USERNAME, user.getUsername());
            data.putString(Keys.Profile.GRAVATAR_HASH, user.getGravatarHash());

            final FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.activity_root, ChatFragment.newInstance(data), "ChatListFragment");
            transaction.commit();
        } else {
            BusProvider.getInstance().post(user);
        }

        callbacks.onNavigationDrawerItemSelected(
            position,
            user.getUsername(),
            getString(NavigationDrawerListAdapter.resolveOnlineStatus(user.getPresenceType()))
        );
    }

    private void showProgress(final boolean show) {
        final View view = getView();
        if (view == null) {
            return;
        }
        view.findViewById(R.id.wrap_loading).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public Loader<Result> onCreateLoader(final int id, final Bundle args) {
        final ListView listView = getListView();
        if (listView == null || listView.getCount() == 0 || args.getBoolean(KEY_DISPLAY_OVERLAY)) {
            showProgress(true);
        }
        return new IntelLoader<ComCenterRequest>(
            getActivity(),
            new SimpleGetRequest(UrlFactory.buildFriendListURL()),
            ComCenterRequest.class
        );
    }

    @Override
    protected void onLoadSuccess(final int loader, final Result result) {
        if (loader == ID_LOADER) {
            final ComCenterRequest comCenter = (ComCenterRequest) result.getData();
            final List<User> friends = comCenter.getInformation().getFriends();
            if (friends == null) {
                showToast(R.string.msg_unable_to_get_friends);
                return;
            }
            Collections.sort(friends, new UserComparator());
            updateListAdapter(friends);
            ((NavigationDrawerListAdapter) getListAdapter()).setItems(friends);
        }
        showProgress(false);
    }

    protected void updateListAdapter(final List<User> friends) {
        final NavigationDrawerListAdapter adapter = (NavigationDrawerListAdapter) getListAdapter();
        if (adapter == null) {
            setListAdapter(new NavigationDrawerListAdapter(getActivity(), friends));
        } else {
            adapter.setItems(friends);
        }
    }

    public static interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelected(final int position, final String title);
        void onNavigationDrawerItemSelected(final int position, final String title, final String subtitle);

        boolean isDrawerOpen();
    }
}