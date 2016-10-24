package yuber.yuber.activity;

/**
 * Created by Agustin on 20-Oct-16.
 */
import android.app.ProgressDialog;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import yuber.yuber.R;

/**
 * A actualFragment that launches other parts of the demo application.
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
    private Fragment actualFragment = null;

    // Progress Dialog Object
    ProgressDialog prgDialog;



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



        //seteando listener en boton
        Button botonOK = (Button) v.findViewById(R.id.button3);;
        botonOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                loginUser(v);
            }
        });

        // PARA TESTING... SEGURAMENTE SIN USO FUTURO, PODRIA SER ELIMINADO O REUSADO EN OTRO CODIGO

        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(getActivity());
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);


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
                switchGPS = (Switch) actualFragment.getView().findViewById(R.id.switchLocalization);
                textoUbicacionOrigen = (TextView) actualFragment.getView().findViewById(R.id.textUbicacionOrigen);
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
                textoUbicacionDestino = (TextView) actualFragment.getView().findViewById(R.id.textUbicacionDestino);
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
                actualFragment = new MapCallYuberFragment();
                break;
            case LLAMANDO_YUBER:
                actualFragment = new MapWaitYFragment();
                break;
            case ELIGIENDO_DESTINO:
                //ELEGIR DESTINO /// AGREGAR CODIGO
                //mActualState = state.ELIGIENDO_ORIGEN;
                actualFragment = new MapYubConfirmadoFragment();

                break;
            default:
                break;
        }
        if (actualFragment != null) {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.FlashBarLayout, actualFragment);
            fragmentTransaction.commit();
        }
    }


    /**
     * Method gets triggered when Login button is clicked
     *
     * @param view
     */
    public void loginUser(View view){
        //under button properties
        //android:onClick="loginUser"

        // Instantiate Http Request Param Object
        RequestParams params = new RequestParams();
        // When Email Edit View and Password Edit View have values other than Null

        params.put("lat", "43"); //http://api.geonames.org/findNearByWeatherJSON?lat=43&lng=-2&username=demo
        params.put("lng", "-2");
        params.put("username", "demo");
       /*
        // Put Http parameter username with value of Email Edit View control
        params.put("username", email);
        // Put Http parameter password with value of Password Edit Value control
        params.put("password", password);
        */
        // Invoke RESTful Web Service with Http parameters
        invokeWS(params);

    }

    /**
     * Method that performs RESTful webservice invocations
     *
     * @param params
     */
    public void invokeWS(RequestParams params){
        // Show Progress Dialog
        prgDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        //  SyncHttpClient client = new SyncHttpClient();

        AsyncHttpClient client = new AsyncHttpClient();
        //client.get("http://api.geonames.org/findNearByWeatherJSON?",params ,new AsyncHttpResponseHandler() {
        client.get("http://api.geonames.org/findNearByWeatherJSON?",params ,new AsyncHttpResponseHandler() {
            // ANTERIOR
            // client.get("http://192.168.2.2:9999/useraccount/login/dologin",params ,new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog
                prgDialog.hide();
                try {
                    // JSON Object
                    JSONObject obj = new JSONObject(response);
                    Boolean funcionaWS = true;
                    if (obj.has("status"))
                        funcionaWS = obj.get("status").toString().contains("been exceeded");
                    else
                        funcionaWS = obj.has("weatherObservation");

                    // When the JSON response has status boolean value assigned with true
                    if( funcionaWS){ //|| obj.getString()
                        Toast.makeText(getActivity().getApplicationContext(), "You are successfully logged in!", Toast.LENGTH_LONG).show();
                        // Navigate to Home screen
                        displayView(state.ELIGIENDO_DESTINO);
                    }
                    // Else display error message
                    else{
                        //errorMsg.setText(obj.getString("error_msg"));
                        //Toast.makeText(getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_LONG).show();
                        Toast.makeText(getActivity().getApplicationContext(), obj.toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getActivity().getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }
            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(getActivity().getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getActivity().getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
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