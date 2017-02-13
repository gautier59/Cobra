package com.example.gauti.cobra;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.gauti.cobra.fragments.HomeFragment;
import com.example.gauti.cobra.fragments.LocalisationFragment;
import com.example.gauti.cobra.fragments.SettingsFragment;
import com.example.gauti.cobra.fragments.history.HistoryFragment;
import com.example.gauti.cobra.global.ApplicationSharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Constants
    // --------------------------------------------------------------------------------------------
    public static final String LATITUDE = "lattitude";
    public static final String LONGITUDE = "longitude";
    public static final String DATE = "date";
    public static final String SPEED = "speed";

    private static final int PERMISSIONS_REQUEST_ALL = 1;

    // Private fields
    // --------------------------------------------------------------------------------------------
    private Bundle bundle;

    // Views
    // --------------------------------------------------------------------------------------------
    @Bind(R.id.tv_history)
    TextView mTvHistory;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.nav_view)
    NavigationView mNavigationView;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    // Life cycle
    // --------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        String[] PERMISSIONS = {
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION};
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_ALL);
        } else {
            launchFirstFragment(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        bundle = intent.getExtras();
        if (bundle != null && bundle.getString(DATE) != null) {
            if ((ApplicationSharedPreferences.getInstance(this).getDateSms().isEmpty())
                    || (!ApplicationSharedPreferences.getInstance(this).getDateSms().equals(bundle.getString(DATE)))) {
                onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.nav_localisation));
            }
        }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            fragment = new HomeFragment();
            showHistoryButton(false);
            mToolbar.setTitle(getResources().getString(R.string.menu_home));
        } else if (id == R.id.nav_localisation) {
            fragment = new LocalisationFragment();
            showHistoryButton(true);
            mToolbar.setTitle(getResources().getString(R.string.menu_localisation));
        } else if (id == R.id.nav_settings) {
            fragment = new SettingsFragment();
            showHistoryButton(false);
            mToolbar.setTitle(getResources().getString(R.string.menu_settings));
        } else if (id == R.id.nav_history) {
            fragment = new HistoryFragment();
            showHistoryButton(false);
            mToolbar.setTitle(getResources().getString(R.string.menu_history));
        }

        if (bundle != null) {
            fragment.setArguments(bundle);
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null &&
                permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) !=
                        PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void launchFirstFragment(boolean permissionGranted) {
        if (permissionGranted) {
            if (ApplicationSharedPreferences.getInstance(this.getApplicationContext()).getSettingsNumero() != null) {
                mNavigationView.getMenu().getItem(0).setChecked(true);
                onNavigationItemSelected(mNavigationView.getMenu().getItem(0));
            } else {
                mNavigationView.getMenu().getItem(2).setChecked(true);
                onNavigationItemSelected(mNavigationView.getMenu().getItem(2));
            }
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.permission_no_granted_title))
                    .setMessage(getResources().getString(R.string.permission_no_granted_text))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            System.exit(0);
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ALL: {
                launchFirstFragment(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
            }
        }
    }

    private void showHistoryButton(boolean show) {
        if (show) {
            mTvHistory.setVisibility(View.VISIBLE);
        } else {
            mTvHistory.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.tv_history)
    void showOrHideLegend() {
        EventBus.getDefault().post(new HistoryClickEvent());
    }

    // EventBus
    // --------------------------------------------------------------------------------------------
    public class HistoryClickEvent {
    }
}
