
package com.atakmap.android.mumbleptt.plugin;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.atakmap.android.maps.MapComponent;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.mumbleptt.mumblePttMapComponent;

import transapps.maps.plugin.lifecycle.Lifecycle;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;

import com.atakmap.android.mumbleptt.mumblePttWidget;
import com.atakmap.coremap.log.Log;

public class mumblePttLifecycle implements Lifecycle {

    private final Context pluginContext;
    private final Collection<MapComponent> overlays;
    private MapView mapView;

    public static Activity activity;

    private final static String TAG = "mumblePttLifecycle";

    public mumblePttLifecycle(Context ctx) {
        this.pluginContext = ctx;
        this.overlays = new LinkedList<>();
        this.mapView = null;
        PluginNativeLoader.init(ctx);
    }

    @Override
    public void onConfigurationChanged(Configuration arg0) {
        for (MapComponent c : this.overlays)
            c.onConfigurationChanged(arg0);
    }

    @Override
    public void onCreate(final Activity arg0,
            final transapps.mapi.MapView arg1) {
        activity = arg0;
        if (arg1 == null || !(arg1.getView() instanceof MapView)) {
            Log.w(TAG, "This plugin is only compatible with ATAK MapView");
            return;
        }
        this.mapView = (MapView) arg1.getView();
        mumblePttLifecycle.this.overlays
                .add(new mumblePttMapComponent());

        // create components
        Iterator<MapComponent> iter = mumblePttLifecycle.this.overlays
                .iterator();
        MapComponent c;
        while (iter.hasNext()) {
            c = iter.next();
            try {
                c.onCreate(mumblePttLifecycle.this.pluginContext,
                        arg0.getIntent(),
                        mumblePttLifecycle.this.mapView);
            } catch (Exception e) {
                Log.w(TAG,
                        "Unhandled exception trying to create overlays MapComponent",
                        e);
                iter.remove();
            }
        }
    }

    @Override
    public void onDestroy() {
        for (MapComponent c : this.overlays)
            c.onDestroy(this.pluginContext, this.mapView);
    }

    @Override
    public void onFinish() {
        // XXX - no corresponding MapComponent method
    }

    @Override
    public void onPause() {
        for (MapComponent c : this.overlays)
            c.onPause(this.pluginContext, this.mapView);
    }

    @Override
    public void onResume() {
        for (MapComponent c : this.overlays)
            c.onResume(this.pluginContext, this.mapView);
    }

    @Override
    public void onStart() {
        for (MapComponent c : this.overlays)
            c.onStart(this.pluginContext, this.mapView);
    }

    @Override
    public void onStop() {
        for (MapComponent c : this.overlays)
            c.onStop(this.pluginContext, this.mapView);
    }
}
