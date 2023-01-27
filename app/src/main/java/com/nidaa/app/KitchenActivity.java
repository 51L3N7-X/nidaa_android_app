package com.nidaa.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nidaa.app.Database.DatabaseClass;
import com.nidaa.app.EntityClass.Table;
import com.nidaa.app.TableDao.TableDao;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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


        DatabaseClass db = Room.databaseBuilder(getApplicationContext(), DatabaseClass.class, "tables").build();
        TableDao tableDao = db.tableDao();

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            final List<Table> tables = tableDao.getAll();

            RecyclerView recyclerView = findViewById(R.id.gird_view);
            int numberOfColumns = 2;
            recyclerView.setLayoutManager(new GridLayoutManager(KitchenActivity.this, numberOfColumns));
            adapter = new RecyclerViewAdapter(KitchenActivity.this, tables);
            adapter.setClickListener(KitchenActivity.this);
            recyclerView.setAdapter(adapter);

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.log_out) {
            new MaterialAlertDialogBuilder(KitchenActivity.this)
                    .setTitle(R.string.areyousure)
                    .setMessage(R.string.logoutareyousure)
                    .setPositiveButton("LOGOUT", (dialogInterface, ii) -> {
                        LogOut logOut = new LogOut();
                        logOut.start();

                    }).setNegativeButton("CANCEL", (dialogInterface, i) -> {


                    }).show();
        }
        return true;
    }

    @Override
    public void onItemClick(View view, int position) {
        try {
            SharedPreferences kitchenSharedPreferences = getSharedPreferences("kitchen_data", Context.MODE_PRIVATE);
            String restaurant_name = kitchenSharedPreferences.getString("restaurant_name", "");
//        Toast.makeText(this, adapter.getItem(position), Toast.LENGTH_SHORT).show();
            int table = Integer.parseInt(adapter.getItem(position));
            int groupSize = 5;
            int groupNumber = (int) Math.ceil(table / (double) groupSize);
            String group = (groupNumber - 1) * groupSize + 1 + "_" + groupNumber * groupSize;
            Log.e("group:" , group);
            Log.e("groupNumber" , String.valueOf(groupNumber));
            JSONObject data = new JSONObject();
            data.put("restaurant_name", restaurant_name);
            data.put("password", kitchenSharedPreferences.getString("password", ""));
            data.put("title" , "المطبخ");
            data.put("body" , adapter.getItem(position));
            data.put("sendTo", restaurant_name + "_" + group);
            Log.e("data:", data.toString());
            new SendThread(data.toString(), "https://www.nidaa.online/api/send").start();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    class SendThread extends Thread {
        private final String jsonData;
        private final String url;

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
                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");

                // Add the data to the request
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream() , StandardCharsets.UTF_8));
                bw.write(jsonData);
                bw.flush();
                bw.close();

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
                runOnUiThread(() -> Toast.makeText(KitchenActivity.this, message, Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(KitchenActivity.this, "Error happened", Toast.LENGTH_SHORT).show());
            }
        }
    }

    class LogOut extends Thread {
        @Override
        public void run() {
            SharedPreferences kitchenSharedPreferences = getSharedPreferences("kitchen_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = kitchenSharedPreferences.edit();
            DatabaseClass db = Room.databaseBuilder(getApplicationContext(), DatabaseClass.class, "tables").build();
            TableDao tableDao = db.tableDao();
            tableDao.deleteAllTables();
            editor.clear().apply();
            Intent intent = new Intent(KitchenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

}