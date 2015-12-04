package io.b1ackr0se.carrental.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.carrental.R;
import io.b1ackr0se.carrental.fragment.ProductFragment;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.progress_bar)ProgressWheel progressBar;
    @Bind(R.id.empty)TextView emptyView;
    @Bind(R.id.navigation_view)NavigationView navigationView;
    @Bind(R.id.drawer_layout)DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        setupDrawerLayout();

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

}
