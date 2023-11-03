package com.example.aplicacionwendy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.aplicacionwendy.databinding.ActivityLoginBinding;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    String url = "https://envelopesoft.000webhostapp.com/mostrar.php";

    private RequestQueue rq;
    Context context;

    String personalToken;
    TextView inputUsername, inputPassword;

    CheckBox checkBoxRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;
        rq = Volley.newRequestQueue(context);
        inputUsername = findViewById(R.id.correoET);
        inputPassword = findViewById(R.id.passwordET);
        TextView REGISTRASE= findViewById(R.id.REGISTRASE);
        checkBoxRememberMe = findViewById(R.id.checkBoxRememberMe);


        tomarToken();
        SharedPreferences sharedPreferences = getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        boolean rememberMe = sharedPreferences.getBoolean("rememberMe", false);
        if (rememberMe) {
            String savedUsername = sharedPreferences.getString("telefono", "");
            String savedPassword = sharedPreferences.getString("clave", "");
            inputUsername.setText(savedUsername);
            inputPassword.setText(savedPassword);
            checkBoxRememberMe.setChecked(true);

            Intent intent = new Intent(LoginActivity.this, Activity_Binding.class);
            startActivity(intent);
            finish();

        }


        REGISTRASE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                com.example.aplicacionwendy.Adaptadores.Utils.EnviarAActividad(context, RegistrarNuevoUsuarioActivity.class, true);
            }
        });
    }

    private void tomarToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String token) {
                        Log.d(Utils.TAG, token);
                        personalToken = token;
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "No se recibio el token", Toast.LENGTH_LONG).show();
                });
    }


    private void guardarCredenciales(String ID_usuario, String nombre, String clave, String telefono, boolean rememberMe) {

        SharedPreferences sharedPreferences = getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ID_usuario", ID_usuario);
        editor.putString("nombre", nombre);
        editor.putString("telefono", telefono);
        editor.putString("clave", rememberMe ? clave : "");
        editor.putBoolean("rememberMe", rememberMe);
        editor.apply();
    }


    public void IniciarSession(View view) {
        IniciarSession();
    }

    private void IniciarSession() {
        String telefono = inputUsername.getText().toString();
        String clave = inputPassword.getText().toString();


        if (telefono.isEmpty() || clave.isEmpty()) {
            Toast.makeText(context, "LLENE TODOS LOS CAMPOS", Toast.LENGTH_SHORT).show();
        } else {
            Login(telefono, clave);
        }
    }



    private void Login(String telefono, String clave) {

        StringRequest requestLogin = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals(response)) {
                    if (response.equals("fallo")) {
                        Toast.makeText(context, "USUARIO O CONTRASEÑA INCORRECTA", Toast.LENGTH_SHORT).show();
                    } else {

                        try {
                            boolean rememberMe = checkBoxRememberMe.isChecked();
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String  ID_usuario = jsonObject.getString("ID_usuario");
                                String nombre= jsonObject.getString("nombre");
                                String clave= jsonObject.getString("clave");
                                String telefono= jsonObject.getString("telefono");

                                RegistrarToken(ID_usuario);

                                guardarCredenciales(ID_usuario, nombre, clave,telefono,rememberMe);

                            }

                            Intent intent = new Intent(LoginActivity.this, Activity_Binding.class);
                            startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Los datos son incorrectos", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(context, "SERVIDORES EN MANTENIMIENTO... VUELVA A INTENTAR MAS TARDE ", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    Toast.makeText(context, "ERROR AL CONECTAR", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "SERVIDORES EN MANTENIMIENTO, VUELVA A INTENTAR MAS TARDE ", Toast.LENGTH_LONG).show();
                }
            }

        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("opcion", "1");
                params.put("telefono", telefono);
                params.put("clave", clave);
                return params;
            }
        };
        rq.add(requestLogin);
    }



    public void RegistrarToken(String ID_usuario) {

        String str_token = personalToken;

        StringRequest request2 = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public void onResponse(String response) {

                Toast.makeText(LoginActivity.this, "Token actualizado", Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }
        ) {
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("opcion", "2");
                params.put("token", str_token);
                params.put("ID_usuario", ID_usuario);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(request2);

    }



}