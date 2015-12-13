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
import io.b1ackr0se.carrental.model.Favorite;
import io.b1ackr0se.carrental.model.Product;
import io.b1ackr0se.carrental.util.Utility;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Favorite> list;
    private OnFavoriteItemClickListener onFavoriteItemClickListener;

    public FavoriteAdapter(Context context, ArrayList<Favorite> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnClickListener(OnFavoriteItemClickListener onFavoriteItemClickListener) {
        this.onFavoriteItemClickListener = onFavoriteItemClickListener;
    }

    @Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Product product = list.get(position).getProduct();
        Glide.with(context).load(product.getFirstImage()).into(holder.productImage);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(Utility.showCurrency(product.getPrice()));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFavoriteItemClickListener.onItemClick(view, list.get(position));
            }
        });
        holder.itemView.setTag(product);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item)View view;
        @Bind(R.id.product_image)ImageView productImage;
        @Bind(R.id.product_name)TextView productName;
        @Bind(R.id.product_price)TextView productPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnFavoriteItemClickListener {
        void onItemClick(View view, Favorite favorite);
    }

}
