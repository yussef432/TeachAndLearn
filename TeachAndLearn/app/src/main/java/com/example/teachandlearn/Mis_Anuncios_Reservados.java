package com.example.teachandlearn;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Mis_Anuncios_Reservados extends Fragment {

    private ViewPager viewPager;
    private AnunciosReservadosPagerAdapter pagerAdapter;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_anuncios_reservados, container, false);

        viewPager = view.findViewById(R.id.view_pager_anuncios_reservados);
        pagerAdapter = new AnunciosReservadosPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabs = view.findViewById(R.id.tabs_anuncios_reservados);
        tabs.setupWithViewPager(viewPager);

        return view;
    }

    private class AnunciosReservadosPagerAdapter extends FragmentPagerAdapter {

        public AnunciosReservadosPagerAdapter(@NonNull FragmentManager fm) {

            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new AnunciosPendientesFragment();
                case 1:
                    return new AnunciosAceptadosFragment();
                case 2:
                    return new Mis_Anuncios();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Pendientes de Aceptar";
                case 1:
                    return "Aceptados";
                case 2:
                    return "Sin reservas";
                default:
                    return null;
            }
        }
    }
}
