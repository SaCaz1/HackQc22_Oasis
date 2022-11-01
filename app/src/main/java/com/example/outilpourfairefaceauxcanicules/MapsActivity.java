package com.example.outilpourfairefaceauxcanicules;

import androidx.fragment.app.FragmentActivity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.outilpourfairefaceauxcanicules.databinding.ActivityMapsBinding;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    List <Marker> arbres = new ArrayList<>();
    List <Marker> clim = new ArrayList<>();
    List <Marker> eau = new ArrayList<>();
    List <Marker> parcs = new ArrayList<>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Repentigny and move the camera
        LatLng repentigny = new LatLng(45.753284, -73.440079);
        mMap.addMarker(new MarkerOptions().position(repentigny).title("Marker in Repentigny"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(repentigny,15));

        arbres = addDataPoint(mMap, "data-arbres.csv", 8, 9, "Arbre", "Les arbres diminuent la temperature de lenvironnement.");
        clim = addDataPoint(mMap, "lieu-climatise.csv",13, 12, "Bâtiment climatisé", "Les parcs peuvent procurer une meilleure solution.");
        parcs = addDataPoint(mMap, "parcs.csv", 8, 7, "Parc","Les parcs sont des espaces froids.");
        //addDataPoint(mMap, "data-mtl-parcs.csv", 22, 21, "Parc", "Info sur les parsc");
        //addDataPoint(mMap, "data-mtl-piscines.csv", 12, 11, "Piscine", "info sur les piscines");
        //À ajouter : lieux à éviter, soit les ilôts de chaleur
    }

    //General function for looping over each dataset and adding it to the map.
    public List<Marker> addDataPoint(GoogleMap map, String filename, int lat, int longi, String type, String text) {
        List<List<String>> records = new ArrayList<>();
        AssetManager assetManager = getAssets();
        List<Marker> markers = new ArrayList<>();

        try {
            InputStream input = assetManager.open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            reader.readLine(); //reads first line
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
                LatLng coord = new LatLng(Double.parseDouble(values[lat]), Double.parseDouble(values[longi]));
                Marker marker = mMap.addMarker(new MarkerOptions().position(coord).title(type).snippet(text).visible(false));

                markers.add(marker);

                //On pourrait ajouter l'option .icon pour modifier la couleur du marqueur pour cette classe de points
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return markers;
    }


    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_arbres:
                if (checked)
                    for (Marker a : arbres)
                        a.setVisible(true);
                else
                    for (Marker a : arbres)
                        a.setVisible(false);
            case R.id.checkbox_eau:
                if (checked)
                    for (Marker e : eau)
                        e.setVisible(true);
                else
                    for (Marker e : eau)
                        e.setVisible(false);
            case R.id.checkbox_parcs:
                if (checked)
                    for (Marker p : parcs)
                        p.setVisible(true);
                else
                    for (Marker p : parcs)
                        p.setVisible(false);
            case R.id.checkbox_clim:
                if (checked)
                    for (Marker c : clim)
                        c.setVisible(true);
                else
                    for (Marker c : clim)
                        c.setVisible(false);
            case R.id.checkbox_chaleur:
                //if (checked)
                break;

        }
    }



}