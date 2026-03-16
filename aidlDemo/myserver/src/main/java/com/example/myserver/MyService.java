package com.example.myserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyunfei on 16/10/12.
 */
public class MyService extends Service {
    public static final String TAG = "MyService";

    /** 持久化的回调列表，支持服务端主动向客户端推送消息 */
    private final RemoteCallbackList<IMyCallback> mCallbackList = new RemoteCallbackList<>();

    /** 服务实例，供 MainActivity 触发服务端主动推送 */
    private static volatile MyService sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCallbackList.kill();
        sInstance = null;
        Log.d(TAG, "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, String.format("on bind,intent = %s", intent.toString()));
        return binder;
    }

    /**
     * 服务端主动向所有已注册的客户端广播消息（从 MainActivity 调用）
     */
    public static void broadcastToAllClients(String message) {
        if (sInstance != null) {
            sInstance.doBroadcastToAllClients(message);
        }
    }

    private void doBroadcastToAllClients(String message) {
        final int len = mCallbackList.beginBroadcast();
        Log.d(TAG, String.format("广播消息给 %d 个客户端: %s", len, message));
        for (int i = 0; i < len; i++) {
            try {
                mCallbackList.getBroadcastItem(i).onServerMessage(message);
            } catch (RemoteException e) {
                Log.e(TAG, "广播时出错", e);
            }
        }
        mCallbackList.finishBroadcast();
    }

    private final IRemoteService.Stub binder = new IRemoteService.Stub() {
        public static final String TAG = "IRemoteService.Stub";
        private List<Entity> data = new ArrayList<Entity>();

        @Override
        public void doSomeThing(int anInt, String aString) throws RemoteException {
            Log.d(TAG, String.format("收到：%s, %s", anInt, aString));
        }

        @Override
        public void addEntity(Entity entity) throws RemoteException {
            Log.d(TAG, String.format("收到：entity = %s", entity));
            data.add(entity);
        }

        @Override
        public List<Entity> getEntity() throws RemoteException {
            return data;
        }

        @Override
        public void asyncCallSomeone(String para, IMyCallback callback) throws RemoteException {
            // 直接回调给请求者，不影响持久化注册的回调列表
            if (callback != null) {
                callback.onSuccess(para + "_callbck");
            }
        }

        @Override
        public void registerCallback(IMyCallback callback) throws RemoteException {
            if (callback != null) {
                mCallbackList.register(callback);
                Log.d(TAG, "注册回调，当前客户端数: " + mCallbackList.getRegisteredCallbackCount());
            }
        }

        @Override
        public void unregisterCallback(IMyCallback callback) throws RemoteException {
            if (callback != null) {
                mCallbackList.unregister(callback);
                Log.d(TAG, "取消注册回调，当前客户端数: " + mCallbackList.getRegisteredCallbackCount());
            }
        }
    };
}
