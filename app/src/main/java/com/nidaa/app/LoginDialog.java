package com.nidaa.app;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

class LoginDialog {
    private final Activity activity;
    private AlertDialog dialog;

    LoginDialog(Activity MainActivity) {
        activity = MainActivity;
    }

    void startLoginDialog(int layout , int text) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(layout , null);
        TextView dialog_text = view.findViewById(R.id.dialog_text);
        dialog_text.setText(text);
        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    void dismissDialog() {
        dialog.dismiss();
    }
}
