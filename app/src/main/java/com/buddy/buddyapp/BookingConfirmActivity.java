package com.buddy.buddyapp;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.buddy.buddyapp.Activities.MenuActivity;
import com.buddy.buddyapp.Constant.Common;
import com.buddy.buddyapp.Constant.PopUp;
import com.buddy.buddyapp.Email.GmailSender;
import com.buddy.buddyapp.Model.BookingInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

import io.paperdb.Paper;

public class BookingConfirmActivity extends AppCompatActivity {

    SimpleDateFormat simpleDateFormat;
    LocalBroadcastManager localBroadcastManager;
    TextView txt_services_booking_text,txt_booking_time_text,txt_user_name_booking,txt_email_user_booking,
    txt_user_phone_booking,txt_user_address_booking, txt_category_booking_text,txt_price_booking_text;
    LinearLayout dialog;
    Button btn_confirm;
    private View layout;
    private Toolbar toolbar;
    final BroadcastReceiver confirmBookingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setData();
        }
    };
    private final PopUp PopUp = new PopUp();
    private final BroadcastReceiver NetworkError = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           BookingProcess();
        }
    };

    public void confirmBooking() {
       btn_confirm.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              BookingProcess();
           }
       });
   }

    private void BookingProcess(){
        dialog.setVisibility(View.VISIBLE);
        if (Common.isOnline(BookingConfirmActivity.this)) {
            String startTime;
            if (Common.currentService.getName().contains(" "))
                startTime = Common.convertTimeToString(Common.currentTimeSlot);
            else
                startTime = Common.ConvertTimeToString(Common.currentTimeSlot);
            String[] convertTime = startTime.split("-");

            String[] startTimeConvert = convertTime[0].split(":");
            int startHourInt = Integer.parseInt(startTimeConvert[0].trim());
            int startMinInt = Integer.parseInt(startTimeConvert[1].trim());

            Calendar bookingDateWithoutHouse = Calendar.getInstance();
            bookingDateWithoutHouse.setTimeInMillis(Common.currentDate.getTimeInMillis());
            bookingDateWithoutHouse.set(Calendar.HOUR_OF_DAY, startHourInt);
            bookingDateWithoutHouse.set(Calendar.MINUTE, startMinInt);

            Timestamp timestamp = new Timestamp(bookingDateWithoutHouse.getTime());
            final BookingInformation bookingInformation = new BookingInformation();

            bookingInformation.setTimestamp(timestamp);
            bookingInformation.setCustomerName(Common.currentUser.getName());
            bookingInformation.setCustomerEmail(Common.currentUser.getEmail());
            bookingInformation.setCustomerPhone(Common.currentUser.getPhoneNumber());
            bookingInformation.setCustomerAddress(Common.currentUser.getAddress());
            bookingInformation.setServiceId(Common.currentService.getServiceId());
            bookingInformation.setCategoryName(Common.currentCategory.getCategoryName());
            bookingInformation.setServiceName(Common.currentService.getName());
            bookingInformation.setPrice(Common.currentCategory.getCategoryPrice());
            if (Common.currentService.getName().contains(" "))
                bookingInformation.setTime(new StringBuilder(Common.convertTimeToString(Common.currentTimeSlot))
                        .append(" at ")
                        .append(simpleDateFormat.format(Common.currentDate.getTime())).toString());
            else
                bookingInformation.setTime(new StringBuilder(Common.ConvertTimeToString(Common.currentTimeSlot))
                        .append(" at ")
                        .append(simpleDateFormat.format(Common.currentDate.getTime())).toString());
            bookingInformation.setSlot(Long.valueOf(Common.currentTimeSlot));
            DocumentReference bookingDate;

            if (Common.currentService.getName().contains(" ")) {
                bookingDate = FirebaseFirestore.getInstance()
                        .collection("Time_Slot_Car_Wash")
                        .document("Slot")
                        .collection(Common.simpleDataFormat.format(Common.currentDate.getTime()))
                        .document(String.valueOf(Common.currentTimeSlot));
            } else {
                bookingDate = FirebaseFirestore.getInstance()
                        .collection("Time_Slot_Cleaning")
                        .document("Slot")
                        .collection(Common.simpleDataFormat.format(Common.currentDate.getTime()))
                        .document(String.valueOf(Common.currentTimeSlot));
            }
            bookingDate.set(bookingInformation)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            addToUserBooking(bookingInformation);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    com.buddy.buddyapp.Constant.PopUp.smallToast(BookingConfirmActivity.this, layout, R.drawable.small_error,e.getMessage(),Toast.LENGTH_SHORT);
                }
            });
        }else{
            PopUp.Toast(BookingConfirmActivity.this, "Connection...", "Please check your internet connectivity and try again", R.drawable.error);
            dialog.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirm);
        txt_services_booking_text = findViewById(R.id.txt_services_booking_text);
        txt_booking_time_text = findViewById(R.id.txt_booking_time_text);
        txt_user_name_booking = findViewById(R.id.txt_user_name_booking);
        txt_email_user_booking = findViewById(R.id.txt_email_user_booking);
        txt_user_phone_booking = findViewById(R.id.txt_user_phone_booking);
        txt_user_address_booking= findViewById(R.id.txt_user_address_booking);
        txt_category_booking_text = findViewById(R.id.txt_category_booking_text);
        toolbar = findViewById(R.id.toolbaruserBooking);
        txt_price_booking_text = findViewById(R.id.txt_price_booking_text);
        btn_confirm = findViewById(R.id.btn_confirm);

        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        setData();
        localBroadcastManager.registerReceiver(NetworkError,new IntentFilter(Common.KEY_TRY));
        localBroadcastManager.registerReceiver(confirmBookingReceiver,new IntentFilter(Common.KEY_CONFIRM_BOOKING));

        dialog = findViewById(R.id.ProgressBar_booking_confirm);

        Actionbar();
         confirmBooking();

    }

    private void addToUserBooking(final BookingInformation bookingInformation) {
        if (Common.isOnline(this)) {
            final CollectionReference userBooking;
            if (Common.currentService.getName().contains(" ")) {
                userBooking = FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(Common.currentUser.getUserId())
                        .collection("Booking_Car_Wash");
            } else {
                userBooking = FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(Common.currentUser.getUserId())
                        .collection("Booking_Cleaning");
            }
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            Timestamp toDayTimeStamp = new Timestamp(calendar.getTime());
            userBooking
                    .whereGreaterThanOrEqualTo("timestamp", toDayTimeStamp)
                    .whereEqualTo("done", false)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.getResult().isEmpty()) {
                                userBooking.document().
                                set(bookingInformation);
                                RealTimeDatabase(bookingInformation);
                                Notify();
                                sendSMSMessage();
                                dialog.setVisibility(View.GONE);
                                if (Common.currentService.getName().contains(" "))
                                    addToCalendar(Common.currentDate,
                                            Common.convertTimeToString(Common.currentTimeSlot));
                                else
                                    addToCalendar(Common.currentDate,
                                            Common.ConvertTimeToString(Common.currentTimeSlot));
                                startActivity(new Intent(BookingConfirmActivity.this, MenuActivity.class));
                                com.buddy.buddyapp.Constant.PopUp.smallToast(BookingConfirmActivity.this, layout, R.drawable.small_success, "Successfully Booked!", Toast.LENGTH_SHORT);
                                finish();
                            } else {
                                dialog.setVisibility(View.GONE);
                                com.buddy.buddyapp.Constant.PopUp.smallToast(BookingConfirmActivity.this, layout, R.drawable.small_error,"Sorry... but already booked for this service",Toast.LENGTH_SHORT);
                                startActivity(new Intent(BookingConfirmActivity.this, MenuActivity.class));
                                finish();
                            }
                        }
                    });
        }else{
            PopUp.Toast(BookingConfirmActivity.this, "Connection...", "Please check your internet connectivity and try again", R.drawable.error);
            dialog.setVisibility(View.GONE);
        }
    }

    private void addToCalendar(Calendar bookingDate, String startDate) {
        String startTime;
        if(Common.currentService.getName().contains(" "))
            startTime = Common.convertTimeToString(Common.currentTimeSlot);
        else
            startTime = Common.ConvertTimeToString(Common.currentTimeSlot);
        String[] convertTime = startTime.split("-");

        String[] startTimeConvert = convertTime[0].split(":");
        int startHourInt = Integer.parseInt(startTimeConvert[0].trim());
        int startMinInt = Integer.parseInt(startTimeConvert[1].trim());

        String[] endTimeConvert = convertTime[1].split(":");
        int endHourInt = Integer.parseInt(endTimeConvert[0].trim());
        int endMinInt = Integer.parseInt(endTimeConvert[1].trim());

        Calendar startEvent = Calendar.getInstance();
        startEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        startEvent.set(Calendar.HOUR_OF_DAY, startHourInt);
        startEvent.set(Calendar.MINUTE, startMinInt);

        Calendar endEvent = Calendar.getInstance();
        endEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        endEvent.set(Calendar.HOUR_OF_DAY, endHourInt);
        endEvent.set(Calendar.MINUTE, endMinInt);

        SimpleDateFormat calendarDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String startEventTime = calendarDateFormat.format(startEvent.getTime());
        String endEventTime = calendarDateFormat.format(endEvent.getTime());

        addToDeviceCalendar(startEventTime, endEventTime,
                new StringBuilder("Buddy ")
                        .append(Common.currentService.getName())
                        .append("Service from")
                        .append(startTime).toString(),
                new StringBuilder("until ").append(endEventTime).toString());
    }

    private void addToDeviceCalendar(String startEventTime, String endEventTime, String description, String location) {
        SimpleDateFormat calendarDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        if (Common.isOnline(this)) {
            try {
                Date end = calendarDateFormat.parse(endEventTime);
                Date start = calendarDateFormat.parse(startEventTime);

                ContentValues event = new ContentValues();
                event.put(CalendarContract.Events.CALENDAR_ID, getCalendar(this));
                event.put(CalendarContract.Events.TITLE, "Buddy Booking");
                event.put(CalendarContract.Events.DESCRIPTION, description);
                event.put(CalendarContract.Events.EVENT_LOCATION, location);

                event.put(CalendarContract.Events.DTSTART, start.getTime());
                event.put(CalendarContract.Events.DTEND, end.getTime());
                event.put(CalendarContract.Events.ALL_DAY, 0);
                event.put(CalendarContract.Events.HAS_ALARM, 1);

                String timeZone = TimeZone.getDefault().getID();


                event.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone);

                Uri calendars;
                if (Build.VERSION.SDK_INT > 8)
                    calendars = Uri.parse("content://com.android.calendar/events");
                else
                    calendars = Uri.parse("content://calendar/events");

                Uri uri_save = this.getContentResolver().insert(calendars, event);
                Paper.init(this);
                Paper.book().write(Common.EVENT_URI_CACHE, uri_save.toString());

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            PopUp.Toast(this, "Connection...", "Please check your internet connectivity and try again", R.drawable.error);
            dialog.setVisibility(View.GONE);
        }
    }
    private void setData() {
        txt_services_booking_text.setText(Common.serviceName);
        if(Common.currentService.getName().contains(" "))
            txt_booking_time_text.setText(new StringBuilder(Common.convertTimeToString(Common.currentTimeSlot))
                    .append(" at ")
                    .append(simpleDateFormat.format(Common.currentDate.getTime())).toString());
        else
            txt_booking_time_text.setText(new StringBuilder(Common.ConvertTimeToString(Common.currentTimeSlot))
                    .append(" at ")
                    .append(simpleDateFormat.format(Common.currentDate.getTime())).toString());

        txt_user_name_booking.setText(Common.currentUser.getName());
        txt_email_user_booking.setText(Common.currentUser.getEmail());
        txt_user_phone_booking.setText(Common.currentUser.getPhoneNumber());
        txt_user_address_booking.setText(Common.currentUser.getAddress());
        txt_price_booking_text.setText(Common.currentCategory.getCategoryPrice());
        txt_category_booking_text.setText(Common.currentCategory.getCategoryName());
    }

    private String getCalendar(Context context) {
        String gmailIdCalendar = "";
        String[] projection ={"_id","calendar_displayName"};
        Uri calendars = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = context.getContentResolver();
        Cursor managedCursor = contentResolver.query(calendars,projection,null,null,null);
        if (managedCursor.moveToFirst())
        {
            String calName;
            int nameCol = managedCursor.getColumnIndex(projection[1]);
            int idCol = managedCursor.getColumnIndex(projection[0]);
            do {
                calName = managedCursor.getString(nameCol);
                if (calName.contains("@gmail.com"))
                {
                    gmailIdCalendar = managedCursor.getString(idCol);
                    break;
                }
            } while (managedCursor.moveToNext());
            managedCursor.close();
        }
        return gmailIdCalendar;
    }
    private void Actionbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(confirmBookingReceiver);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.togglemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signOut:
                com.buddy.buddyapp.Constant.PopUp.SignOut(this);
                return true;
            case R.id.aboutUs:
                startActivity(new Intent(this, AboutUs.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    protected void sendSMSMessage() {
        String subject = "Booking Information";
        String message = "The User " + Common.currentUser.getName() + " had booked a service. Here is the Information" +
                "\n\nService: " + Common.currentService.getName() + "\nCategory: " + Common.currentCategory.getCategoryName() +
                "\nTime And Date: " + new StringBuilder(Common.convertTimeToString(Common.currentTimeSlot))
                .append(" at ")
                .append(simpleDateFormat.format(Common.currentDate.getTime())).toString() +
                "\nPrice: " + Common.currentCategory.getCategoryPrice() +
       "\n\nUser Information\nEmail: " + Common.currentUser.getEmail() + "\nPhone: " + Common.currentUser.getPhoneNumber() +
                "\nAddress: " + Common.currentUser.getAddress();
        GmailSender.GSender(message,subject);
    }
    private void RealTimeDatabase(BookingInformation bookingInformation){
            CollectionReference userBooking = null;
        userBooking = FirebaseFirestore.getInstance()
        .collection("Booking");
        userBooking.document();
        bookingInformation.setBookingId(userBooking.getId());
        userBooking.add(bookingInformation);

    }
    private void Notify(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                int SDK_INT = Build.VERSION.SDK_INT;

                if (SDK_INT > 8) {

                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                            .permitAll().build();

                    StrictMode.setThreadPolicy(policy);
                  //  String message = "Buddy User have booked your service";

                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection)url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic MmE1YTc1YjEtMWY0OS00NmFjLTkwNTAtMjQxNDczZGViYjli");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                +   "\"app_id\": \"6c67ad02-1006-47b2-9ea3-6bafa83c943a\","
                                +   "\"included_segments\": [\"All\"],"
                                +   "\"data\": {\"foo\": \"bar\"},"
                                +   "\"contents\": {\"en\": \"Buddy User have booked your service\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (  httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch(Throwable t) {
                        t.printStackTrace();
                    }

                }

            }

        });
    }

}
