/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaPackages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.parser.ParseException;

/**
 *
 * @author khorzeyi
 */
import java.io.*;
import org.json.simple.parser.*;

public class Net2Local {
    
    public void dataTransformation() throws IOException, ParseException {
        // Check internet connectivity
        if (isInternetConnected()) {
            // If internet is available, fetch data from server
            fetchAndCacheData();
        } else {
            // If no internet, read data from cache
            readCachedData();
        }
    }
    
    private boolean isInternetConnected() {
        try {
            // Attempt to connect to a known server
            HttpURLConnection connection = (HttpURLConnection) new URL("https://www.google.com").openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (IOException e) {
            return false; // If exception occurs, assume no internet connection
        }
    }
    
    private void fetchAndCacheData() throws IOException, ParseException {
        String filePath = "src/jsonData/data.json";
        File file = new File(filePath);

        try (FileWriter writer = new FileWriter(file)) {
            URL url = new URL("https://einfo.my/netlynx.my/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP Error code : " + conn.getResponseCode());
            }
            
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String output;
                StringBuilder response = new StringBuilder();
                while ((output = br.readLine()) != null) {
                    response.append(output);
                }
                // Write data to local file
                writer.write(response.toString());
            }
        }
    }

    private void readCachedData() throws IOException, ParseException {
        String filePath = "src/jsonData/data.json";
        
        try (FileReader reader = new FileReader(filePath)) {
            JSONParser parser = new JSONParser();
            Object jsonData = parser.parse(reader);
        }
    }
}
