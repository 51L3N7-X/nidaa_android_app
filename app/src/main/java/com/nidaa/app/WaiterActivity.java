package com.nidaa.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;


public class WaiterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sp = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.wlc).toLowerCase() + " " + sp.getString("username", ""));
        //TODO: ask for auto start permission for all brands
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("message"));
        SharedPreferences sp = getSharedPreferences("views", Context.MODE_PRIVATE);
        String arr = sp.getString("views", "");
        Gson gson = new Gson();
        if (!arr.isEmpty()) {
            LinearLayout layout = findViewById(R.id.container);
            layout.removeAllViews();
            Type type = new TypeToken<List<Card>>() {
            }.getType();
            List<Card> views = gson.fromJson(arr, type);
            int length = views.size();
            for (int i = 0; i < length; i++) {
                addCard(views.get(i).order, views.get(i).table, views.get(i).time);
            }
        }
    }

    private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String order = intent.getStringExtra("order");
            String table = intent.getStringExtra("table");
            String time = intent.getStringExtra("time");
            addCard(order, table, time);
        }
    };

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        LinearLayout layout = findViewById(R.id.container);
        final int childCount = layout.getChildCount();
        List<Card> list = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            View v = layout.getChildAt(i);
            String order = ((TextView) v.findViewById(R.id.order)).getText().toString();
            String table = ((TextView) v.findViewById(R.id.table)).getText().toString();
            String time = ((TextView) v.findViewById(R.id.time)).getText().toString();
            Card card = new Card(order, table, time);
            list.add(card);
        }
        Gson gson = new Gson();
        String arr = gson.toJson(list);
        Log.e("list:", arr);
        SharedPreferences sp = getSharedPreferences("views", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("views", arr);
        editor.apply();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            new MaterialAlertDialogBuilder(WaiterActivity.this)
                    .setTitle(R.string.areyousure)
                    .setMessage(R.string.logoutareyousure)
                    .setPositiveButton("LOGOUT", (dialogInterface, ii) -> {
                        LoginDialog loginDialog = new LoginDialog(WaiterActivity.this);
                        LogOut logOut = new LogOut(loginDialog);
                        logOut.start();

                    }).setNegativeButton("CANCEL", (dialogInterface, i) -> {


                    }).show();
        }
        return true;
    }

    private void addCard(String name, String table, String time) {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.card, null);
        LinearLayout layout = findViewById(R.id.container);
        TextView order = view.findViewById(R.id.order);
        TextView tableNumber = view.findViewById(R.id.table);
        TextView timeText = view.findViewById(R.id.time);
        Button done = view.findViewById(R.id.done);

        order.setText(name);
        tableNumber.setText(table);
        timeText.setText(time);
        done.setOnClickListener(v -> layout.removeView(view));
        layout.addView(view);
    }


    class LogOut extends Thread {
        private final LoginDialog logoutDialog;

        LogOut(LoginDialog logoutDialog) {
            this.logoutDialog = logoutDialog;
        }

        public void run() {
            runOnUiThread(() -> logoutDialog.startLoginDialog(R.layout.logout_dialog));

            SharedPreferences sharedpreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedpreferences.edit();
            int length = sharedpreferences.getInt("sub_length", 1);
//            Log.e("length:", String.valueOf(length));
            CountDownLatch latch = new CountDownLatch(length);

            for (int i = 0; i < length; i++) {
                String sub = sharedpreferences.getString("sub" + i, "sub" + i);
                Log.e("sub", String.valueOf(sub));
                int finalI = i;
                FirebaseMessaging.getInstance().unsubscribeFromTopic(sub).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        latch.countDown();
                        Log.e("subN: ", "sub" + finalI);
                    }

                });
            }
            try {
                latch.await();
                Log.e("done", "yes");
                editor.clear().apply();
                logoutDialog.dismissDialog();
                Intent intent = new Intent(WaiterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}