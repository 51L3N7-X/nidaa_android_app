package com.nidaa.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nidaa.app.Database.DatabaseClass;
import com.nidaa.app.EntityClass.Table;
import com.nidaa.app.EntityClass.User;
import com.nidaa.app.TableDao.TableDao;
import com.nidaa.app.UserDao.UserDao;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class KitchenActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener {
//    Button button;

    RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);
//        ViewGroup mainGridButton = (ViewGroup) gridLayout.getChildAt(0);
//        mainGridButton.setVisibility(View.GONE);
//        mainGridButton.setTag(1);

        SharedPreferences kitchenSharedPreferences = getSharedPreferences("kitchen_data", Context.MODE_PRIVATE);
        DatabaseClass db = Room.databaseBuilder(getApplicationContext(), DatabaseClass.class, "tables").build();
        TableDao tableDao = db.tableDao();

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final List<Table> tables = tableDao.getAll();

                RecyclerView recyclerView = findViewById(R.id.gird_view);
                int numberOfColumns = 2;
                recyclerView.setLayoutManager(new GridLayoutManager(KitchenActivity.this, numberOfColumns));
                adapter = new RecyclerViewAdapter(KitchenActivity.this, tables);
                adapter.setClickListener(KitchenActivity.this);
                recyclerView.setAdapter(adapter);

            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        try {
            SharedPreferences kitchenSharedPreferences = getSharedPreferences("kitchen_data", Context.MODE_PRIVATE);
            String restaurant_name = kitchenSharedPreferences.getString("restaurant_name", "");
//        Toast.makeText(this, adapter.getItem(position), Toast.LENGTH_SHORT).show();
            JSONObject data = new JSONObject();
            data.put("restaurant_name", restaurant_name);
            data.put("password", kitchenSharedPreferences.getString("password", ""));
            data.put("sendTo", restaurant_name + "_" + adapter.getItem(position));
            Log.e("data:", data.toString());
            new SendThread(data.toString(), "https://www.nidaa.online/api/send").start();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    class SendThread extends Thread {
        private String jsonData;
        private String url;

        public SendThread(String jsonData, String url) {
            this.jsonData = jsonData;
            this.url = url;
        }

        @Override
        public void run() {
            try {
                // Code for sending POST request
                URL url = new URL(this.url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                // Add the data to the request
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonData);
                os.flush();
                os.close();

                int status = conn.getResponseCode();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                final String response = sb.toString();
                JSONObject json = new JSONObject(response);
                String message = json.getString("message");

                // Send the request and read the response

                // Update the UI with the result
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(KitchenActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(KitchenActivity.this, "Error happened", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

}