package io.b1ackr0se.carrental.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.b1ackr0se.carrental.R;
import io.b1ackr0se.carrental.util.Utility;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    @Bind(R.id.input_name)EditText emailEditText;
    @Bind(R.id.input_password)EditText passwordEditText;
    @Bind(R.id.loginButton)TextView loginButton;

    private Context context;
    private ProgressDialog progressDialog;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);

        context = getActivity();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    validate();
                }
                return false;
            }
        });

        return view;
    }

    private void validate() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if(email.isEmpty()) {
            emailEditText.setError("Email cannot be empty");
        } else {
            if(password.isEmpty())
                passwordEditText.setError("Password cannot be empty");
            else
                login(email, password);
        }
    }

    private void login(final String email, String password) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        ParseQuery<ParseObject> query = new ParseQuery<>("User");
        query.whereEqualTo("Email", email).whereEqualTo("Password", password);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(progressDialog.isShowing()) progressDialog.dismiss();
                if (e == null) {
                    Utility.showMessage(context, "Login successfully!");
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("email", email);
                    returnIntent.putExtra("name", object.getString("userName"));
                    returnIntent.putExtra("id", object.getObjectId());
                    returnIntent.putExtra("type", object.getInt("Type"));
                    getActivity().setResult(Activity.RESULT_OK, returnIntent);
                    getActivity().finish();
                } else {
                    e.printStackTrace();
                    Utility.showMessage(context, "An error has happened, please try again.");
                }
            }
        });
    }
}
