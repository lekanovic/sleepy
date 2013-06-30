package com.example.raddeapp;

/**
 * Created by Radovan on 2013-06-29.
 */
public class GPSDistance {

    private Double latitude,longitude;
    private Double dest_latitude, dest_longitude;
    private Integer earth_radius = 6371;

    public void setCordinates(Double lat,
                              Double lng,
                              Double dest_lat,
                              Double dest_lng){
        latitude = lat;
        longitude = lng;
        dest_latitude = dest_lat;
        dest_longitude = dest_lng;
    }
    public Integer getDistance(){
        Double dLat = Math.toRadians(latitude - dest_latitude);
        Double dLng = Math.toRadians(longitude - dest_longitude);

        Double a =
                (Math.sin(dLat/2) *
                 Math.sin(dLat/2)) +
                (Math.cos(Math.toRadians(latitude))) *
                (Math.cos(Math.toRadians(dest_latitude))) *
                (Math.sin(dLng / 2)) *
                (Math.sin(dLng / 2));

        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        Double dist = ((earth_radius * c)*1000);//Return in meters

        return dist.intValue();
    }
}

