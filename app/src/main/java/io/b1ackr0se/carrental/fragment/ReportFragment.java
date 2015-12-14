package io.b1ackr0se.carrental.fragment;


import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import io.b1ackr0se.carrental.adapter.HotProductAdapter;
import io.b1ackr0se.carrental.application.CustomApplication;
import io.b1ackr0se.carrental.model.Order;
import io.b1ackr0se.carrental.model.Product;
import io.b1ackr0se.carrental.util.Utility;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment {

    @Bind(R.id.product)TextView productTextView;
    @Bind(R.id.recycler_view)RecyclerView recyclerView;
    @Bind(R.id.number_of_order)TextView orderTextView;
    @Bind(R.id.order_done)TextView doneOrder;
    @Bind(R.id.order_pending)TextView pendingOrder;
    @Bind(R.id.order_denied)TextView deniedOrder;
    @Bind(R.id.income)TextView incomeTextView;
    @Bind(R.id.member)TextView memberTextView;
    @Bind(R.id.content)View content;

    private Context context;
    private ArrayList<Order> orders;
    private ArrayList<Product> products;

    private boolean isRetrieveProductFinished = false, isRetrieveNewMember = false;

    private int done = 0, pending = 0, denied = 0, newMember = 0, income = 0;


    public ReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        ButterKnife.bind(this, view);

        context = getActivity();

        ((MainActivity) context).showLoading();

        loadOrder();

        loadUser();

        return view;
    }

    private void loadOrder() {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Order");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {
                    if (objects.size() > 0) {
                        orders = new ArrayList<>();
                        for (int i = 0; i < objects.size(); i++) {
                            if(Utility.isDateThisWeek(objects.get(i).getLong("Date"))) {
                                ParseObject object = objects.get(i);
                                Order order = new Order();
                                order.setId(object.getObjectId());
                                order.setDays(object.getInt("Days"));
                                order.setPrice(object.getInt("Price"));
                                order.setStatus(object.getInt("Status"));
                                order.setDate(object.getLong("Date"));
                                order.setProductId(object.getInt("ProductId"));
                                order.setUserId(object.getString("UserId"));
                                orders.add(order);
                            }
                        }

                        for (int j = 0; j < orders.size(); j++) {
                            if(orders.get(j).getStatus() == CustomApplication.ORDER_STATUS_DONE) {
                                done++;
                                income += orders.get(j).getPrice();
                            } else if(orders.get(j).getStatus() == CustomApplication.ORDER_STATUS_DENIED)
                                denied++;
                            else {
                                pending++;
                                income += orders.get(j).getPrice();
                            }
                        }

                        loadProduct(query);
                    }
                }
            }
        });
    }

    private void loadProduct(ParseQuery<ParseObject> query) {
        ParseQuery<ParseObject> productQuery = new ParseQuery<>("Product").whereMatchesKeyInQuery("productId", "ProductId", query);
        productQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() != 0) {
                        products = new ArrayList<>();
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

                            for (int x = 0; x < orders.size(); x++) {
                                Order order = orders.get(x);
                                if (order.getProductId() == id) {
                                    order.setProduct(product);
                                }
                            }
                            if (products.size() == 0) products.add(product);
                            else {
                                if (!products.contains(product))
                                    products.add(product);
                            }
                        }
                    }
                    isRetrieveProductFinished = true;
                    if(isRetrieveNewMember) setUpData();
                } else
                    ((MainActivity) context).hideLoading(true);
            }
        });
    }

    private void loadUser() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.whereEqualTo("Type", CustomApplication.TYPE_USER);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (int i = 0; i < objects.size(); i++) {
                            if (Utility.isDateThisWeek(objects.get(i).getLong("JoinDate")))
                                newMember++;
                        }
                    }
                    isRetrieveNewMember = true;
                    if(isRetrieveProductFinished) setUpData();
                } else
                    ((MainActivity) context).hideLoading(true);
            }
        });
    }

    private void setUpData() {
        ((MainActivity) context).hideLoading(false);
        productTextView.setText(String.valueOf(products.size()));
        orderTextView.setText(String.valueOf(orders.size()));
        doneOrder.setText(String.valueOf(done));
        pendingOrder.setText(String.valueOf(pending));
        deniedOrder.setText(String.valueOf(denied));
        incomeTextView.setText(Utility.showCurrency(income) + "$");
        memberTextView.setText(String.valueOf(newMember));

        if(products.size()>0) {
            HotProductAdapter adapter = new HotProductAdapter(context, products);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
        }

        content.setVisibility(View.VISIBLE);
    }
}
