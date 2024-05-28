package com.example.teachandlearn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Clases extends Fragment {

    private ViewPager viewPager;
    private ClasesPagerAdapter pagerAdapter;
    private AppDatabase db;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clases, container, false);

        viewPager = view.findViewById(R.id.view_pager_clases);
        pagerAdapter = new ClasesPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabs = view.findViewById(R.id.tabs_clases);
        tabs.setupWithViewPager(viewPager);

        return view;
    }

    private class ClasesPagerAdapter extends FragmentPagerAdapter {

        public ClasesPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ClasesPendientesFragment();
                case 1:
                    return new ClasesReservadasFragment();
                case 2:
                    return new ClasesRechazadasFragment();
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
                    return "Pendientes";
                case 1:
                    return "Reservados";
                case 2:
                    return "Rechazados";
                default:
                    return null;
            }
        }
    }
}
