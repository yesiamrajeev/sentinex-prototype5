package com.example.sentenix_prototype2;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Marker haryanaMarker;
    private Polyline currentPolyline;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getLocationPermission();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setupShowRouteButton();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng delhiLatLng = new LatLng(20.24811936715895, 85.80219702893264); // Delhi coordinates
        LatLng haryanaLatLng = new LatLng(20.24783418557049, 85.80123567086278); // Haryana coordinates
        LatLng gurugramLatLng = new LatLng(20.24879434926989, 85.80107513143706); // Gurugram coordinates

        MarkerOptions gurugramOptions = new MarkerOptions()
                .position(delhiLatLng)
                .title("Cafeteria")
                .snippet("Faculty room E006");

        MarkerOptions haryanaMarkerOptions = new MarkerOptions()
                .position(haryanaLatLng)
                .title("E Block")
                .snippet("Faculty room E006");

        mMap.addMarker(gurugramOptions);
        haryanaMarker = mMap.addMarker(haryanaMarkerOptions);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.equals(haryanaMarker)) {

                    haryanaMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    return true;
                } else {

                    if (haryanaMarker != null) {
                        haryanaMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
                    }
                }
                return false;
            }
        });


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (haryanaMarker != null) {
                    haryanaMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
                }
            }
        });


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(delhiLatLng, 10));
    }

    private void getLocationPermission() {
        // Check and request location permissions
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initMap();
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                initMap();
            } else {

                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupShowRouteButton() {
        Button showRouteButton = findViewById(R.id.showRouteButton);
        showRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haryanaMarker != null) {

                    LatLng delhiLatLng = new LatLng(20.249243, 85.801250); // Delhi coordinates
                    LatLng haryanaLatLng = haryanaMarker.getPosition();
                    calculateDirections(delhiLatLng, haryanaLatLng);
                } else {
                    Toast.makeText(MapActivity.this, "Please select a marker first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void calculateDirections(LatLng origin, LatLng destination) {
        // Create a PolylineOptions object for the route
        PolylineOptions polylineOptions = new PolylineOptions()
                .add(origin) // Add the origin point
                .add(new LatLng(20.24879434926989, 85.80107513143706)) // Add Gurugram as a waypoint
                //.add(new LatLng(20.248635554551498, 85.80166924319269))
                .add(new LatLng(20.248617111704224, 85.80109142646087))
                .add(new LatLng(20.248641043230148, 85.80096669747805))
                .add(new LatLng(20.248161864718373, 85.80096245499557))
                .add(new LatLng(20.247947027480805, 85.80126451974314))
                .add(new LatLng(20.24783418557049, 85.80123567086278))
                .add(destination) // Add the final destination
                .color(ContextCompat.getColor(this, R.color.routeColor)) // Customize the color
                .width(10); // Customize the width


        // Add the polyline to the map
        if (currentPolyline != null) {
            currentPolyline.remove(); // Remove previous polyline if exists
        }
        currentPolyline = mMap.addPolyline(polylineOptions);

        // Hide the marker for Gurugram
        if (haryanaMarker != null) {
            haryanaMarker.setVisible(false);
        }
    }
    private double calculateDistanceOfPolyline(List<LatLng> points) {
        double totalDistance = 0;

        if (points.size() < 2) {
            return totalDistance;
        }

        for (int i = 0; i < points.size() - 1; i++) {
            LatLng startPoint = points.get(i);
            LatLng endPoint = points.get(i + 1);


            double distance = calculateHaversineDistance(startPoint, endPoint);
            totalDistance += distance;
            double totalDistanceInMeters = calculateDistanceOfPolyline(currentPolyline.getPoints());
            TextView totalDistanceTextView = findViewById(R.id.totalDistanceTextView);
            totalDistanceTextView.setText("Total Distance: " + totalDistanceInMeters + " meters");
            totalDistanceTextView.setVisibility(View.VISIBLE);        }

        return totalDistance;
    }

    private double calculateHaversineDistance(LatLng point1, LatLng point2) {

        final double R = 6371000.0; // Earth's radius in meters

        double lat1 = Math.toRadians(point1.latitude);
        double lon1 = Math.toRadians(point1.longitude);
        double lat2 = Math.toRadians(point2.latitude);
        double lon2 = Math.toRadians(point2.longitude);


        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return distance;
    }


    private class FetchDirectionsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {

                String url = urls[0];
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {

                PolylineOptions polylineOptions = new PolylineOptions();

                polylineOptions.color(ContextCompat.getColor(MapActivity.this, R.color.routeColor));
                polylineOptions.width(10);


                if (currentPolyline != null) {
                    currentPolyline.remove();
                }
                currentPolyline = mMap.addPolyline(polylineOptions);
            } else {

                Toast.makeText(MapActivity.this, "Error fetching directions", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
