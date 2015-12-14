package io.b1ackr0se.carrental.fragment;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.carrental.R;
import io.b1ackr0se.carrental.activity.MainActivity;
import io.b1ackr0se.carrental.activity.ProductDetailActivity;
import io.b1ackr0se.carrental.adapter.CustomerOrderAdapter;
import io.b1ackr0se.carrental.application.CustomApplication;
import io.b1ackr0se.carrental.model.Order;
import io.b1ackr0se.carrental.model.Product;
import io.b1ackr0se.carrental.util.Utility;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerOrderFragment extends Fragment implements CustomerOrderAdapter.OnOrderClickListener{

    @Bind(R.id.recycler_view)RecyclerView recyclerView;

    private SeekBar seekBar;
    private TextView totalCost;
    private TextView dayIndicator;

    private CustomerOrderAdapter adapter;

    private ArrayList<Order> orderList;

    private Context context;

    private Order order;

    public CustomerOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_order, container, false);

        ButterKnife.bind(this, view);

        context = getActivity();

        orderList = new ArrayList<>();

        final ParseQuery<ParseObject> orderQuery = new ParseQuery<>("Order");
        orderQuery.whereEqualTo("UserId", CustomApplication.userId);

        ((MainActivity) context).showLoading();

        orderQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (int i = objects.size() - 1; i >= 0; i--) {
                        ParseObject object = objects.get(i);
                        Order order = new Order();
                        order.setId(object.getObjectId());
                        order.setDays(object.getInt("Days"));
                        order.setPrice(object.getInt("Price"));
                        order.setStatus(object.getInt("Status"));
                        order.setProductId(object.getInt("ProductId"));
                        orderList.add(order);
                    }
                    final ParseQuery<ParseObject> productQuery = new ParseQuery<>("Product").whereMatchesKeyInQuery("productId", "ProductId", orderQuery);

                    productQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                                if(objects.size() == 0) ((MainActivity) context).hideLoading(true);
                                else {
                                    ((MainActivity) context).hideLoading(false);
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

                                        for (int x = 0; x < orderList.size(); x++) {
                                            Order order = orderList.get(x);
                                            if (order.getProductId() == id) {
                                                order.setProduct(product);
                                                break;
                                            }
                                        }
                                    }
                                    adapter = new CustomerOrderAdapter(context, orderList);
                                    adapter.setOnOrderClickListener(CustomerOrderFragment.this);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    recyclerView.setAdapter(adapter);
                                }
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
    public void onEditClick(View view, Order order, int position) {
        showEditDialog(position);
    }

    @Override
    public void onDeleteClick(View view, final Order order, final int position) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("Confirm action")
                .content("Do you really want to remove this order?")
                .positiveText("Yes")
                .negativeText("No")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        Utility.showMessage(context, "Please consider the order carefully next time!");
                        orderList.remove(order);
                        adapter.removeItem(position);
                        ParseObject.createWithoutData("Order", order.getId()).deleteEventually();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                    }
                }).build();
        dialog.show();
    }

    @Override
    public void onOrderClick(View view, Order order) {
        final Product product = order.getProduct();
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

    private void showEditDialog(final int position) {
        order = orderList.get(position);
        final Product product = order.getProduct();
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("Edit your order")
                .customView(R.layout.custom_order_dialog, true)
                .positiveText("Save")
                .negativeText("Cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Order");
                        query.getInBackground(order.getId(), new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                order.setDays(seekBar.getProgress()+1);
                                order.setPrice((seekBar.getProgress() + 1) * product.getPrice());
                                object.put("Days", order.getDays());
                                object.put("Price", order.getPrice());
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Utility.showInfoDialog(context, "Your order has been updated");
                                            adapter.saveItem(position);
                                            materialDialog.dismiss();
                                        } else
                                            Utility.showInfoDialog(context, "An error happened. Please try again!");
                                    }
                                });
                            }
                        });

                    }
                }).build();

        seekBar = (SeekBar) dialog.getCustomView().findViewById(R.id.day_seekbar);
        totalCost = (TextView) dialog.getCustomView().findViewById(R.id.total_cost);
        dayIndicator = (TextView) dialog.getCustomView().findViewById(R.id.day_indicator);
        TextView productName = (TextView) dialog.getCustomView().findViewById(R.id.name);
        TextView productPrice = (TextView) dialog.getCustomView().findViewById(R.id.price);

        seekBar.setProgress(order.getDays());
        seekBar.setMax(9);

        productName.setText(product.getName());
        productPrice.setText(String.format(Locale.US, "%,d", product.getPrice()).replace(',', '.') + " VND");
        totalCost.setText(String.format(Locale.US, "%,d", order.getPrice()).replace(',', '.') + " VND");
        dayIndicator.setText(String.valueOf(order.getDays()));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int progress = seekBar.getProgress()+1;
                dayIndicator.setText(String.valueOf(progress));
                int cost = product.getPrice()*progress;
                totalCost.setText(cost + " VND");
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
}
