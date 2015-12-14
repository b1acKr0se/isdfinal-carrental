package io.b1ackr0se.carrental.fragment;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.carrental.R;
import io.b1ackr0se.carrental.activity.MainActivity;
import io.b1ackr0se.carrental.adapter.ManageUserAdapter;
import io.b1ackr0se.carrental.application.CustomApplication;
import io.b1ackr0se.carrental.model.User;
import io.b1ackr0se.carrental.util.Utility;

public class ManageUserFragment extends Fragment implements ManageUserAdapter.OnUserClickListener {

    @Bind(R.id.recycler_view)RecyclerView recyclerView;

    private Context context;
    private ArrayList<User> userList;
    private ManageUserAdapter adapter;

    public ManageUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_user, container, false);

        ButterKnife.bind(this, view);

        context = getActivity();

        loadUser();

        return view;
    }

    private void loadUser() {
        ((MainActivity) context).showLoading();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.whereNotEqualTo("Type", CustomApplication.TYPE_ADMIN);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                userList = new ArrayList<>();
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        ParseObject object = objects.get(i);
                        String id = object.getObjectId();
                        String name = object.getString("userName");
                        String email = object.getString("Email");
                        String address = object.getString("Address");
                        String phone = object.getString("Phone");
                        int type = object.getInt("Type");
                        int status = object.getInt("Status");
                        long date = object.getLong("JoinDate");
                        User user = new User();
                        user.setId(id);
                        user.setName(name);
                        user.setEmail(email);
                        user.setAddress(address);
                        user.setPhone(phone);
                        user.setType(type);
                        user.setStatus(status);
                        user.setJoinDate(date);
                        userList.add(user);
                    }
                    ((MainActivity) context).hideLoading(false);
                    adapter = new ManageUserAdapter(context, userList);
                    adapter.setOnUserClickListener(ManageUserFragment.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(adapter);
                } else
                    ((MainActivity) context).hideLoading(true);
            }
        });
    }

    @Override
    public void onUserClick(View view, User user) {
        showUserProfile(user);
    }

    @Override
    public void onBanClick(View view, User user, int position) {
        showBanDialog(user, position);
    }

    private void showUserProfile(User user) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("User information")
                .customView(R.layout.dialog_user_info, true)
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                    }
                }).build();

        TextView userName = (TextView) dialog.getCustomView().findViewById(R.id.name);
        TextView userEmail = (TextView) dialog.getCustomView().findViewById(R.id.email);
        TextView userAddress = (TextView) dialog.getCustomView().findViewById(R.id.address);
        TextView userPhone = (TextView) dialog.getCustomView().findViewById(R.id.phone);
        TextView userType = (TextView) dialog.getCustomView().findViewById(R.id.type);
        TextView userStatus = (TextView) dialog.getCustomView().findViewById(R.id.status);

        userName.setText(user.getName());
        userEmail.setText(user.getEmail());
        userAddress.setText(user.getAddress());
        userPhone.setText(user.getPhone());
        if(user.getType() == CustomApplication.TYPE_USER) userType.setText("User");
        else userType.setText("Admin");
        if(user.getStatus() == CustomApplication.STATUS_NORMAL) {
            userStatus.setText("GOOD STANDING");
            userStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else {
            userStatus.setText("BANNED");
            userStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        dialog.show();
    }

    private void showBanDialog(final User user, final int position) {
        MaterialDialog dialog;
        if(user.getStatus() == CustomApplication.STATUS_NORMAL) {
            dialog = new MaterialDialog.Builder(context)
                    .title("Confirm")
                    .content("Do you want to ban this user?")
                    .positiveText("Yes")
                    .negativeText("Cancel")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            Utility.showMessage(context, "User banned!");
                            userList.get(position).setStatus(CustomApplication.STATUS_BANNED);
                            adapter.updateItem(position, userList.get(position));
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
                            query.getInBackground(user.getId(), new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    object.put("Status", CustomApplication.STATUS_BANNED);
                                    object.saveEventually();
                                }
                            });
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            materialDialog.dismiss();
                        }
                    }).build();
        } else {
            dialog = new MaterialDialog.Builder(context)
                    .title("Confirm")
                    .content("Do you want to unban this user?")
                    .positiveText("Yes")
                    .negativeText("Cancel")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            Utility.showMessage(context, "User unbanned!");
                            userList.get(position).setStatus(CustomApplication.STATUS_NORMAL);
                            adapter.updateItem(position, userList.get(position));
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
                            query.getInBackground(user.getId(), new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    object.put("Status", CustomApplication.STATUS_NORMAL);
                                    object.saveEventually();
                                }
                            });
                            sendUnbanNotification(user.getId());
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            materialDialog.dismiss();
                        }
                    }).build();
        }
        dialog.show();
    }

    private void sendUnbanNotification(String id) {
        if(CustomApplication.userId != null) {
            ParseObject object = new ParseObject("Notification");
            object.put("SenderId", CustomApplication.userId);
            object.put("ReceiverId", id);
            object.put("SenderName", ((MainActivity)context).getLoggedInName());
            object.put("Title", "System message");
            object.put("Content", "You have been unbanned. We hope that there won't be any " +
                    "pity events in the future.");
            object.put("Date", System.currentTimeMillis());
            object.saveEventually();
        }
    }
}
