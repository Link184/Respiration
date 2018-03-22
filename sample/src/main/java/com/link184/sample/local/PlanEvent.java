package com.link184.sample.local;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by eugeniu on 9/26/17.
 */

@IgnoreExtraProperties
public class PlanEvent implements Parcelable {
    public int appID;
    public long date;
    public boolean isDone;
    public String planID;
    public int workoutID;

    /**
     * Use carefully this int
     */
    @Exclude
    private int index;

    public PlanEvent() {
    }

    public PlanEvent(Date date, String planID, int workoutID) {
        this.appID = 1;
        this.date = date.getTime();
        this.planID = planID;
        this.workoutID = workoutID;
    }

    @Exclude
    public int getAppID() {
        return appID;
    }

    public void setAppID(int appID) {
        this.appID = appID;
    }

    @Exclude
    public Date getDate() {
        return new Date(date);
    }

    @Exclude
    public void setDate(Date date) {
        this.date = date.getTime();
    }

    @Exclude
    public Calendar getCalendar() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(getDate());
        return calendar;
    }

    @Exclude
    public boolean isDone() {
        return isDone;
    }

    @Exclude
    public void setDone(boolean done) {
        isDone = done;
    }

    public String getPlanID() {
        return planID;
    }

    public void setPlanID(String planID) {
        this.planID = planID;
    }

    public int getWorkoutID() {
        return workoutID;
    }

    public void setWorkoutID(int workoutID) {
        this.workoutID = workoutID;
    }

    @Exclude
    public boolean isPlanEvent() {
        return planID != null && !planID.isEmpty();
    }

    @Exclude
    public void setIndex(int index) {
        this.index = index;
    }

    @Exclude
    public int getIndex() {
        return index;
    }

    protected PlanEvent(Parcel in) {
        appID = in.readInt();
        date = in.readLong();
        isDone = in.readByte() != 0;
        planID = in.readString();
        workoutID = in.readInt();
    }

    public static final Creator<PlanEvent> CREATOR = new Creator<PlanEvent>() {
        @Override
        public PlanEvent createFromParcel(Parcel in) {
            return new PlanEvent(in);
        }

        @Override
        public PlanEvent[] newArray(int size) {
            return new PlanEvent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(appID);
        dest.writeLong(date);
        dest.writeByte((byte) (isDone ? 1 : 0));
        dest.writeString(planID);
        dest.writeInt(workoutID);
    }
}
