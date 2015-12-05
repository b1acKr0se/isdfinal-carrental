package io.b1ackr0se.carrental.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.carrental.R;
import io.b1ackr0se.carrental.application.CustomApplication;
import io.b1ackr0se.carrental.util.Utility;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    @Bind(R.id.input_name)EditText nameEditText;
    @Bind(R.id.input_email)EditText emailEditText;
    @Bind(R.id.input_password)EditText passwordEditText;
    @Bind(R.id.input_address)EditText addressEditText;
    @Bind(R.id.input_phone)EditText phoneEditText;
    @Bind(R.id.registerButton)TextView registerButton;
    @Bind(R.id.clearButton)TextView clearButton;

    private Context context;
    private ProgressDialog progressDialog;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);

        context = getActivity();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
            }
        });

        return view;
    }

    private void register(final String name, final String email, String password, String address, String phone) {
        ParseObject object = new ParseObject("User");
        object.put("userName", name);
        object.put("Password", password);
        object.put("Email", email);
        object.put("Address", address);
        object.put("Phone", phone);
        object.put("Type", CustomApplication.TYPE_USER);
        object.put("Status", CustomApplication.STATUS_NORMAL);
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                if (e == null) {
                    Utility.showMessage(context, "Your account has been created!");
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("email", email);
                    returnIntent.putExtra("name", name);
                    getActivity().setResult(Activity.RESULT_OK, returnIntent);
                    getActivity().finish();
                } else {
                    e.printStackTrace();
                    Utility.showMessage(context, "An error has happened, please try again.");
                }
            }
        });
    }

    private boolean validate() {
        String name = nameEditText.getText().toString();
        if(name.isEmpty()) {
            nameEditText.setError("Name cannot be empty!");
            return false;
        } else {
            String email = emailEditText.getText().toString();
            if(email.isEmpty()) {
                emailEditText.setError("Email cannot be empty!");
                return false;
            } else {
                String password = passwordEditText.getText().toString();
                if(password.isEmpty()) {
                    passwordEditText.setError("Password cannot be empty!");
                    return false;
                } else {
                    String address = addressEditText.getText().toString();
                    if(address.isEmpty()) {
                        addressEditText.setError("Address cannot be empty!");
                        return false;
                    } else {
                        String phone = phoneEditText.getText().toString();
                        if(phone.isEmpty()) {
                            phoneEditText.setError("Phone cannot be empty!");
                            return false;
                        } else {
                            verifyEmail(name, email, password, address, phone);
                            return true;
                        }
                    }
                }
            }
        }
    }

    private void verifyEmail(final String name, final String email, final String password, final String address, final String phone) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Your account is being processed...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        ParseQuery<ParseObject> query = new ParseQuery<>("User");
        query.whereEqualTo("Email", email);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null && object != null) {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    emailEditText.setError("Email is already existed! Please try another");
                } else
                    register(name, email, password, address, phone);
            }
        });
    }

    private void clear() {
        nameEditText.getText().clear();
        emailEditText.getText().clear();
        passwordEditText.getText().clear();
        addressEditText.getText().clear();
        phoneEditText.getText().clear();
    }


}
