package com.example.christopher_santana.cs457bookstore;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchResult extends AppCompatActivity {
    TextView userQueryLabel;
    TextView matchCount;
    EditText searchQueryInput;
    ArrayList<Book> matchingBooks;
    ListView searchResultList;

    SharedPreferences sessionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        userQueryLabel = (TextView) findViewById(R.id.result_variable_query);
        matchCount = (TextView) findViewById(R.id.result_variable_number);
        searchResultList = (ListView) findViewById(R.id.result_list);
        searchQueryInput = (EditText) findViewById(R.id.result_query_bar);

        matchingBooks = new ArrayList<>();
        searchResultList.setAdapter(new SearchlistAdapterAnimated(getApplicationContext(), matchingBooks));

        String query = getIntent().getExtras().getString("search query", null);
        new SearchTask(query).execute();

        sessionInfo = getSharedPreferences("SessionInfo", 0);
    }

    private class SearchTask extends AsyncTask<Void, Void, Boolean> {
        JSONObject response;
        String searchQuery;

        public SearchTask(String query){
            searchQuery = query;
        }

        @Override
        protected void onPreExecute() {
            //set user's inital query
            if (searchQuery != null){
                userQueryLabel.setText(searchQuery);
            }
            else{
                userQueryLabel.setText("None");
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (searchQuery == null){
                return false;
            }
            try{
                HashMap<String, String> params = new HashMap<>();
                params.put("query", searchQuery);
                URL url = new URL(LoginActivity.urlBuilder("http://people.aero.und.edu/~csantana/457/Exercise%202/Search.php", params));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder mysb = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null){
                    mysb.append(line);
                }
                response = new JSONObject(mysb.toString());
                return true;
            }
            catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            matchingBooks.clear();
            if (aBoolean){
                try{
                    //set query as interpreted by the server
                    userQueryLabel.setText(response.getString("query"));
                    JSONArray books = response.getJSONArray("books");
                    for (int i = 0; i < books.length(); i++) {
                        JSONObject tempJSON = books.getJSONObject(i);
                        Book tempBook = new Book();
                        tempBook.setTitle(tempJSON.getString("title"));
                        tempBook.setPrice(tempJSON.getDouble("price"));
                        tempBook.setISBN(tempJSON.getString("isbn"));
                        matchingBooks.add(tempBook);
                    }
                    //TODO verify notify/notifyDataSetChanged
                    ((SearchlistAdapterAnimated) searchResultList.getAdapter()).update(matchingBooks);
                    matchCount.setText(Integer.toString(matchingBooks.size()));
                }
                catch (Exception e){
                    e.printStackTrace();
                    matchCount.setText("N/A");
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Error connecting to server", Toast.LENGTH_SHORT).show();
                matchCount.setText("N/A");
            }
        }
    }

    void onClick(View view){
        switch (view.getId()){
            case R.id.result_search_button:
                new SearchTask(searchQueryInput.getText().toString()).execute();
                break;
            case R.id.result_clear_button:
                matchingBooks.clear();
                ((SearchlistAdapterAnimated) searchResultList.getAdapter()).update(matchingBooks);
                userQueryLabel.setText("None");
                matchCount.setText("N/A");
                break;
            case R.id.result_purchase_button:
                doPurchase();
                break;
        }
    }

    void doPurchase(){
        SearchlistAdapterAnimated adapter = (SearchlistAdapterAnimated) searchResultList.getAdapter();
        JSONArray booksToPurchase = new JSONArray();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getBookChecked(i)){
                Book tempBook = (Book) adapter.getItem(i);
                JSONObject jsonBook = new JSONObject();
                try{
                    jsonBook.put("isbn", tempBook.getISBN());
                    jsonBook.put("count", adapter.getNumberBooks(i));
                    booksToPurchase.put(jsonBook);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        new BuyTask(booksToPurchase).execute();

    }

    private class BuyTask extends AsyncTask<Void, Void, Boolean>{
        JSONArray booksToPurchase;
        JSONObject response;
        public BuyTask(JSONArray toPurchase){
            booksToPurchase = toPurchase;
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            if (booksToPurchase.length() == 0){
                return false;
            }
            try{
                HashMap<String, String> params = new HashMap<>();
                params.put("act", "Purchase");
                params.put("books", booksToPurchase.toString());
                URL url = new URL(LoginActivity.urlBuilder("http://people.aero.und.edu/~csantana/457/Exercise%202/UserBooks.php", params));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Cookie", sessionInfo.getString("sessionid", ""));
                connection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder mysb = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null){
                    mysb.append(line);
                }
                response = new JSONObject(mysb.toString());
                return response.getBoolean("success");
            }
            catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean){
                matchingBooks.clear();
                ((SearchlistAdapterAnimated) searchResultList.getAdapter()).update(matchingBooks);
                userQueryLabel.setText("None");
                matchCount.setText("N/A");
                Toast.makeText(getApplicationContext(), "Books purchased!", Toast.LENGTH_SHORT).show();
            }
            else{
                if (booksToPurchase.length() == 0){
                    Toast.makeText(getApplicationContext(), "No books selected", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Error connecting to server", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
