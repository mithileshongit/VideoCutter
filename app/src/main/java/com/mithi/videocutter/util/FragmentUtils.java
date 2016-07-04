package com.mithi.videocutter.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.mithi.videocutter.R;


public class FragmentUtils {

    public static <T extends Fragment>void addFragment(FragmentManager fragmentManager, Class<T> fragmentClass) {
        setFragment(fragmentManager, fragmentClass, true);
    }

    public static <T extends Fragment>void setFragment(FragmentManager fragmentManager, Class<T> fragmentClass) {
        setFragment(fragmentManager, fragmentClass, false);
    }

    private static <T extends Fragment>void setFragment(FragmentManager fragmentManager, Class<T> fragmentClass, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        try {
            fragmentTransaction.add(R.id.main_container, fragmentClass.newInstance(), fragmentClass.getCanonicalName());
            if (addToBackStack) {
                fragmentTransaction.addToBackStack(null);
            }
            fragmentTransaction.commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
