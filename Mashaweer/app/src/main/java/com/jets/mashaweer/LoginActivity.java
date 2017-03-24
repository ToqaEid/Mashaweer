package com.jets.mashaweer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.jets.classes.VolleySingleton;
import com.jets.constants.Alert;
import com.jets.constants.SharedPreferenceInfo;
import com.jets.constants.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    //Binding UI fields with objects
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;
    @BindView(R.id.button_facebook_login) com.facebook.login.widget.LoginButton _fbLogin;

    //Firebase Auth object
    private FirebaseAuth auth;
    private CallbackManager mCallbackManager;

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
    private static final int REQUEST_SIGNUP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        //Method to sign in automatically if the user has already signed in
//                if (auth.getCurrentUser() != null) {
//                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//                    finish();
//                }

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mCallbackManager = CallbackManager.Factory.create();


        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                login();
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

        _fbLogin.setReadPermissions("email", "public_profile");
        _fbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _loginButton.setEnabled(false);
                _fbLogin.setEnabled(false);
            }
        });
        _fbLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("3lama", "Inside the success method");
                AccessToken token = loginResult.getAccessToken();
                AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()){

                                }else{
                                    SharedPreferenceInfo.addUserDataToSharedPreference(LoginActivity.this, auth.getCurrentUser().getUid());
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    onLoginSuccess();
                                    Toast.makeText(LoginActivity.this, "Successfull signIn", Toast.LENGTH_SHORT).show();
                                    Log.i("3lama", "success facebook");
                                }
                            }
                        });
            }

            @Override
            public void onCancel() {
                _loginButton.setEnabled(true);
                _fbLogin.setEnabled(true);
            }

            @Override
            public void onError(FacebookException error) {

                _loginButton.setEnabled(true);
                _fbLogin.setEnabled(true);
                Log.i("3lama", "error facebook");
            }
        });

    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // Finish the sign in activity if the signup was successful
                this.finish();
                return;
            }
        }

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //Helper Classes
    public void login() {

        _loginButton.setEnabled(false);

        if (!validate()) {
            onLoginFailed();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme);// TODO: add appTheme_Dark_Dialog theme
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();



        //authenticate user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            // there was an error
                            onLoginFailed();
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Sign In failed", Toast.LENGTH_LONG).show();
                        } else {
                            progressDialog.dismiss();
                            SharedPreferenceInfo.addUserDataToSharedPreference(LoginActivity.this, auth.getCurrentUser().getUid());
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

    }


    public void onLoginSuccess() {
        _loginButton.setEnabled(false);
        _fbLogin.setEnabled(false);
        finish();
    }

    public void onLoginFailed() {

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

        if (password.isEmpty() || password.length() < 6 || password.length() > 20) {
            _passwordText.setError("Too short password");
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
                            SharedPreferenceInfo.addUserDataToSharedPreference(LoginActivity.this, SHAREDPREFERNCE_AUTHORIZED_DATA);
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

