package io.b1ackr0se.carrental.fragment;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.FindCallback;
import com.parse.GetCallback;
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
import io.b1ackr0se.carrental.adapter.ManageOrderAdapter;
import io.b1ackr0se.carrental.application.CustomApplication;
import io.b1ackr0se.carrental.model.Order;
import io.b1ackr0se.carrental.model.Product;
import io.b1ackr0se.carrental.model.User;
import io.b1ackr0se.carrental.util.Utility;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageOrderFragment extends Fragment implements ManageOrderAdapter.OnOrderClickListener{

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    private Context context;
    private ArrayList<Order> orders;
    private ManageOrderAdapter adapter;

    private boolean userQueryFinished = false;
    private boolean productQueryFinished = false;

    private Order order;

    public ManageOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_order, container, false);
        ButterKnife.bind(this, view);

        context = getActivity();

        loadOrder();

        return view;
    }

    private void loadOrder() {
        ((MainActivity) context).showLoading();
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Order");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() != 0) {
                        orders = new ArrayList<>();
                        for (int i = objects.size() - 1; i >= 0; i--) {
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

                        loadProduct(query);
                        loadUser(query);
                    } else
                        ((MainActivity) context).hideLoading(true);
                } else
                    ((MainActivity) context).hideLoading(true);
            }
        });
    }

    private void loadProduct(ParseQuery<ParseObject> query) {
        ParseQuery<ParseObject> productQuery = new ParseQuery<>("Product").whereMatchesKeyInQuery("productId", "ProductId", query);
        productQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() == 0) ((MainActivity) context).hideLoading(true);
                    else {
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
                        }
                        productQueryFinished = true;
                        if(userQueryFinished) {
                            ((MainActivity) context).hideLoading(false);
                            setUpAdapter();
                        }
                    }
                } else
                    ((MainActivity) context).hideLoading(true);
            }
        });
    }

    private void loadUser(ParseQuery<ParseObject> query) {
        ParseQuery<ParseObject> userQuery = new ParseQuery<>("User").whereMatchesKeyInQuery("objectId", "UserId", query);
        userQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() == 0) ((MainActivity) context).hideLoading(true);
                    else {
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject object = objects.get(i);
                            String id = object.getObjectId();
                            String name = object.getString("userName");
                            String email = object.getString("Email");
                            String address = object.getString("Address");
                            String phone = object.getString("Phone");
                            int type = object.getInt("Type");
                            int status = object.getInt("Status");
                            long date = object.getLong("JoinDate");
                            User user = new User();
                            user.setId(id);
                            user.setName(name);
                            user.setEmail(email);
                            user.setAddress(address);
                            user.setPhone(phone);
                            user.setType(type);
                            user.setStatus(status);
                            user.setJoinDate(date);

                            for (int x = 0; x < orders.size(); x++) {
                                Order order = orders.get(x);
                                if (order.getUserId().equals(id)) {
                                    order.setUser(user);
                                }
                            }
                        }
                        userQueryFinished = true;
                        if (productQueryFinished) {
                            ((MainActivity) context).hideLoading(false);
                            setUpAdapter();
                        }
                    }
                } else
                    ((MainActivity) context).hideLoading(true);
            }
        });
    }
    private void setUpAdapter() {
        adapter = new ManageOrderAdapter(context, orders);
        adapter.setOnOrderClickListener(ManageOrderFragment.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    private void sendMessage(String id, Product product, boolean accept) {
        if (CustomApplication.userId != null) {
            ParseObject object = new ParseObject("Notification");
            object.put("SenderId", CustomApplication.userId);
            object.put("ReceiverId", id);
            object.put("SenderName", ((MainActivity) context).getLoggedInName());
            object.put("Title", "System message");
            object.put("Date", System.currentTimeMillis());
            if (accept)
                object.put("Content", "A recent order of the product <b>" + product.getName() + "</b> of yours have been delivered. Thanks for choosing our service!");
            else
                object.put("Content", "A recent order of the product <b>" + product.getName() + "</b> of yours have been denied by the admin. Please validate" +
                        "the information and resend the order. Sorry for the inconvenience!");
            object.saveEventually();
        }

    }

    @Override
    public void onAcceptClick(View view, final Order o, final int position) {
        if(o.getStatus() != CustomApplication.ORDER_STATUS_DONE) {
            MaterialDialog dialog = new MaterialDialog.Builder(context)
                    .title("Confirm")
                    .content("Do you want to mark this order as done?")
                    .positiveText("OK")
                    .negativeText("Cancel")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            order = o;
                            order.setStatus(CustomApplication.ORDER_STATUS_DONE);
                            adapter.updateItem(position, order);
                            orders.get(position).setStatus(CustomApplication.ORDER_STATUS_DONE);
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Order");
                            query.getInBackground(o.getId(), new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    if (e == null) {
                                        object.put("Status", CustomApplication.ORDER_STATUS_DONE);
                                        object.saveEventually();
                                        Utility.showMessage(context, "Marked as done");
                                    }
                                }
                            });
                            sendMessage(o.getUserId(), o.getProduct(), true);
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            materialDialog.dismiss();
                        }
                    }).build();
            dialog.show();
        } else
            Utility.showMessage(context, "Order has already been marked as done!");
    }

    @Override
    public void onDenyClick(View view, final Order o, final int position) {
        if(o.getStatus() != CustomApplication.ORDER_STATUS_DENIED) {
            MaterialDialog dialog = new MaterialDialog.Builder(context)
                    .title("Confirm")
                    .content("Do you want to deny this order?")
                    .positiveText("OK")
                    .negativeText("Cancel")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            order = o;
                            order.setStatus(CustomApplication.ORDER_STATUS_DENIED);
                            adapter.updateItem(position, order);
                            orders.get(position).setStatus(CustomApplication.ORDER_STATUS_DENIED);
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Order");
                            query.getInBackground(o.getId(), new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    if (e == null) {
                                        object.put("Status", CustomApplication.ORDER_STATUS_DENIED);
                                        object.saveEventually();
                                        Utility.showMessage(context, "Order denied");
                                    }
                                }
                            });
                            sendMessage(o.getUserId(), o.getProduct(), false);
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            materialDialog.dismiss();
                        }
                    }).build();
            dialog.show();
        } else
            Utility.showMessage(context, "Order has already been denied!");
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

    @Override
    public void onProfileClick(View view, Order order) {
        User user = order.getUser();
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("User information")
                .customView(R.layout.dialog_user_info, true)
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                    }
                }).build();

        TextView userName = (TextView) dialog.getCustomView().findViewById(R.id.name);
        TextView userEmail = (TextView) dialog.getCustomView().findViewById(R.id.email);
        TextView userAddress = (TextView) dialog.getCustomView().findViewById(R.id.address);
        TextView userPhone = (TextView) dialog.getCustomView().findViewById(R.id.phone);
        TextView userType = (TextView) dialog.getCustomView().findViewById(R.id.type);
        TextView userStatus = (TextView) dialog.getCustomView().findViewById(R.id.status);

        userName.setText(user.getName());
        userEmail.setText(user.getEmail());
        userAddress.setText(user.getAddress());
        userPhone.setText(user.getPhone());
        if(user.getType() == CustomApplication.TYPE_USER) userType.setText("User");
        else userType.setText("Admin");
        if(user.getStatus() == CustomApplication.STATUS_NORMAL) {
            userStatus.setText("GOOD STANDING");
            userStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else {
            userStatus.setText("BANNED");
            userStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        dialog.show();
    }
}
