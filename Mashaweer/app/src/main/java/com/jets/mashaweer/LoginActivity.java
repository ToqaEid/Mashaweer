package com.jets.mashaweer;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jets.classes.VolleySingleton;
import com.jets.constants.Alert;
import com.jets.constants.SharedPreferenceInfo;
import com.jets.constants.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    //Binding UI fields with objects
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;

    //error titles and msgs
    private final String ERROR_LOGIN_DIALOG_TITLE = "Error Login";
    private final String ERROR_LOGIN_DIALOG_MSG = "Connection Error";
    private final String ERROR_EMAIL_TITLE = "Incorrect Email";
    private final String ERROR_EMAIL_MSG = "The email you entered doesn't appear to belong to an acount. Please check your email address and try again";
    private final String ERROR_PASSWORD_TITLE = "Incorrect Password";
    private final String ERROR_PASSWORD_MSG = "The password you entered is incorrect. Please try again.";
    //value of the key stored in the shared preference
    private final String SHAREDPREFERNCE_AUTHORIZED_DATA = "authorized_user";

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int REQUEST_SIGNUP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        // Set up the login form.

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //TODO: implement login authentication in login method
                //login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

    }

    @Override
    public void onBackPressed() {
        //TODO: exit application on back pressed in this view
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    //Helper Classes
    public void login() {

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme);// TODO: add appTheme_Dark_Dialog theme
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onLoginSuccess() {
        _loginButton.setEnabled(false);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    /***
     * addUserDataToSharedPreference()
     *if the user has authorized access in the first time he logged in then:
     * his username & password will be added in the shared preference for quick login next time
     ***/

    //TOQA
    public void addUserDataToSharedPreference(String authorization) {
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferenceInfo.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SharedPreferenceInfo.LOGIN_KEY, authorization);
        editor.commit();

    }


    //TOQA
    public void authenticateInput() {
        //instantiate the requestQueue
        VolleySingleton volleySingleton = VolleySingleton.getInstance(this.getApplicationContext());
        //request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                URLs.LOGIN_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String check = (String) response.get("check");
                    switch (check) {
                        case "wrong email":
                            Alert.showErrorMsg(ERROR_EMAIL_TITLE, ERROR_EMAIL_MSG, LoginActivity.this);
                            break;
                        case "wrong password":
                            Alert.showErrorMsg(ERROR_PASSWORD_TITLE, ERROR_PASSWORD_MSG, LoginActivity.this);
                            break;
                        case "success": //auhtorized user
                            addUserDataToSharedPreference(SHAREDPREFERNCE_AUTHORIZED_DATA);
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Alert.showErrorMsg(ERROR_LOGIN_DIALOG_TITLE, ERROR_LOGIN_DIALOG_MSG, LoginActivity.this);
            }

        });
        jsonObjectRequest.setTag("stop");
        volleySingleton.addToRequestQueue(jsonObjectRequest);
    }
}

