package com.example.myanswer;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
@RequestMapping("/")
public class TransactionRoute {


    ArrayList<JSONObject> JsonList = new ArrayList<JSONObject>();   // Example a JSON-type array to store the json data from the front end
    // Add a route for data in a specific format
    @RequestMapping("AddRoute")
    public String AddRoute(@RequestBody String form) {
        try {
            System.out.println(form);
            JSONObject json = new JSONObject(form);    // Instantiate a json object and serialize the character json data from the front end into a json object
            JsonList.add(json); // Adds the serialized json object to the json type array
            return JsonList.toString(); // Converts the json array to a character type and returns it to the front end
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return JsonList.toString(); // Converts the json array to a character type and returns it to the front end
    }
    // Get the route to the final result
    @RequestMapping("CostPoints")
    public String SpendingPoints(@RequestBody String Points) throws JSONException {
        // Sort the json data by time
        JsonList.sort(new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject a, JSONObject b) {
                try {
                    String s1 = (String) a.get("timestamp");    // Get the timestamp value of the json data
                    String s2 = (String) b.get("timestamp");    // Get the timestamp value of the json data
                    // Remove the space letter "T" and the letter "Z" in the character format time, and convert it to the sort by number size, which indirectly realizes the sort by time
                    Long data_1 = Long.valueOf(s1.replace("T","").replace("Z","").replace("-","").replace(":",""));
                    Long date_2 = Long.valueOf(s2.replace("T","").replace("Z","").replace("-","").replace(":",""));
                    return data_1.compareTo(date_2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        JSONObject costpointjson = new JSONObject(Points);  // The front-end parameters are serialized in json format
        int CostPoint = costpointjson.getInt("points");     // Obtain front-end points
        String Type = costpointjson.getString("Type");      // Gets the value of the front-end Type parameter, which determines the different results to be returned

        JSONObject Costjson = new JSONObject();      // Spending points record

        // Go through the entire ordered json array
        for (JSONObject jsonobject: JsonList){
            String NAME= jsonobject.getString("payer"); // Obtain the name of the payer
            int POINTS = jsonobject.getInt("points");   // Get the points held by the payer
            // Check whether the current key-value pair exists in the json file. If no, add the current key-value pair and initialize the points to 0
            if (!Costjson.has(NAME)){
                Costjson.put(NAME,0);
            }

            if (CostPoint >= POINTS){
                Costjson.put(NAME,Costjson.getInt(NAME)+POINTS);
                CostPoint -= POINTS;
            }else if (CostPoint < POINTS){
                Costjson.put(NAME,Costjson.getInt(NAME)+CostPoint);
                break;
            }

        }

        ArrayList<JSONObject> RESULT = new ArrayList<JSONObject>();     // Create a json array to hold the expense records
        Iterator iterator = Costjson.keys();    // The iterator of all payers' names
        // Loop through the json data object that records spending points, summing up the points they hold by holders and placing them in the spending record
        while(iterator.hasNext()){
            JSONObject j =new JSONObject();
            String key = (String) iterator.next();
            int value = Costjson.getInt(key) * -1;
            j.put("payer",key);
            j.put("points",value);
            RESULT.add(j);
        }

        JSONObject result = new JSONObject();  // Create a json object to store the details of the remaining points
        // Walk through the raw data and add the number of points held by the holders according to their names
        for (int i=0;i<JsonList.size();i++){
            String K = JsonList.get(i).getString("payer");
            if (!result.has(K)){
                result.put(K,0);
            }
            result.put(K,result.getInt(K)+JsonList.get(i).getInt("points"));
        }

        Iterator iterator1 = Costjson.keys();   // The iterator of all payers' names
        // Loop through the json data object that records the points spent, summing them up with the points held and updating them into the remaining points details based on the holder's name
        while(iterator1.hasNext()){
            String K = (String) iterator1.next();   // Get the key of the json object
            int V = Costjson.getInt(K) * -1;        // Get the value of the json object and take a negative value
            result.put(K,result.getInt(K)+V);   // Add to the json object of the remaining points details
        }
        // Return a payout record or a list of remaining points according to the front-end request Type type parameter
        if (Type.equals("A")){
            return RESULT.toString();
        }else{
            return result.toString();
        }


    }
}
