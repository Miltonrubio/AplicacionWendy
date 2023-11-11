package com.example.aplicacionwendy;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.aplicacionwendy.Adaptadores.AdaptadorCitas;
import com.example.aplicacionwendy.Adaptadores.AdaptadorDocumentos;
import com.example.aplicacionwendy.Adaptadores.Utiles;
import com.example.aplicacionwendy.Adaptadores.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        VerDocs("1");
        return view;
    }


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
/*
                    if (dataList.size() > 0) {

                        mostrarYOcultar("contenido");
                    } else {

                        mostrarYOcultar("SinContenido");
                    }
*/
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


    @Override
    public void onDeleteActivity(String ID_actividad) {

    }
}