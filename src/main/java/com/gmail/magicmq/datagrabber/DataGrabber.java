package com.gmail.magicmq.datagrabber;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.text.similarity.LevenshteinDistance;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataGrabber {

    public static void main(String[] args) throws IOException {
        BufferedReader data = new BufferedReader(new FileReader(new File(DataGrabber.class.getClassLoader().getResource("footballdata.txt").getFile().replace("%20", " ").replace("\\", "/"))));
        ArrayList<String> list = new ArrayList<>();

        try {
            String line;
            while ((line = data.readLine()) != null) {
                line = line.replace("\t", " ");

                while (line.contains("  ")) {
                    line = line.replace("  ", " ");
                }

                line = line.replace("- ", "-");
                line = line.trim();
                char[] chars = line.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == ' ') {
                        int next = chars[i + 1];
                        if (next >= 48 && next <= 57) chars[i] = ',';
                    }
                }

                String toprocess = String.valueOf(chars);
                toprocess = toprocess.replaceAll("\\s+$", "");
                toprocess = toprocess.replace(" ", "_");
                toprocess = toprocess.toLowerCase();

                //City processing
                toprocess = toprocess.replace("el_paso", "ep");

                toprocess = toprocess.replace("_*", ",").replace("_.", "").replace("_-", "-").replace("*", "").replace("$", "");

                String[] array = toprocess.split(",");
                array[1] = array[1].replace("-", ",");
                array[2] = array[2].replace("-", ",");
                array[3] = array[3].replace("-", ",");

                list.add(String.join(",", array));
            }

            data.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Entry> entries = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(new File(DataGrabber.class.getClassLoader().getResource("input.csv").getFile().replace("%20", " ").replace("\\", "/"))));
        String nextline;
        while ((nextline = reader.readLine()) != null) {
            if (nextline.contains("ncesid")) continue;
            entries.add(new Entry(nextline));
        }

        PrintWriter extrachecks = new PrintWriter("checks.txt", "UTF-8");
        PrintWriter nulls = new PrintWriter("errors.txt", "UTF-8");

        for (String raw : list) {
            String school = raw.split(",")[0];
            Entry toadd = null;
            
            try {
                //Ignore schools that have closed
                if (school.contains("#")) continue;

                if (school.contains("_")) {
                    List<Entry> matchedcity = new ArrayList<>();
                    for (Entry entry : entries) {
                        if (entry.isCharter()) continue;
                        if (entry.getCity().equals(school.split("_")[0])) {
                            matchedcity.add(entry);
                        }
                    }

                    for (Entry entry : matchedcity) {
                        if (entry.isCharter()) continue;
                        if (entry.getSchool().contains(concatenateToEnd(school.split("_")))) {
                            toadd = entry;
                        }
                    }

                    if (toadd == null) {
                        for (Entry entry : entries) {
                            if (entry.isCharter()) continue;
                            if (!entry.stillNull()) continue;
                            if (entry.getSchool().contains(school.replace("_", " "))) {
                                toadd = entry;
                            }
                        }
                    }
                } else {
                    for (Entry entry : entries) {
                        if (entry.isCharter()) continue;
                        if (entry.getSchool().equals(school)) toadd = entry;
                    }
                }

                if (toadd != null) {
                    if (LevenshteinDistance.getDefaultInstance().apply(school, toadd.getSchool()) > 3) {
                        extrachecks.println("Check: " + school + " TO " + toadd.getSchool());
                        extrachecks.println();
                    }

                    toadd.setFootballData(raw);
                }
                else {
                    nulls.println(school);
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
                System.out.println("School errored out: " + school);
            }
        }

        extrachecks.close();
        nulls.close();

        BufferedReader incomereader = new BufferedReader(new FileReader(new File(DataGrabber.class.getClassLoader().getResource("incomedata.csv").getFile().replace("%20", " ").replace("\\", "/"))));
        CSVParser parser = CSVFormat.EXCEL.withHeader().parse(incomereader);

        for (CSVRecord record : parser) {
            for (Entry entry : entries) {
                String district = record.get("district").replace("Consolidated Independent School District", "CISD")
                        .replace("Independent School District", "ISD")
                        .replace("Common School District", "CSD")
                        .replace("Municipal School District", "MSD").toUpperCase();
                if (district.matches(".*[(][a-zA-Z].*")) {
                    String county = district.substring(district.indexOf("(") + 1, district.indexOf(")"));
                    if (entry.getCounty().toLowerCase().equals(county.toLowerCase())) {
                        if (entry.getDistrict().equals(district.substring(0, district.indexOf("(") - 1))) {
                            entry.setDistrictMedianIncome(Integer.parseInt(record.get("medianfamilyincome")));
                            entry.setDistrictMeanIncome(Integer.parseInt(record.get("meanfamilyincome")));
                        }
                    }
                } else {
                    if (entry.getDistrict().equals(district)) {
                        entry.setDistrictMedianIncome(Integer.parseInt(record.get("medianfamilyincome")));
                        entry.setDistrictMeanIncome(Integer.parseInt(record.get("meanfamilyincome")));
                    }
                }
            }
        }

        FileWriter writer = new FileWriter("output.csv");
        writer.write("ncesid,stateschoolid,ncesdistrictid,statedistrictid,school,district,city,county,zip,locale,charter,magnet,students,teachers,studentteacherratio,freelunch,reducedlunch,districtmedianfamilyincome,districtmeanfamilyincome,wins,losses,ties,percentage,playoffappearances,districtchampionships,statechampionships,powins,polosses,poties,popercentage\n");
        for (Entry entry : entries) {
            if (entry.isCharter()) continue;
            writer.write(entry.exportData() + "\n");
        }
        writer.close();
    }

    private static String concatenateToEnd(String[] string) {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < string.length; i++) {
            builder.append(string[i]);
            if (i != string.length - 1) builder.append(" ");
        }
        return builder.toString();
    }
}

