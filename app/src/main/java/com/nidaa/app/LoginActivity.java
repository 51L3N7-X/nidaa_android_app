package com.nidaa.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nidaa.app.Database.DatabaseClass;
import com.nidaa.app.EntityClass.Table;
import com.nidaa.app.TableDao.TableDao;


public class LoginActivity extends AppCompatActivity {
    Button login_btn;
    TextInputLayout et_restaurant_name, et_username, et_password;
    RadioGroup login_as;

    private static final String TAG = "MyActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences waiterSharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences kitchenSharedPreferences = getSharedPreferences("kitchen_data" , Context.MODE_PRIVATE);
        String waiter_restaurant_name = waiterSharedPreferences.getString("restaurant_name", null);
        String waiterUsername = waiterSharedPreferences.getString("username", null);
        String waiterPassword = waiterSharedPreferences.getString("password", null);
        String kitchen_restaurant_name = kitchenSharedPreferences.getString("restaurant_name" , null);
        String kitchenPassword = kitchenSharedPreferences.getString("password" , null);
        String token = waiterSharedPreferences.getString("token", null);
        if (token == null) {
            SharedPreferences.Editor editor = waiterSharedPreferences.edit();
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                Log.e("token :", task.getResult());
                editor.putString("token", task.getResult()).apply();
            });
        }
        if (waiter_restaurant_name != null && waiterUsername != null && waiterPassword != null) {
            startActivity(new Intent(LoginActivity.this, WaiterActivity.class));
            finish();
        } else if (kitchen_restaurant_name != null & kitchenPassword != null) {
            startActivity(new Intent(LoginActivity.this , KitchenActivity.class));
            finish();
        }

        setContentView(R.layout.login);

        et_username = findViewById(R.id.username);
        et_restaurant_name = findViewById(R.id.restaurant_name);
        et_password = findViewById(R.id.password);
        login_as = findViewById(R.id.login_as);
        login_btn = findViewById(R.id.login_btn);

        login_btn.setOnClickListener(view -> {
            String SecUsername = Objects.requireNonNull(et_username.getEditText()).getText().toString().trim();
            Log.e(TAG, SecUsername);
            String SecPassword = Objects.requireNonNull(et_password.getEditText()).getText().toString().trim();
            Log.e(TAG, SecPassword);
            String SecRestaurantName = Objects.requireNonNull(et_restaurant_name.getEditText()).getText().toString().trim();
            Log.e(TAG, SecRestaurantName);
            int selectedId = login_as.getCheckedRadioButtonId();
            if (SecUsername.length() >= 1 && SecPassword.length() >= 1 && SecRestaurantName.length() >= 1 && selectedId != -1) {
                String checkedText = getResources().getResourceEntryName(selectedId);
                JSONObject data = new JSONObject();
                try {
                    data.put("login_as", checkedText);
                    data.put("restaurant_name", SecRestaurantName);
                    data.put("username", SecUsername);
                    data.put("password", SecPassword);
                    data.put("token", getSharedPreferences("user_data", MODE_PRIVATE).getString("token", ""));
                    LoginDialog loginDialog = new LoginDialog(LoginActivity.this);
                    LoginExecutor loginExecutor = new LoginExecutor(loginDialog, LoginActivity.this);
                    loginExecutor.execute(data.toString(), checkedText);

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

            notificationManager.createNotificationChannelGroup(new NotificationChannelGroup("test", "Nidaa Notifications"));
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Orders Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setGroup("test");
            notificationManager.createNotificationChannel(channel);

        }
    }

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

        public void execute(String data, String login_as) {
            handler.post(() -> loginDialog.startLoginDialog(R.layout.login_dialog , R.string.login_dialog_text));

            executor.execute(() -> {
                try {
                    URL url = new URL("https://www.nidaa.online/api/login");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream() , StandardCharsets.UTF_8));
                    writer.write(data);
                    writer.flush();
                    writer.close();

                    int status = connection.getResponseCode();
                    Log.e(TAG, String.valueOf(status));
                    if (status == HttpURLConnection.HTTP_OK) {
                        StringBuilder sb = new StringBuilder();
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }
                        br.close();
                        SharedPreferences sharedpreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        JSONObject SecData = new JSONObject(sb.toString());

                        if (login_as.equals("waiter")) {

                            JSONArray subscribeTo = SecData.getJSONArray("receive");
                            int length = subscribeTo.length();
                            CountDownLatch latch = new CountDownLatch(length);
                            for (int i = 0; i < length; i++) {
                                String sub = subscribeTo.get(i).toString();
                                int index = i;
                                FirebaseMessaging.getInstance().subscribeToTopic(subscribeTo.get(i).toString())
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                editor.putString("sub" + index, sub);
                                                latch.countDown();
                                            }
                                        });
                            }
                            latch.await();
                            editor.putInt("sub_length", length);
                            editor.putString("restaurant_name", SecData.get("restaurant_name").toString());
                            editor.putString("username", SecData.get("username").toString());
                            editor.putString("password", SecData.get("password").toString());
                            editor.apply();
                            Log.e("done", "yes");

                            handler.post(() -> {
                                loginDialog.dismissDialog();
                                Toast.makeText(context, R.string.login_successfully, Toast.LENGTH_SHORT).show();
                            });

                            handler.post(() -> {
                                Intent intent = new Intent(LoginActivity.this, WaiterActivity.class);
                                startActivity(intent);
                                finish();
                            });

                        } else if (login_as.equals("kitchen")) {
                            SharedPreferences kitchenSharedPreferences = getSharedPreferences("kitchen_data", Context.MODE_PRIVATE);
                            SharedPreferences.Editor kEditor = kitchenSharedPreferences.edit();
                            DatabaseClass db = Room.databaseBuilder(getApplicationContext(), DatabaseClass.class, "tables").build();
                            TableDao tableDao = db.tableDao();
                            String max = SecData.getString("max");
                            int length = Integer.parseInt(max);
                            CountDownLatch latch = new CountDownLatch(length);
                            for (int i = 1; i <= length; i++) {
                                tableDao.insertTable(new Table(String.valueOf(i)));
                                Log.e("i:" ,String.valueOf(i));
                                latch.countDown();

                            }
                            latch.await();
                            kEditor.putString("restaurant_name", SecData.getString("restaurant_name"));
                            kEditor.putString("password", SecData.getString("password"));
                            kEditor.apply();
                            handler.post(() -> {
                                loginDialog.dismissDialog();
                                Toast.makeText(context, R.string.login_successfully, Toast.LENGTH_SHORT).show();
                            });

                            handler.post(() -> {
                                Intent intent = new Intent(LoginActivity.this, KitchenActivity.class);
                                startActivity(intent);
                                finish();
                            });
                        }
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