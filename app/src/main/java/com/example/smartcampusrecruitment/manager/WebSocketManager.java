package com.example.smartcampusrecruitment.manager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.smartcampusrecruitment.Tool.MyApplication;
import com.example.smartcampusrecruitment.model.dto.ChatMessageDTO;


import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class WebSocketManager {
    private static WebSocketManager instance;
    private StompClient stompClient;
    private String wsUrl = "ws://192.168.31.122:8081/ws-chat";
    private boolean isConnected = false;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private WebSocketManager() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl);
    }

    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    public void connect() {
        if (isConnected) {
            Log.d("WS", "WebSocket已连接");
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Log.d("WS", "延迟200ms后允许发消息");
            }, 200);
            return;
        }

        Log.d("WS", "开始连接WebSocket: " + wsUrl);

        // 订阅生命周期事件
        Disposable lifecycleDisposable = stompClient.lifecycle()
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            isConnected = true;
                            Log.d("WS", "WebSocket连接已打开");
                            break;
                        case ERROR:
                            isConnected = false;
                            Log.e("WS", "WebSocket连接错误", lifecycleEvent.getException());
                            break;
                        case CLOSED:
                            isConnected = false;
                            Log.d("WS", "WebSocket连接已关闭");
                            break;
                    }
                }, throwable -> {
                    Log.e("WS_LIFECYCLE_ERR", "生命周期监听错误", throwable);
                });

        compositeDisposable.add(lifecycleDisposable);

        // 建立连接
        stompClient.connect();
    }

    public boolean isConnected() {
        return isConnected && stompClient != null && stompClient.isConnected();
    }

    public void disconnect() {
        isConnected = false;

        // 清理所有订阅
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }

        if (stompClient != null) {
            stompClient.disconnect();
        }
    }

    public void sendMessage(ChatMessageDTO message) {
        Log.d("WS_SEND", "发送的 Base64: " + message.getTouxiang());
        Log.d("WS_SEND", "发送的 Base64cd: " + message.getTouxiang().length());
        if (isConnected()) {
            stompClient.send("/app/chat.send", MyApplication.getGson().toJson(message))
                    .subscribe(
                            () -> Log.d("WS_SEND", "消息已发出: " + message.getContent()),
                            throwable -> Log.e("WS_SEND_ERR", "消息发送失败", throwable)
                    );
        } else {
            Log.w("WS_SEND", "WebSocket未连接，无法发送消息。当前连接状态: isConnected=" + isConnected);
        }
    }

    public void subscribeToUser(String phoneNumber, MessageCallback callback) {
        if (stompClient != null) {
            Disposable subscribeDisposable = stompClient.topic("/user/" + phoneNumber + "/queue/messages")
                    .subscribe(stompMessage -> {
                        try {
                            Log.d("WS_RECEIVE", "收到消息: " + stompMessage.getPayload());
                            ChatMessageDTO message = MyApplication.getGson().fromJson(
                                    stompMessage.getPayload(), ChatMessageDTO.class);
                            callback.onMessageReceived(message);
                        } catch (Exception e) {
                            Log.e("WS_MESSAGE_PARSE", "解析消息时出错", e);
                        }
                    }, throwable -> {
                        Log.e("WS_SUBSCRIBE_ERR", "订阅失败", throwable);
                    });

            compositeDisposable.add(subscribeDisposable);
        }
    }

    public interface MessageCallback {
        void onMessageReceived(ChatMessageDTO message);
    }


}
