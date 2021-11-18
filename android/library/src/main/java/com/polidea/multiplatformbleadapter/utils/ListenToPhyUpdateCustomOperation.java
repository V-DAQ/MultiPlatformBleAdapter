package com.polidea.multiplatformbleadapter.utils;

import android.bluetooth.BluetoothGatt;
import android.util.Log;

import androidx.annotation.NonNull;

import com.polidea.rxandroidble.RxBleCustomOperation;
import com.polidea.rxandroidble.internal.connection.RxBleGattCallback;

import rx.Observable;
import rx.Scheduler;


public class ListenToPhyUpdateCustomOperation implements RxBleCustomOperation<Integer> {
    @NonNull
    @Override
    public Observable<Integer> asObservable(
            final BluetoothGatt bluetoothGatt,
            final RxBleGattCallback rxBleGattCallback,
            final Scheduler scheduler
    ) {
        Log.d("CodedPhy", "Getting onPhyUpdate");
        return rxBleGattCallback.getOnPhyUpdate();
    }
}