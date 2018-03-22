package com.link184.sample.local;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.link184.sample.utils.ParserEnum;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by eugeniu on 5/24/17.
 */

@IgnoreExtraProperties
public class User {
    public String firstName;
    public String lastName;
    public String profileImageName;
    public String dob;
    public Map<String, PlanEvent> events;
    public int gender;
    public float height;
    public int uom;
    public float weight;

    public User() {
    }

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfileImageName() {
        return profileImageName;
    }

    public void setProfileImageName(String profileImageName) {
        this.profileImageName = profileImageName;
    }

    @Exclude
    public Date getDob() {
        try {
            return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(dob);
        } catch (NullPointerException | ParseException e) {
            try {
                return new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).parse(dob);
            } catch (NullPointerException | ParseException e1) {
                return null;
            }
        }
    }

    @Exclude
    public void setDob(Date dob) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        this.dob = sdf.format(dob);
    }

    public Map<String, PlanEvent> getEvents() {
        return events;
    }

    public void setEvents(Map<String, PlanEvent> events) {
        this.events = events;
    }

    @Exclude
    public Gender getGender() {
        return ParserEnum.parse(Gender.class, gender);
    }

    @Exclude
    public void setGender(Gender gender) {
        this.gender = gender.ordinal();
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    @Exclude
    public UnitsOfMeasure getUom() {
        return ParserEnum.parse(UnitsOfMeasure.class, uom);
    }

    @Exclude
    public void setUom(UnitsOfMeasure uom) {
        this.uom = uom.ordinal();
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Exclude
    public String getLocalizedHeight() {
        if (getUom() == UnitsOfMeasure.IMPERIAL) {
            float heightInInches = (float) (height * 0.393701);
            int feet = (int) (heightInInches / 12.0f);
            int inches = (int) (heightInInches - feet * 12);
            return feet + "\' " + inches + "\" ft";
        }

        if (getUom() == UnitsOfMeasure.METRIC) {
            return Math.round(height) + " cm";
        }

        return null;
    }

    @Exclude
    public String getLocalizedWeight() {
        if (getUom() == UnitsOfMeasure.IMPERIAL) {
            int intValue = (int) (weight / 0.453592);
            int floatValue = (int) Math.round(((weight / 0.453592) - intValue) * 10);
            return intValue + "." + floatValue + " lbs";
        }

        if (getUom() == UnitsOfMeasure.METRIC) {
            return weight + " kg";
        }

        return null;
    }

    @Exclude
    public int getAge() {
        Date dateOfBirth = this.getDob();

        if (dateOfBirth == null) {
            return 0;
        }

        Date now = new Date();
        long timeBetween = now.getTime() - dateOfBirth.getTime();
        double yearsBetween = timeBetween / 86_400_000 / 365;
        int age = (int) Math.floor(yearsBetween);
        return age;
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
                map.put("firstName", firstName);
                map.put("lastName", lastName);
                map.put("profileImageName", profileImageName);
                map.put("dob", dob);
                map.put("gender", gender);
                map.put("height", height);
                map.put("uom", uom);
                map.put("weight", weight);
        return map;
    }
}
