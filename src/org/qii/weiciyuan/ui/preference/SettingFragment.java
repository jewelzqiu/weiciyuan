package org.qii.weiciyuan.ui.preference;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ListView;

import org.qii.weiciyuan.R;
import org.qii.weiciyuan.support.utils.Utility;
import org.qii.weiciyuan.ui.main.MainTimeLineActivity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by jewelzqiu on 10/12/13.
 */
public class SettingFragment extends ListFragment implements MainTimeLineActivity.ScrollableListFragment {

    /**
     * The starting request code given out to preference framework.
     */
    private static final int FIRST_REQUEST_CODE = 100;
    private static final int MSG_BIND_PREFERENCES = 0;
    private PreferenceManager mPreferenceManager;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MSG_BIND_PREFERENCES:
                    bindPreferences();
                    break;
            }
        }
    };
    private ListView lv;
    private int xmlId;

    public SettingFragment(int xmlId) {
        this.xmlId = xmlId;
    }

    // must be provided
    public SettingFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle b) {
        postBindPreferences();
        return lv;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewParent p = lv.getParent();
        if (p != null)
            ((ViewGroup) p).removeView(lv);
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        if (b != null)
            xmlId = b.getInt("xml");
        mPreferenceManager = onCreatePreferenceManager();
        lv = (ListView) LayoutInflater.from(getActivity()).inflate(
                R.layout.preference_list_content, null);
//        lv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        addPreferencesFromResource(xmlId);
        postBindPreferences();
//        ((OnPreferenceAttachedListener) getActivity()).onPreferenceAttached(
//                getPreferenceScreen(), xmlId);
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            Method m = PreferenceManager.class
                    .getDeclaredMethod("dispatchActivityStop");
            m.setAccessible(true);
            m.invoke(mPreferenceManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lv = null;
        try {
            Method m = PreferenceManager.class
                    .getDeclaredMethod("dispatchActivityDestroy");
            m.setAccessible(true);
            m.invoke(mPreferenceManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("xml", xmlId);
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Method m = PreferenceManager.class.getDeclaredMethod(
                    "dispatchActivityResult", int.class, int.class,
                    Intent.class);
            m.setAccessible(true);
            m.invoke(mPreferenceManager, requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Posts a message to bind the preferences to the list view.
     * <p/>
     * Binding late is preferred as any custom preference types created in
     * {@link #onCreate(Bundle)} are able to have their views recycled.
     */
    private void postBindPreferences() {
        if (mHandler.hasMessages(MSG_BIND_PREFERENCES))
            return;
        mHandler.obtainMessage(MSG_BIND_PREFERENCES).sendToTarget();
    }

    private void bindPreferences() {
        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.bind(lv);
        }
    }

    /**
     * Creates the {@link PreferenceManager}.
     *
     * @return The {@link PreferenceManager} used by this activity.
     */
    private PreferenceManager onCreatePreferenceManager() {
        try {
            Constructor<PreferenceManager> c = PreferenceManager.class
                    .getDeclaredConstructor(Activity.class, int.class);
            c.setAccessible(true);
            PreferenceManager preferenceManager = c.newInstance(
                    this.getActivity(), FIRST_REQUEST_CODE);
            return preferenceManager;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the {@link PreferenceManager} used by this activity.
     *
     * @return The {@link PreferenceManager}.
     */
    public PreferenceManager getPreferenceManager() {
        return mPreferenceManager;
    }

    /**
     * Gets the root of the preference hierarchy that this activity is showing.
     *
     * @return The {@link PreferenceScreen} that is the root of the preference
     * hierarchy.
     */
    public PreferenceScreen getPreferenceScreen() {
        try {
            Method m = PreferenceManager.class
                    .getDeclaredMethod("getPreferenceScreen");
            m.setAccessible(true);
            return (PreferenceScreen) m.invoke(mPreferenceManager);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sets the root of the preference hierarchy that this activity is showing.
     *
     * @param preferenceScreen The root {@link PreferenceScreen} of the preference hierarchy.
     */
    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod(
                    "setPreferences", PreferenceScreen.class);
            m.setAccessible(true);
            boolean result = (Boolean) m.invoke(mPreferenceManager,
                    preferenceScreen);
            if (result && preferenceScreen != null) {
                postBindPreferences();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds preferences from activities that match the given {@link Intent}.
     *
     * @param intent The {@link Intent} to query activities.
     */
    public void addPreferencesFromIntent(Intent intent) {
        throw new RuntimeException("too lazy to include this bs");
    }

    /**
     * Inflates the given XML resource and adds the preference hierarchy to the
     * current preference hierarchy.
     *
     * @param preferencesResId The XML resource ID to inflate.
     */
    public void addPreferencesFromResource(int preferencesResId) {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod(
                    "inflateFromResource", Context.class, int.class,
                    PreferenceScreen.class);
            m.setAccessible(true);
            PreferenceScreen prefScreen = (PreferenceScreen) m.invoke(
                    mPreferenceManager, getActivity(), preferencesResId,
                    getPreferenceScreen());
            setPreferenceScreen(prefScreen);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds a {@link android.preference.Preference} based on its key.
     *
     * @param key The key of the preference to retrieve.
     * @return The {@link android.preference.Preference} with the key, or null.
     * @see PreferenceGroup#findPreference(CharSequence)
     */
    public Preference findPreference(CharSequence key) {
        if (mPreferenceManager == null) {
            return null;
        }
        return mPreferenceManager.findPreference(key);
    }

    @Override
    public void scrollToTop() {
        Utility.stopListViewScrollingAndScrollToTop(getListView());
    }

    public interface OnPreferenceAttachedListener {
        public void onPreferenceAttached(PreferenceScreen root, int xmlId);
    }

    public void buildActionBarAndViewPagerTitles() {
        ((MainTimeLineActivity) getActivity()).setCurrentFragment(this);

        if (Utility.isDevicePort()) {
            ((MainTimeLineActivity) getActivity()).setTitle(getString(R.string.setting));
            getActivity().getActionBar().setIcon(R.drawable.ic_menu_settings);
        } else {
            ((MainTimeLineActivity) getActivity()).setTitle(getString(R.string.setting));
            getActivity().getActionBar().setIcon(R.drawable.ic_launcher);
        }

        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActivity().getActionBar().removeAllTabs();
    }

}

