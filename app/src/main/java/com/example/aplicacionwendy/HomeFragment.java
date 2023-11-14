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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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


        textViewBienvenido.setText("¡Bienvenido " + nombre + " !");

        recyclerViewCitas.setLayoutManager(new LinearLayoutManager(context));
        adaptadorVerCitas = new AdaptadorCitas(listaCitas, context, this);
        recyclerViewCitas.setAdapter(adaptadorVerCitas);


        recyclerViewActividades.setLayoutManager(new LinearLayoutManager(context));
        adaptadorVerActividades = new AdaptadorCitas(listaActividades, context, this);
        recyclerViewActividades.setAdapter((adaptadorVerActividades));

        /*

        editTextBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adaptadorVerCitas.filter(s.toString().toLowerCase());
            }


            @Override
            public void afterTextChanged(Editable s) {
            }
        });
*/
        VerCitas(ID_usuario);

/*
        btnAgregarCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.registrar_cita, null);

                builder.setView(ModalRedondeado(context, customView));
                AlertDialog dialogAgregarProduccion = builder.create();
                dialogAgregarProduccion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogAgregarProduccion.show();

                /*

                final EditText editText2 = dialogView.findViewById(R.id.editText2);
                final DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
                final TimePicker timePicker = dialogView.findViewById(R.id.timePicker);

                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Obtener el texto del EditText
                        String descripcion = editText2.getText().toString();

                        // Obtener la fecha seleccionada del DatePicker y formatearla
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth() + 1; // Sumar 1 ya que los meses van de 0 a 11
                        int dayOfMonth = datePicker.getDayOfMonth();
                        String fechaFormateada = String.format("%04d-%02d-%02d", year, month, dayOfMonth);

                        // Obtener la hora y minuto seleccionados del TimePicker y formatearlos
                        int hourOfDay = timePicker.getHour();
                        int minute = timePicker.getMinute();
                        String horaFormateada = String.format("%02d:%02d", hourOfDay, minute);

                        // Llamar a tu método AgregarCita() con los valores formateados
                        AgregarCita(descripcion, fechaFormateada, horaFormateada);
                    }
                });

                builder.setNegativeButton("Cancelar", null);
*/
/*
            }
        });

        botonAgregarCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.registrar_cita, null);

                builder.setView(ModalRedondeado(context, customView));
                AlertDialog dialogAgregarProduccion = builder.create();
                dialogAgregarProduccion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogAgregarProduccion.show();


                DatePicker datePickerFecha = customView.findViewById(R.id.datePickerFecha);
                TimePicker timePicker2 = customView.findViewById(R.id.timePicker2);
                EditText EditDescripcion = customView.findViewById(R.id.EditDescripcion);

                        /*
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.agregar_cita, null);
                builder.setView(dialogView);

                final EditText editText2 = dialogView.findViewById(R.id.editText2);
                final DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
                final TimePicker timePicker = dialogView.findViewById(R.id.timePicker);

                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Obtener el texto del EditText
                        String descripcion = editText2.getText().toString();

                        // Obtener la fecha seleccionada del DatePicker y formatearla
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth() + 1; // Sumar 1 ya que los meses van de 0 a 11
                        int dayOfMonth = datePicker.getDayOfMonth();
                        String fechaFormateada = String.format("%04d-%02d-%02d", year, month, dayOfMonth);

                        // Obtener la hora y minuto seleccionados del TimePicker y formatearlos
                        int hourOfDay = timePicker.getHour();
                        int minute = timePicker.getMinute();
                        String horaFormateada = String.format("%02d:%02d", hourOfDay, minute);

                        // Llamar a tu método AgregarCita() con los valores formateados
                        AgregarCita(descripcion, fechaFormateada, horaFormateada);
                    }
                });

                builder.setNegativeButton("Cancelar", null);

                // Mostrar el AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });

                         */
    }


    private void VerCitas(String idusuario) {

        dataList.clear();

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


    private void AgregarCita(String detalles_cita, String fecha_cita, String hora_cita) {
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VerCitas(ID_usuario);
                Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                Log.d("Respuesta de api para agregar: ", response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "5");
                params.put("ID_usuario", ID_usuario);
                params.put("detalles_cita", detalles_cita);
                params.put("fecha_cita", fecha_cita);
                params.put("hora_cita", hora_cita);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }


    @Override
    public void onEditActivity(String ID_cita, String fecha_cita, String hora_cita, String ID_usuario) {
        EditarCita(ID_cita, fecha_cita, hora_cita, ID_usuario);
    }

    @Override
    public void onDeleteActivity(String ID_actividad) {
        EliminarCita(ID_actividad);
    }


    private void EliminarCita(String ID_cita) {
        /*
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ActividadesPorUsuario(ID_usuario);
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Se eliminó la actividad", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Hubo un error", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "18");
                params.put("ID_actividad", ID_actividad);
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(postrequest);*/
    }


    private void EditarCita(String ID_cita, String fecha_cita, String hora_cita, String ID_usuario) {

        /*
        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ActividadesPorUsuario(ID_usuario);
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Se actualizó la actividad", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                if (isAdded()) {
                    Toast.makeText(requireContext(), "Hubo un error", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "17");
                params.put("ID_nombre_actividad", ID_nombre_actividad);
                params.put("descripcionActividad", descripcionActividad);
                params.put("ID_actividad", ID_actividad);
                return params;
            }
        };
        Volley.newRequestQueue(requireContext()).add(postrequest);

         */
    }

}

