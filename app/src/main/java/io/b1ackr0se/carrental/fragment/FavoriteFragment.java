package io.b1ackr0se.carrental.fragment;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
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
import io.b1ackr0se.carrental.activity.ProductDetailActivity;
import io.b1ackr0se.carrental.adapter.FavoriteAdapter;
import io.b1ackr0se.carrental.application.CustomApplication;
import io.b1ackr0se.carrental.model.Favorite;
import io.b1ackr0se.carrental.model.Product;
import io.b1ackr0se.carrental.util.Utility;
import io.b1ackr0se.carrental.view.SimpleDividerItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment implements FavoriteAdapter.OnFavoriteItemClickListener{

    @Bind(R.id.recycler_view)RecyclerView recyclerView;

    private Context context;
    private FavoriteAdapter adapter;
    private ArrayList<Favorite> favoriteList;


    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        ButterKnife.bind(this, view);

        context = getActivity();

        favoriteList = new ArrayList<>();

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Favorite");
        query.whereEqualTo("UserId", CustomApplication.userId);
        ((MainActivity) context).showLoading();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (int i = objects.size() - 1; i >= 0; i--) {
                        ParseObject object = objects.get(i);
                        String id = object.getObjectId();
                        int productId = object.getInt("ProductId");
                        Favorite favorite = new Favorite();
                        favorite.setId(id);
                        favorite.setProductId(productId);
                        favoriteList.add(favorite);
                    }
                    final ParseQuery<ParseObject> productQuery = new ParseQuery<>("Product").whereMatchesKeyInQuery("productId", "ProductId", query);

                    productQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            ((MainActivity) context).hideLoading(false);
                            if (e == null) {
                                for (int i = objects.size() - 1, j = 0; i >= 0; i--, j++) {
                                    ParseObject object = objects.get(i);
                                    int id = object.getInt("productId");
                                    String name = object.getString("Name");
                                    String description = object.getString("Description");
                                    int price = object.getInt("Price");
                                    int category = object.getInt("CategoryId");
                                    ArrayList<String> list = new ArrayList<>();
                                    String image = object.getParseFile("Image").getUrl();
                                    String imageUrl = Uri.parse(image).toString();
                                    list.add(imageUrl);
                                    Product product = new Product();
                                    product.setId(id);
                                    product.setName(name);
                                    product.setDescription(description.replaceAll("(?m)^[ \t]*\r?\n", ""));
                                    product.setPrice(price);
                                    product.setCategory(category);
                                    product.setImages(list);

                                    for (int x = 0; x < favoriteList.size(); x++) {
                                        Favorite favorite = favoriteList.get(x);
                                        if (favorite.getProductId() == id) {
                                            favorite.setProduct(product);
                                            break;
                                        }
                                    }
                                }
                                adapter = new FavoriteAdapter(context, favoriteList);
                                adapter.setOnClickListener(FavoriteFragment.this);
                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
                                recyclerView.setAdapter(adapter);
                            } else {
                                ((MainActivity) context).hideLoading(true);
                            }
                        }
                    });
                } else {
                    ((MainActivity) context).hideLoading(true);
                }
            }
        });

        return view;
    }

    @Override
    public void onItemClick(View view, Favorite favorite) {
        final Product product = favorite.getProduct();
        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .progress(true, 100)
                .content("Gathering information")
                .cancelable(false)
                .build();
        dialog.show();
        ParseQuery<ParseObject> getImageQuery = ParseQuery.getQuery("Image");
        getImageQuery.whereEqualTo("ProductId", product.getId());
        getImageQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null) {
                    ArrayList<String> list = new ArrayList<>();
                    for (int i = 0; i < objects.size(); i++) {
                        String image = objects.get(i).getParseFile("File").getUrl();
                        String imageUrl = Uri.parse(image).toString();
                        list.add(imageUrl);
                    }
                    product.setImages(list);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(dialog.isShowing()) dialog.dismiss();
                            Intent intent = new Intent(context, ProductDetailActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("product", product);
                            intent.putExtra("product", bundle);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                        }
                    }, 300);
                } else {
                    if(dialog.isShowing()) dialog.dismiss();
                    Utility.showMessage(context, "Failed to get product information");
                }
            }
        });
    }
}
