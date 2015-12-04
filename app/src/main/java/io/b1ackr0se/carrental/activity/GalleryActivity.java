package io.b1ackr0se.carrental.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.b1ackr0se.carrental.R;
import io.b1ackr0se.carrental.adapter.PreviewAdapter;
import io.b1ackr0se.carrental.model.Product;

public class GalleryActivity extends Activity {

    @Bind(R.id.name)TextView nameTextView;
    @Bind(R.id.page)TextView pageTextView;
    @Bind(R.id.view_pager)ViewPager viewPager;
    @Bind(R.id.close_button)ImageView closeButton;

    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("product");
        product = (Product) bundle.getSerializable("product");

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (product != null) {
            nameTextView.setText(product.getName());
            String text = "1 of " + product.getImages().size();
            pageTextView.setText(text);
            final PreviewAdapter adapter = new PreviewAdapter(this, product.getImages());
            viewPager.setAdapter(adapter);
            viewPager.setPageMargin(convertToPx(20));
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    String text = position+1+" of " + adapter.getCount();
                    pageTextView.setText(text);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }


    public int convertToPx(int dp) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (dp * scale + 0.5f);
    }
}
