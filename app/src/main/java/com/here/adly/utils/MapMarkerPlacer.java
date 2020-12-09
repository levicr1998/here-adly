package com.here.adly.utils;

import android.content.Context;

import com.here.adly.R;
import com.here.adly.models.Feature;
import com.here.sdk.core.Anchor2D;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapview.MapImage;
import com.here.sdk.mapview.MapImageFactory;
import com.here.sdk.mapview.MapMarker;
import com.here.sdk.mapview.MapView;

import java.util.ArrayList;
import java.util.List;

public class MapMarkerPlacer {

private List<Feature> features = new ArrayList<>();
private Context context;
private MapView mapView;

    public MapMarkerPlacer(Context context, MapView mapView, List<Feature> features) {
        this.features = features;
        this.context = context;
        this.mapView = mapView;
    }

    public void placeMapMarkers(){
        for ( Feature feature: features) {
            GeoCoordinates geoCoordinates = new GeoCoordinates(feature.getGeometry().getCoordinates().get(1),feature.getGeometry().getCoordinates().get(0));
            Anchor2D anchor2D = new Anchor2D(0.5f, 1.0f);
            MapImage mapImage = MapImageFactory.fromResource(context.getResources(), R.drawable.ad_picture);
            MapMarker mapMarker = new MapMarker(geoCoordinates, mapImage, anchor2D);
            mapView.getMapScene().addMapMarker(mapMarker);
        }
    }
}
