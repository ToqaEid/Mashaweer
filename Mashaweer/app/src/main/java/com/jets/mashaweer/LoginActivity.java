package com.jets.mashaweer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jets.classes.Trip;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener {

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


    private GoogleApiClient mGoogleApiClient;

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
                R.style.AppTheme_Dark_Dialog);
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

                            String err = task.getException().getMessage();
                            progressDialog.dismiss();
                            if (err.equals("A network error (such as timeout, interrupted connection or unreachable host) has occurred.")){

                                Toast.makeText(LoginActivity.this, "Network Error", Toast.LENGTH_LONG).show();

                            }else if (err.equals("There is no user record corresponding to this identifier. The user may have been deleted.")){

                                Toast.makeText(LoginActivity.this, "No user found with this Email", Toast.LENGTH_LONG).show();

                            }else if (err.equals("The password is invalid or the user does not have a password.")){

                                Toast.makeText(LoginActivity.this, "Incorrect Password", Toast.LENGTH_LONG).show();

                            }else {

                                Toast.makeText(LoginActivity.this, "Sign In Failed", Toast.LENGTH_LONG).show();

                            }

                        } else {

                            Log.i("MyTag","Login Success");

                            SharedPreferenceInfo.addUserDataToSharedPreference(LoginActivity.this, auth.getCurrentUser().getUid());


                            // TODO: Get user image from google place api and save it in internal storage

                            // reading and updating data from database
                            String userID = SharedPreferenceInfo.getUserId(LoginActivity.this);

                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference db = database.getReference("users/" + userID);


                            ////////// Leeeeh msh byod5ol fi addListener



//                            db.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//
//
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });


                            db.addListenerForSingleValueEvent(new ValueEventListener() {

//                                {
//                                    Log.i("MyTag","Login Success -----");
//
//                                }

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {


                                    Log.i("MyTag","Login Success 2");

                                    Iterable<DataSnapshot> trips = dataSnapshot.child("trips").getChildren();

                                    while (trips.iterator().hasNext()) {

                                        Log.i("MyTag","Login Success___ inside Iterator");
                                        DataSnapshot returnedData = trips.iterator().next();
                                        final Trip trip = returnedData.getValue(Trip.class);
                                        trip.setTripId(returnedData.getKey());

                                        //TODO Check if image exists in internal storage
                                        // TODO if NOT, Then downLoad it

                                        Log.i("MyTag", "Getting Images internal");

                                        Bitmap tripImage = loadImageFromStorage( getFilesDir().getAbsolutePath() , trip.getTripPlaceId());

                                        Log.i("MyTag","Back from internal storage :: " + (tripImage == null) );

                                        if (tripImage == null)
                                        {
                                            new Thread() {
                                                public void run() {

                                                    Log.i("MyTag" , "Thread is running ....");
                                                    Log.i("MyTag" , "Getting >> " + trip.getTripPlaceId());

                                                    Bitmap bitmap = downloadBitmap(  trip.getTripPlaceId() );


                                                    if (bitmap == null){

                                                        Log.i("MyTag" ,"Image Not Found ");

                                                    }
                                                    else {

                                                        ///////////// ---- saving photo to internal storage
                                                        String path = saveToInternalStorage(bitmap, trip.getTripPlaceId());
                                                        Log.i("MyTag" ,"Image is Saved in " + path);

                                                    }

                                                }
                                            }.start();

                                        }
                                        else{
                                            Log.i("MyTag","Image ALREADY Exists in Memory ");
                                        }


                                    }



                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    progressDialog.dismiss();
                                }
                            });


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


    ////////////////// image processing
    ////////////// load image from internal storage
    private Bitmap loadImageFromStorage(String path, String placeId)
    {
        Bitmap b = null;

        Log.i("MyTag","Loading image from internal storage ... ");

        try {

            ContextWrapper cw = new ContextWrapper(getApplicationContext());

            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

            // Create imageDir
            File f=new File(directory.getAbsolutePath(), placeId + ".jpg");


            //File f=new File(path+"/", placeId+".jpg");
            Log.i("MyTag","Image Path .. " + directory.getAbsolutePath() + "/" +  placeId+".jpg");
            b = BitmapFactory.decodeStream(new FileInputStream(f));
            Log.i("MyTag","Image successfully found");
            return  b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        Log.i("MyTag","Image not found");
        return b;
    }


    ////////////////////// ---- download image
    private Bitmap downloadBitmap(String url) {

        Bitmap bitmap = null;

        Log.i("MyTag" , "Downloading Image now ...  ");

        if (mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                    // .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Places.GEO_DATA_API)
                    .build();

            mGoogleApiClient.connect();
        }
        else if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        PlacePhotoMetadataResult result = Places.GeoDataApi
                .getPlacePhotos(mGoogleApiClient, url).await();

        if (result.getStatus().isSuccess()) {

            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();

            if (photoMetadataBuffer.getCount() > 0) {

                int rand = (int) (Math.floor(Math.random()) * photoMetadataBuffer.getCount());

                PlacePhotoMetadata photo = photoMetadataBuffer.get(rand);

                bitmap = photo.getScaledPhoto(mGoogleApiClient, 300, 300).await().getBitmap();

            }
            else{
                bitmap = null;
            }

            photoMetadataBuffer.release();
        }

        return bitmap;
    }



    ///////////// save image to internal storage
    private String saveToInternalStorage(Bitmap bitmapImage, String targetPlaceId){

        Log.i("MyTag", "Saving into internal");

        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        // Create imageDir
        File mypath=new File(directory, targetPlaceId + ".jpg");

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(mypath);

            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);

            Log.i("MyTag", "Saved into internal");

        } catch (Exception e) {

            e.printStackTrace();

        }
        finally {

            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.i("MyTag", "image to be returned");

        return directory.getAbsolutePath();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(this, "Error while downloading images !", Toast.LENGTH_SHORT).show();

    }


    /***
     * addUserDataToSharedPreference()
     *if the user has authorized access in the first time he logged in then:
     * his username & password will be added in the shared preference for quick login next time
     ***/


//    public void authenticateInput() {
//        //instantiate the requestQueue
//        VolleySingleton volleySingleton = VolleySingleton.getInstance(this.getApplicationContext());
//        //request
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
//                URLs.LOGIN_URL, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    String check = (String) response.get("check");
//                    switch (check) {
//                        case "wrong email":
//                            Alert.showErrorMsg(ERROR_EMAIL_TITLE, ERROR_EMAIL_MSG, LoginActivity.this);
//                            break;
//                        case "wrong password":
//                            Alert.showErrorMsg(ERROR_PASSWORD_TITLE, ERROR_PASSWORD_MSG, LoginActivity.this);
//                            break;
//                        case "success": //auhtorized user
//                            SharedPreferenceInfo.addUserDataToSharedPreference(LoginActivity.this, SHAREDPREFERNCE_AUTHORIZED_DATA);
//                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                            startActivity(intent);
//                            break;
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Alert.showErrorMsg(ERROR_LOGIN_DIALOG_TITLE, ERROR_LOGIN_DIALOG_MSG, LoginActivity.this);
//            }
//
//        });
//        jsonObjectRequest.setTag("stop");
//        volleySingleton.addToRequestQueue(jsonObjectRequest);
//    }
}

