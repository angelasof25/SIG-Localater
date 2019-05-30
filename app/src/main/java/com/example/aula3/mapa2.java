package com.example.aula3;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class mapa2 extends AppCompatActivity {


    MapView map = null;
    MyLocationNewOverlay myLocationOverlay = null;
    CompassOverlay mCompassOverlay =null;
    GeoPoint loc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.mapa2_layout);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

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

        findViewById(R.id.refresh2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRestart();
            }
        });


    }
    @Override
    protected void onRestart() {

        // TODO Auto-generated method stub
        super.onRestart();
        Intent i = new Intent(mapa2.this, mapa2.class);  //your class
        startActivity(i);
        finish();

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
