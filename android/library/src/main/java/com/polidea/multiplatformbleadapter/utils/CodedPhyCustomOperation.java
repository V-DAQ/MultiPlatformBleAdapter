package com.polidea.multiplatformbleadapter.utils;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.polidea.rxandroidble.RxBleCustomOperation;
import com.polidea.rxandroidble.internal.RxBleLog;
import com.polidea.rxandroidble.internal.connection.RxBleGattCallback;

import rx.Emitter;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;


public class CodedPhyCustomOperation implements RxBleCustomOperation<Integer> {
    @NonNull
    @Override
    public Observable<Integer> asObservable(
            final BluetoothGatt bluetoothGatt,
            final RxBleGattCallback rxBleGattCallback,
            final Scheduler scheduler
    ) {
        RxBleLog.i("Calling BluetoothGatt.setPreferredPhy()");
        Log.i("CodedPhy", String.format("Calling BluetoothGatt.setPreferredPhy()"));

        return Observable.amb(
                setPhyAndObserve(bluetoothGatt, rxBleGattCallback)
                        .subscribeOn(scheduler),
                rxBleGattCallback.<Boolean>observeDisconnect().map(status -> 257)
        );
    }

    @NonNull
    private Observable<Integer> setPhyAndObserve(
            final BluetoothGatt bluetoothGatt,
            final RxBleGattCallback rxBleGattCallback) {
        Observable<Integer> onPhyUpdate = rxBleGattCallback.getOnPhyUpdate();

        return Observable.create(emitter -> {
            Subscription subscription = onPhyUpdate.subscribe(emitter);
            emitter.setCancellation(subscription::unsubscribe);

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    bluetoothGatt.setPreferredPhy(BluetoothDevice.PHY_LE_CODED_MASK,
                            BluetoothDevice.PHY_LE_CODED_MASK,
                            BluetoothDevice.PHY_OPTION_S2
                    );
                }
            } catch (Throwable throwable) {
                emitter.onError(throwable);
            }
        }, Emitter.BackpressureMode.BUFFER);
    }
}