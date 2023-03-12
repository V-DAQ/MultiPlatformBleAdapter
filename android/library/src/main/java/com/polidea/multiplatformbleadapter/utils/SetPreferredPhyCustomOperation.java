package com.polidea.multiplatformbleadapter.utils;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.os.Build;

import androidx.annotation.NonNull;

import com.polidea.rxandroidble2.RxBleCustomOperation;
import com.polidea.rxandroidble2.internal.RxBleLog;
import com.polidea.rxandroidble2.internal.connection.RxBleGattCallback;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;


public class SetPreferredPhyCustomOperation implements RxBleCustomOperation<Boolean> {
    @NonNull
    @Override
    public Observable<Boolean> asObservable(
            final BluetoothGatt bluetoothGatt,
            final RxBleGattCallback rxBleGattCallback,
            final Scheduler scheduler
    ) {
        return Observable.amb(
                Observable.fromCallable(() -> {
                    boolean success = false;
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            bluetoothGatt.setPreferredPhy(
                                    BluetoothDevice.PHY_LE_CODED_MASK,
                                    BluetoothDevice.PHY_LE_CODED_MASK,
                                    BluetoothDevice.PHY_OPTION_S2
                            );
                        }
                        success = true;
                    } catch (Exception e) {
                        RxBleLog.d(e, "Could not call function BluetoothGatt.setPreferredPhy()");
                    }
                    RxBleLog.i("Calling BluetoothGatt.refresh() status: %s", success ? "Success" : "Failure");
                    return success;
                })
                        .subscribeOn(scheduler)
                        .delay(100, TimeUnit.MILLISECONDS, scheduler),
                rxBleGattCallback.<Boolean>observeDisconnect()
        );
    }
}