package com.example.outilpourfairefaceauxcanicules;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    int counter = 0;
    List <Polygon> ilots = new ArrayList<>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        // Add a marker in Repentigny and move the camera
        LatLng repentigny = new LatLng(45.753284, -73.440079);
        mMap.addMarker(new MarkerOptions().position(repentigny).title("Marker in Repentigny"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(repentigny,15));

        //Marqueurs d'arbres -- regex being weird here I think
        arbres.addAll(addDataPoint(mMap, "data-arbres.csv", 8, 9, "Arbre", BitmapDescriptorFactory.HUE_GREEN,"arbre.png", arbre));
        arbres.addAll(addDataPoint(mMap, "data-mtl-arbres-publics.csv", 21, 20, "Arbre", BitmapDescriptorFactory.HUE_GREEN,"arbre.png", arbre));

        //Marqueurs de bâtiments climatisés
        clim.addAll(addDataPoint(mMap, "data-clim.csv",12, 11, "Bâtiment climatisé", BitmapDescriptorFactory.HUE_VIOLET,"clim.png", coolplace));
        clim.addAll(addDataPoint(mMap, "clim-mtl.csv",1,0,"Bâtiment climatisé",BitmapDescriptorFactory.HUE_VIOLET,"clim.png", coolplace));

        //Marqueurs de parcs
        parcs.addAll(addDataPoint(mMap, "data-parcs.csv", 7, 6, "Parc", BitmapDescriptorFactory.HUE_ORANGE, "parc.png", parc));

        //Marqueurs d'eau
        eau.addAll(addDataPoint(mMap, "data-mtl-piscines.csv", 11, 10, "Piscine", BitmapDescriptorFactory.HUE_BLUE,"piscine.png", piscine));
        eau.addAll(addDataPoint(mMap, "data-eau.csv",15,14,"Eau",BitmapDescriptorFactory.HUE_BLUE,"eau.png", fontaines));
        eau.addAll(addDataPoint(mMap, "data-mtl-fontaine-eau.csv",12,11,"Point d'eau", BitmapDescriptorFactory.HUE_BLUE,"eau.png", fontaines));

        //Marqueurs d'ilôts de chaleur à éviter
        ilots.add(addPolygonToMap(mMap, "data-ilots-chaleur-zone-1.csv", 3, 2, "Ilots", "Zone de chaleur", BitmapDescriptorFactory.HUE_BLUE));
        ilots.add(addPolygonToMap(mMap, "data-ilots-chaleur-zone-2.csv", 3, 2, "Ilots", "Zone de chaleur", BitmapDescriptorFactory.HUE_BLUE));
        ilots.add(addPolygonToMap(mMap, "data-ilots-chaleur-zone-3.csv", 3, 2, "Ilots", "Zone de chaleur", BitmapDescriptorFactory.HUE_BLUE));
        ilots.add(addPolygonToMap(mMap, "data-ilots-chaleur-zone-4.csv", 3, 2, "Ilots", "Zone de chaleur", BitmapDescriptorFactory.HUE_BLUE));
        ilots.add(addPolygonToMap(mMap, "data-ilots-chaleur-zone-5.csv", 3, 2, "Ilots", "Zone de chaleur", BitmapDescriptorFactory.HUE_BLUE));
        ilots.add(addPolygonToMap(mMap, "data-ilots-chaleur-zone-6.csv", 3, 2, "Ilots", "Zone de chaleur", BitmapDescriptorFactory.HUE_BLUE));
        ilots.add(addPolygonToMap(mMap, "data-ilots-chaleur-zone-7.csv", 3, 2, "Ilots", "Zone de chaleur", BitmapDescriptorFactory.HUE_BLUE));

    }

    //General function for looping over each dataset and adding it to the map.
    public List<Marker> addDataPoint(GoogleMap map, String filename, int lat, int longi, String type, float colour, String icone,List<String> listeInfo) {
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
                Marker marker = mMap.addMarker(new MarkerOptions().position(coord).title(type).snippet(listeInfo.get(counter % (listeInfo.size()))).visible(false).icon(BitmapDescriptorFactory.fromAsset(icone)));
                counter = counter + 1;
                markers.add(marker);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return markers;
    }

    public Polygon addPolygonToMap(GoogleMap map, String filename, int lat, int longi, String type, String text, float colour) {
        ArrayList<LatLng> points = new ArrayList<>();
        AssetManager assetManager = getAssets();
        try {
            InputStream input = assetManager.open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            reader.readLine(); //reads first line
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                LatLng coord = new LatLng(Double.parseDouble(values[lat]), Double.parseDouble(values[longi]));
                points.add(coord);
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return mMap.addPolygon(new PolygonOptions()
                .addAll(points)
                .strokeWidth(10)
                .strokeColor(Color.RED)
                .fillColor(0x33FF0000)
                .visible(false));
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
            case R.id.checkbox_clim:
                if (checked)
                    for (Marker c : clim)
                        c.setVisible(true);
                else
                    for (Marker c : clim)
                        c.setVisible(false);
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

    //Liste des informations éducatives à inclure dans l'application.
    List<String> piscine = Arrays.asList("Heures d’ouverture : ");
    List<String> fontaines = Arrays.asList("Buvez de l’eau régulièrement (toutes les 20 minutes), \n sans attendre d’avoir soif.","Chaque minute, 1 million de bouteilles de plastique \n sont achetées à travers le monde.", "Au Québec, 600 millions de bouteilles sont disposées \n dans les sites d’enfouissement chaque année, \n un volume équivalent à 240 piscines olympiques.", "L’eau en bouteille coûte 1 500 fois plus cher que l’eau du robinet.","90% des bouteilles d’eau à usage unique ne sont pas recyclées.");
    List<String> parc = Arrays.asList("Diminuez l’intensité de vos activités physiques extérieures et pratiquez-les en équipes ou en paire.","Limiter les durées d’exposition en plein soleil entre 12h et 16h.","Portez des vêtements de couleurs pâles, légers, amples et de préférence en coton lors de vos sorties extérieures.", "Portez un chapeau à large bord lors de vos sorties extérieures.", "Un terrain gazonné réduit la température ambiante de 1 à 2°C par rapport à un stationnement.","Le gazon réfléchit 25-30% de la radiation solaire contre 5-10% pour l’asphalte. La portion réfléchie ne réchauffe pas la surface.", "Planifiez vos activités extérieures durant les périodes plus fraîches (avant 11h ou après 18h).");
    List<String> coolplace = Arrays.asList("Heures d’ouverture : ");
    List<String> arbre = Arrays.asList("Un arbre mature de 40 ans réduit les besoins en climatisation de 237 kWh.","Les surfaces boisées réfléchissent 10-20% des radiations solaires, limitant ainsi le réchauffement du sol.","En banlieue, la présence d’arbres réduit la température de 2 à 3°C.", "En hiver, la présence d’arbres réduit les besoins en chauffage jusqu’à 25%, surtout dans les endroits venteux.","Planter des feuillus à l’ouest, au sud-ouest et au sud favorise la climatisation naturelle.", "Les arbres climatisent naturellement leur environnement en interceptant et en réfléchissant la radiation solaire.","En transpirant et en respirant, les arbres dégagent des goutellettes d’eau qui réduisent la chaleur environnante.", "L’ombre créée par les arbres réduit la température des bâtiments en réduisant la radiation solaire qui pénètre par les fenêtres.","La présence d’un bosquet d’arbre (couverture de 500 à 5 000 m2) réduit la température ambiante jusqu’à 5°C par rapport à un terrain découvert.","Les espaces boisés climatisent naturellement les villes en favorisant une meilleure ventillation.");


}