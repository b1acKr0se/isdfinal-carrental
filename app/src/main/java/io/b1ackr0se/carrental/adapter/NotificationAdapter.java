package io.b1ackr0se.carrental.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.carrental.R;
import io.b1ackr0se.carrental.model.Notification;
import io.b1ackr0se.carrental.util.Utility;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Notification> list;

    public NotificationAdapter(Context context, ArrayList<Notification> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Notification notification = list.get(position);

        holder.title.setText(notification.getTitle());
        holder.content.setText(Html.fromHtml(notification.getContent()));
        holder.sender.setText(notification.getSenderName());
        holder.date.setText(Utility.formatNotificationDate(notification.getDate()));

        holder.itemView.setTag(notification);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.title)TextView title;
        @Bind(R.id.content)TextView content;
        @Bind(R.id.sender)TextView sender;
        @Bind(R.id.date)TextView date;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
