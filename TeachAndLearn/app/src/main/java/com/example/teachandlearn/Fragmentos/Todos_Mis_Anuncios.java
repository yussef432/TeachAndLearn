package com.example.teachandlearn.Fragmentos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.teachandlearn.Anuncios.AnunciosAceptadosFragment;
import com.example.teachandlearn.Anuncios.AnunciosPendientesFragment;
import com.example.teachandlearn.Anuncios.Mis_Anuncios;
import com.example.teachandlearn.R;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.Nullable;

public class Todos_Mis_Anuncios extends Fragment {

    private ViewPager viewPager;
    private AnunciosReservadosPagerAdapter pagerAdapter;

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
                    return new Mis_Anuncios();
                case 1:
                    return new AnunciosPendientesFragment();
                case 2:
                    return new AnunciosAceptadosFragment();
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
                    return "Mis Anuncios";
                case 1:
                    return "Pendientes de Aceptar";
                case 2:
                    return "Aceptados";
                default:
                    return null;
            }
        }
    }
}
