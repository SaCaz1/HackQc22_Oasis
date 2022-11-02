package com.example.outilpourfairefaceauxcanicules;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.outilpourfairefaceauxcanicules.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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

    private static final int COLOR_WHITE_ARGB = 0xffffffff;
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
    List <Polygon> ilots = new ArrayList<>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(true);

        // Add a marker in Repentigny and move the camera
        LatLng repentigny = new LatLng(45.753284, -73.440079);
        mMap.addMarker(new MarkerOptions().position(repentigny).title("Marker in Repentigny"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(repentigny,15));

        //Marqueurs d'arbres -- regex being weird here I think
        arbres.addAll(addDataPoint(mMap, "data-arbres.csv", 8, 9, "Arbre", "Les arbres diminuent la temperature de lenvironnement.", BitmapDescriptorFactory.HUE_GREEN));
        //arbres.addAll(addDataPoint(mMap, "data-mtl-arbres-publics.csv", 21, 20, "Arbre", "Les arbres diminuent la temperature de lenvironnement.", BitmapDescriptorFactory.HUE_GREEN));

        //Marqueurs de bâtiments climatisés
        clim.addAll(addDataPoint(mMap, "data-clim.csv",12, 11, "Bâtiment climatisé", "Les parcs peuvent procurer une meilleure solution.", BitmapDescriptorFactory.HUE_VIOLET));
        //addLayerKLM("data-mtl-clim.txt"); //Not working currently


        //Marqueurs de parcs
        parcs.addAll(addDataPoint(mMap, "data-parcs.csv", 7, 6, "Parc","Les parcs sont des espaces froids.", BitmapDescriptorFactory.HUE_ORANGE));

        //Marqueurs d'eau
        eau.addAll(addDataPoint(mMap, "data-mtl-piscines.csv", 11, 10, "Piscine", "info sur les piscines",BitmapDescriptorFactory.HUE_BLUE));
        eau.addAll(addDataPoint(mMap, "data-eau.csv",15,14,"Eau","L'eau rafraîchit.",BitmapDescriptorFactory.HUE_BLUE));
        eau.addAll(addDataPoint(mMap, "data-mtl-fontaine-eau.csv",12,11,"Fontaine d'eau","S'hydrater est important.",BitmapDescriptorFactory.HUE_BLUE));

        //Marqueurs d'ilôts de chaleur à éviter
        ilots.addAll(addPolygonToMap(mMap, "data-ilots-chaleur.csv", 3, 2, "Ilots", "Les ilots de chaleur", BitmapDescriptorFactory.HUE_BLUE));

    }

    //General function for looping over each dataset and adding it to the map.
    public List<Marker> addDataPoint(GoogleMap map, String filename, int lat, int longi, String type, String text, float colour) {
        List<List<String>> records = new ArrayList<>();
        AssetManager assetManager = getAssets();
        List<Marker> markers = new ArrayList<>();

        try {
            InputStream input = assetManager.open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            reader.readLine(); //reads first line
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                records.add(Arrays.asList(values));
                LatLng coord = new LatLng(Double.parseDouble(values[lat]), Double.parseDouble(values[longi]));
                //Adding the elemtn with visibility = False to be able to switch between visible and not visible.
                Marker marker = mMap.addMarker(new MarkerOptions().position(coord).title(type).snippet(text).visible(false).icon(BitmapDescriptorFactory.defaultMarker(colour)));

                markers.add(marker);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return markers;
    }

    public List<Polygon> addPolygonToMap(GoogleMap map, String filename, int lat, int longi, String type, String text, float colour) {
        List<Polygon> polygon = new ArrayList<>();
        List<LatLng> coordlist = new ArrayList<>();
        AssetManager assetManager = getAssets();
        try {
            InputStream input = assetManager.open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            reader.readLine(); //reads first line
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                LatLng coord = new LatLng(Double.parseDouble(values[lat]), Double.parseDouble(values[longi]));
                coordlist.add(coord);
            }
            reader.close();
            for (int i = 0; i < coordlist.size(); i++)
            {
                Polygon poly = mMap.addPolygon(new PolygonOptions().add(coordlist.get(i)).visible(true));
                if (i == coordlist.size()-1) {
                    polygon.add(poly);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return polygon;
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
                break;
            case R.id.checkbox_eau:
                if (checked)
                    for (Marker e : eau)
                        e.setVisible(true);
                else
                    for (Marker e : eau)
                        e.setVisible(false);
                break;
            case R.id.checkbox_parcs:
                if (checked)
                    for (Marker p : parcs)
                        p.setVisible(true);
                else
                    for (Marker p : parcs)
                        p.setVisible(false);
                break;
            case R.id.checkbox_chaleur:
                if (checked)
                    for (Polygon i : ilots)
                        i.setVisible(true);
                else
                    for (Polygon i : ilots)
                        i.setVisible(false);
                break;
        }
    }



}