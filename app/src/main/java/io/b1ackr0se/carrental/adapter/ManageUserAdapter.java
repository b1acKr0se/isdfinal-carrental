package io.b1ackr0se.carrental.adapter;

import android.content.Context;
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
import io.b1ackr0se.carrental.model.User;

public class ManageUserAdapter extends RecyclerView.Adapter<ManageUserAdapter.ViewHolder> {
    private Context context;
    private ArrayList<User> userList;

    public ManageUserAdapter(Context context, ArrayList<User> userList) {
        this.context = context;
        this.userList = userList;
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        holder.userName.setText(user.getName());
        holder.itemView.setTag(user);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.swipe)SwipeLayout swipeLayout;
        @Bind(R.id.user_info)View userInfo;
        @Bind(R.id.ban_button)View banButton;
        @Bind(R.id.user_name)TextView userName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
