package com.nidaa.app;

//import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;

//import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.content.res.Configuration;
//import android.nfc.Tag;
//import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
//import android.view.View;
import android.widget.Button;
//import android.widget.RadioButton;
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
import java.net.URL;
import java.nio.charset.StandardCharsets;
//import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


//import io.socket.client.IO;
//import io.socket.client.Socket;
//import io.socket.emitter.Emitter;


//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;


public class LoginActivity extends AppCompatActivity {
    Button login_btn;
    TextInputLayout et_restaurant_name, et_username, et_password;
    RadioGroup login_as;
//    RadioButton checkedRadioButton;
//    int i = 0;

//    private Socket mSocket;
//
//    {
//        try {
//            mSocket = IO.socket("https://letsgoo.jjjjkkjjjjkkm.repl.co");
//
//        } catch (URISyntaxException e) {
//            Log.e(TAG, String.valueOf(e));
//        }
//    }


    private static final String TAG = "MyActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        et_username = findViewById(R.id.username);
        et_restaurant_name = findViewById(R.id.restaurant_name);
        et_password = findViewById(R.id.password);
        login_as = findViewById(R.id.login_as);
        login_btn = findViewById(R.id.login_btn);

        login_btn.setOnClickListener(view -> {
            String username = Objects.requireNonNull(et_username.getEditText()).getText().toString().trim();
            Log.e(TAG, username);
            String password = Objects.requireNonNull(et_password.getEditText()).getText().toString().trim();
            Log.e(TAG, password);
            String restaurant_name = Objects.requireNonNull(et_restaurant_name.getEditText()).getText().toString().trim();
            Log.e(TAG, restaurant_name);
            int selectedId = login_as.getCheckedRadioButtonId();
            if (username.length() >= 1 && password.length() >= 1 && restaurant_name.length() >= 1 && selectedId != -1) {
//                    checkedRadioButton = (RadioButton) findViewById(selectedId);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        Log.e(TAG,checkedRadioButton.att);
//                    }
//                    Configuration config = new Configuration(getResources().getConfiguration());
//                    config.setLocale(new Locale("en"));
//                    String test = createConfigurationContext(config).getResources().getString(R.string.kitchen);

                String checkedText = getResources().getResourceEntryName(selectedId);
                JSONObject data = new JSONObject();
                try {
                    data.put("login_as", checkedText);
                    data.put("restaurant_name", restaurant_name);
                    data.put("username", username);
                    data.put("password", password);
                    LoginDialog loginDialog = new LoginDialog(LoginActivity.this);
                    LoginExecutor loginExecutor = new LoginExecutor(loginDialog, LoginActivity.this);
                    loginExecutor.execute(data.toString());
//                    LoginPost task = new LoginPost();
//                    task.execute(data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });
        String channelId = "fcm_default_channel";
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

// The user-visible name of the group
            notificationManager.createNotificationChannelGroup(new NotificationChannelGroup("test", "Nidaa Notifications"));
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Orders Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setGroup("test");
            notificationManager.createNotificationChannel(channel);
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//
//            notificationManager.createNotificationChannel(
//                    new NotificationChannel(
//                            // channel id
//                            "dd",
//                            // user visible name of the channel
//                            "Misc",
//                            // shows everywhere, makes noise, but does not visually intrude.
//                            NotificationManager.IMPORTANCE_HIGH));
//            NotificationChannel channel1 = new NotificationChannel("channel1", "Channel 1", NotificationManager.IMPORTANCE_HIGH);
//            channel1.setDescription("Channe 1 is here");
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel1);
//            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
//            Notification notification = new NotificationCompat.Builder(this , "channel1")
//                    .setSmallIcon(R.drawable.ic_launcher_foreground)
//                    .setContentTitle("testy")
//                    .setContentText("hello?")
//                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                    .build();
//            notificationManagerCompat.notify(1 , notification);

//            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            NotificationChannel channel = new NotificationChannel(channelId, "Orders Notifications", NotificationManager.IMPORTANCE_HIGH);
//            channel.enableVibration(true);
////            channel.enableLights(true);
//            channel.setDescription("test test");
//            channel.setBypassDnd(true);
////            channel.setSound(defaultSoundUri);
//            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//            NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(
//                    new NotificationChannel(
//                            // channel id
//                            "t",
//                            // user visible name of the channel
//                            "Misc",
//                            // shows everywhere, makes noise, but does not visually intrude.
//                            NotificationManager.IMPORTANCE_DEFAULT));
////            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
//            NotificationCompat.Builder notificationBuilder =
//                    new NotificationCompat.Builder(this, "t")
//                            .setContentTitle("test")
//                            .setContentText("test")
//                            .setAutoCancel(true)
//                            .setSound(defaultSoundUri)
////                        .setContentIntent(pendingIntent)
//                            .setSmallIcon(R.drawable.notification)
//                            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
//                            .setPriority(NotificationCompat.PRIORITY_HIGH)
//                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                            .setNumber(++i);
//            notificationManager.notify("Test" , 0 /* ID of notification */, notificationBuilder.build());
//            notificationManager.createNotificationChannel(channel);
//
//            NotificationChannel ch = notificationManager.getNotificationChannel(channelId);
//            Log.e("im: ", String.valueOf(ch.getImportance()));
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
//        mSocket.connect();
//        mSocket.on("to waiter", new Emitter.Listener() {
//
//            @Override
//            public void call(Object... args) {
//                new Thread() {
//                    public void run() {
//                        try {
//                            runOnUiThread(new Runnable() {
//
//                                @Override
//                                public void run() {
//
//                                    JSONObject data = (JSONObject) args[0];
//                                    String orderName = null;
//                                    String table = null;
//                                    try {
//                                        orderName = data.getString("order");
//                                        table = data.getString("table");
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
////                                addCard(orderName, table);
//                                    Log.e(TAG, "" + args[0]);
//
//                                }
//                            });
//                            Thread.sleep(100);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }.start();
//            }
//        });
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
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

//
//    @SuppressLint("StaticFieldLeak")
//    private class LoginPost extends AsyncTask<String, Void, StringBuilder> {
//        private LoginDialog loginDialog;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            loginDialog = new LoginDialog(MainActivity.this);
//            loginDialog.startLoginDialog();
//        }
//
//        @Override
//        protected StringBuilder doInBackground(String... data) {
//            try {
//                URL url = new URL("https://www.nidaa.online/api/login");
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("POST");
//                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//                connection.setRequestProperty("Accept", "application/json");
//                connection.setDoInput(true);
//                connection.setDoOutput(true);
//                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
//                writer.write(data[0]);
//                writer.flush();
//
//                StringBuilder sb = new StringBuilder();
//                int status = connection.getResponseCode();
//                Log.e(TAG, String.valueOf(status));
//                if (status == HttpURLConnection.HTTP_OK) {
//                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
//                    String line;
//                    while ((line = br.readLine()) != null) {
//                        sb.append(line);
//                    }
//                    br.close();
////                    Log.e(TAG, sb.toString());
////                    return sb;
//                    SharedPreferences sharedpreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedpreferences.edit();
//                    JSONObject dataa = new JSONObject(sb.toString());
//                    JSONArray subscribeTo = dataa.getJSONArray("receive");
//                    int length = subscribeTo.length();
//                    CountDownLatch latch = new CountDownLatch(length);
//                    for (int i = 0; i < length; i++) {
//                        String sub = null;
//                        try {
//                            sub = subscribeTo.get(i).toString();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        int finalI = i;
//                        String finalSub = sub;
//                        FirebaseMessaging.getInstance().subscribeToTopic(subscribeTo.get(i).toString())
//                                .addOnCompleteListener(task -> {
//                                    if (task.isSuccessful()) {
//                                        Log.e("sub", "suuub");
//                                        editor.putString("sub" + finalI, finalSub);
//                                        latch.countDown();
//                                    }
//                                });
//                    }
//                    latch.await();
//                    editor.putInt("sub_length", length);
//                    editor.putString("restaurant_name", dataa.get("restaurant_name").toString());
//                    editor.putString("username", dataa.get("username").toString());
//                    editor.putString("password", dataa.get("password").toString());
//                    editor.apply();
//                    Log.e("done", "yes");
//                    return sb;
//                } else {
//
//                    return null;
//                }
//
//
//            } catch (IOException | JSONException | InterruptedException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(StringBuilder stringBuilder) {
//            super.onPostExecute(stringBuilder);
//            if (stringBuilder == null) {
//                loginDialog.dismissDialog();
//                Toast.makeText(MainActivity.this, "Can't log in", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
//            loginDialog.dismissDialog();
//            startActivity(intent);
//            finish();
//        }
////
//    }

    public class LoginExecutor {
        private final Context context;
        private final Executor executor;
        private final LoginDialog loginDialog;
        private final Handler handler;

        public LoginExecutor(LoginDialog loginDialog, Context context) {
            this.loginDialog = loginDialog;
            this.context = context;
            this.executor = Executors.newSingleThreadExecutor();
            this.handler = new Handler(context.getMainLooper());
        }

        public void execute(String data) {
            handler.post(() -> loginDialog.startLoginDialog(R.layout.login_dialog));

            executor.execute(() -> {
                try {
                    URL url = new URL("https://www.nidaa.online/api/login");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                    writer.write(data);
                    writer.flush();

                    StringBuilder sb = new StringBuilder();
                    int status = connection.getResponseCode();
                    Log.e(TAG, String.valueOf(status));
                    if (status == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }
                        br.close();
                        SharedPreferences sharedpreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        JSONObject dataa = new JSONObject(sb.toString());
                        JSONArray subscribeTo = dataa.getJSONArray("receive");
                        int length = subscribeTo.length();
                        CountDownLatch latch = new CountDownLatch(length);
                        for (int i = 0; i < length; i++) {
                            String sub = null;
                            try {
                                sub = subscribeTo.get(i).toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            int finalI = i;
                            String finalSub = sub;
                            FirebaseMessaging.getInstance().subscribeToTopic(subscribeTo.get(i).toString())
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Log.e("sub", "suuub");
                                            editor.putString("sub" + finalI, finalSub);
                                            latch.countDown();
                                        } else {
                                            //TODO:end the task , toast , and collapse the dialog
                                        }
                                    });
                        }
                        latch.await();
                        editor.putInt("sub_length", length);
                        editor.putString("restaurant_name", dataa.get("restaurant_name").toString());
                        editor.putString("username", dataa.get("username").toString());
                        editor.putString("password", dataa.get("password").toString());
                        editor.apply();
                        Log.e("done", "yes");

                        handler.post(() -> {
                            loginDialog.dismissDialog();
                            Toast.makeText(context, R.string.login_successfully, Toast.LENGTH_SHORT).show();
                        });

                        handler.post(() -> {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        handler.post(() -> {
                            loginDialog.dismissDialog();
                            Toast.makeText(context, "Can't log in", Toast.LENGTH_SHORT).show();
                        });

                    }


                } catch (IOException | JSONException | InterruptedException e) {
                    e.printStackTrace();
                    handler.post(() -> {
                        loginDialog.dismissDialog();
                        Toast.makeText(context, R.string.login_error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        }
    }
}