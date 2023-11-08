package com.example.aplicacionwendy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.aplicacionwendy.Adaptadores.Utiles;

import java.util.HashMap;
import java.util.Map;

public class RegistrarNuevoUsuarioActivity extends AppCompatActivity {

    String url = "https://envelopesoft.000webhostapp.com/mostrar.php";

    Context context;
    String personalToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_nuevo_usuario);


        context = this;


        LinearLayout LayoutCerrar = findViewById(R.id.LayoutCerrar);
        LinearLayout btnGuardarUsuario = findViewById(R.id.btnGuardarUsuario);


        EditText EditTextClave = findViewById(R.id.EditTextClave);
        EditText EditTextNombre = findViewById(R.id.EditTextNombre);
        EditText EditTextTelefono = findViewById(R.id.EditTextTelefono);


        LayoutCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {  Utiles.EnviarAActividad(context, LoginActivity.class, true);

            }
        });


        btnGuardarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String clave = EditTextClave.getText().toString().trim();
                String nombre = EditTextNombre.getText().toString().trim();
                String telefono = EditTextTelefono.getText().toString().trim();

                RegistrarNuevoUsuario(telefono, nombre, clave);

            }
        });

    }


    private void RegistrarNuevoUsuario(String telefono, String nombre, String clave) {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String response) {

                if (response.equalsIgnoreCase("    El numero de telefono ya esta registrado")) {
                    Utiles.crearToastPersonalizado(context, "Este número de teléfono ya está registrado.");

                } else {
                    Utiles.crearToastPersonalizado(context, "Usuario creado perfectamente \nPor favor inicia sesion");
                    Utiles.EnviarAActividad(context, LoginActivity.class, true);

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Utiles.crearToastPersonalizado(context, "Hubo un error al crear el usuario. Revisa la conexi{on");

            }
        }
        ) {
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("opcion", "6");
                params.put("nombreUsuarioNuevo", nombre);
                params.put("telefonoUsuarioNuevo", telefono);
                params.put("claveUsuarioNuevo", clave);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);


    }


}