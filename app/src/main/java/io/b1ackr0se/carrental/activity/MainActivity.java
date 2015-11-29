package io.b1ackr0se.carrental.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.carrental.R;
import io.b1ackr0se.carrental.fragment.ProductFragment;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.fab)FloatingActionButton fab;
    @Bind(R.id.progress_bar)ProgressBar progressBar;
    @Bind(R.id.empty)TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

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

    public void showFab() {
        fab.show();
    }

    public void hideFab() {
        fab.hide();
    }

}
