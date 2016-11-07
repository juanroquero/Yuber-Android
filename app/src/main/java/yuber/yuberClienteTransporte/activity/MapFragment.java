package yuber.yuberClienteTransporte.activity;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;


import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import yuber.yuberClienteTransporte.R;

/**
 * A actualFragment that launches other parts of the demo application.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener{



    public static final String TAG = "MAPA";

    private String Ip = "54.213.51.6";
    private String Puerto = "8080";

    MapView mMapView;
    private GoogleMap googleMap;
    private static int REQUEST_LOCATION;

    //del tutorial https://androidkennel.org/android-tutorial-getting-the-users-location/ para manejar el identificador del permiso concedido
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private Marker mDestinationMarker;

    //
    LocationRequest mLocationRequest;



    //Elementos del UI
    private Switch switchGPS;
    private TextView textoUbicacionOrigen;
    private TextView textoUbicacionDestino;
    private Button buttonLlammarUber;

    private enum mapState {ELIGIENDO_ORIGEN, BUSCANDO_YUBER, YUBER_EN_CAMINO, ELIGIENDO_DESTINO, DESTINO_ELEGIDO}


    private mapState mActualState;
    private Fragment actualFragment = null;


    //Banderas del broadcaster
    public static final String ACTION_INTENT = "MapFragment.action.BOX_UPDATE";
    public static final String ACTION_MI_UBICACION = "MapFragment.action.MI_UBICACION";


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
        } catch (Exception e) { //comentario
            e.printStackTrace();
        }


        //SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
        //       .findFragmentById(R.id.mapView);
        mMapView.getMapAsync(this);


        buttonLlammarUber = (Button) v.findViewById(R.id.callYuberButton);
        mActualState = mapState.ELIGIENDO_ORIGEN;
        displayView(mActualState);

        //seteando listener en boton
        buttonLlammarUber.setOnClickListener(createListenerBottomButton());


        //seteando listener en boton
        Button botonOK = (Button) v.findViewById(R.id.button3);
        botonOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (mActualState) {
                    case ELIGIENDO_ORIGEN:
                        //mostrarViajeFinalizado();
                        String test1 = "{\"usuarioPromedioPuntaje\":2.5,\"usuarioCorreo\":\"maxi@gmail.com\", \"usuarioTelefono\":\"098839498\",\"usuarioNombre\":\"ElMasi\",\"usuarioApellido\":\"Barnech\", \"marca\":\"BMW\", \"modelo\":\"320\",\"estado\":\"Ok\"}";
                        mostrarDialAceptarProveedor(test1);
                        break;
                    case BUSCANDO_YUBER:
                        String test = "{\"usuarioPromedioPuntaje\":2.5,\"usuarioCorreo\":\"maxi@gmail.com\", \"usuarioTelefono\":\"098839498\",\"usuarioNombre\":\"ElMasi\",\"usuarioApellido\":\"Barnech\", \"marca\":\"BMW\", \"modelo\":\"320\",\"estado\":\"Ok\"}";
                        mostrarDialAceptarProveedor(test);
                        //login_user()
                        break;
                    case DESTINO_ELEGIDO:
                        mostrarViajeFinalizado();
                        break;
                    case ELIGIENDO_DESTINO:
                        break;
                    default:
                        break;
                }
            }
        });

        // PARA TESTING... SEGURAMENTE SIN USO FUTURO, PODRIA SER ELIMINADO O REUSADO EN OTRO CODIGO
        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(getActivity());
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);


        IntentFilter filter = new IntentFilter(ACTION_INTENT);
         filter.addAction(ACTION_MI_UBICACION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(ActivityDataReceiver, filter);


        //EventBus.getDefault().register(this);

        Log.d(TAG, "SE CREO EL MAPA");

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }
        // Perform any camera updates here
        return v;
    } // FIN onCreate()

    // TO DELETE OR REUSE XXXX
    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        Log.d(TAG, "ADENTRO del eventbus: " + event.message);

        if (event.message.toString().equals("Mi Ubicacion")){


            //Toast.makeText(getActivity(), event.message, Toast.LENGTH_SHORT).show();


        }

    }
    /*
    // This method will be called when a SomeOtherEvent is posted
    @Subscribe
    public void handleSomethingElse(SomeOtherEvent event) {
        doSomethingWith(event);
    }
    */

    protected BroadcastReceiver ActivityDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(ACTION_INTENT.equals(intent.getAction()) && (mActualState == mapState.BUSCANDO_YUBER) ){
                //Si llega una notificacion "Proveedor acepto viaje" y se esta buscando Yuber
                String jsonProveedor = intent.getStringExtra("DATOS_PROVEEDOR");
                mActualState = mapState.YUBER_EN_CAMINO;
                displayView(mActualState);
                mostrarDialAceptarProveedor(jsonProveedor);
            }
             if(ACTION_MI_UBICACION.equals(intent.getAction())) {
                 LatLng myActualLatLng;


                 if(mCurrentLocation!= null)
                     myActualLatLng = new LatLng( mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude() );//new LatLng(-34.9, -56.16);
                 else
                     myActualLatLng = new LatLng(-34.9, -56.16);//new LatLng(-34.9, -56.16);
                 // LatLng myActualLatLng = new LatLng(-34.9, -56.16);//new LatLng(-34.9, -56.16);
                 //

                 if (mDestinationMarker != null)
                     mDestinationMarker.remove();

                 //marker inicial
                 //mDestinationMarker = googleMap.addMarker(new MarkerOptions().position(myActualLatLng).title(getAddressFromLatLng(myActualLatLng)));

                 MarkerOptions options;

                 options = new MarkerOptions().position(myActualLatLng);
                 options.title(getAddressFromLatLng(myActualLatLng));
                 options.icon(BitmapDescriptorFactory.defaultMarker());
                 mDestinationMarker = googleMap.addMarker(options);

                 textoUbicacionOrigen = (TextView) actualFragment.getView().findViewById(R.id.textUbicacionOrigen);
                 textoUbicacionOrigen.setText(getAddressFromLatLng(myActualLatLng));

                 // Llevar a la posicion actual
                 CameraPosition position = CameraPosition.builder()
                         .target(myActualLatLng)
                         .zoom(16f)
                         .bearing(0.0f)
                         .tilt(0.0f)
                         .build();
                 googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), null);



                 //DO
             }
        }
    };


    @Override
    public void onMapReady(GoogleMap googleMapParam) {
        googleMap = googleMapParam;
        LatLng myLocatLatLng;
        LatLng mdeoLatLng = new LatLng(-34, -56);
        Location myLocation = null;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Check Permissions Now
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);

        }
        else {
            // permission has been granted, continue as usual
            myLocation =
                    LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        // Add a marker in Montevideo and move the camera
        if (myLocation == null){
            myLocatLatLng = mdeoLatLng;
        }
        else{
            myLocatLatLng = new LatLng( myLocation.getLatitude(), myLocation.getLongitude());
        }


        initListeners();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void initListeners() {
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setOnMapClickListener(this);
    }




    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }



    @Override
    public void onConnected(Bundle bundle) {


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


        } else {
            // permission has been granted, continue as usual
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }


        mCurrentLocation = LocationServices
                .FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        initCamera(mCurrentLocation);


    /*
        podria usarse para hallar la velociad y mandarlo?
        http://www.androidtutorialpoint.com/intermediate/android-map-app-showing-current-location-android/
        */

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        /*

    */

    }

    //gennymotion
