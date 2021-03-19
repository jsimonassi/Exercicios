/*
 * Fragment: MapsFragment
 *
 * Descrição: Fragmento usado dentro de "Exercise Activity" com a api do google maps. Nesta classe
 * são calculadas as distâncias através de polylines, altitude, dentre outros recursos da api do google maps.
 */




package com.simonassi.exercicios.ui;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.simonassi.exercicios.R;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {

    private MapView mMapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient client;
    private Polyline polyline;
    private static List<LatLng> list;
    private Marker marker;
    public static boolean IN_EXERCISE = false;
    public static double currentAltitude = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);

        mMapView =  rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                list = new ArrayList<>();
                Log.d("MAP", "Entrei aqui, mapa tá pronto");

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // You can use the API that requires the permission.
                    Log.d("MAP", "Entrei aqui, tenho permissão");
                    refreshMap();
                }else{
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                    Log.d("MAP", "Entrei aqui, não tenho permissão, mas vou pedir");
                }
            }
        });

        return rootView;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                refreshMap();
            else
                Toast.makeText(getContext(), "Localização indisponível!", Toast.LENGTH_LONG).show();
        }
    }


    private void refreshMap(){

        client = LocationServices.getFusedLocationProviderClient(getActivity());

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5*1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Alta precisão

        LocationCallback locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null){
                    Log.d("MAP", "Local é nulo");
                    return;
                }

                for(Location location: locationResult.getLocations()){
                    currentAltitude = location.getAltitude();
                    LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

                    if(marker != null){
                        marker.remove();
                    }
                    MarkerOptions markerOptions = new MarkerOptions().position(currentPosition).title("Marker Title").snippet("Marker Description");
                    marker = googleMap.addMarker(markerOptions);
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(currentPosition).zoom(18).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    if(IN_EXERCISE){
                        list.add(currentPosition);
                        drawRoute();
                    }
                    //Log.d("MAP", location.getLatitude()+"");
                }
            }
        };
        client.requestLocationUpdates(locationRequest, locationCallback, null);

    }

    private void drawRoute(){
        PolylineOptions po;

        if(polyline == null){
            po = new PolylineOptions();
            for(int i = 0, tam = list.size(); i<tam;i++)
                po.add(list.get(i));

            po.color(Color.BLACK);
            polyline = googleMap.addPolyline(po);
        }else{
            polyline.setPoints(list);
        }
    }

    public static double getDistance(){
        double distance = 0;

        for(int i = 0, tam = list.size(); i<tam; i++){
            if(i < tam - 1){
                distance += distance(list.get(i), list.get(i+1));
            }
        }

        return distance/1000;//Em Km
    }

    public static double getAltitude(){
        return currentAltitude;
    }

    private static double distance(LatLng StartP, LatLng EndP) {
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return 6366000 * c;
    }




    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}