package com.bignerdranch.android.nerdlauncher;

import android.support.v4.app.Fragment;

/**
 * TEst123
 */
public class NerdLauncherActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return NerdLauncherFragment.newInstance();
    }
}
