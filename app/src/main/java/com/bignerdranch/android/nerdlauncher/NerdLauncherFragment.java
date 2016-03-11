package com.bignerdranch.android.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Fragment class hosted by NerdLauncherActivity
 * Uses Recyclerview layout
 *
 * Created by Rudolf on 3/11/2016.
 */
public class NerdLauncherFragment extends Fragment{

    // TAG for filtering log messages
    private static final String TAG = "NerdLauncherFragment";

    // RecyclerView
    private RecyclerView mRecyclerView;

    public static NerdLauncherFragment newInstance() {
        return new NerdLauncherFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_nerd_launcher, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_nerd_launcher_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter();
        return view;
    }

    /**
     * Creates Adapter to connect RecyclerView to ViewHolder ActivityHolder
     */
    private void setupAdapter() {

        // Create Implicit intent with MAIN and add LAUNCHER category
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        // Query intent activities with MAIN/LAUNCHER intent filters
        // into a list of ResolveInfo objects
        final PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(startupIntent, 0);

        // Sort ResolveInfo objects alphabetically
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                return String.CASE_INSENSITIVE_ORDER.compare(
                        a.loadLabel(packageManager).toString(),
                        b.loadLabel(packageManager).toString()
                );
            }
        });

        // Print number of apps the PackageManager returned
        Log.i(TAG, "Found " + activities.size() + " activities.");

        // Instantiate ActivityAdapter and set to mRecyclerView's adapter
        mRecyclerView.setAdapter(new ActivityAdapter(activities));
    }

    /**
     * Adapter that connects RecyclerView layout to ActivityHolder
     */
    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {

        private final List<ResolveInfo> mActivities;        // List of MAIN/LAUNCHER activities

        private ActivityAdapter(List<ResolveInfo> activities) {
            mActivities = activities;
        }

        @Override
        public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);

            return new ActivityHolder(view);
        }

        @Override
        public void onBindViewHolder(ActivityHolder activityHolder, int position) {
            ResolveInfo resolveInfo = mActivities.get(position);
            activityHolder.bindActivity(resolveInfo);
        }

        @Override
        public int getItemCount() {
            return mActivities.size();
        }
    }

    /**
     * ViewHolder that displays an activity's label
     */
    private class ActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ResolveInfo mResolveInfo;           // Information returned from resolving an intent
                                                    // against an IntentFilter
        private TextView mNameTextView;             // Activity's label

        public ActivityHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView;
            mNameTextView.setOnClickListener(this);
        }

        /**
         * Binds Activity Label to TextView
         *
         * Displays app icon and name.
         *
         * @param resolveInfo
         */
        public void bindActivity(ResolveInfo resolveInfo) {

            mResolveInfo = resolveInfo;
            PackageManager packageManager = getActivity().getPackageManager();

            // Retrieve app name and icon
            String appName = mResolveInfo.loadLabel(packageManager).toString();
            Drawable appIcon = mResolveInfo.loadIcon(packageManager);

            // Scale all appIcon's to uniform size
            Bitmap bitmap = ((BitmapDrawable) appIcon).getBitmap();
            Drawable icon = new BitmapDrawable(getResources(),
                    Bitmap.createScaledBitmap(bitmap, 150, 150, true));

            // Display app name and icon
            mNameTextView.setText(appName);
            mNameTextView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        }

        /**
         * Creates implicit intent and starts clicked app
         *
         * @param v
         */
        @Override
        public void onClick(View v) {

            ActivityInfo activityInfo = mResolveInfo.activityInfo;

            // Create Explicit Intent using package name and class name
            Intent intent = new Intent(Intent.ACTION_MAIN)
                    .setClassName(activityInfo.applicationInfo.packageName,
                            activityInfo.name)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }
    }

}
