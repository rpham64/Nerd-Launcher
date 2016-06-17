package com.bignerdranch.android.nerdlauncher;

import android.support.v4.app.Fragment;

/**
 * feature23
 */
public class NerdLauncherActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return NerdLauncherFragment.newInstance();
    }
}
