package yuber.yuber.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import yuber.yuber.R;

/**
 * Fragmento con un diálogo personalizado
 */
public class FragmentDialogYuberDisponible extends DialogFragment {
    private static final String TAG = FragmentDialogYuberDisponible.class.getSimpleName();

    public FragmentDialogYuberDisponible() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createLoginDialogo();
    }

    /**
     * Crea un diálogo con personalizado para comportarse
     * como formulario de login
     *
     * @return Diálogo
     */
    public AlertDialog createLoginDialogo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialogo_fin_viaje, null);

        builder.setView(v);

        Button signup = (Button) v.findViewById(R.id.entrar_boton);
        Button signin = (Button) v.findViewById(R.id.entrar_boton);

        Log.d(TAG, "Se creo el dialogo con el login");

        signup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Crear Cuenta...
                        dismiss();
                    }
                }
        );

        signin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Loguear...
                        dismiss();
                    }
                }

        );

        return builder.create();
    }

}

