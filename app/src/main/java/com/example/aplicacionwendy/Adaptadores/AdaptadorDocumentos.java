package com.example.aplicacionwendy.Adaptadores;

import static android.app.PendingIntent.getActivity;

import static androidx.core.content.ContextCompat.startActivity;

import static com.example.aplicacionwendy.Adaptadores.Utiles.ModalRedondeado;
import static com.google.common.reflect.Reflection.getPackageName;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacionwendy.R;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AdaptadorDocumentos extends RecyclerView.Adapter<AdaptadorDocumentos.ViewHolder> {

    private Context context;
    private List<JSONObject> filteredData;
    private List<JSONObject> data;
    private FrameLayout loadingIndicator;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_docs, parent, false);
        return new ViewHolder(view);

    }

    @SuppressLint("ResourceAsColor")
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        try {
            JSONObject jsonObject2 = filteredData.get(position);
            String ID_archivo = jsonObject2.optString("ID_archivo", "");
            String nombreVisualizacion = jsonObject2.optString("nombreVisualizacion", "");
            String nombre_archivo = jsonObject2.optString("nombre_archivo", "");
            String fecha = jsonObject2.optString("fecha", "");

            Bundle bundle = new Bundle();
            bundle.putString("ID_archivo", ID_archivo);
            bundle.putString("fecha", fecha);

            setTextViewText(holder.textNombre, nombreVisualizacion, "Asigna un nombre a este archivo");
            holder.textPuesto.setText("Agregado el " + fecha);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View customView = LayoutInflater.from(view.getContext()).inflate(R.layout.opciones_archivos, null);

                    builder.setView(ModalRedondeado(context, customView));
                    AlertDialog dialogArchivos = builder.create();
                    dialogArchivos.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogArchivos.show();


                    TextView EliminarArchivo = customView.findViewById(R.id.EliminarArchivo);
                    TextView verArchivo = customView.findViewById(R.id.verArchivo);
                    TextView CambiarNombre = customView.findViewById(R.id.CambiarNombre);


                    CambiarNombre.setVisibility(View.GONE);



                    EliminarArchivo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogArchivos.dismiss();
                            actionListener.onDeleteArchivo(ID_archivo);

                        }
                    });


                    verArchivo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String pdfUrl = "https://envelopesoft.000webhostapp.com/docs/" + nombre_archivo;

                            new DownloadPdfTask().execute(pdfUrl);
                        }
                    });

                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private class DownloadPdfTask extends AsyncTask<String, Void, File> {
        @Override
        protected File doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // Guardar el PDF en el almacenamiento local
                File pdfFile = savePdfToStorage(connection.getInputStream());
                return pdfFile;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(File pdfFile) {
            if (pdfFile != null) {
                // Abre el visor de PDF predeterminado cuando se hace clic en el PDF
                openPdfInDefaultViewer(pdfFile);
            } else {
                // Manejar el error al descargar el PDF
                Toast.makeText(context, "Error al descargar el PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File savePdfToStorage(InputStream inputStream) {
        try {
            // Crear un archivo temporal en el directorio de almacenamiento interno de la aplicación
            File directory = new File(context.getFilesDir(), "temp");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File pdfFile = new File(new File(directory, "tu_archivo.pdf").getAbsolutePath());

            // Guardar el contenido del PDF en el archivo
            FileOutputStream fos = new FileOutputStream(pdfFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            fos.close();

            return pdfFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void openPdfInDefaultViewer(File pdfFile) {
        // Crear una URI para el archivo PDF utilizando FileProvider
        Uri pdfUri = FileProvider.getUriForFile(context, "com.example.aplicacionwendy.fileprovider", pdfFile);

        // Crear una intención para abrir el visor de archivos predeterminado del sistema
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            // Intenta abrir el visor de PDF
            startActivity(context, intent, null);
        } catch (ActivityNotFoundException e) {
            // Manejar el caso en el que no se encuentre una aplicación para abrir PDF
            Toast.makeText(context, "No se encontró una aplicación para abrir PDF", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public int getItemCount() {

        return filteredData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNombre, textPuesto;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textNombre = itemView.findViewById(R.id.textNombre);
            textPuesto = itemView.findViewById(R.id.textPuesto);
        }
    }

    public void filter(String query) {
        filteredData.clear();

        if (TextUtils.isEmpty(query)) {
            filteredData.addAll(data);
        } else {
            String[] keywords = query.toLowerCase().split(" ");

            for (JSONObject item : data) {


                String ID_archivo = item.optString("ID_archivo", "").toLowerCase();
                String nombre_archivo = item.optString("nombre_archivo", "").toLowerCase();


                boolean matchesAllKeywords = true;

                for (String keyword : keywords) {
                    if (!(nombre_archivo.contains(keyword) || ID_archivo.contains(keyword))) {
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

        void onDeleteArchivo(String ID_archivo);


    }


    private AdaptadorDocumentos.OnActivityActionListener actionListener;


    public AdaptadorDocumentos(List<JSONObject> data, Context context, AdaptadorDocumentos.OnActivityActionListener actionListener) {
        this.data = data;
        this.context = context;
        this.filteredData = new ArrayList<>(data);
        this.actionListener = actionListener;
    }


}

