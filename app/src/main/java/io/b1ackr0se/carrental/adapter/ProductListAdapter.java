package io.b1ackr0se.carrental.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.etsy.android.grid.util.DynamicHeightImageView;

import java.util.ArrayList;

import io.b1ackr0se.carrental.R;
import io.b1ackr0se.carrental.model.Product;

public class ProductListAdapter extends ArrayAdapter<Product> {

    private Context context;
    private int resourceId;
    private ArrayList<Product> products;

    public ProductListAdapter(Context context, int resourceId, ArrayList<Product> list) {
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
        Glide.with(context).load(product.getImages().get(0)).into(holder.image);
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

//    public ProductListAdapter(Context context, ArrayList<Product> products) {
//        this.context = context;
//        this.products = products;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
//
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        Product product = products.get(position);
//
//        Picasso.with(context).load(product.getImage()).into(holder.image);
//        holder.name.setText(product.getName());
//        holder.price.setText(String.valueOf(product.getPrice()));
//        holder.itemView.setTag(product);
//    }
//
//    @Override
//    public int getItemCount() {
//        return products.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        @Bind(R.id.image)ImageView image;
//        @Bind(R.id.name)TextView name;
//        @Bind(R.id.price)TextView price;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//    }

}
