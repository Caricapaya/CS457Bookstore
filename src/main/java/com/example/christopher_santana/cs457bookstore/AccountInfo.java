package com.example.christopher_santana.cs457bookstore;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class AccountInfo extends AppCompatActivity {
    ListView booksPurchased;
    TextView usernameLabel;
    TextView numberPurchasedLabel;
    TextView moneySpentLabel;
    SharedPreferences sessionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        usernameLabel = (TextView) findViewById(R.id.account_info_variable_username);
        numberPurchasedLabel = (TextView) findViewById(R.id.account_info_variable_number_purchased);
        moneySpentLabel = (TextView) findViewById(R.id.account_info_variable_money_spent);
        booksPurchased = (ListView) findViewById(R.id.account_info_list_view);

        sessionInfo = getSharedPreferences("SessionInfo", 0);

        new GetAccountInfoTask().execute();
    }

    private class GetAccountInfoTask extends AsyncTask<Void, Void, Boolean>{
        JSONObject response;

        @Override
        protected Boolean doInBackground(Void... voids) {
            try{
                HashMap<String, String> params = new HashMap<>();
                params.put("act", "Account+Info");
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
                Log.d("DEBUG", mysb.toString());
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
                ArrayList<Book> purchasedBooks = new ArrayList<>();
                ArrayList<Integer> purchasedCount = new ArrayList<>();
                try{
                    String username = response.getString("username");
                    Double spent = response.getDouble("money spent");
                    int count = response.getInt("total count");

                    JSONArray books = response.getJSONArray("books");
                    for (int i = 0; i < books.length(); i++) {
                        JSONObject jsonBook = books.getJSONObject(i);
                        Book tempBook = new Book();
                        tempBook.setTitle(jsonBook.getString("title"));
                        tempBook.setPrice(jsonBook.getDouble("price"));
                        tempBook.setISBN(jsonBook.getString("isbn"));

                        int tempCount = jsonBook.getInt("count");
                        purchasedBooks.add(tempBook);
                        purchasedCount.add(tempCount);
                    }

                    usernameLabel.setText(username);
                    numberPurchasedLabel.setText(Integer.toString(count));
                    moneySpentLabel.setText(String.format("%.2f", spent));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    booksPurchased.setAdapter(new AccountInfoAdapter(getApplicationContext(), purchasedBooks, purchasedCount));
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Error connecting to server", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
