package io.b1ackr0se.carrental.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

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
import io.b1ackr0se.carrental.activity.ProductDetailActivity;
import io.b1ackr0se.carrental.adapter.ProductListAdapter;
import io.b1ackr0se.carrental.model.Product;

public class ProductFragment extends Fragment {

    @Bind(R.id.grid_view)StaggeredGridView staggeredGridView;

    private Context context;
    private ArrayList<Product> products;
    private ProductListAdapter adapter;

    private int count = 0;

    public ProductFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        ButterKnife.bind(this, view);

        context = getActivity();

        setHasOptionsMenu(true);

        loadProduct();

        staggeredGridView.setOnItemClickListener(new AbsListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = adapter.getItem(i);
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", product);
                intent.putExtra("product", bundle);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

            }
        });

        return view;
    }

    private void loadProduct() {
        count = 0;
        ((MainActivity) context).showLoading();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    products = new ArrayList<>();
                    for (int i = 0; i < objects.size(); i++) {
                        ParseObject object = objects.get(i);
                        int id = object.getInt("productId");
                        String name = object.getString("Name");
                        String description = object.getString("Description");
                        int price = object.getInt("Price");
                        int category = object.getInt("CategoryId");
                        Product product = new Product();
                        product.setId(id);
                        product.setName(name);
                        product.setDescription(description.replaceAll("(?m)^[ \t]*\r?\n", ""));
                        product.setPrice(price);
                        product.setCategory(category);
                        products.add(product);
                    }
                    for (int i = 0; i < products.size(); i++) {
                        final int j = i;
                        ParseQuery<ParseObject> getImageQuery = ParseQuery.getQuery("Image");
                        getImageQuery.whereEqualTo("ProductId", products.get(i).getId()).findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    ArrayList<String> list = new ArrayList<>();
                                    for (int i = 0; i < objects.size(); i++) {
                                        String image = objects.get(i).getParseFile("File").getUrl();
                                        String imageUrl = Uri.parse(image).toString();
                                        list.add(imageUrl);
                                    }
                                    products.get(j).setImages(list);
                                    count++;
                                    if (count == products.size()) {
                                        adapter = new ProductListAdapter(context, R.layout.item_product, products);
                                        staggeredGridView.setAdapter(adapter);
                                        ((MainActivity) context).hideLoading(false);
                                        staggeredGridView.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    ((MainActivity) context).hideLoading(true);
                                }
                            }
                        });
                    }
                } else {
                    ((MainActivity) context).hideLoading(true);
                }
            }
        });

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_product_list, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView actionSearchView = (SearchView) searchItem.getActionView();
        actionSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }
        });

        actionSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                adapter.getFilter().filter("");
                return false;
            }
        });
    }
}
