package io.b1ackr0se.carrental.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.carrental.R;
import io.b1ackr0se.carrental.model.Product;
import io.b1ackr0se.carrental.util.Utility;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Product> list;
    private OnProductClickListener onProductClickListener;

    public AdminProductAdapter(Context context, ArrayList<Product> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnProductClickListener(OnProductClickListener onProductClickListener) {
        this.onProductClickListener = onProductClickListener;
    }


    @Override
    public AdminProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_product, parent, false);
        return new ViewHolder(view);
    }

    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void updateItem(int position, Product product) {
        list.set(position, product);
        notifyItemChanged(position);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Product product = list.get(position);
        Glide.with(context).load(product.getFirstImage()).into(holder.image);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(Utility.showCurrency(product.getPrice()));
        holder.orderCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onProductClickListener.onProductClick(view, product);
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                holder.swipeLayout.close(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onProductClickListener.onDeleteClick(view, product, position);
                    }
                }, 500);

            }
        });
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                holder.swipeLayout.close(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onProductClickListener.onEditClick(view, product, position);
                    }
                }, 300);
            }
        });
        holder.itemView.setTag(product);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.swipe)SwipeLayout swipeLayout;
        @Bind(R.id.order_card)CardView orderCard;
        @Bind(R.id.image)ImageView image;
        @Bind(R.id.product_name)TextView productName;
        @Bind(R.id.product_price)TextView productPrice;
        @Bind(R.id.edit_button)View editButton;
        @Bind(R.id.delete_button)View deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnProductClickListener {
        void onEditClick(View view, Product product, int position);
        void onDeleteClick(View view, Product product, int position);
        void onProductClick(View view, Product product);
    }
}
