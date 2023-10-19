package com.example.aplicacionwendy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.textfield.TextInputLayout;

public class UsuarioFragment extends Fragment {

    public UsuarioFragment() {
        // Required empty public constructor
    }

    public static UsuarioFragment newInstance(String param1, String param2) {
        UsuarioFragment fragment = new UsuarioFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuario, container, false);

        TextView tvNombreMecanico = view.findViewById(R.id.textNombreUsuario);
        TextView tvTel= view.findViewById(R.id.textTelefonoUsuario);
        ImageView ImagenSesionIniciada=  view.findViewById(R.id.iconImageView);


        Button customButton = view.findViewById(R.id.cerrarSesion);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Credenciales", Context.MODE_PRIVATE);

        String ID_usuario = sharedPreferences.getString("ID_usuario", "");
        String nombre = sharedPreferences.getString("nombre", "");
        String telefono = sharedPreferences.getString("telefono", "");



        tvTel.setText("Telefono: " + telefono);
        tvNombreMecanico.setText(nombre);

        customButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });

        return view;
    }

    private void cerrarSesion() {

        if (isAdded()) {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("telefono");
            editor.remove("clave");
            editor.remove("rememberMe");
            editor.apply();

            Intent intent = new Intent(requireContext(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }
    }

}