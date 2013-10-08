package org.qii.weiciyuan.ui.userinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.View;

import org.qii.weiciyuan.R;
import org.qii.weiciyuan.bean.UserBean;
import org.qii.weiciyuan.support.lib.AppFragmentPagerAdapter;
import org.qii.weiciyuan.support.lib.MyViewPager;
import org.qii.weiciyuan.support.lib.SwipeRightToCloseOnGestureListener;
import org.qii.weiciyuan.ui.interfaces.AbstractAppActivity;
import org.qii.weiciyuan.ui.interfaces.IUserInfo;
import org.qii.weiciyuan.ui.main.MainTimeLineActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * User: qii
 * Date: 13-6-21
 */
public class UserTimeLineActivity extends AbstractAppActivity implements IUserInfo {
    private UserBean bean;
    private String token;
    private MyViewPager mViewPager;
    private GestureDetector gestureDetector;


    @Override
    public UserBean getUser() {
        return bean;
    }

    public String getToken() {
        return token;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_with_bg_layout);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        token = getIntent().getStringExtra("token");
        bean = (UserBean) getIntent().getParcelableExtra("user");
        getActionBar().setTitle(bean.getScreen_name());
//        if (getSupportFragmentManager().findFragmentByTag(StatusesByIdTimeLineFragment.class.getName()) == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(android.R.id.content, new StatusesByIdTimeLineFragment(getUser(), token), StatusesByIdTimeLineFragment.class.getName())
//                    .commit();
//        }
        buildViewPager();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(this, MainTimeLineActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
        }
        return false;
    }

    private void buildViewPager() {
        mViewPager = (MyViewPager) findViewById(R.id.viewpager);
        TimeLinePagerAdapter adapter = new TimeLinePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        gestureDetector = new GestureDetector(UserTimeLineActivity.this
                , new SwipeRightToCloseOnGestureListener(UserTimeLineActivity.this, mViewPager));
        mViewPager.setGestureDetector(this, gestureDetector);
        getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
    }

    class TimeLinePagerAdapter extends AppFragmentPagerAdapter {

        List<Fragment> list = new ArrayList<Fragment>();
        List<String> tagList = new ArrayList<String>();

        public TimeLinePagerAdapter(FragmentManager fm) {
            super(fm);
            Fragment fragment = fm.findFragmentByTag(StatusesByIdTimeLineFragment.class.getName());
            if (fragment == null) {
                fragment = new StatusesByIdTimeLineFragment(getUser(), getToken());
            }
            list.add(fragment);
            tagList.add(StatusesByIdTimeLineFragment.class.getName());
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        protected String getTag(int position) {
            return tagList.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }
}