// jwt token


    @Override
    public void onConnectionSuspended(int i) {
        //handle play services disconnecting if location is being constantly used
    }

    private void initCamera(Location location) {
        //improvisacion para ver si anda con ubicacio inventada
        LatLng myActualLatLng;
        if(location!= null)
            myActualLatLng = new LatLng( location.getLatitude(),location.getLongitude() );//new LatLng(-34.9, -56.16);
        else
            myActualLatLng = new LatLng(-34.9, -56.16);//new LatLng(-34.9, -56.16);
        // LatLng myActualLatLng = new LatLng(-34.9, -56.16);//new LatLng(-34.9, -56.16);
        //
        CameraPosition position = CameraPosition.builder()
                .target(myActualLatLng)
                .zoom(16f)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();

        //marker inicial
        mDestinationMarker = googleMap.addMarker(new MarkerOptions().position(myActualLatLng).title(getAddressFromLatLng(myActualLatLng)));

        textoUbicacionOrigen = (TextView) actualFragment.getView().findViewById(R.id.textUbicacionOrigen);
        textoUbicacionOrigen.setText(getAddressFromLatLng(myActualLatLng));




        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), null);



        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled( true );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(getActivity(), "Need your location!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
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


    private void displayView(mapState estado) {
        switch (estado) {
            case ELIGIENDO_ORIGEN:
                actualFragment = new MapCallYuberFragment();
                break;
            case BUSCANDO_YUBER:
                actualFragment = new MapWaitYFragment();
                break;
            case YUBER_EN_CAMINO:
                actualFragment = new MapYubConfirmadoFragment();
                break;
            case ELIGIENDO_DESTINO:
                //ELEGIR DESTINO /// AGREGAR CODIGO
                //mActualState = state.ELIGIENDO_ORIGEN;
                actualFragment = new MapYubConfirmadoFragment();
                if (mDestinationMarker != null)
                    mDestinationMarker.remove();
                mDestinationMarker = null;

                break;
            default:
                break;
        }
        if (actualFragment != null) {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.containerMiniFrameMapFragment, actualFragment);
            fragmentTransaction.commit();
        }
    }


    private View.OnClickListener createListenerBottomButton(){
        View.OnClickListener clickListtener = new View.OnClickListener() {
            public void onClick(View v) {
                // Estados del boton en funcion de los clicks
                switch (mActualState) {
                    case ELIGIENDO_ORIGEN:
                        //Se pidio un Yuber
                        mActualState = mapState.BUSCANDO_YUBER;
                        displayView(mActualState);
                        buttonLlammarUber.setText("CANCELAR YUBER");
                        //BUSCAR LA UBICACION, MANDARLA EN PEDIR SERVICIO Y BLOQUEAR EL EL ORIGEN...
                        pedirServicio();
                        break;
                    case BUSCANDO_YUBER:
                        //Se cancelo el Yuber pedido
                        mActualState = mapState.ELIGIENDO_ORIGEN;
                        displayView(mActualState);
                        if (mDestinationMarker != null)
                            mDestinationMarker.remove();
                        buttonLlammarUber.setText("SOLICITAR YUBER");
                        break;
                    case ELIGIENDO_DESTINO:
                        //ELEGIR DESTINO /// AGREGAR CODIGO
                        if (mDestinationMarker != null) { //.isVisible())

                            mActualState = mapState.DESTINO_ELEGIDO;
                            buttonLlammarUber.setEnabled(false);
                        } else
                            Toast.makeText(getActivity().getApplicationContext(), "Por favor, elija un destino", Toast.LENGTH_LONG).show();


                        break;
                    default:
                        break;
                }
            }
        };
        return clickListtener;
    }




    private void mostrarViajeFinalizado(){

        //A USARSE EN UN FUTURO PARA MANDAR ARGUMENTOS
        // Bundle args = new Bundle();
        // args.putString("key", "value");
        FragmentDialogFinViaje newFragment = new FragmentDialogFinViaje();
        // newFragment.setArguments(args);
        newFragment.show(getActivity().getSupportFragmentManager(), "TAG");
    }

    private void mostrarDialAceptarProveedor(String jProveedor){

        Bundle args = new Bundle();
        args.putString("datos", jProveedor);
        FragmentDialogYuberDisponible newFragmentDialog = new FragmentDialogYuberDisponible();
        newFragmentDialog.setArguments(args);
        newFragmentDialog.show(getActivity().getSupportFragmentManager(), "TAG");

    }


    private void pedirServicio(){



        /*****************  Consulta a BD si existe el user ********************/
        String url = "http://" + Ip + ":" + Puerto + "/YuberWEB/rest/Cliente/PedirServicio";
        JSONObject obj = new JSONObject();
        try {
            //LA CHANCHA

            JSONObject jsonOrigen = new JSONObject();
            jsonOrigen.put("longitud", mCurrentLocation.getLongitude());
            jsonOrigen.put("latitud", mCurrentLocation.getLatitude());
            jsonOrigen.put("estado", "Ok");

            obj.put("correo", "pepe@hotmail.com");
            obj.put("servicioId", 2);
            obj.put("ubicacion", jsonOrigen);

            //LA POSTA
            /*
            obj.put("correo", email);
            obj.put("password", password);
            obj.put("deviceId", token);
            */

        } catch (JSONException e) {
            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(obj.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        RequestHandle Rq = client.post(null, url, entity, "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(String response) {
                if (response.contains("true") ){
                    //guardo token y email
                    Toast.makeText(getActivity().getApplicationContext(), "se pidio el servicio PAPAA", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "El usuario y/o contraseña son incorrectos", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Throwable error, String content){

                mActualState = mapState.ELIGIENDO_ORIGEN;
                displayView(mapState.ELIGIENDO_ORIGEN);
                if(statusCode == 404){
                    Toast.makeText(getActivity().getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }else if(statusCode == 500){
                    Toast.makeText(getActivity().getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Unexpected Error occured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
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


    @Override
    public void onLocationChanged(Location location)
    {
        mCurrentLocation = location;


        /* lo que hace abajo es tirar un marcador por cada vez que se mueve....
        Marker mCurrLocationMarker = null;

        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = googleMap.addMarker(markerOptions);

        //move map camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
*/

          /*
        //SE QUEDA LO DE ABAJO? O SE DEBERIA IR? XXX
        //stop location updates ---> ESTO PARA EL LISTENER de cuando se mueve el GPS
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

*/

    }




}// FIN CLASS MapFragment