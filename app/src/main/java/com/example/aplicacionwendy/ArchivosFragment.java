package com.example.aplicacionwendy;

import static android.app.Activity.RESULT_OK;

import static com.example.aplicacionwendy.Adaptadores.Utiles.ModalRedondeado;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.aplicacionwendy.Adaptadores.AdaptadorCitas;
import com.example.aplicacionwendy.Adaptadores.AdaptadorDocumentos;
import com.example.aplicacionwendy.Adaptadores.Utiles;
import com.example.aplicacionwendy.Adaptadores.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ArchivosFragment extends Fragment implements AdaptadorDocumentos.OnActivityActionListener {

    public ArchivosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    String url = "https://envelopesoft.000webhostapp.com/mostrar.php";

    Context context;

    String ID_usuario;
    List<JSONObject> dataList = new ArrayList<>();

    AdaptadorDocumentos adaptadorDocumentos;

    RelativeLayout ContenedorContenido;
    ConstraintLayout ContenedorSinInternet;
    ConstraintLayout ContenedorSinContenido;


    AlertDialog modalCargando;
    AlertDialog.Builder builder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_archivos, container, false);
        context = requireContext();

        RecyclerView recyclerViewDocs = view.findViewById(R.id.recyclerViewDocs);

        builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);

        ContenedorContenido = view.findViewById(R.id.ContenedorContenido);
        ContenedorSinInternet = view.findViewById(R.id.ContenedorSinInternet);
        ContenedorSinContenido = view.findViewById(R.id.ContenedorSinContenido);

        GridLayoutManager gridLayoutManagerDelantero = new GridLayoutManager(context, 2);
        recyclerViewDocs.setLayoutManager(gridLayoutManagerDelantero);
        adaptadorDocumentos = new AdaptadorDocumentos(dataList, context, this);
        recyclerViewDocs.setAdapter(adaptadorDocumentos);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        ID_usuario = sharedPreferences.getString("ID_usuario", "");
        VerDocs(ID_usuario);


        FloatingActionButton botonAgregarArchivo = view.findViewById(R.id.botonAgregarArchivo);
        botonAgregarArchivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AbrirGaleria();
            }
        });


        return view;
    }


    private void AbrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 2);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri selectedFileUri = data.getData();

            String mimeType = getActivity().getContentResolver().getType(selectedFileUri);

            if (mimeType != null && mimeType.startsWith("image")) {

            /*    // El archivo seleccionado es una imagen
                Bitmap selectedBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedFileUri);
                MandarFoto2(selectedBitmap);
*/

                Utiles.crearToastPersonalizado(context, "Solo puedes subir archivos pdf");
            } else if (mimeType != null && mimeType.equals("application/pdf")) {
                // El archivo seleccionado es un PDF

                MandarPDF(selectedFileUri);
            } else {
                // Tipo de archivo no compatible
                //        Log.e("Error", "Tipo de archivo no compatible");
                Utiles.crearToastPersonalizado(context, "Solo puedes subir archivos pdf");
            }
        }
    }

    private void MandarFoto2(Bitmap imageBitmap) {
        new SendFileTask().execute(imageBitmap);
    }

    private void MandarPDF(Uri pdfUri) {
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(pdfUri);
            byte[] pdfBytes = new byte[inputStream.available()];
            inputStream.read(pdfBytes);

            new SendFileTask().execute(pdfBytes, "documento.pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class SendFileTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... objects) {
            byte[] fileBytes = (byte[]) objects[0];
            String fileName = (String) objects[1];

            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("opcion", "7")
                    .addFormDataPart("ID_usuario", ID_usuario)
                    .addFormDataPart("archivo", fileName,
                            RequestBody.create(MediaType.parse("multipart/form-data"), fileBytes))
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("Respuesta del servidor", responseData);
                } else {
                    Log.e("Error en la solicitud", String.valueOf(response.code()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Utiles.crearToastPersonalizado(context, "Archivo PDF enviado al servidor");
            VerDocs(ID_usuario);
        }
    }

    private void VerDocs(String ID_usuario) {
        dataList.clear();

        modalCargando = Utiles.ModalCargando(context, builder);
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("opcion", "8")
                .add("ID_usuario", ID_usuario)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseBody);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            dataList.add(jsonObject);
                        }

                        // Acceder al contexto de la actividad asociada al fragmento
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adaptadorDocumentos.notifyDataSetChanged();
                                    adaptadorDocumentos.setFilteredData(dataList);
                                    adaptadorDocumentos.filter("");

                                    if (dataList.size() > 0) {
                                        MostrarLayout("Contenido");
                                    } else {
                                        MostrarLayout("SinContenido");
                                    }
                                }
                            });
                        }
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MostrarLayout("SinContenido");
                            }
                        });
                    }
                } catch (JSONException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MostrarLayout("SinContenido");
                        }
                    });
                } catch (IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MostrarLayout("SinContenido");
                        }
                    });
                }
            }


            @Override
            public void onFailure(Call call, IOException e) {


                MostrarLayout("SinInternet");
            }
        });
    }


    private void MostrarLayout(String estado) {

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




    @Override
    public void onDeleteArchivo(String ID_archivo) {

        StringRequest request2 = new StringRequest(com.android.volley.Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            public void onResponse(String response) {
                Utiles.crearToastPersonalizado(context, "Se eliminó correctamente");
                VerDocs(ID_usuario);

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utiles.crearToastPersonalizado(context, "No se eliminó, revisa la conexión");
            }
        }
        ) {
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("opcion", "12");
                params.put("ID_archivo", ID_archivo);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request2);

    }


}