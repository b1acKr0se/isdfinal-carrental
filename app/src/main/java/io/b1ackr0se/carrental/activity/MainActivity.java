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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.pnikosis.materialishprogress.ProgressWheel;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.carrental.R;
import io.b1ackr0se.carrental.application.CustomApplication;
import io.b1ackr0se.carrental.fragment.AdminProductFragment;
import io.b1ackr0se.carrental.fragment.CustomerOrderFragment;
import io.b1ackr0se.carrental.fragment.FavoriteFragment;
import io.b1ackr0se.carrental.fragment.ManageOrderFragment;
import io.b1ackr0se.carrental.fragment.ManageUserFragment;
import io.b1ackr0se.carrental.fragment.NotificationFragment;
import io.b1ackr0se.carrental.fragment.ProductFragment;
import io.b1ackr0se.carrental.fragment.ReportFragment;
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

        CustomApplication.userType = sharedPreferences.getInt("type", -1);

        CustomApplication.userId = sharedPreferences.getString("id", null);

        validateLoginCredentials();

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
                setLayout(CustomApplication.userType);
            }
        });

        setLayout(CustomApplication.userType);
    }

    private void setTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void validateLoginCredentials() {
        if (CustomApplication.userId != null) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .progress(true, 100)
                    .content("Validating your login credentials...")
                    .cancelable(false)
                    .build();
            dialog.show();
            ParseQuery<ParseObject> query = new ParseQuery<>("User");
            query.getInBackground(CustomApplication.userId, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (dialog.isShowing())
                        dialog.dismiss();
                    if (e == null) {
                        if (object.getInt("Status") == CustomApplication.STATUS_BANNED) {
                            removeCredentials();
                            Utility.showMessage(MainActivity.this, "Your account was banned by the admin!");
                        } else {
                            Utility.showMessage(MainActivity.this, "Login successfully!");
                        }
                        setupDrawerLayout();
                    } else {
                        removeCredentials();
                        setupDrawerLayout();
                        Utility.showMessage(MainActivity.this, "An error happened when validating your login!");
                    }
                }
            });
        } else
            setupDrawerLayout();
    }

    private void loadProduct() {
        ProductFragment fragment = new ProductFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit();
        setTitle("Products");
    }

    private void loadNotification() {
        NotificationFragment fragment = new NotificationFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit();
        setTitle("Notifications");
    }

    private void loadCustomerOrder() {
        CustomerOrderFragment fragment = new CustomerOrderFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit();
        setTitle("Order");
    }

    private void loadFavorite() {
        FavoriteFragment fragment = new FavoriteFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit();
        setTitle("Favorite");
    }

    private void loadAdminProduct() {
        AdminProductFragment fragment = new AdminProductFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit();
        setTitle("Product");
    }

    private void loadUserList() {
        ManageUserFragment fragment = new ManageUserFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit();
        setTitle("Users");
    }

    private void loadManageOrder() {
        ManageOrderFragment fragment = new ManageOrderFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit();
        setTitle("Orders");
    }

    private void loadReport() {
        ReportFragment fragment = new ReportFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit();
        setTitle("Report for this week");
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
                    case R.id.drawer_notification:
                        loadNotification();
                        break;

                    case R.id.drawer_favorite:
                        loadFavorite();
                        break;

                    case R.id.drawer_cart:
                        loadCustomerOrder();
                        break;

                    case R.id.drawer_settings:
                        break;

                    case R.id.drawer_manage_user:
                        loadUserList();
                        break;

                    case R.id.drawer_manage_product:
                        loadAdminProduct();
                        break;

                    case R.id.drawer_manage_orders:
                        loadManageOrder();
                        break;

                    case R.id.drawer_view_report:
                        loadReport();
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
                String id = data.getStringExtra("id");
                int type = data.getIntExtra("type", CustomApplication.TYPE_USER);

                View headerView = navigationView.getHeaderView(0);
                TextView nameTextView = (TextView) headerView.findViewById(R.id.name);
                nameTextView.setText("Welcome, " + name);
                nameTextView.setVisibility(View.VISIBLE);

                saveCredentials(name, email, id, type);

                setLayout(type);

                loginControl.setVisibility(View.GONE);
                logoutButton.setVisibility(View.VISIBLE);

            }
        }
    }

    public String getLoggedInName() {
        return sharedPreferences.getString("name", null);
    }

    private void saveCredentials(String name, String email, String id, int type) {
        CustomApplication.isLoggedIn = true;
        CustomApplication.userType = type;
        CustomApplication.userId = id;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("id", id);
        editor.putInt("type", type);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();

    }

    private void removeCredentials() {
        CustomApplication.userType = -1;
        CustomApplication.userId = null;
        CustomApplication.isLoggedIn = false;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("name");
        editor.remove("email");
        editor.remove("id");
        editor.remove("type");
        editor.putBoolean("isLoggedIn", false);
        editor.apply();
    }

    private void setLayout(int type) {
        Menu menu = navigationView.getMenu();
        switch (type) {
            case CustomApplication.TYPE_USER:
                menu.findItem(R.id.drawer_explore).setVisible(true);
                menu.findItem(R.id.drawer_favorite).setVisible(true);
                menu.findItem(R.id.drawer_cart).setVisible(true);
                menu.findItem(R.id.drawer_notification).setVisible(true);
                menu.findItem(R.id.drawer_manage_product).setVisible(false);
                menu.findItem(R.id.drawer_manage_user).setVisible(false);
                menu.findItem(R.id.drawer_manage_orders).setVisible(false);
                menu.findItem(R.id.drawer_view_report).setVisible(false);
                loadProduct();
                break;
            case CustomApplication.TYPE_ADMIN:
                menu.findItem(R.id.drawer_explore).setVisible(false);
                menu.findItem(R.id.drawer_favorite).setVisible(false);
                menu.findItem(R.id.drawer_cart).setVisible(false);
                menu.findItem(R.id.drawer_notification).setVisible(false);
                menu.findItem(R.id.drawer_manage_product).setVisible(true);
                menu.findItem(R.id.drawer_manage_user).setVisible(true);
                menu.findItem(R.id.drawer_manage_orders).setVisible(true);
                menu.findItem(R.id.drawer_view_report).setVisible(true);
                loadAdminProduct();
                break;
            default:
                menu.findItem(R.id.drawer_explore).setVisible(true);
                menu.findItem(R.id.drawer_favorite).setVisible(false);
                menu.findItem(R.id.drawer_cart).setVisible(false);
                menu.findItem(R.id.drawer_notification).setVisible(false);
                menu.findItem(R.id.drawer_manage_product).setVisible(false);
                menu.findItem(R.id.drawer_manage_user).setVisible(false);
                menu.findItem(R.id.drawer_manage_orders).setVisible(false);
                menu.findItem(R.id.drawer_view_report).setVisible(false);
                loadProduct();
                break;
        }
    }
}
