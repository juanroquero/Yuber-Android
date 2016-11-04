package yuber.yuber.activity;

import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


import yuber.yuber.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;

    /*@Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailView = (EditText) findViewById(R.id.input_email);
        mPasswordView = (EditText) findViewById(R.id.input_password);
        Button loginButton = (Button) findViewById(R.id.btn_login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        TextView signUpLink = (TextView) findViewById(R.id.link_signup);
        assert signUpLink != null;
        signUpLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activitylink_signup
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });


        //BOTON OPCIONAL PARA SALTEARSE EL LOGIN
        Button botonSaltearLogin = (Button) findViewById(R.id.button4);
        botonSaltearLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saltearLogin(v);
                // <editor-fold defaultstate="collapsed" desc="EVENTO ASOCIADO AL SWITCH implementar en fragmento swtich?">
                /*
                try {
                    invokeWS();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                */ // </editor-fold>
            }
        });

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
              //  mEmailView.append("\n" + key + ": " + value);//tomar por culo
            }
        }


        //adding token
        String token = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "Token: " + token);



        // PARA TESTING... SEGURAMENTE SIN USO FUTURO, PODRIA SER ELIMINADO O REUSADO EN OTRO CODIGO
        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        Button loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Ingresando...");
        progressDialog.show();

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void saltearLogin(View view){
        //under button properties
        //android:onClick="loginUser"
        Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //QUE ES ESTO? jaja
        startActivity(homeIntent);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the OLDMainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        Button loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        Button loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailView.setError("Introduzca un email válido");
            valid = false;
        } else {
            mEmailView.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mPasswordView.setError("La contraseña debe ser de entre 4 y 10 caracteres alfanuméricos");
            valid = false;
        } else {
            mPasswordView.setError(null);
        }

        return valid;
    }






    // Progress Dialog Object
    ProgressDialog prgDialog;

    /**
     * Method that performs RESTful webservice invocations
     *
     * @param //params
     */
    public void invokeWS() throws JSONException, UnsupportedEncodingException {
        // Show Progress Dialog
        prgDialog.show();

        // Make RESTful webservice call using AsyncHttpClient object
        JSONObject obj = new JSONObject();

        obj.put("usuarioDireccion", "lsls123");
        obj.put("usuarioContraseña", "123");
        obj.put("usuarioTelefono", "050505050");
        obj.put("usuarioApellido", "FAFAFA");
        obj.put("usuarioNombre", "Sancho");
        obj.put("usuarioPromedioPuntaje", 0.0);
        obj.put("usuarioCorreo", "alfalfa@Gmail.com");
        obj.put("usuarioCiudad", "montevideo");
        obj.put("estado", "OK");

        AsyncHttpClient client = new AsyncHttpClient();
        ByteArrayEntity entity = new ByteArrayEntity(obj.toString().getBytes("UTF-8"));
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        rePost = 0;
        final Boolean[] funcionoWS = {false};
     //   while (rePost < 2 && !funcionoWS[0]){
            client.post(null, "http://54.191.204.230:8080/YuberWEB/rest/Cliente/RegistrarCliente/", entity, "application/json", new AsyncHttpResponseHandler(){
                // When the response returned by REST has Http response code '200'
                @Override
                public void onSuccess(String response) {
                    Log.d(TAG, "ADENTRO DEL ONSUCCESSSSSSSSSSSSSSSSSSSSSSSSSSS");
                    // Hide Progress Dialog
                    prgDialog.hide();
                    try {
                        if (response.contains("Ok")){
                            funcionoWS[0] = true;
                            Toast.makeText(getApplicationContext(), "Se creo el usuario!", Toast.LENGTH_LONG).show();
                        }
                        // Else display error message
                        else{
                           // if (rePost == 1){
                                funcionoWS[0] = true;
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                           // }
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                       // if (rePost == 1){
                            Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                       // }
                    }
                }
                // When the response returned by REST has Http response code other than '200'
                @Override
                public void onFailure(int statusCode, Throwable error,
                                      String content) {
                    Log.d(TAG, "FALLOOOOOOOO :'( ");
                  //  if (rePost == 1){
                        // Hide Progress Dialog
                        prgDialog.hide();
                        // When Http response code is '404'
                        if(statusCode == 404){
                            Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if(statusCode == 500){
                            Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else{
                            Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                        }
                    //}

                }
            });
           // rePost++;
        //}

    }

    private int rePost;





/*

    client.addHeader("Content-Type","application/json");
    client.post("http://172.16.113.205:8080/YuberWEB/rest/Cliente/RegistrarCliente/"

    String datos = '"usuarioDireccion": "lsls123"), "usuarioContraseña":"123"), "usuarioTelefono": "050505050"),
                    "usuarioApellido": "FAFAFA","usuarioNombre": "EL_FARRUKO"), "usuarioPromedioPuntaje": 0.0,
                            "usuarioCorreo": "FARRUKO@REGGAETON.com");\n    params.put("usuarioCiudad", "montevideo");\n    params.put("estado", "OK"';
*/
    // HTTP POST request
    private void sendPost() throws Exception {

        RequestParams params = new RequestParams();
        JSONObject obj = new JSONObject();

        obj.put("usuarioDireccion", "lsls123");
        obj.put("usuarioContraseña", "123");
        obj.put("usuarioTelefono", "050505050");
        obj.put("usuarioApellido", "FAFAFA");
        obj.put("usuarioNombre", "EL_FARRUKO");
        obj.put("usuarioPromedioPuntaje", 0.0);
        obj.put("usuarioCorreo", "FARRUKO@REGGAETON.com");
        obj.put("usuarioCiudad", "montevideo");
        obj.put("estado", "OK");

        URL url = new URL("http://172.16.113.205:8080/YuberWEB/rest/Cliente/RegistrarCliente");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();


        // Obtener la conexión
        //HttpURLConnection con = null;

        try {
            // Construir los datos a enviar
            String data = "body=" + URLEncoder.encode(obj.toString(),"UTF-8");

            con = (HttpURLConnection)url.openConnection();

            // Activar método POST
            con.setDoOutput(true);

            // Tamaño previamente conocido
            con.setFixedLengthStreamingMode(data.getBytes().length);

            // Establecer application/x-www-form-urlencoded debido a la simplicidad de los datos
            con.setRequestProperty("Content-Type","application/json");

            OutputStream out = new BufferedOutputStream(con.getOutputStream());

            out.write(data.getBytes());
            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(con!=null)
                con.disconnect();
        }
    }

    public boolean sendPost2() throws JSONException {
        RequestParams params = new RequestParams();
        JSONObject obj = new JSONObject();
/*
        obj.put("usuarioDireccion","lsls123");
        obj.put("usuarioContraseña", "123");
        obj.put("usuarioTelefono", "050505050");
        obj.put("usuarioApellido", "FAFAFA");
        obj.put("usuarioNombre", "EL_FARRUKO");
        obj.put("usuarioPromedioPuntaje", new Double(0.0));
        obj.put("usuarioCorreo", "FARRUKO@REGGAETON.com");
        obj.put("usuarioCiudad", "montevideo");
        obj.put("estado", "OK");

        URL url = new URL("http://172.16.113.205:8080/YuberWEB/rest/Cliente/RegistrarCliente");


        HttpURLConnection connection;
        try {





            URL gcmAPI = new URL("http://172.16.113.205:8080/YuberWEB/rest/Cliente/RegistrarCliente");
            connection = (HttpURLConnection) gcmAPI.openConnection();

            connection.setRequestMethod("POST");// type of request
            connection.setRequestProperty("Content-Type", "application/json");//some header you want to add
            connection.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            //content is the object you want to send, use instead of NameValuesPair
            mapper.writeValue(dataOutputStream, obj);

            dataOutputStream.flush();
            dataOutputStream.close();

            responseCode = connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (responseCode == 200) {
            Log.i("Request Status", "This is success response status from server: " + responseCode);
            return true;
        } else {
            Log.i("Request Status", "This is failure response status from server: " + responseCode);
            return false;
        }

        */
        return true;
    }











} // FIN CLASE LOGIN
