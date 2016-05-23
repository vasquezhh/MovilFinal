package com.example.vasquezh.stuff;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationStuff#newInstance} factory method to
 * create an instance of this fragment.
 */

public class LocationStuff extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback,TagDialog.NoticeTagDialogListener,AudioRecordTest.audioRecordTestInterface {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private TagDialog td;
    private AudioRecordTest audioRecordTest;


    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    protected Location mLastLocation;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private Boolean mRequestingLocationUpdates = true;
    private GoogleMap mMap;
    private ArrayList<LatLng> latLonArray;
    private FloatingActionButton fab, fab2,fab3, fab4;
    private Firebase m_fb;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    static final String ID_KEY = "idKeyStudent";
    static final String ID_NAME = "idNameStudent";
    static final String ID_KEY_ACTIVITY = "idKeyActivity";

    // TODO: Rename and change types of parameters
    private String keyStudent;
    private String nameStudent;
    private String keyActivity;

    public LocationStuff() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param keyS Parameter 1.
     * @param name Parameter 2.
     * @param keyA Parameter 2.
     * @return A new instance of fragment Student.
     */
    // TODO: Rename and change types and number of parameters
    public static LocationStuff newInstance(String keyS, String name,String keyA) {
        LocationStuff fragment = new LocationStuff();
        Bundle args = new Bundle();
        args.putString(ID_KEY, keyS);
        args.putString(ID_NAME, name);
        args.putString(ID_KEY_ACTIVITY,keyA);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        td = new TagDialog();
        td.mListener=this;

        audioRecordTest = new AudioRecordTest();
        audioRecordTest.audioInterface = this;

        if (getArguments() != null) {
            keyStudent  = getArguments().getString(ID_KEY);
            nameStudent = getArguments().getString(ID_NAME);
            keyActivity = getArguments().getString(ID_KEY_ACTIVITY);
        }
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        Date dateStart = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy 'at' hh:mm:ss");

        String strDateStart = sdf.format(dateStart);

        createLocationRequest();
        latLonArray = new ArrayList<LatLng>();
        Log.i("onCreate","LocationStuff" +" "+keyStudent+" "+nameStudent+" "+keyActivity);
        m_fb = new Firebase("https://vasquezhproyectofinal.firebaseio.com/");
        //m_fb.child("members").child(keyActivity).child(keyStudent)
        m_fb.child("members").child(keyActivity).child(keyStudent).child("startActivity").setValue(strDateStart);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_location_stuff, container, false);
        fab = (FloatingActionButton)v.findViewById(R.id.fab_cam);
        fab2 = (FloatingActionButton)v.findViewById(R.id.fab_voice);
        fab3 = (FloatingActionButton)v.findViewById(R.id.fab_text);
        fab4 = (FloatingActionButton)v.findViewById(R.id.fab_tag);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mGoogleApiClient.isConnected()) {
                    addLines();
                }else{
                    Snackbar.make(getView(),"Google is not connected",Snackbar.LENGTH_SHORT).show();
                }

                // create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, audioRecordTest).addToBackStack(null).commit();
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Snackbar.make(getView(),"hello note",Snackbar.LENGTH_SHORT).show();
                td.show(getFragmentManager(), "TagDialog");
            }
        });

        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mGoogleApiClient.isConnected()) {
                    if(mLastLocation!=null) {
                        LatLng pos = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,18));
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(pos)                // Sets the center of the map to lastLocation
                                .zoom(17)                   // Sets the zoom
                                .bearing(90)                // Sets the orientation of the camera to east
                                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                                .build();                   // Creates a CameraPosition from the builder
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }
            }
        });

        /*
           Obtencion del fragment mediante la actividad asi de esta manera realizar el casting del fragment a mapfragment
           para su posterior uso*/
        //MapFragment mapFragment = (MapFragment) (getActivity()).getFragmentManager().findFragmentById(R.id.map);
        //MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);

        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentByTag("mapFragment");
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.mapFragmentContainer, mapFragment, "mapFragment");
            ft.commit();
            fm.executePendingTransactions();
        }
        mapFragment.getMapAsync(this);

        Log.i("onCreateView","LocationStuff");
        return v;
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        Log.i("onStart","LocationStuff");
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        Log.i("onStop","LocationStuff");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        Log.i("onResume","LocationStuff");
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
        Log.i("onPause","LocationStuff");
    }

    @Override
    public void onDestroy(){
        Date dateFinish = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy 'at' hh:mm:ss");
        String strDateFinish = sdf.format(dateFinish);
        m_fb.child("members").child(keyActivity).child(keyStudent).child("finishActivity").setValue(strDateFinish);
        super.onDestroy();
        Log.i("onDestroy","LocationStuff");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            //latitud.setText(String.valueOf(mLastLocation.getLatitude()));
            //longitude.setText(String.valueOf(mLastLocation.getLongitude()));
        }
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latLonArray.add(new LatLng(location.getLatitude(),location.getLongitude()));
        mLastLocation=location;
        Log.i("LocationChanged", location.getLatitude() + " " + location.getLongitude());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(mMap==null) {
            mMap = googleMap;
            Log.e("onMapRea..",mMap.toString());
            addLines();
        }
        /*if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);*/

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    private void addLines() {
        if(latLonArray.size()>0) {
            mMap.addPolyline((new PolylineOptions())
                    .addAll(latLonArray).width(5).color(Color.BLUE)
                    .geodesic(true));

            // move camera to zoom on map
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLonArray.get(0),
                    18));
        }
    }

    /*
    * Code for camera
    *
    * */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.i("onActivityResult","LocationStuff"+" "+requestCode +" "+resultCode +" "+Activity.RESULT_OK);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                // Image captured and saved to fileUri specified in the Intent
                //Toast.makeText(getActivity(), "Image saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
               //Log.i("OnActivityResult","Image saved to:\n" + data.getData());
                LatLng lastLocation=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(lastLocation).title("Photo"));
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.i("LocationStuff","DataDialog Positive");
        String name = ((EditText)dialog.getDialog().findViewById(R.id.edtTagNote)).getText().toString();
        LatLng lastLocation=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(lastLocation).title(name));

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public String text(String textTag) {
        Log.i("LocationStuff","textTag");
        LatLng lastLocation=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(lastLocation).title(textTag));
        return null;
    }
}
