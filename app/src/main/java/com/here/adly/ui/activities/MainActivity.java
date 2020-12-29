/*
 * Copyright (C) 2019-2020 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package com.here.adly.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.here.adly.ui.fragments.FavoritesFragment;
import com.here.adly.ui.fragments.FilterDialogFragment;
import com.here.adly.ui.fragments.MapFragment;
import com.here.adly.preferences.SessionManager;
import com.here.adly.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {

    private SessionManager sessionManager;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private TextView tvNavHeaderName;
    private boolean toolbarNavigationListenerIsRegistered;
    private NavigationView navigationView;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        sessionManager = new SessionManager(this);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        tvNavHeaderName = headerView.findViewById(R.id.nav_header_name);
        mAuth = FirebaseAuth.getInstance();
        tvNavHeaderName.setText(mAuth.getCurrentUser().getDisplayName());
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        displayHomeUpOrHamburger();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_items, menu);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        System.out.println(item.getItemId());
        switch (item.getItemId()) {
            case R.id.nav_home:
                break;

            case R.id.nav_favorites:
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, new FavoritesFragment()).commit();
                break;
            case R.id.nav_about:
            case R.id.nav_settings:
            case R.id.nav_contact:
                item.setChecked(false);
                item.setCheckable(false);
                break;
            case R.id.nav_logout:
                sessionManager.removeSession();
                mAuth.signOut();
                startLoginActivity();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_item_filter:
                FragmentManager fm = getSupportFragmentManager();
               FilterDialogFragment filterDialogFragment = FilterDialogFragment.newInstance("Some Title");
                filterDialogFragment.show(fm, "fragment_filter");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayHomeUpOrHamburger() {
        boolean upBtn = getSupportFragmentManager().getBackStackEntryCount() > 0;
        if (upBtn) {
           toolbar.getMenu().clear();
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.setDrawerIndicatorEnabled(false);
            if (!toolbarNavigationListenerIsRegistered) {
                toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getSupportFragmentManager().popBackStackImmediate();
                    }
                });
                toolbarNavigationListenerIsRegistered = true;
            }
        } else {
            MenuInflater menuInflater = getMenuInflater();
            getSupportActionBar().setTitle("");
           menuInflater.inflate(R.menu.toolbar_items, toolbar.getMenu());
            navigationView.setCheckedItem(R.id.nav_home);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.setToolbarNavigationClickListener(null);
            toolbarNavigationListenerIsRegistered = false;
        }


}

    private void startLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackStackChanged() {
        displayHomeUpOrHamburger();
    }
}


