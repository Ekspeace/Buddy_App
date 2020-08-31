package com.buddy.buddyapp.Constant;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.buddy.buddyapp.Activities.LoginActivity;
import com.buddy.buddyapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import io.paperdb.Paper;

public class PopUp extends AppCompatActivity {

    static TextView tvTitle, tvDesc;
    static ImageView imIcon;
    static Button btnClose, btnConfirm;
    static LinearLayout linearLayout;
    static CardView cardView;
    private LocalBroadcastManager localBroadcastManager;

    public static void Whatsapp(Context context, String title, String message, int icon) {
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
                        .putExtra(ContactsContract.Intents.Insert.PHONE, "0719073758");

                context.startActivity(intent);
                alertDialog.dismiss();

            }
        });

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    public static void SignOut(Context context) {
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

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

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

    public void Toast(Context context, String title, String message, int icon) {
        Dialog alertDialog = new Dialog(context);
        alertDialog.setContentView(R.layout.pop_up);
        tvTitle = alertDialog.findViewById(R.id.dialog_title);
        tvDesc = alertDialog.findViewById(R.id.dialog_desc);
        imIcon = alertDialog.findViewById(R.id.dialog_icon);
        cardView = alertDialog.findViewById(R.id.pop_up_cardView);
        btnClose = alertDialog.findViewById(R.id.dialog_about_close);
        localBroadcastManager = LocalBroadcastManager.getInstance(context);

        tvTitle.setText(title);
        tvDesc.setText(message);
        imIcon.setImageResource(icon);


        if(icon == R.drawable.error)
        {
            btnClose.setVisibility(View.VISIBLE);
            btnClose.setText("Try Again");
            cardView.setBackgroundResource(R.drawable.error_backg);
            tvDesc.setTextColor(context.getResources().getColor(R.color.colorPrimaryErrorLight));
            tvTitle.setTextColor(context.getResources().getColor(R.color.colorPrimaryErrorLight));

        }

        if (alertDialog.isShowing()) {
            alertDialog.cancel();
        }

      btnClose.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              alertDialog.cancel();
              alertDialog.dismiss();
              Intent intent = new Intent(Common.KEY_TRY);
              localBroadcastManager.sendBroadcast(intent);

          }
      });

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        if(!((Activity) context).isFinishing())
        {
            alertDialog.show();
        }


    }

}
