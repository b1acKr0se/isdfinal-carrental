package io.b1ackr0se.carrental.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.b1ackr0se.carrental.R;
import io.b1ackr0se.carrental.model.Product;

public class ProductAdapter extends ArrayAdapter<Product> {

    private Context context;
    private int resourceId;
    private ArrayList<Product> products;

    public ProductAdapter(Context context, int resourceId, ArrayList<Product> list) {
        super(context,resourceId, list);
        this.context = context;
        this.resourceId = resourceId;
        this.products = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final ViewHolder holder;

        if (row == null) {
            row = LayoutInflater.from(context).inflate(resourceId, parent, false);
            holder = new ViewHolder();
            holder.image = (DynamicHeightImageView) row.findViewById(R.id.image);
            holder.name = (TextView) row.findViewById(R.id.name);
            holder.price = (TextView) row.findViewById(R.id.price);
            row.setTag(holder);
        } else holder = (ViewHolder) row.getTag();

        final Product product = products.get(position);

        Picasso.with(context).load(product.getImage()).into(holder.image);
        holder.image.setHeightRatio(1.0);

        holder.name.setText(product.getName().trim());
        holder.price.setText(String.valueOf(product.getPrice()));

        return row;
    }


    private static class ViewHolder {
        DynamicHeightImageView image;
        TextView name;
        TextView price;
    }

}
