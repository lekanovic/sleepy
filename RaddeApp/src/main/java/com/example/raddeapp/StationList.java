package com.example.raddeapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Radovan on 2013-07-01.
 */
public class StationList {
    private String url = "http://api.tagtider.net/v1/stations.json";
    private JSONParser jParser;
    private JSONObject json;
    private JSONArray items;

    public StationList(){
        jParser = new JSONParser();
        json = jParser.getJSONFromUrl(url,Boolean.TRUE);
    }

    public JSONArray getItems(){
        try {
            JSONObject c1 = json.getJSONObject("stations");
            items = c1.getJSONArray("station");
            //for(int i = 0; i < items.length(); i++){
            //    JSONObject c = items.getJSONObject(i);
            //}
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }
}