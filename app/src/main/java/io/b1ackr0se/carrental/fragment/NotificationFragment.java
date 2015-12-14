package io.b1ackr0se.carrental.fragment;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.carrental.R;
import io.b1ackr0se.carrental.activity.MainActivity;
import io.b1ackr0se.carrental.adapter.NotificationAdapter;
import io.b1ackr0se.carrental.application.CustomApplication;
import io.b1ackr0se.carrental.model.Notification;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    @Bind(R.id.recycler_view)RecyclerView recyclerView;

    private Context context;
    private ArrayList<Notification> notifications;
    private NotificationAdapter adapter;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        ButterKnife.bind(this, view);

        context = getActivity();

        loadNotification();

        return view;
    }


    private void loadNotification() {
        if(CustomApplication.userId != null) {
            ((MainActivity) context).showLoading();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Notification");
            query.whereEqualTo("ReceiverId", CustomApplication.userId);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() == 0) ((MainActivity) context).hideLoading(true);
                        else {
                            notifications = new ArrayList<>();
                            for (int i = objects.size() - 1; i >= 0; i--) {
                                ParseObject object = objects.get(i);
                                String id = object.getObjectId();
                                String senderId = object.getString("SenderId");
                                String receiverId = object.getString("ReceiverId");
                                String title = object.getString("Title");
                                String content = object.getString("Content");
                                long date = object.getLong("Date");
                                String senderName = object.getString("SenderName");
                                Notification notification = new Notification();
                                notification.setId(id);
                                notification.setSenderId(senderId);
                                notification.setSenderName(senderName);
                                notification.setReceiverId(receiverId);
                                notification.setTitle(title);
                                notification.setContent(content);
                                notification.setDate(date);
                                notifications.add(notification);
                            }
                            ((MainActivity) context).hideLoading(false);
                            adapter = new NotificationAdapter(context, notifications);
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            recyclerView.setAdapter(adapter);
                        }
                    } else ((MainActivity) context).hideLoading(true);
                }
            });
        }
    }
}
