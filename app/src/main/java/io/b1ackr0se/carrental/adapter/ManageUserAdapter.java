package io.b1ackr0se.carrental.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.carrental.R;
import io.b1ackr0se.carrental.application.CustomApplication;
import io.b1ackr0se.carrental.model.User;
import io.b1ackr0se.carrental.util.Utility;

public class ManageUserAdapter extends RecyclerView.Adapter<ManageUserAdapter.ViewHolder> {
    private Context context;
    private ArrayList<User> userList;
    private OnUserClickListener onUserClickListener;

    public ManageUserAdapter(Context context, ArrayList<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    public void setOnUserClickListener(OnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
    }

    public void updateItem(int position, User user) {
        userList.set(position, user);
        notifyItemChanged(position, user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        User user = userList.get(position);
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        holder.userName.setText(user.getName());
        holder.userJoinDate.setText("Member since " + Utility.formatDate(user.getJoinDate()));
        if(user.getStatus() == CustomApplication.STATUS_NORMAL)
            holder.banButton.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
        else
            holder.banButton.setBackgroundColor(ContextCompat.getColor(context, R.color.green));

        holder.userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUserClickListener.onUserClick(view, userList.get(position));
            }
        });

        holder.banButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                holder.swipeLayout.close(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onUserClickListener.onBanClick(view, userList.get(position), position);
                    }
                }, 500);
            }
        });

        holder.itemView.setTag(user);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.swipe)SwipeLayout swipeLayout;
        @Bind(R.id.user_info)View userInfo;
        @Bind(R.id.ban_button)View banButton;
        @Bind(R.id.ban_icon)View banIcon;
        @Bind(R.id.user_name)TextView userName;
        @Bind(R.id.user_join_date)TextView userJoinDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnUserClickListener {
        void onUserClick(View view, User user);
        void onBanClick(View view, User user, int position);
    }
}
