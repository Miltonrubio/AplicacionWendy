package com.example.aplicacionwendy.Adaptadores;

import static android.app.PendingIntent.getActivity;

import static com.example.aplicacionwendy.Adaptadores.Utiles.ModalRedondeado;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacionwendy.R;

import org.json.JSONObject;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AdaptadorCitas extends RecyclerView.Adapter<AdaptadorCitas.ViewHolder> {

    private Context context;
    private List<JSONObject> filteredData;
    private List<JSONObject> data;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_citas, parent, false);
        return new ViewHolder(view);

    }

    @SuppressLint("ResourceAsColor")
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {



        try {
            JSONObject jsonObject2 = filteredData.get(position);
            String ID_cita = jsonObject2.optString("ID_cita", "");
            String ID_usuario = jsonObject2.optString("ID_usuario", "");
            String fecha_cita = jsonObject2.optString("fecha_cita", "");
            String detalles_cita = jsonObject2.optString("detalles_cita", "");
            String hora_cita = jsonObject2.optString("hora_cita", "");
            String nombre = jsonObject2.optString("nombre", "");
            String telefono = jsonObject2.optString("telefono", "");
            String tipo_evento = jsonObject2.optString("tipo_evento", "");


            Bundle bundle = new Bundle();
            bundle.putString("ID_cita", ID_cita);
            bundle.putString("ID_usuario", ID_usuario);
            bundle.putString("fecha_cita", fecha_cita);
            bundle.putString("detalles_cita", detalles_cita);
            bundle.putString("hora_cita", hora_cita);
            bundle.putString("nombre", nombre);
            bundle.putString("telefono", telefono);


            setTextViewText(holder.textNombreUsuario, detalles_cita, "Nombre no disponible");
            setTextViewText(holder.textTelefonoPaciente, telefono, "Nombre no disponible");

            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date date = inputFormat.parse(fecha_cita);
                SimpleDateFormat outputDayOfWeek = new SimpleDateFormat("EEEE", new Locale("es", "ES"));
                String dayOfWeek = outputDayOfWeek.format(date);
                SimpleDateFormat outputFormat = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
                String formattedDate = outputFormat.format(date);

                // Imprimir el resultado
                System.out.println("Día de la semana: " + dayOfWeek);
                System.out.println("Fecha formateada: " + formattedDate);

                setTextViewText(holder.fechadeCita, "El " + dayOfWeek + " " + formattedDate, "Fecha no disponible");

            } catch (ParseException e) {
                e.printStackTrace();
            }


            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");
                Date time = inputFormat.parse(hora_cita);
                SimpleDateFormat outputFormat = new SimpleDateFormat("'A las' h:mm a");
                String formattedTime = outputFormat.format(time);

                setTextViewText(holder.horaCita, formattedTime, "Nombre no disponible");
            } catch (ParseException e) {
                e.printStackTrace();
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.modal_opciones_citas, null);

                    builder.setView(ModalRedondeado(context, customView));
                    AlertDialog dialogCitas = builder.create();
                    dialogCitas.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogCitas.show();


                    TextView EliminarCita = customView.findViewById(R.id.EliminarCita);
                    TextView EditarCita = customView.findViewById(R.id.EditarCita);


                    EditarCita.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.registrar_cita, null);

                            builder.setView(ModalRedondeado(context, customView));
                            AlertDialog dialogEditarCita = builder.create();
                            dialogEditarCita.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialogEditarCita.show();

                            TextView textView8= customView.findViewById(R.id.textView8);
                            textView8.setText("Actualiza tu cita");

                            EditText EditDescripcion = customView.findViewById(R.id.EditDescripcion);
                            DatePicker datePickerFecha = customView.findViewById(R.id.datePickerFecha);
                            TimePicker timePicker2 = customView.findViewById(R.id.timePicker2);
                            RadioButton radioButtonActividad = customView.findViewById(R.id.radioButtonActividad);
                            RadioButton radioButtonCita = customView.findViewById(R.id.radioButtonCita);
                            Button botonCancelar = customView.findViewById(R.id.botonCancelar);
                            Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);

                            EditDescripcion.setText(detalles_cita);


                            if (tipo_evento.equalsIgnoreCase("cita")) {
                                radioButtonCita.setChecked(true);
                                radioButtonActividad.setChecked(false);
                            } else {

                                radioButtonCita.setChecked(false);
                                radioButtonActividad.setChecked(true);
                            }


                            botonCancelar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogEditarCita.dismiss();
                                }
                            });

                            buttonAceptar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    String descripcion = EditDescripcion.getText().toString();

                                    String tipo_evento_seleccionado;

                                    if (radioButtonCita.isChecked()) {
                                        tipo_evento_seleccionado = "cita";
                                    } else {
                                        tipo_evento_seleccionado = "actividad";
                                    }

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

                                        dialogEditarCita.dismiss();
                                        dialogCitas.dismiss();
                                        //Mandar datos a Api
                                        actionListener.onEditActivity(ID_cita, fechaFormateada, horaFormateada, descripcion, tipo_evento_seleccionado);

                                    }
                                }
                            });


                        }
                    });


                    EliminarCita.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.confirmacion, null);

                            builder.setView(ModalRedondeado(context, customView));
                            AlertDialog dialogConfirmacion = builder.create();
                            dialogConfirmacion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialogConfirmacion.show();

                            Button buttonCancelar = customView.findViewById(R.id.buttonCancelar);
                            Button buttonAceptar = customView.findViewById(R.id.buttonAceptar);


                            buttonCancelar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogConfirmacion.dismiss();
                                }
                            });

                            buttonAceptar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogConfirmacion.dismiss();
                                    dialogCitas.dismiss();
                                    //Mandar Datos a api
                                    actionListener.onDeleteActivity(ID_cita, tipo_evento);
                                }
                            });


                        }
                    });


                }
            });


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public int getItemCount() {

        return filteredData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNombreUsuario, fechadeCita, horaCita, textTelefonoPaciente;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textNombreUsuario = itemView.findViewById(R.id.textNombreUsuario);
            fechadeCita = itemView.findViewById(R.id.fechadeCita);
            horaCita = itemView.findViewById(R.id.horaCita);
            textTelefonoPaciente = itemView.findViewById(R.id.textTelefonoPaciente);
        }
    }

    public void filter(String query) {
        filteredData.clear();

        if (TextUtils.isEmpty(query)) {
            filteredData.addAll(data);
        } else {
            String[] keywords = query.toLowerCase().split(" ");

            for (JSONObject item : data) {


                String ID_cita = item.optString("ID_cita", "").toLowerCase();
                String ID_usuario = item.optString("ID_usuario", "").toLowerCase();
                String fecha_cita = item.optString("fecha_cita", "").toLowerCase();
                String detalles_cita = item.optString("detalles_cita", "").toLowerCase();
                String hora_cita = item.optString("hora_cita", "").toLowerCase();
                String telefono = item.optString("telefono", "").toLowerCase();
                String nombre = item.optString("nombre", "").toLowerCase();


                boolean matchesAllKeywords = true;

                for (String keyword : keywords) {
                    if (!(detalles_cita.contains(keyword) || fecha_cita.contains(keyword) || ID_cita.contains(keyword) || hora_cita.contains(keyword) ||
                            nombre.contains(keyword) || telefono.contains(keyword) || ID_usuario.contains(keyword))) {
                        matchesAllKeywords = false;
                        break;
                    }
                }

                if (matchesAllKeywords) {
                    filteredData.add(item);
                }
            }
        }

        notifyDataSetChanged();
    }

    public void setFilteredData(List<JSONObject> filteredData) {
        this.filteredData = new ArrayList<>(filteredData);
        notifyDataSetChanged();
    }


    private void setTextViewText(TextView textView, String text, String defaultText) {
        if (text.equals(null) || text.equals("") || text.equals(":null") || text.equals("null") || text.isEmpty()) {
            textView.setText(defaultText);
        } else {
            textView.setText(text);
        }
    }


    public interface OnActivityActionListener {

        void onDeleteActivity(String ID_cita, String tipo_evento);

        void onEditActivity(String ID_cita, String fecha_cita, String hora_cita, String descripcion, String tipo_evento_cita);

    }


    private AdaptadorCitas.OnActivityActionListener actionListener;


    public AdaptadorCitas(List<JSONObject> data, Context context, AdaptadorCitas.OnActivityActionListener actionListener) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        this.actionListener = actionListener;
    }


}

