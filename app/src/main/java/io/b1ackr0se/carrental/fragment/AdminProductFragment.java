package io.b1ackr0se.carrental.fragment;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
import io.b1ackr0se.carrental.adapter.AdminProductAdapter;
import io.b1ackr0se.carrental.model.Product;
import io.b1ackr0se.carrental.util.Utility;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminProductFragment extends Fragment implements AdminProductAdapter.OnProductClickListener {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    private Context context;
    private ArrayList<Product> products;
    private AdminProductAdapter adapter;

    private int count = 0;

    private EditText nameEditText;
    private EditText descEditText;
    private EditText priceEditText;


    public AdminProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_product, container, false);
        ButterKnife.bind(this, view);

        context = getActivity();

        loadProduct();

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
                                        adapter = new AdminProductAdapter(context, products);
                                        adapter.setOnProductClickListener(AdminProductFragment.this);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                        recyclerView.setAdapter(adapter);
                                        ((MainActivity) context).hideLoading(false);
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
    public void onEditClick(View view, Product product, int position) {
        showEditDialog(product, position);
    }

    @Override
    public void onDeleteClick(View view, Product product, int position) {
        showDeleteDialog(product, position);
    }

    @Override
    public void onProductClick(View view, Product product) {
        viewProduct(product);
    }


    private void viewProduct(Product product) {
        Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", product);
        intent.putExtra("product", bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    private void showDeleteDialog(final Product product, final int position) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("Confirm action")
                .content("Do you really want to remove this product?")
                .positiveText("Yes")
                .negativeText("No")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        products.remove(product);
                        adapter.removeItem(position);
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
                        query.whereEqualTo("productId", product.getId());
                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if (e == null) {
                                    object.deleteEventually();
                                } else {
                                    Utility.showMessage(context, "An error happened.");
                                }
                            }
                        });
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

    private void showEditDialog(final Product product, final int position) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("Edit your order")
                .customView(R.layout.dialog_product_edit, true)
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
                        final String name = nameEditText.getText().toString().trim();
                        final String desc = descEditText.getText().toString().trim();
                        try {
                            final int price = Integer.valueOf(priceEditText.getText().toString());
                            if(!isDifferent(product, name, desc, price)) {
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
                                query.whereEqualTo("productId", product.getId());
                                query.getFirstInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject object, ParseException e) {
                                        if(e == null) {
                                            object.put("Name", name);
                                            object.put("Description", desc);
                                            object.put("Price", price);
                                            object.saveEventually();
                                        }
                                    }
                                });
                                Product updatedProduct = (Product) product.clone();
                                updatedProduct.setName(name);
                                updatedProduct.setDescription(desc);
                                updatedProduct.setPrice(price);
                                products.set(products.indexOf(product), updatedProduct);
                                adapter.updateItem(position, updatedProduct);
                            } else
                                Utility.showMessage(context, "Product info not updated");
                            materialDialog.dismiss();
                        }catch (NumberFormatException e) {
                            Utility.showMessage(context, "Price must be number!");
                        } catch (CloneNotSupportedException e) {
                            Utility.showMessage(context, "An error happened!");
                        }

                    }
                }).build();

        nameEditText = (EditText) dialog.getCustomView().findViewById(R.id.product_name);
        descEditText = (EditText) dialog.getCustomView().findViewById(R.id.product_description);
        priceEditText = (EditText) dialog.getCustomView().findViewById(R.id.product_price);

        nameEditText.setText(product.getName());
        descEditText.setText(product.getDescription());
        priceEditText.setText(product.getPrice() + "");

        dialog.show();
    }

    private boolean isDifferent(Product product, String name, String desc, int price) {
        return product.getName().equals(name) && product.getDescription().equals(desc) && product.getPrice() == price;
    }
}
