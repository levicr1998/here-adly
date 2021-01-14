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
    private final List<MapMarker> mapMarkers = new ArrayList<>();
    private Context context;
    private MapView mapView;
    public static final String SPACE_NAME_EUROPANEL = "bXsuXVfP";
    public static final String SPACE_NAME_TWOSIGN = "t5tgnuZA";
    public static final String SPACE_NAME_ABRI = "OA2v5p9Z";

    public MapMarkerPlacer(Context context, MapView mapView) {
        this.context = context;
        this.mapView = mapView;
    }

    public void placeMapMarkers(List<Feature> features) {
        for (Feature feature : features) {
            this.features = features;
            GeoCoordinates geoCoordinates = new GeoCoordinates(feature.getGeometry().getCoordinates().get(1), feature.getGeometry().getCoordinates().get(0));
            Anchor2D anchor2D = new Anchor2D(0.5f, 1.0f);
            MapImage mapImage = chooseAdPicture(feature.getProperties().getNsComHereXyz().getSpace());
            MapMarker mapMarker = new MapMarker(geoCoordinates, mapImage, anchor2D);
            this.mapMarkers.add(mapMarker);
            this.mapView.getMapScene().addMapMarker(mapMarker);
        }
    }

    public void removeMapMarkers() {
        if (!this.mapMarkers.isEmpty()) {
            for (MapMarker mapMarker : this.mapMarkers) {
                mapView.getMapScene().removeMapMarker(mapMarker);
            }
            mapMarkers.clear();
        }
    }

    public MapImage chooseAdPicture(String spaceName) {
        MapImage mapImage = MapImageFactory.fromResource(context.getResources(), R.drawable.location_spot_europanel);
        switch (spaceName) {
            case SPACE_NAME_EUROPANEL:
                mapImage = MapImageFactory.fromResource(context.getResources(), R.drawable.location_spot_europanel);
                break;
            case SPACE_NAME_TWOSIGN:
                mapImage = MapImageFactory.fromResource(context.getResources(), R.drawable.location_spot_twosign);
                break;
            case SPACE_NAME_ABRI:
                mapImage = MapImageFactory.fromResource(context.getResources(), R.drawable.location_spot_abri);
                break;

        }
        return mapImage;
    }
}
