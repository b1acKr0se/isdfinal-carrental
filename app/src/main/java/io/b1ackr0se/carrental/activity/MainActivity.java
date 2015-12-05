package io.b1ackr0se.carrental.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.carrental.R;
import io.b1ackr0se.carrental.application.CustomApplication;
import io.b1ackr0se.carrental.fragment.ProductFragment;
import io.b1ackr0se.carrental.util.Utility;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.progress_bar)ProgressWheel progressBar;
    @Bind(R.id.empty)TextView emptyView;
    @Bind(R.id.navigation_view)NavigationView navigationView;
    @Bind(R.id.drawer_layout)DrawerLayout drawerLayout;
    @Bind(R.id.login_control)View loginControl;
    @Bind(R.id.loginButton)TextView loginButton;
    @Bind(R.id.registerButton)TextView registerButton;
    @Bind(R.id.logoutButton)TextView logoutButton;

    private SharedPreferences sharedPreferences;
    private static final String LOGIN_CREDENTIALS = "login_credentials";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences(LOGIN_CREDENTIALS, MODE_PRIVATE);

        CustomApplication.isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        setupDrawerLayout();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("tab", 0);
                startActivityForResult(intent, 1);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("tab", 1);
                startActivityForResult(intent, 1);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View headerView = navigationView.getHeaderView(0);
                TextView nameTextView = (TextView) headerView.findViewById(R.id.name);
                nameTextView.setVisibility(View.GONE);
                removeCredentials();
                loginControl.setVisibility(View.VISIBLE);
                logoutButton.setVisibility(View.GONE);
                Utility.showMessage(MainActivity.this, "You have logged out!");
            }
        });

        if (savedInstanceState == null) {
            loadProduct();
        }
    }

    private void setTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void loadProduct() {
        ProductFragment fragment = new ProductFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit();
        setTitle("Products");
    }

    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.INVISIBLE);
    }

    public void hideLoading(boolean empty) {
        progressBar.setVisibility(View.INVISIBLE);
        if(empty)
            emptyView.setVisibility(View.VISIBLE);
        else
            emptyView.setVisibility(View.INVISIBLE);
    }

    private void setupDrawerLayout() {

        if(!CustomApplication.isLoggedIn) {
            loginControl.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
        } else {
            loginControl.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            View headerView = navigationView.getHeaderView(0);
            TextView nameTextView = (TextView) headerView.findViewById(R.id.name);
            nameTextView.setText("Welcome, " + getLoggedInName());
            nameTextView.setVisibility(View.VISIBLE);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.drawer_explore:
                        loadProduct();
                        break;
                    case R.id.drawer_favorite:
                        break;
                    case R.id.drawer_cart:
                        break;
                    case R.id.drawer_logout:
                        break;
                    case R.id.drawer_settings:
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
//                                startActivity(intent);
//                            }
//                        }, 200);
                        break;
                }
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {

                String name = data.getStringExtra("name");
                String email = data.getStringExtra("email");

                View headerView = navigationView.getHeaderView(0);
                TextView nameTextView = (TextView) headerView.findViewById(R.id.name);
                nameTextView.setText("Welcome, " + name);
                nameTextView.setVisibility(View.VISIBLE);

                saveCredentials(name, email);

                loginControl.setVisibility(View.GONE);
                logoutButton.setVisibility(View.VISIBLE);

            }
        }
    }

    private String getLoggedInName() {
        return sharedPreferences.getString("name", null);
    }

    private void saveCredentials(String name, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }

    private void removeCredentials() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("name");
        editor.remove("email");
        editor.putBoolean("isLoggedIn", false);
        editor.apply();
    }
}
