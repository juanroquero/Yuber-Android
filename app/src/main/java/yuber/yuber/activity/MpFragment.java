package yuber.yuber.activity;

/**
 * Created by Agustin on 20-Oct-16.
 */
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import yuber.yuber.R;

/**
 * A fragment that launches other parts of the demo application.
 */
public class MpFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener {

    MapView mMapView;
    private GoogleMap googleMap;

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private Marker mDestinationMarker;

    private final int[] MAP_TYPES = { GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE }; /// NO NECESARIO SE PUEDE SACAR YA QUE NO INTERESA LA FORMA DEL TERRENO
    private int curMapTypeIndex = 1;

    //Elementos del UI
    private Switch switchGPS;
    private TextView textoUbicacionOrigen;
    private TextView textoUbicacionDestino;
    private Button buttonLlammarUber;
    private enum state {ELIGIENDO_ORIGEN, LLAMANDO_YUBER, ELIGIENDO_DESTINO};
    private state mActualState;
    private Fragment fragment = null;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.fragment_mp, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();


        buttonLlammarUber = (Button) v.findViewById(R.id.callYuberButton);
        mActualState = state.ELIGIENDO_ORIGEN;
        displayView(mActualState);

        //seteando listener en boton
        buttonLlammarUber.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Estados del boton en funcion de los clicks
                switch (mActualState) {
                    case ELIGIENDO_ORIGEN:
                        mActualState = state.LLAMANDO_YUBER;
                        displayView(mActualState);
                        buttonLlammarUber.setText("CANCELAR YUBER");
                        break;
                    case LLAMANDO_YUBER:
                        mActualState = state.ELIGIENDO_ORIGEN;
                        displayView(mActualState);
                        if (mDestinationMarker != null)
                            mDestinationMarker.remove();
                        buttonLlammarUber.setText("SOLICITAR YUBER");
                        break;
                    case ELIGIENDO_DESTINO:
                        //ELEGIR DESTINO /// AGREGAR CODIGO
                        mActualState = state.ELIGIENDO_ORIGEN;

                        break;
                    default:
                        break;
                }
            }
        });




/*
        // EVENTO ASOCIADO AL SWITCH
        switchGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                 @Override
                                                 public void onCheckedChanged(CompoundButton cb, boolean on) {
                                                     if (on) {
                                                         //Do something when Switch button is on/checked
                                                         textoUbicacionOrigen.setText("Tu ubicacion actual (del GPS no funciona)");
                                                     } else {
                                                         //Do something when Switch is off/unchecked
                                                         textoUbicacionOrigen.setText("Ubicacion del GPS... no funciona");
                                                     }
                                                 }
        });
               /*
        PORQUERIA ----posiblemente util en un futuro?

        // latitude and longitude
        double latitude = 17.385044;
        double longitude = 78.486671;

        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(latitude, longitude)).title("Hello Maps");

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        // adding marker
        googleMap.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(17.385044, 78.486671)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        */

        // Perform any camera updates here
        return v;
    }




    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        mGoogleApiClient = new GoogleApiClient.Builder( getActivity() )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi( LocationServices.API )
                .build();

        initListeners();
    }

    private void initListeners() {
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnInfoWindowClickListener( this );
        googleMap.setOnMapClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        mCurrentLocation = LocationServices
                .FusedLocationApi
                .getLastLocation( mGoogleApiClient );

        initCamera( mCurrentLocation );
    }

    //gennymotion
// jwt token


    @Override
    public void onConnectionSuspended(int i) {
        //handle play services disconnecting if location is being constantly used
    }

    private void initCamera( Location location ) {
        //improvisacion para ver si anda con ubicacio inventada
        LatLng ubicacion = new LatLng(-34.9, -56.16 );
        //
        CameraPosition position = CameraPosition.builder()
                .target( ubicacion)//new LatLng( location.getLatitude(),location.getLongitude() ) )
                .zoom( 14f )
                .bearing( 0.0f )
                .tilt( 0.0f )
                .build();

        googleMap.animateCamera( CameraUpdateFactory
                .newCameraPosition( position ), null );

        googleMap.setMapType( MAP_TYPES[curMapTypeIndex] );
        googleMap.setMyLocationEnabled( true );
        googleMap.getUiSettings().setZoomControlsEnabled( true );
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Create a default location if the Google API Client fails. Placing location at Googleplex
        mCurrentLocation = new Location( "" );
        mCurrentLocation.setLatitude( -34.9 );
        mCurrentLocation.setLongitude( -56.16 );
        initCamera(mCurrentLocation);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText( getActivity(), "Clicked on marker", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        MarkerOptions options;
        switch (mActualState) {
            case ELIGIENDO_ORIGEN:
                switchGPS = (Switch) fragment.getView().findViewById(R.id.switchLocalization);
                textoUbicacionOrigen = (TextView) fragment.getView().findViewById(R.id.textUbicacionOrigen);
                switchGPS.setChecked(false);
                if (mDestinationMarker != null)
                    mDestinationMarker.remove();
                options = new MarkerOptions().position(latLng);
                options.title(getAddressFromLatLng(latLng));
                options.icon(BitmapDescriptorFactory.defaultMarker());
                mDestinationMarker = googleMap.addMarker(options);
                textoUbicacionOrigen.setText(getAddressFromLatLng(latLng));
                break;
            case ELIGIENDO_DESTINO:
                //ELEGIR DESTINO /// AGREGAR CODIGO
                textoUbicacionDestino = (TextView) fragment.getView().findViewById(R.id.textUbicacionDestino);
                if (mDestinationMarker != null)
                    mDestinationMarker.remove();
                options = new MarkerOptions().position(latLng);
                options.title(getAddressFromLatLng(latLng));
                options.icon(BitmapDescriptorFactory.defaultMarker());
                mDestinationMarker = googleMap.addMarker(options);
                textoUbicacionDestino.setText(getAddressFromLatLng(latLng));

                break;
            default:
                break;
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        MarkerOptions options = new MarkerOptions().position( latLng );
        options.title( getAddressFromLatLng( latLng ) );

        options.icon( BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource( getResources(),
                        R.mipmap.ic_launcher ) ) );

        googleMap.addMarker( options );
    }

    private String getAddressFromLatLng( LatLng latLng ) {
        Geocoder geocoder = new Geocoder( getActivity() );
        String address = "";
        try {
            address =geocoder
                    .getFromLocation( latLng.latitude, latLng.longitude, 1 )
                    .get( 0 ).getAddressLine( 0 ) ;
        } catch (IOException e ) {
            // this is the line of code that sends a real error message to the  log
            Log.e("ERROR", "ERROR IN CODE: " + e.toString());
            // this is the line that prints out the location in the code where the error occurred.
            e.printStackTrace();
            return "ERROR_IN_CODE";
        }
        return address;
    }


    private void displayView(state estado) {
        switch (estado) {
            case ELIGIENDO_ORIGEN:
                fragment = new MapCallYuberFragment();
                break;
            case LLAMANDO_YUBER:
                fragment = new MapWaitYFragment();
                break;
            case ELIGIENDO_DESTINO:
                //ELEGIR DESTINO /// AGREGAR CODIGO
                mActualState = state.ELIGIENDO_ORIGEN;

                break;
            default:
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.FlashBarLayout, fragment);
            fragmentTransaction.commit();
        }
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