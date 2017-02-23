package com.example.kurt.kitakasama;

import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.sql.Connection;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    int numOfTabs = 2;
    CharSequence Titles[] = {"Start Session", "Track Session"};
    MySQLiteModel localDb;
    LocalUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        localDb = new MySQLiteModel(getBaseContext());
        if(localDb.getUserCnt() == 0) {
            user = new LocalUser("Kurt Aquino", "09273597974");
            user.setExtremeMsg("Extreme halp!");
            user.setNeutralMsg("Neutral halp!");
            user.setNegativeMsg("Negative halp!");
            user.setCheckMsg("How u?");
            user.setConfirmMsg("I gotchu");
            localDb.addUser(user);
        }

        if(localDb.getTrackerCnt() == 0) {
            LocalTracker tracker1 = new LocalTracker("Luigi Ramos", "09154731120");
            localDb.addTracker(tracker1);
            LocalTracker tracker2 = new LocalTracker("Seaver Choy", "09271273470");
            localDb.addTracker(tracker2);
            LocalTracker tracker3 = new LocalTracker("Miss Courtney", "09996482819");
            localDb.addTracker(tracker3);
        }

        for(int i = 0; i < localDb.getListTrackers().size(); i ++)
        {
            System.out.println(localDb.getListTrackers().get(i).getTrackerName());
        }

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles ,numOfTabs);

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        tabs = (SlidingTabLayout) findViewById(R.id.tabs);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        tabs.setViewPager(pager);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public LocationManager getLocationManager(){
        return (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tracker) {

        } else if (id == R.id.nav_add_tracker) {

        } else if (id == R.id.nav_user_profile) {

        } else if (id == R.id.nav_user_message) {

        } else if (id == R.id.nav_tracker_message) {

        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
