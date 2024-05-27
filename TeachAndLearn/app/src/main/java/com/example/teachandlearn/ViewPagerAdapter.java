package com.example.teachandlearn;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

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
            case 3: return new Mis_Anuncios();
            case 4: return new Mis_Anuncios_Reservados();
            default: return new Perfil();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
