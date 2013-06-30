package com.example.raddeapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by 23053969 on 2013-06-28.
 */
public class NearbyStations {
    private String url = "https://api.trafiklab.se/samtrafiken/resrobot/StationsInZone.json?";
    /*
        private String url = "https://api.trafiklab.se/samtrafiken/resrobot/StationsInZone.json?
                 apiVersion=2.1&
                 centerX=1322751&
                 centerY=6166984&
                 radius=1000&
                 coordSys=RT90&
                 key=da562e01a1a10ae57652788f1d5dd642";
    */
    private JSONParser jParser;
    private JSONObject json;
    private JSONArray items;

    public NearbyStations(Double x, Double y, Integer r){
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        sb.append("apiVersion=2.1&");
        sb.append("centerX=" + x.toString() + "&");
        sb.append("centerY=" + y.toString() + "&");
        sb.append("radius=" + r.toString() + "&" );
        sb.append("coordSys=WGS84&");
        sb.append("key=da562e01a1a10ae57652788f1d5dd642");
        url = sb.toString();

        System.out.println("Radde123" + url);
        jParser = new JSONParser();
        json = jParser.getJSONFromUrl(url,Boolean.FALSE);

    }
    public JSONArray getItems(){
        try {
            JSONObject c1 = json.getJSONObject("stationsinzoneresult");
            items = c1.getJSONArray("location");
            for(int i = 0; i < items.length(); i++){
                JSONObject c = items.getJSONObject(i);
                //System.out.println("Radde123" + c.getString("name"));
            }
            //System.out.println("Radde123 " + items.length());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }
}
