package com.example.takahiro.altbeaconsample;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

/**
 * Created by katotakahiro on 2015/05/29.
 */
public class BeaconApplication extends Application implements BootstrapNotifier {
    public static final String TAG = BeaconApplication.class.getSimpleName();

    // iBeaconのデータを認識するためのParserフォーマット
    public static final String IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";

    private RegionBootstrap regionBootstrap;

    private BeaconManager beaconManager;

    @Override
    public void onCreate() {
        super.onCreate();

        // iBeaconのデータを受信できるようにParserを設定
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON_FORMAT));
        beaconManager.setBackgroundBetweenScanPeriod(1000);

        // UUIDの作成
        Identifier identifier = Identifier.parse("55430405-8DA3-4D08-AAA4-8C5D8FB60165");
        // major, minorの指定はしない
        Region region = new Region("MyBeacon-000206C6", identifier, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
    }

    @Override
    public void didEnterRegion(Region region) {
        //領域侵入
        Log.d(TAG, "Enter Region");
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void didExitRegion(Region region) {
        // 領域退室
        Log.d(TAG, "Exit Region");
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        // 領域に対する状態が変化
        Log.d(TAG, "Determine State: " + i);
    }
}
