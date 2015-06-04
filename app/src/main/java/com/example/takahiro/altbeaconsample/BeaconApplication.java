package com.example.takahiro.altbeaconsample;

import android.app.Application;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.Collection;

/**
 * Created by katotakahiro on 2015/05/29.
 */
public class BeaconApplication extends Application implements BootstrapNotifier {
    public static final String TAG = BeaconApplication.class.getSimpleName();

    // iBeaconのデータを認識するためのParserフォーマット
    public static final String IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private RegionBootstrap regionBootstrap;
    private BeaconManager beaconManager;
    private Identifier identifier;
    private Region region;
    private String beaconName;

    @Override
    public void onCreate() {
        super.onCreate();

        // iBeaconのデータを受信できるようにParserを設定
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON_FORMAT));
        beaconManager.setForegroundBetweenScanPeriod(1000);
        beaconManager.setBackgroundBetweenScanPeriod(1000);

        // UUIDの作成
        identifier = Identifier.parse("55430405-8DA3-4D08-AAA4-8C5D8FB60165");
        // Beacon名の作成
        beaconName = "MyBeacon-000206C6";
        // major, minorの指定はしない
        region = new Region(beaconName, identifier, null, null);
        regionBootstrap = new RegionBootstrap(this, region);

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                // 検出したビーコンの情報を全部Logに書き出す
                for(Beacon beacon : beacons) {
                    Log.d(TAG, "UUID:" + beacon.getId1() + ", major:" + beacon.getId2() + ", minor:" + beacon.getId3() + ", Distance:" + beacon.getDistance() + ",RSSI" + beacon.getRssi() + ", TxPower" + beacon.getTxPower());
                }
            }
        });

    }

    @Override
    public void didEnterRegion(Region region) {
        //領域侵入
        Log.d(TAG, "Enter Region");
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        try {
            // レンジング開始
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            // 例外が発生した場合
            e.printStackTrace();
        }
    }

    @Override
    public void didExitRegion(Region region) {
        // 領域退室
        Log.d(TAG, "Exit Region");
        try {
            // レンジング停止
            beaconManager.stopRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            // 例外が発生した場合
            e.printStackTrace();
        }
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        // 領域に対する状態が変化
        Log.d(TAG, "Determine State: " + i);
    }
}
