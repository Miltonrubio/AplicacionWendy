package com.example.aplicacionwendy;

import static android.app.Activity.RESULT_OK;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


    List<JSONObject> dataList = new ArrayList<>();

    AdaptadorDocumentos adaptadorDocumentos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_archivos, container, false);
        context = requireContext();

        RecyclerView recyclerViewDocs = view.findViewById(R.id.recyclerViewDocs);

        GridLayoutManager gridLayoutManagerDelantero = new GridLayoutManager(context, 2);
        recyclerViewDocs.setLayoutManager(gridLayoutManagerDelantero);
        adaptadorDocumentos = new AdaptadorDocumentos(dataList, context, this);
        recyclerViewDocs.setAdapter(adaptadorDocumentos);

        //  VerDocs("1");


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
        intent.setType("*/*");  // Permite seleccionar cualquier tipo de archivo
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 2);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri selectedFileUri = data.getData();

            try {
                String mimeType = getActivity().getContentResolver().getType(selectedFileUri);

                if (mimeType != null && mimeType.startsWith("image")) {
                    // El archivo seleccionado es una imagen
                    Bitmap selectedBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedFileUri);
                    MandarFoto2(selectedBitmap);
                } else if (mimeType != null && mimeType.equals("application/pdf")) {
                    // El archivo seleccionado es un PDF
                    MandarPDF(selectedFileUri);
                } else {
                    // Tipo de archivo no compatible
                    Log.e("Error", "Tipo de archivo no compatible");
                }
            } catch (IOException e) {
                e.printStackTrace();
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
                    .addFormDataPart("ID_usuario", "1")
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
            Intent intent = new Intent(context, Activity_Binding.class);
            startActivity(intent);
        }
    }









/*
    private void AbrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            // La imagen seleccionada desde la galería está en 'data.getData()'
            Uri selectedImageUri = data.getData();

            try {
                Bitmap selectedBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImageUri);

                // Luego puedes procesar 'selectedBitmap' y enviarlo al servidor
                MandarFoto2(selectedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private void MandarFoto2(Bitmap imageBitmap) {
        new SendImageTask().execute(imageBitmap);
    }

    @Override
    public void onDeleteActivity(String ID_actividad) {

    }


    private class SendImageTask extends AsyncTask<Bitmap, Void, Void> {

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            Bitmap imageBitmap = bitmaps[0];

            OkHttpClient client = new OkHttpClient();

            String nombreArchivo = "imagen" + System.currentTimeMillis() + ".jpg";
            //  File imageFile = bitmapToFile(imageBitmap, "image.jpg");
            File imageFile = bitmapToFile(imageBitmap, nombreArchivo);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("opcion", "7")
                    .addFormDataPart("ID_usuario", "1")
                    .addFormDataPart("archivo", nombreArchivo,
                            RequestBody.create(MediaType.parse("multipart/form-data"), imageFile))

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
            // Toast.makeText(SubirFotosUnidadesActivity.this, "Imagen " + idSerVenta + " Enviada al servidor", Toast.LENGTH_SHORT).show();
            Utiles.crearToastPersonalizado(context, "Imagen enviada al servidor");
            Intent intent = new Intent(context, Activity_Binding.class);
            startActivity(intent);
        }
    }


    private File bitmapToFile(Bitmap bitmap, String fileName) {
        File file = new File(getActivity().getCacheDir(), fileName);
        try {
            file.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bos);
            byte[] bitmapData = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    */







/*
    private void VerDocs(String idusuario) {

        dataList.clear();

        StringRequest postrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        dataList.add(jsonObject);
                    }


                    adaptadorDocumentos.notifyDataSetChanged();
                    adaptadorDocumentos.setFilteredData(dataList);
                    adaptadorDocumentos.filter("");


                } catch (JSONException e) {
                    //     mostrarYOcultar("SinContenido");
                    Utiles.crearToastPersonalizado(context, "Algo fallo");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utiles.crearToastPersonalizado(context, "Algo fallo");
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("opcion", "8");
                params.put("ID_usuario", idusuario);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(postrequest);
    }




 */


    @Override
    public void onDeleteActivity(String ID_actividad) {

    }
}