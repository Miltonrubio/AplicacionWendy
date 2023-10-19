package com.example.aplicacionwendy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.aplicacionwendy.R;
import com.example.aplicacionwendy.databinding.ActivityBindingBinding;

public class Activity_Binding extends AppCompatActivity {

    ActivityBindingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding);

        binding = ActivityBindingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case (R.id.home):
                    replaceFragment(new HomeFragment());
                    break;
                case (R.id.usuario):
                    replaceFragment(new UsuarioFragment());
                    break;

            }
            return true;
        });
        replaceFragment(new HomeFragment());

    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layouts_fragments, fragment);
        fragmentTransaction.commit();

    }

}