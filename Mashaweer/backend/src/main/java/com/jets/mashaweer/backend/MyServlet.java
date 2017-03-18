/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package com.jets.mashaweer.backend;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import javax.servlet.http.*;

//url for the servlet is
// http://mashaweer-24be7.appspot.com/servletURLInWeb.xml

public class MyServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
        // Note: Ensure that the [PRIVATE_KEY_FILENAME].json has read
        // permissions set.
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setServiceAccount(getServletContext().getResourceAsStream("/WEB-INF/Mashaweer-feb533bff062.json"))
                .setDatabaseUrl("https://mashaweer-24be7.firebaseio.com/")
                .build();

        FirebaseApp.initializeApp(options);
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference("user");

        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object val = dataSnapshot.getValue();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        try {
//            FirebaseApp.getInstance();
//            resp.getWriter().println(FirebaseApp.getInstance().toString());
//            resp.getWriter().println("Successfull db connection");
//        }
//        catch (Exception error){
//            resp.getWriter().println("Faiiiiiiiiiiiiil");
//        }
//
//        try {
//            FirebaseApp.initializeApp(options);
//        }
//        catch(Exception error){
//
//        }

        // As an admin, the app has access to read and write all data, regardless of Security Rules
//        DatabaseReference ref = FirebaseDatabase
//                .getInstance()
//                .getReference();


    }
}
