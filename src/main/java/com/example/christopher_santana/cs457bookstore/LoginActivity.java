package com.example.christopher_santana.cs457bookstore;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask loginTask = null;
    private UserSignupTask signupTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    SharedPreferences sessionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
        }

        //set sign in button
        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        //set sign up button
        Button signUpButton = (Button) findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignup();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        sessionInfo = getSharedPreferences("SessionInfo", 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED){
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (loginTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            loginTask = new UserLoginTask(email, password);
            loginTask.execute((Void) null);
        }
    }
    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email);
    }

    private void attemptSignup(){
        if (loginTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            signupTask = new UserSignupTask(email, password);
            signupTask.execute((Void) null);
        }
    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }

    private class UserSignupTask extends AsyncTask<Void, Void, Boolean>{
        private final String mUsername;
        private final String mPassword;
        JSONObject response = null;
        String defaultErrorMessage = "Error connecting to server";

        UserSignupTask(String email, String password) {
            mUsername = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean signupSuccessful = false;
            try{
                URL url = new URL("http://people.aero.und.edu/~csantana/457/Exercise%202/Authenticate.php");
                String body =   URLEncoder.encode("act", "UTF-8") + "=" +  URLEncoder.encode("Signup", "UTF-8") + "&";
                body +=         URLEncoder.encode("user", "UTF-8") + "=" +  URLEncoder.encode(mUsername, "UTF-8") + "&";
                body +=         URLEncoder.encode("password", "UTF-8") + "=" +  URLEncoder.encode(mPassword, "UTF-8");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(body);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder mysb = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null){
                    mysb.append(line);
                }
                response = new JSONObject(mysb.toString());
                signupSuccessful = response.getBoolean("success");
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                return signupSuccessful;
            }
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            signupTask = null;

            if (!aBoolean){
                if (response == null){
                    Toast.makeText(getBaseContext(), defaultErrorMessage, Toast.LENGTH_LONG).show();
                }
                else {
                    try{
                        Toast.makeText(getBaseContext(), response.getString("errormsg"), Toast.LENGTH_LONG).show();
                    }
                    catch (Exception e){
                        Toast.makeText(getBaseContext(), defaultErrorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            }
            else{
                Toast.makeText(getBaseContext(), "New user created!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String urlBuilder(String baseURL, HashMap<String, String> values){
        String url = baseURL;
        boolean first = true;
        for (String key : values.keySet()){
            if (first){
                url += "?";
                first = false;
            }
            else{
                url += "&";
            }
            url+= key + "=" + values.get(key);
        }
        return url;
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        JSONObject response;

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mUsername = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean signupSuccessful = false;
            try{
                URL url = new URL("http://people.aero.und.edu/~csantana/457/Exercise%202/Authenticate.php");
                String body =   URLEncoder.encode("act", "UTF-8") + "=" +  URLEncoder.encode("Login", "UTF-8") + "&";
                body +=         URLEncoder.encode("user", "UTF-8") + "=" +  URLEncoder.encode(mUsername, "UTF-8") + "&";
                body +=         URLEncoder.encode("password", "UTF-8") + "=" +  URLEncoder.encode(mPassword, "UTF-8");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(body);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder mysb = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null){
                    mysb.append(line);
                }
                response = new JSONObject(mysb.toString());
                signupSuccessful = response.getBoolean("success");

                SharedPreferences.Editor sessionEditor = sessionInfo.edit();
                sessionEditor.putString("sessionid", connection.getHeaderField("Set-Cookie"));
                sessionEditor.commit();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                return signupSuccessful;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            loginTask = null;

            if (success) {
                Intent intent_MainMenu = new Intent(getApplicationContext(), MainMenu.class);
                startActivity(intent_MainMenu);
            } else {
                mPasswordView.setError("Incorrect username or password");
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            loginTask = null;
        }
    }
}

