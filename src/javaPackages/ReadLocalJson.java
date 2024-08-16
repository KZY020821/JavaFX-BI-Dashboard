/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaPackages;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author khorzeyi
 */
public class ReadLocalJson {
    public ArrayList<DataRecord> readJson() throws IOException, ParseException, JSONException {
        String filePath = "src/jsonData/data.json";
        File files = new File(filePath);
        
        FileReader reader = new FileReader(files.getAbsolutePath());
        JSONParser jp = new JSONParser();
        Object obj = jp.parse(reader);
        ArrayList<DataRecord> arr = new ArrayList<>();

        JSONArray alldata = new JSONArray(obj.toString());

        for (int x = 0; x < alldata.length(); x++) {
            JSONObject data = alldata.getJSONObject(x);
            int year = data.getInt("Year");
            int qtr = data.getInt("QTR");
            String region = data.getString("Region");
            String vehicle = data.getString("Vehicle");
            int quantity = data.getInt("Quantity");
            arr.add(new DataRecord(year, qtr, region, vehicle, quantity));
        }

        return arr;
    }

    public static class DataRecord {
        private final int year;
        private final int qtr;
        private final String region;
        private final String vehicle;
        private final int quantity;

        public DataRecord(int year, int qtr, String region, String vehicle, int quantity) {
            this.year = year;
            this.qtr = qtr;
            this.region = region;
            this.vehicle = vehicle;
            this.quantity = quantity;
        }

        public int getYear() {
            return year;
        }

        public int getQtr() {
            return qtr;
        }

        public String getRegion() {
            return region;
        }

        public String getVehicle() {
            return vehicle;
        }

        public int getQuantity() {
            return quantity;
        }
        
        public int getElise() {
            return vehicle.equals("Elise") ? quantity : 0;
        }

        public int getEvora() {
            return vehicle.equals("Evora") ? quantity : 0;
        }

        public int getExige() {
            return vehicle.equals("Exige") ? quantity : 0;
        }
    }
}
