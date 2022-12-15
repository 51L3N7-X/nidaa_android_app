package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.zip.Inflater;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("message"));
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String order = intent.getStringExtra("order");
            String table = intent.getStringExtra("table");
            addCard(order, table);
        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out:
                new MaterialAlertDialogBuilder(MainActivity2.this)
                        .setTitle("Are You Sure?")
                        .setMessage("Are you sure to log out ? you will need to log in again to receive orders.")
                        .setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int ii) {
                                SharedPreferences sharedpreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                int length = sharedpreferences.getInt("sub_length", 1);
                                for (int i = 0; i < length; i++) {
                                    String sub = sharedpreferences.getString("sub" + i, "sub" + i);
                                    Log.e("sub", String.valueOf(sub));
                                    int finalI = i;
                                    FirebaseMessaging.getInstance().unsubscribeFromTopic(sub).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(MainActivity2.this, "Can't logout please try again later", Toast.LENGTH_SHORT);
                                                return;
                                            }
                                            editor.remove("sub" + finalI);

                                        }
                                    });
                                }
                                editor.remove("restaurant_name");
                                editor.remove("password");
                                editor.remove("username");
                                editor.apply();
                                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                            }
                        }).show();
        }
        return true;
    }

    private void addCard(String name, String table) {
        View view = getLayoutInflater().inflate(R.layout.card, null);
        LinearLayout layout = (LinearLayout) findViewById(R.id.container);
        TextView order = view.findViewById(R.id.order);
        TextView tableNumber = view.findViewById(R.id.textView);
        Button done = view.findViewById(R.id.done);

        order.setText(name);
        tableNumber.setText(table);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.removeView(view);
            }
        });
        layout.addView(view);
    }
}