package com.polidea.multiplatformbleadapter.utils;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.os.Build;
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
                        call: try {
                            Method setPreferredPhyFunction = bluetoothGatt.getClass().getMethod("setPreferredPhy");
                            if (setPreferredPhyFunction == null) {
                                RxBleLog.d("Could not find function BluetoothGatt.setPreferredPhy()");
                                break call;
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                setPreferredPhyFunction.invoke(bluetoothGatt,
                                        BluetoothDevice.PHY_LE_CODED_MASK,
                                        BluetoothDevice.PHY_LE_CODED_MASK,
                                        BluetoothDevice.PHY_OPTION_S2);
                            }
                            success = true;
                        } catch (Exception e) {
                            RxBleLog.d(e, "Could not call function BluetoothGatt.setPreferredPhy()");
                        }
                        RxBleLog.i("Calling BluetoothGatt.setPreferredPhy() status: %s", success ? "Success" : "Failure");
                        return success;
                    }
                })
                        .subscribeOn(scheduler)
                        .delay(1, TimeUnit.SECONDS, scheduler),
                rxBleGattCallback.<Boolean>observeDisconnect()
        );
    }
}
