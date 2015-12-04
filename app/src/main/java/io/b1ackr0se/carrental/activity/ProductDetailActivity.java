package io.b1ackr0se.carrental.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.carrental.R;
import io.b1ackr0se.carrental.adapter.PreviewAdapter;
import io.b1ackr0se.carrental.model.Product;

public class ProductDetailActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.view_pager)ViewPager viewPager;
    @Bind(R.id.price)TextView priceTextView;
    @Bind(R.id.description)TextView descTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("product");
        Product product = (Product) bundle.getSerializable("product");

        if(product!=null) {
            getSupportActionBar().setTitle(product.getName());
            getSupportActionBar().setSubtitle(product.getPrice() + " VND per day");
            priceTextView.setText(product.getPrice() + " VND/day");
            descTextView.setText(product.getDescription().trim());
            PreviewAdapter adapter = new PreviewAdapter(this, product.getImages());
            viewPager.setAdapter(adapter);
        }
    }

}
