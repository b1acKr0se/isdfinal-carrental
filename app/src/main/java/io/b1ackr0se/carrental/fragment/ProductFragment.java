package io.b1ackr0se.carrental.fragment;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.etsy.android.grid.StaggeredGridView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.carrental.R;
import io.b1ackr0se.carrental.activity.MainActivity;
import io.b1ackr0se.carrental.adapter.ProductAdapter;
import io.b1ackr0se.carrental.model.Product;

public class ProductFragment extends Fragment {

    @Bind(R.id.grid_view)StaggeredGridView staggeredGridView;

    private Context context;
    private ArrayList<Product> products;
    private ProductAdapter adapter;

    public ProductFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        ButterKnife.bind(this, view);

        context = getActivity();

        loadProduct();

        staggeredGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (mLastFirstVisibleItem < firstVisibleItem)
                    ((MainActivity) context).hideFab();
                if (mLastFirstVisibleItem > firstVisibleItem)
                    ((MainActivity) context).showFab();
                mLastFirstVisibleItem = firstVisibleItem;
            }
        });

        return view;
    }

    private void loadProduct() {
        ((MainActivity) context).showLoading();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ((MainActivity) context).hideLoading(false);
                    products = new ArrayList<>();
                    System.out.println("Number of object " + objects.size());
                    for (int i = 0; i < objects.size(); i++) {
                        ParseObject object = objects.get(i);
                        int id = object.getInt("productId");
                        String name = object.getString("Name");
                        String description = object.getString("Description");
                        String image = object.getParseFile("Image").getUrl();
                        String imageUrl = Uri.parse(image).toString();
                        int price = object.getInt("Price");
                        int category = object.getInt("CategoryId");
                        Product product = new Product(id, name, description, imageUrl, price, category);
                        products.add(product);
                    }
                    adapter = new ProductAdapter(context, R.layout.item_product, products);
                    staggeredGridView.setAdapter(adapter);
                } else {
                    e.printStackTrace();
                    ((MainActivity) context).hideLoading(true);
                }
            }
        });
    }

}
