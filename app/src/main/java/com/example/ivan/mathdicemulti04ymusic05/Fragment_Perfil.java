package com.example.ivan.mathdicemulti04ymusic05;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Fragment_Perfil extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    //Tipos definidos
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;


    //Fichero de guardado
    private Uri fileUri;

    //Necesario para la parte de la localizacion
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    protected static final String TAG = "Localizando";

    //Declaramos los TextViews de las coordenadas
    public TextView latitud;
    public TextView longitud;


    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Conectado con éxito!!");
        try {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {

                //Metemos las coordenadas en dos TextView.
                TextView longitud = (TextView) getView().findViewById(R.id.longitud);
                TextView latitud = (TextView) getView().findViewById(R.id.latitud);

                longitud.setText(" " + String.valueOf(mLastLocation.getLongitude()));
                latitud.setText(" " + String.valueOf(mLastLocation.getLatitude()));

                Log.i(TAG, String.valueOf(mLastLocation.getLatitude()));
                Log.i(TAG, String.valueOf(mLastLocation.getLongitude()));

            }
        } catch (SecurityException e) {
            Log.i(TAG, "Denegada la localización!!");

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Finalizado o Suspendido!!");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.i(TAG, "Error en la conexion " + connectionResult.getErrorMessage());

    }


    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    public interface PerfilFragmentListener {
        public void onListSelected(int position);
    }


    public static Fragment_Perfil newInstance(String param1, String param2) {
        Fragment_Perfil fragment = new Fragment_Perfil();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }


    public Fragment_Perfil() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fragment__perfil, container, false);


        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        //Boton para la foto!
        final Button boton = (Button) v.findViewById(R.id.button);

        //Implementamos las preferencias
        //para guardar el nombre y apellidos introducidos por el user


        //Declaramos los EditText que recogeran los que el ususario escriba.
        //Declaramos el botón donde guardaremos los datos del user.

        final EditText nombreusuario = (EditText) v.findViewById(R.id.escribenombre);
        final EditText apellidosusuario = (EditText) v.findViewById(R.id.escribeapellidos);
        final Button btnGuardarDatosUser = (Button) v.findViewById(R.id.guardarPref);

        //Recuperamos el objeto de preferencias
        final SharedPreferences preferenciasuser = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //Obtenemos un editor para modificar las preferencias
        final SharedPreferences.Editor editor = preferenciasuser.edit();

        nombreusuario.setText(preferenciasuser.getString("Nombre Usuario", ""));
        apellidosusuario.setText(preferenciasuser.getString("Apellidos Usuario", ""));

        btnGuardarDatosUser.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Toast.makeText(Fragment_Perfil.this, "He recuperado "+ isTrue + " y " + text, Toast.LENGTH_SHORT).show();

                        //Guardamos los nuevos valores.
                        editor.putString("Nombre Usuario",
                                nombreusuario.getText().toString());

                        editor.putString("Apellidos Usuario",
                                apellidosusuario.getText().toString());

                        //Guardamos los cambios
                        editor.commit();
                    }
                }
        );

        boton.setOnClickListener(new Button.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         // create Intent to take a picture and return control to the calling application
                                         Intent camara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                         fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                                         camara.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                                         // start the image capture Intent
                                         startActivityForResult(camara, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

                                         //ImageView que captura la camara
                                         final ImageView imgCamara = (ImageView) getView().findViewById(R.id.imagenCamara);

                                         Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath()
                                                 + "/Pictures/MyCameraApp/CAPTURA_IMAGEN_CAMARA.jpg");
                                         imgCamara.setImageURI(uri);
                                     }
                                 }
        );
        return v;
    }

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "CAPTURA_IMAGEN_CAMARA" + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
