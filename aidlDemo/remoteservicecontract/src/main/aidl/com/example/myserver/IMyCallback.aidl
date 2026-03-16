// Callback.aidl
package com.example.myserver;

// Declare any non-default types here with import statements

interface IMyCallback {
    /**
     * 异步调用的成功回调
     */
    void onSuccess(String aString);

    /**
     * 服务端主动向客户端推送消息
     */
    void onServerMessage(String message);
}
