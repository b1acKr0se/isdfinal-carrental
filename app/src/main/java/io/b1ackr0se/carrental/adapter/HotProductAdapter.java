package io.b1ackr0se.carrental.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.carrental.R;
import io.b1ackr0se.carrental.model.Product;

public class HotProductAdapter extends RecyclerView.Adapter<HotProductAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Product> products;

    public HotProductAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hot_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HotProductAdapter.ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.name.setText(product.getName());
        Glide.with(context).load(product.getFirstImage()).into(holder.image);
        holder.itemView.setTag(product);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image)ImageView image;
        @Bind(R.id.name)TextView name;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
