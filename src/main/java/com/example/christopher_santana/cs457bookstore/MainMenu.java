package com.example.christopher_santana.cs457bookstore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class MainMenu extends AppCompatActivity {
    EditText searchBar;
    SharedPreferences sessionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        searchBar = (EditText) findViewById(R.id.main_query_bar);
        sessionInfo = getSharedPreferences("SessionInfo", 0);
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.main_search_button:
                doSearch();
                break;
            case R.id.account_button:
                Intent intent_AccountInfo = new Intent(getApplicationContext(), AccountInfo.class);
                startActivity(intent_AccountInfo);
                break;
            case R.id.sign_out_button:
                doLogout();
                break;
        }
    }

    private void doSearch(){
        String query = searchBar.getText().toString();
        Intent intent_SearchResult = new Intent(getApplicationContext(), SearchResult.class);
        intent_SearchResult.putExtra("search query", query);
        startActivity(intent_SearchResult);
    }

    private void doLogout(){
        new LogOutTask().execute();
    }

    private class LogOutTask extends AsyncTask<Void, Void, Boolean>{
        @Override
        protected Boolean doInBackground(Void... voids) {
            try{
                URL url = new URL("http://people.aero.und.edu/~csantana/457/Exercise%202/Authenticate.php");
                String body =   URLEncoder.encode("act", "UTF-8") + "=" +  URLEncoder.encode("Log Out", "UTF-8");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                connection.setRequestProperty("Cookie", sessionInfo.getString("sessionid", ""));
                writer.write(body);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder mysb = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null){
                    mysb.append(line);
                }
                return true;
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Intent intent_LoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent_LoginActivity);
        }
    }




}
