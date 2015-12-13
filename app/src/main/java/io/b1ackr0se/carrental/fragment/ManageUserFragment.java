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
import io.b1ackr0se.carrental.adapter.ManageUserAdapter;
import io.b1ackr0se.carrental.application.CustomApplication;
import io.b1ackr0se.carrental.model.User;

public class ManageUserFragment extends Fragment {

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
                        User user = new User();
                        user.setId(id);
                        user.setName(name);
                        user.setEmail(email);
                        user.setAddress(address);
                        user.setPhone(phone);
                        user.setType(type);
                        user.setStatus(status);
                        userList.add(user);
                    }
                    ((MainActivity) context).hideLoading(false);
                    adapter = new ManageUserAdapter(context, userList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(adapter);
                } else
                    ((MainActivity) context).hideLoading(true);
            }
        });
    }
}
