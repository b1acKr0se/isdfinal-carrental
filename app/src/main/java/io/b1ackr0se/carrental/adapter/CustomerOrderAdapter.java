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
import io.b1ackr0se.carrental.model.Order;
import io.b1ackr0se.carrental.util.Utility;

public class CustomerOrderAdapter extends RecyclerView.Adapter<CustomerOrderAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Order> orderList;

    private OnOrderClickListener onOrderClickListener;

    public CustomerOrderAdapter(Context context, ArrayList<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    public void setOnOrderClickListener(OnOrderClickListener onOrderClickListener) {
        this.onOrderClickListener = onOrderClickListener;
    }

    @Override
    public CustomerOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomerOrderAdapter.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            final Order order = getItem(position);
            holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            holder.productName.setText(order.getProduct().getName());
            holder.numberOfDays.setText(String.valueOf(order.getDays()));
            holder.totalPrice.setText(Utility.showCurrency(order.getPrice()));
            Glide.with(context).load(order.getProduct().getFirstImage()).into(holder.image);
            holder.itemView.setTag(order);
            holder.orderCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onOrderClickListener.onOrderClick(view, order);
                }
            });
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    holder.swipeLayout.close(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onOrderClickListener.onDeleteClick(view, order, position);
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
                            onOrderClickListener.onEditClick(view, order, position);
                        }
                    }, 300);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private Order getItem(int position) {
        return orderList.get(position);
    }

    public void removeItem(int position) {
        orderList.remove(position);
        notifyItemRemoved(position);
    }

    public void saveItem(int position) {
        notifyItemChanged(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.swipe)SwipeLayout swipeLayout;
        @Bind(R.id.order_card)CardView orderCard;
        @Bind(R.id.image)ImageView image;
        @Bind(R.id.product_name)TextView productName;
        @Bind(R.id.days)TextView numberOfDays;
        @Bind(R.id.price)TextView totalPrice;
        @Bind(R.id.edit_button)View editButton;
        @Bind(R.id.delete_button)View deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnOrderClickListener {
        void onEditClick(View view, Order order, int position);
        void onDeleteClick(View view, Order order, int position);
        void onOrderClick(View view, Order order);
    }
}
