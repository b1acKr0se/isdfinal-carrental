package io.b1ackr0se.carrental.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.carrental.R;
import io.b1ackr0se.carrental.model.Product;
import io.b1ackr0se.carrental.view.CustomImageView;

public class ProductDetailActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.collapsing_toolbar_layout)CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.image)CustomImageView image;
    @Bind(R.id.fab)FloatingActionButton floatingActionButton;
    @Bind(R.id.description)TextView descriptionTextView;
    @Bind(R.id.price)TextView priceTextView;

    private Product product;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_detail);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("product");
        product = (Product) bundle.getSerializable("product");

        descriptionTextView.setText(product.getDescription());

        priceTextView.setText(product.getPrice() + " VND per day");

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailActivity.this, GalleryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", product);
                intent.putExtra("product", bundle);
                startActivity(intent);
            }
        });

        setUpToolbar();
    }

    private void setUpToolbar() {
        collapsingToolbarLayout.setTitle(product.getName());
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));

        Glide.with(this).load(product.getFirstImage())
                .asBitmap()
                .into(new BitmapImageViewTarget(image) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        super.onResourceReady(bitmap, anim);
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette palette) {
                                applyPalette(palette);
                            }
                        });
                    }
                });
    }

    private void applyPalette(Palette palette) {
        int colorPrimaryDark = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
        collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(colorPrimary));
        collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(colorPrimaryDark));

        updateFabColor(floatingActionButton, palette);

    }

    private void updateFabColor(FloatingActionButton floatingActionButton, Palette palette) {
        int lightVibrantColor = palette.getLightVibrantColor(ContextCompat.getColor(this, android.R.color.white));
        int vibrantColor = palette.getVibrantColor(ContextCompat.getColor(this, R.color.colorAccent));

        GradientDrawable background = (GradientDrawable) priceTextView.getBackground();
        background.setColor(vibrantColor);

        floatingActionButton.setRippleColor(lightVibrantColor);
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
