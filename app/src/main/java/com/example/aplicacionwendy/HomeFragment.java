package com.example.aplicacionwendy;

import static com.example.aplicacionwendy.Adaptadores.Utiles.ModalRedondeado;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.aplicacionwendy.Adaptadores.AdaptadorCitas;
import com.example.aplicacionwendy.Adaptadores.Utiles;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements AdaptadorCitas.OnActivityActionListener {

    private ArrayList<String> nombresClientes = new ArrayList<>();
    String url = "https://envelopesoft.000webhostapp.com/mostrar.php";
    String ID_usuario;
    private RecyclerView recyclerViewCitas;
    private AdaptadorCitas adaptadorVerCitas;
    private AdaptadorCitas adaptadorVerActividades;
    private List<JSONObject> dataList = new ArrayList<>();
    private List<JSONObject> listaCitas = new ArrayList<>();
    private List<JSONObject> listaActividades = new ArrayList<>();

    private EditText editTextBusqueda;
    private FloatingActionButton botonAgregarCita;

    Context context;


    ConstraintLayout ContenedorContenido;

    ConstraintLayout ContenedorSinInternet;
    ConstraintLayout ContenedorSinContenido;

    AlertDialog.Builder builder;

    AlertDialog modalCargando;

    String tipo_evento="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = requireContext();

        builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Credenciales", Context.MODE_PRIVATE);

        ID_usuario = sharedPreferences.getString("ID_usuario", "");
        String nombre = sharedPreferences.getString("nombre", "");
        String correo = sharedPreferences.getString("correo", "");
        String telefono = sharedPreferences.getString("telefono", "");

        //  botonAgregarCita = view.findViewById(R.id.botonAgregarCita);
        recyclerViewCitas = view.findViewById(R.id.recyclerViewCitas);
        TextView textViewBienvenido = view.findViewById(R.id.bienvenida2);
        //  editTextBusqueda = view.findViewById(R.id.searchEditTextCitas);
        RecyclerView recyclerViewActividades = view.findViewById(R.id.recyclerViewActividades);
        ContenedorSinInternet = view.findViewById(R.id.ContenedorSinInternet);
        ContenedorContenido = view.findViewById(R.id.ContenedorContenido);
        ContenedorSinContenido = view.findViewById(R.id.ContenedorSinContenido);
        //     ImageView btnAgregarCita = view.findViewById(R.id.btnAgregarCita);

        FloatingActionButton floatingButton = view.findViewById(R.id.floatingButton);


        textViewBienvenido.setText("¡Bienvenido " + nombre + " !");

        recyclerViewCitas.setLayoutManager(new LinearLayoutManager(context));
        adaptadorVerCitas = new AdaptadorCitas(listaCitas, context, this);
        recyclerViewCitas.setAdapter(adaptadorVerCitas);


        recyclerViewActividades.setLayoutManager(new LinearLayoutManager(context));
        adaptadorVerActividades = new AdaptadorCitas(listaActividades, context, this);
        recyclerViewActividades.setAdapter((adaptadorVerActividades));

        VerCitas(ID_usuario);


        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.registrar_cita, null);

                builder.setView(ModalRedondeado(context, customView));
                AlertDialog dialogAgendarCita = builder.create();
                dialogAgendarCita.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogAgendarCita.show();


                DatePicker datePickerFecha = customView.findViewById(R.id.datePickerFecha);
                TimePicker timePicker2 = customView.findViewById(R.id.timePicker2);
                EditText EditDescripcion = customView.findViewById(R.id.EditDescripcion);

                RadioButton radioButtonCita= customView.findViewById(R.id.radioButtonCita);
                RadioButton radioButtonActividad= customView.findViewById(R.id.radioButtonActividad);
                RadioButton radioButtonEvento= customView.findViewById(R.id.radioButtonEvento);

                Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);
                Button botonCancelar = customView.findViewById(R.id.botonCancelar);


                radioButtonCita.setChecked(true);

                radioButtonActividad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        tipo_evento="actividad";
                        radioButtonEvento.setChecked(false);
                        radioButtonActividad.setChecked(true);
                        radioButtonCita.setChecked(false);
                    }
                });

                radioButtonCita.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        tipo_evento="cita";
                        radioButtonEvento.setChecked(false);
                        radioButtonActividad.setChecked(false);
                        radioButtonCita.setChecked(true);
                    }
                });


                radioButtonEvento.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        tipo_evento="evento";
                        radioButtonEvento.setChecked(true);
                        radioButtonActividad.setChecked(false);
                        radioButtonCita.setChecked(false);
                    }
                });



                botonCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogAgendarCita.dismiss();
                    }
                });


                buttonAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String descripcion = EditDescripcion.getText().toString();

                        int year = datePickerFecha.getYear();
                        int month = datePickerFecha.getMonth() + 1; // Sumar 1 ya que los meses van de 0 a 11
                        int dayOfMonth = datePickerFecha.getDayOfMonth();
                        String fechaFormateada = String.format("%04d-%02d-%02d", year, month, dayOfMonth);

                        int hourOfDay = timePicker2.getHour();
                        int minute = timePicker2.getMinute();
                        String horaFormateada = String.format("%02d:%02d", hourOfDay, minute);


                        if (descripcion.isEmpty()) {
                            Utiles.crearToastPersonalizado(context, "Tienes campos vacios, por favor rellenalos");
                        } else {

                            dialogAgendarCita.dismiss();
                            AgregarCita(descripcion, fechaFormateada, horaFormateada, tipo_evento);
                        }

                    }
                });

            }
        });

    }

    private void VerCitas(String idusuario) {

        dataList.clear();
        listaCitas.clear();
        listaActividades.clear();

        modalCargando = Utiles.ModalCargando(context, builder);
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String tipo_evento = jsonObject.getString("tipo_evento");
                        if (tipo_evento.equalsIgnoreCase("cita")) {
                            listaCitas.add(jsonObject);
                        } else if (tipo_evento.equalsIgnoreCase("actividad")) {
                            listaActividades.add(jsonObject);
                        }

                        dataList.add(jsonObject);
                    }

                    if (dataList.size() > 0) {

                        mostrarYOcultar("contenido");
                    } else {

                        mostrarYOcultar("SinContenido");
                    }

                    adaptadorVerCitas.notifyDataSetChanged();
                    adaptadorVerCitas.setFilteredData(listaCitas);
                    adaptadorVerCitas.filter("");


                    adaptadorVerActividades.notifyDataSetChanged();
                    adaptadorVerActividades.setFilteredData(listaActividades);
                    adaptadorVerActividades.filter("");

                } catch (JSONException e) {
                    mostrarYOcultar("SinContenido");

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mostrarYOcultar("SinInternet");
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "4");
                params.put("ID_usuario", idusuario);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


    private void mostrarYOcultar(String estado) {


        if (estado.equalsIgnoreCase("SinContenido")) {
            ContenedorContenido.setVisibility(View.GONE);
            ContenedorSinInternet.setVisibility(View.GONE);
            ContenedorSinContenido.setVisibility(View.VISIBLE);
        } else if (estado.equalsIgnoreCase("SinInternet")) {

            ContenedorContenido.setVisibility(View.GONE);
            ContenedorSinInternet.setVisibility(View.VISIBLE);
            ContenedorSinContenido.setVisibility(View.GONE);
        } else {
            ContenedorContenido.setVisibility(View.VISIBLE);
            ContenedorSinInternet.setVisibility(View.GONE);
            ContenedorSinContenido.setVisibility(View.GONE);

        }

        onLoadComplete();

    }

    private void onLoadComplete() {
        if (modalCargando.isShowing() && modalCargando != null) {
            modalCargando.dismiss();
        }
    }


    private void AgregarCita(String detalles_cita, String fecha_cita, String hora_cita, String tipo_evento) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VerCitas(ID_usuario);
                Utiles.crearToastPersonalizado(context, response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utiles.crearToastPersonalizado(context, "Hubo un error, por favor revisa la conexi{on");
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "5");
                params.put("ID_usuario", ID_usuario);
                params.put("detalles_cita", detalles_cita);
                params.put("fecha_cita", fecha_cita);
                params.put("hora_cita", hora_cita);
                params.put("tipo_evento", tipo_evento);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


    @Override
    public void onEditActivity(String ID_cita, String fecha_cita, String hora_cita, String descripcion, String tipo_evento_cita) {
            StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Utiles.crearToastPersonalizado(context, response);
                    VerCitas(ID_usuario);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Utiles.crearToastPersonalizado(context, "Hubo un error, revisa tu conexión");
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("opcion", "9");
                    params.put("ID_cita", ID_cita);
                    params.put("detalles_cita", descripcion);
                    params.put("tipo_evento", tipo_evento_cita);
                    params.put("hora_cita", hora_cita);
                    params.put("fecha_cita", fecha_cita);


                    return params;
                }
            };
            Volley.newRequestQueue(context).add(postrequest);

        }



        @Override
    public void onDeleteActivity(String ID_cita, String tipo_evento) {

        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

               // Utiles.crearToastPersonalizado(context, "La " + tipo_evento+ " se eliminó correctamente");
                Utiles.crearToastPersonalizado(context, response);
                VerCitas(ID_usuario);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Utiles.crearToastPersonalizado(context, "Hubo un error, revisa tu conexión");
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "10");
                params.put("ID_cita", ID_cita);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }



}

