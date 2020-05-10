package com.example.buddyapp.Constant;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.buddyapp.Activities.EmailActivity;
import com.example.buddyapp.Activities.LoginActivity;
import com.example.buddyapp.Activities.MainActivity;
import com.example.buddyapp.Activities.SplashActivity;
import com.example.buddyapp.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Callable;

import io.paperdb.Paper;

public class PopUp extends AppCompatActivity {

    static TextView tvTitle, tvDesc;
    static ImageView imIcon;
    static Button btnClose, btnConfirm;
    static TextInputLayout InputEmail, InputPassword;
    static LinearLayout linearLayout;
    static CardView cardView;

    public static Dialog emailUserCredential(Context context, String title, String message, int icon) {
        Dialog alertDialog = new Dialog(context);
        alertDialog.setContentView(R.layout.pop_up);

        tvTitle = alertDialog.findViewById(R.id.dialog_title);
        tvDesc = alertDialog.findViewById(R.id.dialog_desc);
        imIcon = alertDialog.findViewById(R.id.dialog_icon);
        btnClose = alertDialog.findViewById(R.id.dialog_close);
        btnConfirm = alertDialog.findViewById(R.id.dialog_confirm);
        InputEmail = alertDialog.findViewById(R.id.Real_user_email_credential);
        InputPassword = alertDialog.findViewById(R.id.Real_user_password_credential);


        tvTitle.setText(title);
        tvDesc.setText(message);
        imIcon.setImageResource(icon);
        InputEmail.getEditText().setText(Common.currentUser.getEmail());

        btnConfirm.setVisibility(View.VISIBLE);
        btnClose.setVisibility(View.VISIBLE);
        InputPassword.setVisibility(View.VISIBLE);
        InputEmail.setVisibility(View.VISIBLE);


        if (alertDialog.isShowing()) {
            alertDialog.cancel();
        }
        btnClose.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = InputEmail.getEditText().getText().toString().trim();
                String Password = InputPassword.getEditText().getText().toString().trim();
                boolean error = false;
                if(!error)
                {
                    if(TextUtils.isEmpty(Email)){
                        InputEmail.setError("Email is Required.");
                        error = true;
                    }

                    if(TextUtils.isEmpty(Password)) {
                        InputPassword.setError("Password is Required.");
                        error = true;
                    }else
                    if(Password.length() < 9){
                        InputPassword.setError("Gmail Password Must be greater 8 Characters");
                        error = true;
                    }
                    if(error)
                        return;

                }
                if(Common.real_user_email != null)
                {
                    Common.real_user_email = null;
                }
                Common.real_user_email = Email;
                Common.real_user_password = Password;
                alertDialog.dismiss();
                context.startActivity(new Intent(context, EmailActivity.class));
            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        return alertDialog;
    }

    public static Dialog Toast(Context context, String title, String message, int icon) {
        Dialog alertDialog = new Dialog(context);
        alertDialog.setContentView(R.layout.pop_up);

        tvTitle = alertDialog.findViewById(R.id.dialog_title);
        tvDesc = alertDialog.findViewById(R.id.dialog_desc);
        imIcon = alertDialog.findViewById(R.id.dialog_icon);
        cardView = alertDialog.findViewById(R.id.pop_up_cardView);

        tvTitle.setText(title);
        tvDesc.setText(message);
        imIcon.setImageResource(icon);

        if(icon == R.drawable.error)
        {
            cardView.setBackgroundResource(R.drawable.error_backg);
            tvDesc.setTextColor(context.getResources().getColor(R.color.colorPrimaryErrorLight));
            tvTitle.setTextColor(context.getResources().getColor(R.color.colorPrimaryErrorLight));

        }


        if (alertDialog.isShowing()) {
            alertDialog.cancel();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               alertDialog.dismiss();
            }
        }, 4000);

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        return alertDialog;
    }

    public static Dialog ToastBtn(Context context, String title, String message, int icon) {
        Dialog alertDialog = new Dialog(context);
        alertDialog.setContentView(R.layout.pop_up);

        tvTitle = alertDialog.findViewById(R.id.dialog_title);
        tvDesc = alertDialog.findViewById(R.id.dialog_desc);
        imIcon = alertDialog.findViewById(R.id.dialog_icon);
        btnClose = alertDialog.findViewById(R.id.dialog_about_close);

        tvTitle.setText(title);
        tvDesc.setText(message);
        imIcon.setImageResource(icon);
        btnClose.setVisibility(View.VISIBLE);

        if (alertDialog.isShowing()) {
            alertDialog.cancel();
        }

        btnClose.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        return alertDialog;
    }
    public static Dialog Whatsapp(Context context, String title, String message, int icon) {
        Dialog alertDialog = new Dialog(context);
        alertDialog.setContentView(R.layout.pop_up);

        tvTitle = alertDialog.findViewById(R.id.dialog_title);
        tvDesc = alertDialog.findViewById(R.id.dialog_desc);
        imIcon = alertDialog.findViewById(R.id.dialog_icon);
        btnClose = alertDialog.findViewById(R.id.dialog_close);
        btnConfirm = alertDialog.findViewById(R.id.dialog_confirm);

        tvTitle.setText(title);
        tvDesc.setText(message);
        imIcon.setImageResource(icon);
        btnConfirm.setVisibility(View.VISIBLE);
        btnClose.setVisibility(View.VISIBLE);


        if (alertDialog.isShowing()) {
            alertDialog.cancel();
        }

        btnClose.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

                intent.putExtra(ContactsContract.Intents.Insert.NAME, "BuddyApp")
                        .putExtra(ContactsContract.Intents.Insert.PHONE, "0895623115");

                context.startActivity(intent);
                alertDialog.dismiss();

            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        return alertDialog;
    }
    public static void smallToast(Context context, View layout, int icon, String message, int duration)
    {

        tvDesc = layout.findViewById(R.id.toast_message);
        imIcon = layout.findViewById(R.id.toast_img);
        linearLayout = layout.findViewById(R.id.custom_toast_container);

        if(icon == R.drawable.small_error)
        {
            linearLayout.setBackgroundResource(R.drawable.error_backg);
            tvDesc.setTextColor(context.getResources().getColor(R.color.colorPrimaryErrorLight));
        }

        tvDesc.setText(message);
        imIcon.setImageResource(icon);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 40);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }
    public static Dialog SignOut(Context context) {
        Dialog alertDialog = new Dialog(context);
        alertDialog.setContentView(R.layout.pop_up);

        tvTitle = alertDialog.findViewById(R.id.dialog_title);
        tvDesc = alertDialog.findViewById(R.id.dialog_desc);
        imIcon = alertDialog.findViewById(R.id.dialog_icon);
        btnClose = alertDialog.findViewById(R.id.dialog_close);
        btnConfirm = alertDialog.findViewById(R.id.dialog_confirm);



        tvTitle.setText("Sign Out...");
        tvDesc.setText("Are you sure? You want to sign out?");
        imIcon.setImageResource(R.drawable.sign_out);

        btnConfirm.setVisibility(View.VISIBLE);
        btnClose.setVisibility(View.VISIBLE);


        if (alertDialog.isShowing()) {
            alertDialog.cancel();
        }
        btnClose.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Paper.book().destroy();
                context.startActivity(new Intent(context, LoginActivity.class));
            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        return alertDialog;
    }

}
