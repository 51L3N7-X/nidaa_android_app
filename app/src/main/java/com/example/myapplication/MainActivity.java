package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;


import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;


public class MainActivity extends AppCompatActivity {
    Button login_btn;
    TextInputLayout et_restaurant_name, et_username, et_password;
    RadioGroup login_as;
    RadioButton checkedRadioButton;


    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("https://letsgoo.jjjjkkjjjjkkm.repl.co");

        } catch (URISyntaxException e) {
            Log.e(TAG, String.valueOf(e));
        }
    }


    private static final String TAG = "MyActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        et_username = (TextInputLayout) findViewById(R.id.username);
        et_restaurant_name = (TextInputLayout) findViewById(R.id.restaurant_name);
        et_password = (TextInputLayout) findViewById(R.id.password);
        login_as = (RadioGroup) findViewById(R.id.login_as);
        login_btn = (Button) findViewById(R.id.login_btn);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = et_username.getEditText().getText().toString().trim();
                String password = et_password.getEditText().getText().toString().trim();
                String restaurant_name = et_restaurant_name.getEditText().getText().toString().trim();
                int selectedId = login_as.getCheckedRadioButtonId();
                if (username.length() >= 1 && password.length() >= 1 && restaurant_name.length() >= 1 && selectedId != -1) {
                    checkedRadioButton = (RadioButton) findViewById(selectedId);
                    String checkedText = (String) checkedRadioButton.getText();
                    JSONObject data = new JSONObject();
                    try {
                        data.put("login_as", checkedText);
                        data.put("restaurant_name", restaurant_name);
                        data.put("username", username);
                        data.put("password", password);
                        LoginPost task = new LoginPost();
                        task.execute(data.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        String channelId = "fcm_default_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            NotificationChannel channel = new NotificationChannel(channelId, "Orders Notifications", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        }

//        FirebaseMessaging.getInstance().subscribeToTopic("orders")
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        String msg = "Subscribed";
//                        if (!task.isSuccessful()) {
//                            msg = "Subscribe failed";
//                        }
//                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                    }
//                });

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                if (notificationManager.getNotificationChannel("default_notification_channel_id") == null) {
//                NotificationChannel channel = new NotificationChannel(getResources().getString(R.string.default_notification_channel_id),
//                        "Orders Notifications",
//                        NotificationManager.IMPORTANCE_HIGH);
//                channel.enableLights(true);
//                channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
//                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//                channel.setShowBadge(true);
//                channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
//                notificationManager.createNotificationChannel(channel);
//            }
//        }

//        layout = findViewById(R.id.);


//        if(savedInstanceState.getBoolean("hasConnection")) {
//            Log.v(TAG , "true");
//        }
        mSocket.connect();
        mSocket.on("to waiter", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                new Thread() {
                    public void run() {
                        try {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    JSONObject data = (JSONObject) args[0];
                                    String orderName = null;
                                    String table = null;
                                    try {
                                        orderName = data.getString("order");
                                        table = data.getString("table");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
//                                addCard(orderName, table);
                                    Log.e(TAG, "" + args[0]);

                                }
                            });
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
//        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
//            @Override
//            public void onComplete(@NonNull Task<String> task) {
//                if(!task.isSuccessful()) {
//                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
//                    return;
//                }
//
//                String token = task.getResult();
//
//               Log.d(TAG,token);
//                Toast.makeText(MainActivity.this , token , Toast.LENGTH_SHORT).show();
//            }
//
//        });

//    }
//    private void addCard(String name , String table) {
//
//        View view = getLayoutInflater().inflate(R.layout.card , null);
//        TextView order = view.findViewById(R.id.order);
//        Button done = view.findViewById(R.id.done);
//        TextView tableNumber = view.findViewById(R.id.number);
//
//        order.setText(name);
//        tableNumber.setText(table);
//        done.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                layout.removeView(view);
//            }
//        });
//
//        layout.addView(view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String restaurant_name = sharedPreferences.getString("restaurant_name", null);
        String username = sharedPreferences.getString("username", null);
        String password = sharedPreferences.getString("password", null);
//        Log.e("t" , username);
        if (restaurant_name != null && username != null && password != null) {
            startActivity(new Intent(MainActivity.this, MainActivity2.class));
        }
    }


    private class LoginPost extends AsyncTask<String, Void, StringBuilder> {
        @Override
        protected StringBuilder doInBackground(String... data) {
            try {
                URL url = new URL("http://192.168.1.103/api/login");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(data[0]);
                writer.flush();

                StringBuilder sb = new StringBuilder();
                int status = connection.getResponseCode();
                if (status == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                    return sb;
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "login in...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(StringBuilder stringBuilder) {
            if (stringBuilder != null) {
                super.onPostExecute(stringBuilder);
//                Toast.makeText(MainActivity.this, "done", Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, stringBuilder.toString(), Toast.LENGTH_LONG).show();
                SharedPreferences sharedpreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                JSONObject data = null;
                JSONArray subscribeTo = null;
                try {
                    data = new JSONObject(stringBuilder.toString());
                    subscribeTo = (JSONArray) data.getJSONArray("receive");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                int length = subscribeTo.length();
                for (int i = 0; i < length; i++) {
                    String sub = null;
                    try {
                        sub = subscribeTo.get(i).toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int finalI = i;
                    JSONObject finalData = data;
                    String finalSub = sub;
                    FirebaseMessaging.getInstance().subscribeToTopic(sub).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "can't login in", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (finalI == 2) {
                                try {
                                    editor.putInt("sub_length", length);
                                    editor.putString("restaurant_name", finalData.get("restaurant_name").toString());
                                    editor.putString("username", finalData.get("username").toString());
                                    editor.putString("password", finalData.get("password").toString());
                                    editor.putString("sub" + finalI, finalSub);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                                startActivity(intent);
                                finish();
                                Log.e("test", "sub" + finalI);
                                editor.apply();
                            }
                        }

                    });
                }


            } else {
                Toast.makeText(MainActivity.this, "can't login in", Toast.LENGTH_SHORT).show();
            }
        }
    }
}