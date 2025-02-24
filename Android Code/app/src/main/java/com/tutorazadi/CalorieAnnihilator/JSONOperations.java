/**
 * Created by azadi on 3/29/15.
 */

package com.tutorazadi.CalorieAnnihilator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class JSONOperations {
    public ArrayList<FoodItem> getResults(String request) {
        request = sanitize(request);

        ArrayList<FoodItem> results = new ArrayList<>();
        JSONParser parser = new JSONParser();

        for (int i = 0; i < 20; i++)
        {
            try {
                String name = issueGetRequest("http://api.data.gov/usda/ndb/search/?format=json&q=" + request + "&max=25&offset=0&api_key=3hVnhFvj1VAagD29p9Q5b5MeYenARhmAvyX2suCf");
                Object parsedName = parser.parse(name);
                Object foodName = getName(parsedName, i);
                Object ndbno = getNDB(parsedName, i);

                Object parsedData = parser.parse(issueGetRequest("http://api.data.gov/usda/ndb/nutrients/?format=json&api_key=3hVnhFvj1VAagD29p9Q5b5MeYenARhmAvyX2suCf&nutrients=208&nutrients=269&nutrients=203&nutrients=204&ndbno=" + ndbno + "&max=1"));
                Object servingSize = getServingSize(parsedData, 0);
                Object[] result = getCalories(parsedData);

                /** @todo
                * Fix this so that it dynamically takes protein and fat content from DB.
                */
                results.add(new FoodItem(foodName.toString(), servingSize.toString(), result[0].toString(), "", "", result[1].toString()));
            }
            catch (ParseException | IOException | IndexOutOfBoundsException e) {
                if (e instanceof ParseException)
                {
                    ArrayList<FoodItem> noItemFound = new ArrayList<>();
                    noItemFound.add(new FoodItem("Network error", "", "", "", "", ""));
                    return noItemFound;
                }
                else if (e instanceof IOException)
                {
                    ArrayList<FoodItem> noItemFound = new ArrayList<>();
                    noItemFound.add(new FoodItem("No items found", "", "", "", "", ""));
                    return noItemFound;
                }
                else
                    break;
            }
        }
        return results;
    }

    public String sanitize(String input)
    {
        input = input.replace(" ", "%20");
        return input;
    }

    public static String issueGetRequest(String urlQuery) throws IOException {
        URL url = new URL(urlQuery);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        if (connection.getResponseCode() != 200)
            throw new IOException(connection.getResponseMessage());

        // Buffer the result into a string
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();

        connection.disconnect();
        return sb.toString();
    }

    public static Object getServingSize(Object input, int index) {
        Object reportData = ((JSONObject) input).get("report");
        Object foodsData = ((JSONObject) reportData).get("foods");
        Object foodsArray = ((JSONArray) foodsData).get(index);
        Object servingSize = ((JSONObject) foodsArray).get("measure");
        return servingSize;
    }

    public static Object[] getCalories(Object input) {
        Object reportData = ((JSONObject) input).get("report");
        Object foodsData = ((JSONObject) reportData).get("foods");
        Object foodsArray = ((JSONArray) foodsData).get(0);
        Object nutrientsData = ((JSONObject) foodsArray).get("nutrients");
        Object caloriesElement = ((JSONArray) nutrientsData).get(1);
        Object sugarElement = ((JSONArray) nutrientsData).get(0);
        Object caloriesValue = ((JSONObject) caloriesElement).get("value");
        Object sugarValue = ((JSONObject) sugarElement).get("value");
        Object[] result = new Object[2];
        result[0] = caloriesValue;
        result[1] = sugarValue;
        return result;
    }

/*    public static Object getSugar(Object input, int index) {
        Object reportData = ((JSONObject) input).get("report");
        Object foodsData = ((JSONObject) reportData).get("foods");
        Object foodsArray = ((JSONArray) foodsData).get(index);
        Object nutrientsData = ((JSONObject) foodsArray).get("nutrients");
        Object sugarElement = ((JSONArray) nutrientsData).get(index);
        Object sugarValue = ((JSONObject) sugarElement).get("value");
        return sugarValue;
    }*/

    public static Object getName(Object input, int index) {
        Object listData = ((JSONObject) input).get("list");
        Object itemData = ((JSONObject) listData).get("item");
        Object itemArray = ((JSONArray) itemData).get(index);
        Object name = ((JSONObject) itemArray).get("name");
        return name;
    }

    public static Object getNDB(Object input, int index) {
        Object listData = ((JSONObject) input).get("list");
        Object itemData = ((JSONObject) listData).get("item");
        Object itemArray = ((JSONArray) itemData).get(index);
        Object ndb = ((JSONObject) itemArray).get("ndbno");
        return ndb;
    }
}
