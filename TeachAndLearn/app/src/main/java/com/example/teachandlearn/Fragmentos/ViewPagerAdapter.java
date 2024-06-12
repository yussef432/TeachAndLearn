package com.example.teachandlearn.Fragmentos;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.teachandlearn.Adaptadores.Calendario;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new Perfil();
            case 1: return new Clases();
            case 2: return new Calendario();
            case 3: return new Todos_Mis_Anuncios();
            default: return new Perfil();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
