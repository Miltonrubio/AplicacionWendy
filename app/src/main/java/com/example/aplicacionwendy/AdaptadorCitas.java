package com.example.aplicacionwendy;
import static android.app.Activity.RESULT_OK;
import static android.app.PendingIntent.getActivity;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.Manifest;


public class AdaptadorCitas extends RecyclerView.Adapter<AdaptadorCitas.ViewHolder> {


    private static final int VIEW_TYPE_ERROR = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private Context context;
    String IDSesionIniciada;
    private List<JSONObject> filteredData;
    private List<JSONObject> data;
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;


    public AdaptadorCitas(List<JSONObject> data, Context context) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_citas, parent, false);
            return new ViewHolder(view);
        } else {

            View errorView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nocitas, parent, false);
            return new ViewHolder(errorView);
        }

    }

    @SuppressLint("ResourceAsColor")
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

         context = holder.itemView.getContext();


        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            try {
                JSONObject jsonObject2 = filteredData.get(position);
                String ID_cita = jsonObject2.optString("ID_cita", "");
                String ID_usuario = jsonObject2.optString("ID_usuario", "");
                String fecha_cita = jsonObject2.optString("fecha_cita", "");
                String hora_cita = jsonObject2.optString("hora_cita", "");
                String nombre = jsonObject2.optString("nombre", "");
                String telefono = jsonObject2.optString("telefono", "");

                Bundle bundle = new Bundle();
                bundle.putString("ID_cita", ID_cita);
                bundle.putString("ID_usuario", ID_usuario);
                bundle.putString("fecha_cita", fecha_cita);
                bundle.putString("hora_cita", hora_cita);
                bundle.putString("nombre", nombre);
                bundle.putString("telefono", telefono);


                setTextViewText(holder.textNombreUsuario, nombre, "Nombre no disponible");
                setTextViewText(holder.textTelefonoPaciente, telefono, "Nombre no disponible");
                setTextViewText(holder.fechadeCita, fecha_cita, "Nombre no disponible");
                setTextViewText(holder.horaCita, hora_cita, "Nombre no disponible");

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }


    @Override
    public int getItemCount() {

        //return filteredData.size();
        return filteredData.isEmpty() ? 1 : filteredData.size();

    }

    @Override
    public int getItemViewType(int position) {
        return filteredData.isEmpty() ? VIEW_TYPE_ERROR : VIEW_TYPE_ITEM;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNombreUsuario,fechadeCita,horaCita, textTelefonoPaciente;

        ImageView fotoDeUsuario;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textNombreUsuario = itemView.findViewById(R.id.textNombreUsuario);
            fechadeCita = itemView.findViewById(R.id.fechadeCita);
            horaCita = itemView.findViewById(R.id.horaCita);
            textTelefonoPaciente = itemView.findViewById(R.id.textTelefonoPaciente);
            fotoDeUsuario = itemView.findViewById(R.id.fotoDeUsuario);
        }
    }

    public void filter(String query) {
        filteredData.clear();

        if (TextUtils.isEmpty(query)) {
            filteredData.addAll(data);
        } else {
            String[] keywords = query.toLowerCase().split(" ");

            for (JSONObject item : data) {
                String ID_actividad = item.optString("ID_actividad", "").toLowerCase();
                String nombre_actividad = item.optString("nombre_actividad", "").toLowerCase();
                String descripcionActividad = item.optString("descripcionActividad", "").toLowerCase();
                String estadoActividad = item.optString("estadoActividad", "").toLowerCase();
                String fecha_inicio = item.optString("fecha_inicio", "").toLowerCase();
                String fecha_fin = item.optString("fecha_fin", "").toLowerCase();


                String ID_usuario = item.optString("ID_usuario", "").toLowerCase();
                String ID_nombre_actividad = item.optString("ID_nombre_actividad", "").toLowerCase();
                String permisos = item.optString("permisos", "").toLowerCase();
                String correo = item.optString("correo", "").toLowerCase();
                String telefono = item.optString("telefono", "").toLowerCase();

                boolean matchesAllKeywords = true;

                for (String keyword : keywords) {
                    if (!(estadoActividad.contains(keyword) || descripcionActividad.contains(keyword) || nombre_actividad.contains(keyword) || ID_actividad.contains(keyword) ||
                            fecha_inicio.contains(keyword) || fecha_fin.contains(keyword) || ID_usuario.contains(keyword) || ID_nombre_actividad.contains(keyword) || permisos.contains(keyword) || telefono.contains(keyword) || correo.contains(keyword))) {
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

        void  onDeleteActivity(String ID_actividad);

        void  onEditActivity(String ID_cita, String fecha_cita, String hora_cita, String ID_usuario);

    }


    private AdaptadorCitas.OnActivityActionListener actionListener;


    public AdaptadorCitas(List<JSONObject> data, Context context, AdaptadorCitas.OnActivityActionListener actionListener) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        this.actionListener = actionListener;
    }


}

