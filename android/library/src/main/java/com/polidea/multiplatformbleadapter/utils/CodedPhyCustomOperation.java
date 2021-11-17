package com.polidea.multiplatformbleadapter.utils;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.polidea.rxandroidble.RxBleCustomOperation;
import com.polidea.rxandroidble.internal.RxBleLog;
import com.polidea.rxandroidble.internal.connection.RxBleGattCallback;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;


public class CodedPhyCustomOperation implements RxBleCustomOperation<Boolean> {

    @NonNull
    @Override
    public Observable<Boolean> asObservable(
            final BluetoothGatt bluetoothGatt,
            final RxBleGattCallback rxBleGattCallback,
            final Scheduler scheduler
    ) throws Throwable {

        return Observable.amb(
                Observable.fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        boolean success = false;
                        call:
                        try {
                            Method setPreferredPhyFunction = bluetoothGatt.getClass().getMethod("setPreferredPhy", int.class, int.class, int.class);
                            if (setPreferredPhyFunction == null) {
                                RxBleLog.d("Could not find function BluetoothGatt.setPreferredPhy()");
                                Log.d("CodedPhy", "Could not find function BluetoothGatt.setPreferredPhy()");
                                break call;
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                setPreferredPhyFunction.invoke(bluetoothGatt,
                                                BluetoothDevice.PHY_LE_CODED_MASK,
                                                BluetoothDevice.PHY_LE_CODED_MASK,
                                                BluetoothDevice.PHY_OPTION_S2
                                );
                            }
                            success = true;
                        } catch (Exception e) {
                            RxBleLog.d(e, "Could not call function BluetoothGatt.setPreferredPhy()");
                            Log.d("CodedPhy", String.format("Could not call function BluetoothGatt.setPreferredPhy(): %s", e.getMessage()));
                        }
                        RxBleLog.i("Calling BluetoothGatt.setPreferredPhy() status: %s", success ? "Success" : "Failure");
                        Log.i("CodedPhy", String.format("Calling BluetoothGatt.setPreferredPhy() status: %s", success ? "Success" : "Failure"));
                        return success;
                    }
                })
                        .subscribeOn(scheduler),
                rxBleGattCallback.<Boolean>observeDisconnect()
        );
    }
}
