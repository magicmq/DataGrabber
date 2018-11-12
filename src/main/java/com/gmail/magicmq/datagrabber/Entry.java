package com.gmail.magicmq.datagrabber;

import com.opencsv.bean.CsvBindByName;

import javax.annotation.Nullable;
import java.text.DecimalFormat;

public class Entry {

    private double ncesid;
    private String stateschoolid;
    private Integer ncesdistrictid;
    private String statedistrictid;
    private String school;
    private String district;
    private String city;
    private String county;
    private Integer zip;
    private String locale;
    private String charter;
    private String magnet;
    private Integer students;
    private Double teachers;
    private Double studentteacherratio;
    private Integer freelunch;
    private Integer reducedlunch;
    private Integer districtmedianfamilyincome;
    private Integer districtmeanfamilyincome;

    private Integer wins;
    private Integer losses;
    private Integer ties;

    private Double percentage;

    private Integer playoffappearances;
    private Integer districtchampionships;
    private Integer statechampionships;

    private Integer powins;
    private Integer polosses;
    private Integer poties;

    private Double popercentage;

    public Entry(String input) {
        String[] raw = input.split(",", -1);

        ncesid = Double.parseDouble(raw[0]);
        stateschoolid = raw[1];
        ncesdistrictid = sneakyParseInt(raw[2]);
        statedistrictid = raw[3];
        school = raw[4];
        district = raw[5];
        city = raw[6];
        county = raw[7];
        zip = sneakyParseInt(raw[8]);
        locale = raw[9];
        charter = raw[10];
        magnet = raw[11];
        students = sneakyParseInt(raw[12]);
        teachers = sneakyParseDouble(raw[13]);
        studentteacherratio = sneakyParseDouble(raw[14]);
        freelunch = sneakyParseInt(raw[15]);
        reducedlunch = sneakyParseInt(raw[16]);
    }

    public boolean isCharter() {
        return charter.equals("Yes");
    }

    public String getSchool() {
        return school
                .replace(" SECONDARY", "")
                .replace(" JUNIOR HIGH/HIGH SCHOOL", "")
                .replace(" J H-H S", "")
                .replace(" J H H S", "")
                .replace(" JH/HS", "")
                .replace(" JH/H S", "")
                .replace(" JR-SR H S", "")
                .replace(" JR/SR H S", "")
                .replace(" SCHOOLS", "")
                .replace(" JR-SR HIGH SCHOOL", "")
                .replace(" MIDDLE/HIGH SCHOOL", "")
                .replace(" COLLEGIATE H S", "")
                .replace(" MIDDLE AND H S", "")
                .replace(" JUNIOR/SENIOR HIGH", "")
                .replace(" EARLY COLLEGE H S", "")
                .replace(" H S", "")
                .replace(" HS", "")
                .replace(" HIGH", "")
                .replace(" SCHOOL", "")
                .toLowerCase();
    }

    public boolean stillNull() {
        return wins == null;
    }

    public String getCity() {
        String toreturn = city;

        if (toreturn.equals("SAN ANTONIO")) toreturn = "SA";
        if (toreturn.equals("EL PASO")) toreturn = "EP";
        if (toreturn.equals("FORT WORTH")) toreturn = "FW";
        if (toreturn.equals("SPRING BRANCH")) toreturn = "SB";
        if (toreturn.equals("CORPUS CHRISTI")) toreturn = "C.C.";
        if (toreturn.equals("WICHITA FALLS")) toreturn = "WF";
        if (toreturn.equals("ROUND ROCK")) toreturn = "RR";
        if (toreturn.equals("GALENA PARK")) toreturn = "GP";
        if (toreturn.equals("JOHNSON CITY")) toreturn = "JC";
        if (toreturn.equals("LITTLE RIVER")) toreturn = "LR";
        if (toreturn.equals("NEW BRAUNFELS")) toreturn = "NB";
        
        return toreturn.toLowerCase();
    }

    public void setFootballData(String data) {
        String[] split = data.split(",");

        wins = Integer.parseInt(split[1]);
        losses = Integer.parseInt(split[2]);
        ties = Integer.parseInt(split[3]);

        percentage = (wins + ties / 2.0) / (wins + losses + ties);

        playoffappearances = Integer.parseInt(split[4]);
        districtchampionships = Integer.parseInt(split[5]);
        statechampionships = Integer.parseInt(split[6]);

        powins = Integer.parseInt(split[7]);
        polosses = Integer.parseInt(split[8]);
        poties = Integer.parseInt(split[9]);

        popercentage = (powins + poties / 2.0) / (powins + polosses + poties);
    }

    public String getDistrict() {
        return district;
    }

    public String getCounty() {
        return county;
    }

    public void setDistrictMedianIncome(int districtmedianfamilyincome) {
        this.districtmedianfamilyincome = districtmedianfamilyincome;
    }

    public void setDistrictMeanIncome(int districtmeanfamilyincome) {
        this.districtmeanfamilyincome = districtmeanfamilyincome;
    }

    public String exportData() {
        DecimalFormat format = new DecimalFormat("#");
        format.setMaximumIntegerDigits(12);

        return format.format(ncesid) + "," +
                stateschoolid + "," +
                ncesdistrictid + "," +
                statedistrictid + "," +
                school + "," +
                district + "," +
                city + "," +
                county + "," +
                zip + "," +
                locale + "," +
                charter + "," +
                magnet + "," +
                (students == null ? "" : students) + "," +
                (teachers == null ? "" : teachers) + "," +
                (studentteacherratio == null ? "" : studentteacherratio) + "," +
                (freelunch == null ? "" : freelunch) + "," +
                (reducedlunch == null ? "" : reducedlunch) + "," +
                (districtmedianfamilyincome == null ? "" : districtmedianfamilyincome) + "," +
                (districtmeanfamilyincome == null ? "" : districtmeanfamilyincome) + "," +
                (wins == null ? "" : wins) + "," +
                (losses == null ? "" : losses) + "," +
                (ties == null ? "" : ties) + "," +
                (percentage == null ? "" : percentage) + "," +
                (playoffappearances == null ? "" : playoffappearances) + "," +
                (districtchampionships == null ? "" : districtchampionships) + "," +
                (statechampionships == null ? "" : statechampionships) + "," +
                (powins == null ? "" : powins) + "," +
                (polosses == null ? "" : polosses) + "," +
                (poties == null ? "" : poties) + "," +
                (popercentage == null ? "" : popercentage);
    }

    @Nullable
    private Integer sneakyParseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    @Nullable
    private Double sneakyParseDouble(String string) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
