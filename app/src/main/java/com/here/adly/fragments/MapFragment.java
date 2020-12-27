package com.here.adly.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.here.adly.R;
import com.here.adly.activities.MainActivity;
import com.here.adly.models.Feature;
import com.here.adly.models.FeatureCollection;
import com.here.adly.utils.MapMarkerPlacer;
import com.here.adly.utils.PermissionsRequestor;
import com.here.adly.webservices.APIServiceHERE;
import com.here.adly.webservices.FeatureLocationCollectionManager;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.Point2D;
import com.here.sdk.gestures.TapListener;
import com.here.sdk.mapview.MapError;
import com.here.sdk.mapview.MapMarker;
import com.here.sdk.mapview.MapScene;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;
import com.here.sdk.mapview.MapViewBase;
import com.here.sdk.mapview.PickMapItemsResult;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment {

    private static final String TAG = MainActivity.class.getSimpleName();
    private PermissionsRequestor permissionsRequestor;
    private APIServiceHERE apiServiceHERE;
    private FeatureLocationCollectionManager featureLocationCollectionManager;
    private MapMarkerPlacer mapMarkerPlacer;
    private MapView mapView;
    private List<Feature> features;

    private static final String SPACE_ID_EUROPANEL = "bXsuXVfP";
    private static final String SPACE_ID_TWOSIGN = "t5tgnuZA";
    private static final String SPACE_ID_ABRI = "OA2v5p9Z";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        features = new ArrayList<>();
        // Get a MapView instance from the layout.
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        featureLocationCollectionManager = new FeatureLocationCollectionManager();
        this.getFeatures();

        mapView.setOnReadyListener(new MapView.OnReadyListener() {
            @Override
            public void onMapViewReady() {
                // This will be called each time after this activity is resumed.
                // It will not be called before the first map scene was loaded.
                // Any code that requires map data may not work as expected beforehand.
                Log.d(TAG, "HERE Rendering Engine attached.");

            }
        });

        handleAndroidPermissions();
        return view;
    }

    private void handleAndroidPermissions() {
        permissionsRequestor = new PermissionsRequestor(getActivity());
        permissionsRequestor.request(new PermissionsRequestor.ResultListener() {

            @Override
            public void permissionsGranted() {
                loadMapScene();
                setTapGestureHandler();
            }

            @Override
            public void permissionsDenied() {
                Log.e(TAG, "Permissions denied by user.");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsRequestor.onRequestPermissionsResult(requestCode, grantResults);
    }


    private void loadMapScene() {
        // Load a scene from the HERE SDK to render the map with a map scheme.

        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapError mapError) {
                if (mapError == null) {
                    double distanceInMeters = 1000 * 10;
                    mapView.getCamera().lookAt(
                            new GeoCoordinates(51.44416, 5.4788), distanceInMeters);


                } else {
                    Log.d(TAG, "Loading map failed: mapError: " + mapError.name());
                }
            }
        });

    }


    private void setTapGestureHandler() {
        mapView.getGestures().setTapListener(new TapListener() {
            @Override
            public void onTap(Point2D touchPoint) {
                pickMapMarker(touchPoint);
            }
        });
    }

    private void pickMapMarker(final Point2D touchPoint) {
        float radiusInPixel = 2;
        mapView.pickMapItems(touchPoint, radiusInPixel, pickMapItemsResult -> {
            List<MapMarker> mapMarkerList = pickMapItemsResult.getMarkers();
            if (mapMarkerList.size() == 0) {
                return;
            }
            MapMarker topmostMapMarker = mapMarkerList.get(0);
            Feature matchedFeature = getFeatureFromCoordinates(topmostMapMarker);
            startDetailsFragment(matchedFeature.getProperties().getName(), matchedFeature.getId(), matchedFeature.getSpaceId());


        });
    }

    private void startDetailsFragment(String featureName, String featureId, String featureSpaceId) {
        DetailsFragment detailsFragment = new DetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("adName", featureName);
        bundle.putString("adId", featureId);
        bundle.putString("adSpaceId", featureSpaceId);
        detailsFragment.setArguments(bundle);
        this.getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, detailsFragment).commit();
    }


    private Feature getFeatureFromCoordinates(MapMarker mapMarker) {
        Feature matchedFeature = new Feature();
        double latitudeMapMarker = mapMarker.getCoordinates().latitude;
        double longitudeMapMarker = mapMarker.getCoordinates().longitude;
        for (Feature feature : features) {
            Double latitudeFeature = feature.getGeometry().getCoordinates().get(1);
            Double longitudeFeature = feature.getGeometry().getCoordinates().get(0);
            if (longitudeFeature.doubleValue() == longitudeMapMarker && latitudeFeature.doubleValue() == latitudeMapMarker) {
                return feature;
            }
        }
        return matchedFeature;
    }

    private void getFeatures() {
        apiServiceHERE = featureLocationCollectionManager.setupClient();
        Call<FeatureCollection> callEuropanel = apiServiceHERE.getFeatures(SPACE_ID_EUROPANEL);
        executeGetFeatures(callEuropanel, SPACE_ID_EUROPANEL);
        Call<FeatureCollection> callTwoSign = apiServiceHERE.getFeatures(SPACE_ID_TWOSIGN);
        executeGetFeatures(callTwoSign, SPACE_ID_TWOSIGN);
        Call<FeatureCollection> callAbri = apiServiceHERE.getFeatures(SPACE_ID_ABRI);
        executeGetFeatures(callAbri, SPACE_ID_ABRI);
    }

    private void executeGetFeatures(Call<FeatureCollection> call, String spaceId) {
        call.enqueue(new Callback<FeatureCollection>() {
            @Override
            public void onResponse(Call<FeatureCollection> call, Response<FeatureCollection> response) {
                if (!response.isSuccessful()) {
                }

                FeatureCollection featureCollection = response.body();
                List<Feature> allCollectedFeatures = featureCollection.getFeatures();
                for (Feature feature : allCollectedFeatures) {
                    feature.setSpaceId(spaceId);
                    features.add(feature);
                }
                mapMarkerPlacer = new MapMarkerPlacer(getActivity(), mapView, features);
                mapMarkerPlacer.placeMapMarkers();

            }

            @Override
            public void onFailure(Call<FeatureCollection> call, Throwable t) {
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
