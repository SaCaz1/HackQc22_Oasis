package com.example.outilpourfairefaceauxcanicules;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.outilpourfairefaceauxcanicules.databinding.ActivityMapsBinding;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Repentigny and move the camera
        LatLng repentigny = new LatLng(45.753284, -73.440079);
        mMap.addMarker(new MarkerOptions().position(repentigny).title("Marker in Repentigny"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(repentigny,15));

        //Add layer to map  --testing git changes   to see if it works
        //JSONObject geoJsonData = new JSONObject();// JSONObject containing the GeoJSON data
        //GeoJsonLayer layer = new GeoJsonLayer(mMap, geoJsonData);
        //GeoJsonLayer layer = new GeoJsonLayer(mMap, R.raw.geojson_file, context);
        //layer.addLayerToMap();
        //Changing something to see if git works.

        List<List<String>> records = new ArrayList<>();
        try
        {
            BufferedReader reader = new BufferedReader((new FileReader("data/Data-arbres.csv")));
            String line;
            while ((line = reader.readLine()) != null)
            {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
                LatLng arbres = new LatLng(Double.parseDouble(values[8]), Double.parseDouble(values[9]));
                mMap.addMarker(new MarkerOptions().position(arbres).title("Arbres"));
            }
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }



}