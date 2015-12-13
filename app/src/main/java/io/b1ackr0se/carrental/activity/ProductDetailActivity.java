package io.b1ackr0se.carrental.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.carrental.R;
import io.b1ackr0se.carrental.application.CustomApplication;
import io.b1ackr0se.carrental.model.Product;
import io.b1ackr0se.carrental.util.Utility;
import io.b1ackr0se.carrental.view.CustomImageView;

public class ProductDetailActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.collapsing_toolbar_layout)CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.image)CustomImageView image;
    @Bind(R.id.fab)FloatingActionButton floatingActionButton;
    @Bind(R.id.description)TextView descriptionTextView;
    @Bind(R.id.price)TextView priceTextView;
    @Bind(R.id.favorite_panel)View favoritePanel;
    @Bind(R.id.favorite_image)ImageView favoriteImage;
    @Bind(R.id.favorite_text)TextView favoriteText;

    private Product product;

    private SeekBar seekBar;
    private TextView totalCost;
    private TextView dayIndicator;

    private boolean favorite = false;

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

        priceTextView.setText(Utility.showCurrency(product.getPrice()) + " VND per day");

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

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CustomApplication.isLoggedIn) {
                    if (CustomApplication.userType == CustomApplication.TYPE_ADMIN)
                        Utility.showMessage(ProductDetailActivity.this, "You cannot perform this action as an admin");
                    else if (CustomApplication.userType == CustomApplication.TYPE_USER)
                        showOrderDialog();
                } else
                    Utility.showMessage(ProductDetailActivity.this, "You have to login to perform this action");
            }
        });

        favoriteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CustomApplication.isLoggedIn && CustomApplication.userType == CustomApplication.TYPE_USER) {
                    if (favorite) {
                        favorite = false;
                        favoriteImage.setImageResource(R.drawable.star_unfav);
                        favoriteText.setText("Add to favorite");
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Favorite");
                        query.whereEqualTo("UserId", CustomApplication.userId).whereEqualTo("ProductId", product.getId());
                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if (e == null) {
                                    object.deleteEventually();
                                } else {
                                    Utility.showMessage(ProductDetailActivity.this, "An error happened.");
                                }
                            }
                        });
                    } else {
                        favorite = true;
                        favoriteImage.setImageResource(R.drawable.star_fav);
                        favoriteText.setText("Remove from favorite");
                        ParseObject object = new ParseObject("Favorite");
                        object.put("UserId", CustomApplication.userId);
                        object.put("ProductId", product.getId());
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Utility.showMessage(ProductDetailActivity.this, "An error happened.");
                                }
                            }
                        });
                    }
                }
            }
        });

        checkFavoriteStatus();

        setUpToolbar();
    }

    private void checkFavoriteStatus() {
        if (CustomApplication.isLoggedIn) {
            if (CustomApplication.userType == CustomApplication.TYPE_ADMIN)
                favoritePanel.setVisibility(View.GONE);
            else if (CustomApplication.userType == CustomApplication.TYPE_USER) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Favorite");
                query.whereEqualTo("UserId", CustomApplication.userId).whereEqualTo("ProductId", product.getId());
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            favorite = true;
                            favoriteImage.setImageResource(R.drawable.star_fav);
                            favoriteText.setText("Remove from favorite");
                        } else {
                            final int statusCode = e.getCode();
                            if (statusCode == ParseException.OBJECT_NOT_FOUND) {
                                favorite = false;
                                favoriteImage.setImageResource(R.drawable.star_unfav);
                                favoriteText.setText("Add to favorite");
                            }

                        }
                        favoritePanel.setVisibility(View.VISIBLE);
                    }
                });
            }
        } else
            favoritePanel.setVisibility(View.GONE);


    }

    private void showOrderDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Confirm your order")
                .customView(R.layout.custom_order_dialog, true)
                .positiveText("Send order")
                .negativeText("Cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        ParseObject object = new ParseObject("Order");
                        object.put("ProductId", product.getId());
                        object.put("UserId", CustomApplication.userId);
                        object.put("Price", (seekBar.getProgress()+1)*product.getPrice());
                        object.put("Days", seekBar.getProgress()+1);
                        object.put("Status", 0);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e != null) {
                                    e.printStackTrace();
                                    Utility.showMessage(ProductDetailActivity.this, "There is an error while processing your order, please try again later.");
                                } else {
                                    Utility.showMessage(ProductDetailActivity.this, "Your order has been saved, and will be reviewed shortly.");
                                }
                            }
                        });
                    }
                }).build();

        seekBar = (SeekBar) dialog.getCustomView().findViewById(R.id.day_seekbar);
        totalCost = (TextView) dialog.getCustomView().findViewById(R.id.total_cost);
        dayIndicator = (TextView) dialog.getCustomView().findViewById(R.id.day_indicator);
        TextView productName = (TextView) dialog.getCustomView().findViewById(R.id.name);
        TextView productPrice = (TextView) dialog.getCustomView().findViewById(R.id.price);

        seekBar.setProgress(0);
        seekBar.setMax(9);

        productName.setText(product.getName());
        productPrice.setText(Utility.showCurrency(product.getPrice()) + " VND");
        totalCost.setText(Utility.showCurrency(product.getPrice()) + " VND");
        dayIndicator.setText(String.valueOf(seekBar.getProgress()+1));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int progress = seekBar.getProgress()+1;
                dayIndicator.setText(String.valueOf(progress));
                int cost = product.getPrice()*progress;
                totalCost.setText(Utility.showCurrency(cost) + " VND");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        dialog.show();
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
