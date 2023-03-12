package com.polidea.multiplatformbleadapter.utils;

import android.bluetooth.BluetoothGatt;
import android.util.Log;

import androidx.annotation.NonNull;

import com.polidea.rxandroidble2.RxBleCustomOperation;
import com.polidea.rxandroidble2.internal.connection.RxBleGattCallback;

import rx.Emitter;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;


public class ListenToPhyUpdateCustomOperation implements RxBleCustomOperation<Integer> {
    @NonNull
    @Override
    public Observable<Integer> asObservable(BluetoothGatt bluetoothGatt,
                                           RxBleGattCallback rxBleGattCallback,
                                           Scheduler scheduler) {
        Log.d("CodedPhy", "ListenToPhyUpdateCustomOperation.asObservable");
        return observeOnPhyUpdate(rxBleGattCallback)
                .subscribeOn(scheduler)
                .take(1);
    }

    @NonNull
    private Observable<Integer> observeOnPhyUpdate(RxBleGattCallback rxBleGattCallback) {
        Log.d("CodedPhy", "ListenToPhyUpdateCustomOperation.observeOnPhyUpdate");
        Observable<Integer> onPhyUpdate = rxBleGattCallback.getOnPhyUpdate();
        return Observable.create(emitter -> {
            Log.d("CodedPhy", "Observable.create");
            Subscription subscription = onPhyUpdate.subscribe(emitter);
            Log.d("CodedPhy", "Observable.create after subscribe");
            emitter.setCancellation(subscription::unsubscribe);
        }, Emitter.BackpressureMode.BUFFER);
    }
}