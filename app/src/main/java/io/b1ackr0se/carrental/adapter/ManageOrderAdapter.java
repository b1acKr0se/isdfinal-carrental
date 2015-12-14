package io.b1ackr0se.carrental.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
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
import io.b1ackr0se.carrental.application.CustomApplication;
import io.b1ackr0se.carrental.model.Order;
import io.b1ackr0se.carrental.util.Utility;

public class ManageOrderAdapter extends RecyclerView.Adapter<ManageOrderAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Order> orderList;

    private OnOrderClickListener onOrderClickListener;

    public ManageOrderAdapter(Context context, ArrayList<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    public void setOnOrderClickListener(OnOrderClickListener onOrderClickListener) {
        this.onOrderClickListener = onOrderClickListener;
    }

    @Override
    public ManageOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ManageOrderAdapter.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            final Order order = getItem(position);
            holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            holder.productName.setText(order.getProduct().getName());
            holder.numberOfDays.setText(String.valueOf(order.getDays()));
            holder.totalPrice.setText(Utility.showCurrency(order.getPrice()));
            Glide.with(context).load(order.getProduct().getFirstImage()).into(holder.image);
            holder.userName.setText(order.getUser().getName());
            holder.time.setText(Utility.formatOrderDate(order.getDate()));
            if(order.getStatus() == CustomApplication.ORDER_STATUS_PENDING) {
                holder.status.setText("PENDING");
                holder.status.setTextColor(Color.BLACK);
            } else if (order.getStatus() == CustomApplication.ORDER_STATUS_DONE) {
                holder.status.setText("DONE");
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.green));
            } else {
                holder.status.setText("DENIED");
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            }
            holder.itemView.setTag(order);
            holder.orderCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onOrderClickListener.onOrderClick(view, order);
                }
            });
            holder.denyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    holder.swipeLayout.close(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onOrderClickListener.onDenyClick(view, order, position);
                        }
                    }, 500);

                }
            });
            holder.doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    holder.swipeLayout.close(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onOrderClickListener.onAcceptClick(view, order, position);
                        }
                    }, 300);
                }
            });
            holder.profileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    holder.swipeLayout.close(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onOrderClickListener.onProfileClick(view, order);
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

    public void updateItem(int position, Order order) {
        orderList.set(position, order);
        notifyItemChanged(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.swipe)SwipeLayout swipeLayout;
        @Bind(R.id.order_card)CardView orderCard;
        @Bind(R.id.image)ImageView image;
        @Bind(R.id.user_name)TextView userName;
        @Bind(R.id.order_time)TextView time;
        @Bind(R.id.product_name)TextView productName;
        @Bind(R.id.days)TextView numberOfDays;
        @Bind(R.id.price)TextView totalPrice;
        @Bind(R.id.accept_button)View doneButton;
        @Bind(R.id.deny_button)View denyButton;
        @Bind(R.id.profile_button)View profileButton;
        @Bind(R.id.status)TextView status;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnOrderClickListener {
        void onAcceptClick(View view, Order order, int position);
        void onDenyClick(View view, Order order, int position);
        void onOrderClick(View view, Order order);
        void onProfileClick(View view, Order order);
    }
}