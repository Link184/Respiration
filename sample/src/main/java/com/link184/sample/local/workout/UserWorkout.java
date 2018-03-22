package com.link184.sample.local.workout;

import android.graphics.Color;
import android.support.annotation.ColorInt;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.link184.sample.utils.ParserEnum;

import java.util.Date;

/**
 * Created by eugeniu on 5/26/17.
 */

@IgnoreExtraProperties
public class UserWorkout {
    @Exclude
    private long userWorkoutId;
    public String color;
    public long created_timestamp;
    public String description;
    public int difficulty;
    public String structure;
    public String title;
    public int type;
    public int week;
    public int year;
    public int music_package_id;

    public UserWorkout() {
    }

    /**
     * Adapted for DEFAULT_WORKOUT {@link WorkoutFirebaseType}
     */
    public UserWorkout(long userWorkoutId, String description, Difficulty difficulty, String structure, String title) {
        this.userWorkoutId = userWorkoutId;
        this.created_timestamp = new Date().getTime();
        this.description = description;
        this.difficulty = difficulty.ordinal();
        this.structure = structure;
        this.title = title;
        this.type = WorkoutFirebaseType.DEFAULT_WORKOUT.ordinal();
    }

    @Exclude
    public long getUserWorkoutId() {
        return userWorkoutId;
    }

    @Exclude
    public void setUserWorkoutId(long userWorkoutId) {
        this.userWorkoutId = userWorkoutId;
    }

    @Exclude
    @ColorInt
    public int getColorInt() {
        return Color.parseColor("#" + color);
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Exclude
    public Date getCreated_timestamp() {
        return new Date(created_timestamp);
    }

    @Exclude
    public void setCreated_timestamp(Date created_timestamp) {
        this.created_timestamp = created_timestamp.getTime();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Exclude
    public Difficulty getDifficulty() {
        return ParserEnum.parse(Difficulty.class, difficulty);
    }

    @Exclude
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty.ordinal();
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Exclude
    public WorkoutFirebaseType getType() {
        return ParserEnum.parse(WorkoutFirebaseType.class, type);
    }

    @Exclude
    public void setType(WorkoutFirebaseType type) {
        this.type = type.ordinal();
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMusic_package_id() {
        return music_package_id;
    }

    public void setMusic_package_id(int music_package_id) {
        this.music_package_id = music_package_id;
    }
}
