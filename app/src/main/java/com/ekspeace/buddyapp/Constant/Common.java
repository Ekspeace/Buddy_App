package com.ekspeace.buddyapp.Constant;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ekspeace.buddyapp.Model.BookingInformation;
import com.ekspeace.buddyapp.Model.Cannabis;
import com.ekspeace.buddyapp.Model.Category;
import com.ekspeace.buddyapp.Model.OrderInformation;
import com.ekspeace.buddyapp.Model.Services;
import com.ekspeace.buddyapp.Model.TimeSlot;
import com.ekspeace.buddyapp.Model.User;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Common {
    public static final int TIME_SLOT_TOTAL = 10;
    public static final String KEY_DISPLAY_TIME_SLOT = "DISPLAY TIME SLOT";
    public static final Object DISABLE_TAG = "DISABLE";
    public static final String KEY_CONFIRM_BOOKING = "CONFIRM_BOOKING";
    public static final String KEY_CLICKED_BUTTON_DELETE = "BUTTON_DELETE";
    public static final String KEY_DISABLE_NO_BOOKING_TEXT = "NO_BOOKING_TEXT";
    public static final String KEY_ENABLE_BUTTON_SERVICE = "BUTTON_SERVICE";
    public static final String KEY_DISABLE_NO_ORDER_TEXT = "NO_ORDER_TEXT" ;
    public static final String KEY_SLOT_BUTTON_NEXT = "SLOT_BUTTON" ;
    public static final String KEY_TRY = "KEY_TRY";
    public static User currentUser;
    public static final String EVENT_URI_CACHE = "URI_EVENT_SAVE";
    public static final String KEY_TIME_SLOT = "TIME_SLOT";
    public static final String KEY_STEP = "STEP";
    public static int currentTimeSlot = -1;
    public static Calendar currentDate = Calendar.getInstance();
    public static final SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd_MM_yyyy");
    public static Services currentService;
    public static Category currentCategory;
    public static String categoryEdit;
    public static String service;
    public static String serviceName;
    public static Cannabis currentCannabis;
    public static BookingInformation currentBooking;
    public static String currentBookingId1;
    public static String currentBookingId2;
    public static String currentBookingId;
    public static String currentCannabisPrice;
    public static String currentAlterType = "";
    public static String groceryStore;
    public static String groceryList;
    public static String otherType;
    public static String otherInfo;
    public static OrderInformation currentOrder;
    public static String currentOrderId;
    public static final String UserEmailKey = "UserPassword";
    public static HashMap<String, Object> currentOrderInfo;
    public static int OrderPosition;
    public static int BookPosition;

    public static String convertTimeToString(int slot) {

        switch (slot)
        {
            case 0:
                return "08:00 - 09:00";
            case 1:
                return "09:00 - 10:00";
            case 2:
                return "10:00 - 11:00";
            case 3:
                return "11:00 - 12:00";
            case 4:
                return "12:00 - 13:00";
            case 5:
                return "13:00 - 14:00";
            case 6:
                return "14:00 - 15:00";
            case 7:
                return "15:00 - 16:00";
            case 8:
                return "16:00 - 17:00";
            case 9:
                return "17:00 - 18:00";
                default:
                    return "Closed";

        }

    }


    public static String ConvertTimeToString(int slot) {

        switch (slot)
        {
            case 0:
                return "08:00 - 10:30";
            case 1:
                return "11:00 - 13:30";
            case 2:
                return "14:00 - 16:30";
            case 3:
                return "17:00 - 18:30";
            default:
                return "Closed";

        }

    }
    public static String convertTimeStampToStringKey(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        return simpleDateFormat.format(date);
    }

    public static Boolean isOnline(Context context)	{
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

}