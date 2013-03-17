package com.homkimi.calendarprovidertest;

import java.util.Date;
import java.util.HashSet;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContentResolver contentResolver = getContentResolver();

        final Cursor cursor = contentResolver.query(
                Uri.parse("content://com.android.calendar/calendars"), (new String[] {
                        "_id", "displayName", "selected" }), null, null, null);

        HashSet<String> calendarIds = new HashSet<String>();

        while (cursor.moveToNext()) {

            final String _id = cursor.getString(0);
            final String displayName = cursor.getString(1);
            final Boolean selected = !cursor.getString(2).equals("0");

            System.out.println("Id: " + _id + " Display Name: " + displayName
                    + " Selected: " + selected);
            calendarIds.add(_id);
        }

        // For each calendar, display all the events from the previous week to
        // the end of next week.
        for (String id : calendarIds) {
            Uri.Builder builder = Uri
                    .parse("content://com.android.calendar/instances/when").buildUpon();
            long now = new Date().getTime();
            ContentUris.appendId(builder, now - DateUtils.WEEK_IN_MILLIS);
            ContentUris.appendId(builder, now + DateUtils.WEEK_IN_MILLIS);

            Cursor eventCursor = contentResolver.query(builder.build(),
                    new String[] { "title", "begin", "end", "allDay" },
                    "Calendars._id=" + id, null,
                    "startDay ASC, startMinute ASC");
            // For a full list of available columns see
            // http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/1.6_r2/android/provider/Calendar.java

            while (eventCursor.moveToNext()) {
                final String title = eventCursor.getString(0);
                final Date begin = new Date(eventCursor.getLong(1));
                final Date end = new Date(eventCursor.getLong(2));
                final Boolean allDay = !eventCursor.getString(3).equals("0");

                System.out.println("Title: " + title + " Begin: " + begin
                        + " End: " + end + " All Day: " + allDay);
            }
        }

    }
}
