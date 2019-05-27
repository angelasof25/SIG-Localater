package com.example.aula3;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.mapsforge.map.android.layers.MyLocationOverlay;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.gpkg.tiles.feature.GeopackageFeatureTilesOverlay;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class mapa1 extends AppCompatActivity {

    MapView map = null;
    MyLocationNewOverlay myLocationOverlay = null;
    CompassOverlay mCompassOverlay =null;
    GeoPoint loc = null;
    GeoPoint carro_loc =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.mapa1_layout);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);


        //GpsMyLocationProvider prov= new GpsMyLocationProvider(ctx);
        //prov.addLocationSource(LocationManager.GPS_PROVIDER);
        //isto diz na documentacao nao sei o que faz
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        GpsMyLocationProvider provider = new GpsMyLocationProvider(this);
        provider.addLocationSource(LocationManager.GPS_PROVIDER);
        myLocationOverlay = new MyLocationNewOverlay(provider, map);


        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();


        map.getOverlays().add(myLocationOverlay);


        mCompassOverlay = new CompassOverlay(ctx, new InternalCompassOrientationProvider(ctx), map);
        mCompassOverlay.enableCompass();
        map.getOverlays().add(this.mCompassOverlay);


        LocationManager locationmanager =(LocationManager)getSystemService(ctx.LOCATION_SERVICE);
        try{
            Location location = locationmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            double lon = location.getLongitude();
            double lat = location.getLatitude();
            loc = new GeoPoint(lat, lon);
        }
        catch (SecurityException e){

        }


        IMapController mapController = map.getController();
        mapController.setZoom(18.5);
        mapController.setCenter(loc);

        /*----------------- FTP apache commons ------------------------
         */

        /* comentar para teste *
        FTPClient ftp = new FTPClient();

        try {
            ftp.connect("192.168.1.4",21);
            Log.e("connect",ftp.getReplyString()+ "connect ?");

            ftp.login("pi","raspberry");
            Log.e("login",ftp.getReplyString());

            InputStream is = new BufferedInputStream(ftp.retrieveFileStream("file.txt"));
            Log.e("get_file", ftp.getReplyString());
            Scanner scan = new Scanner(is).useDelimiter("\\A");
            String coord = scan.nextLine();
            String[] coord_str = coord.split(",");
            Log.e("log_tag",coord_str[0] +"," + coord_str[1]);
            double al = Double.parseDouble(coord_str[0]);
            double lo = Double.parseDouble(coord_str[1]);
            Log.e("double",al + "," +lo);
            carro_loc = new GeoPoint(al,lo);

        } catch (IOException e) {
            e.printStackTrace();
        }*/



        /* -----------------------routing---------------------------------------
         usar na mesma geopoints anteriores possivelmente necessario outras classes
         Questmap Key:  ptdB2Pv0sWFWbNoWsSQTAtA3FSfrFz66 */

        //RoadManager roadManager = new OSRMRoadManager(ctx);
        RoadManager roadManager = new MapQuestRoadManager("ptdB2Pv0sWFWbNoWsSQTAtA3FSfrFz66");
        roadManager.addRequestOption("routeType=pedestrian");

        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();

        waypoints.add(loc);
        GeoPoint endPoint = new GeoPoint(41.18447, -8.63047);
        waypoints.add(endPoint);

        Marker carro = new Marker(map);
        Drawable carroicon = getResources().getDrawable(R.drawable.carmarker);
        carro.setPosition(endPoint);
        carro.setIcon(carroicon);
        carro.setTitle("Está aqui o Carro!");
        map.getOverlays().add(carro);


        Road road = roadManager.getRoad(waypoints);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);

        map.getOverlays().add(roadOverlay);

        /* meter as bubbles com direções*/
        Drawable nodeIcon = getResources().getDrawable(R.drawable.marker5);
        for (int i=0; i<road.mNodes.size() -1; i++){
            RoadNode node = road.mNodes.get(i);
            Marker nodeMarker = new Marker(map);
            nodeMarker.setPosition(node.mLocation);
            nodeMarker.setIcon(nodeIcon);
            nodeMarker.setTitle("Step "+i);
            map.getOverlays().add(nodeMarker);

            nodeMarker.setSnippet(node.mInstructions);
            nodeMarker.setSubDescription(Road.getLengthDurationText(this, node.mLength, node.mDuration));

            int arrow = node.mManeuverType;
            String a = "a"+arrow;
            int id = getResources().getIdentifier(a,"drawable",getPackageName());

            try {
                Drawable icon = getResources().getDrawable(id);
                nodeMarker.setImage(icon);
            }
            catch (Exception e){

            }

        }


    }
    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
        myLocationOverlay.disableMyLocation();
        myLocationOverlay.disableFollowLocation();
    }
}
