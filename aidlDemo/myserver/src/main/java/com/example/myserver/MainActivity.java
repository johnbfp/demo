package com.example.myserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private int mMsgCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 服务端主动向所有已注册客户端广播消息
        findViewById(R.id.btnNotify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMsgCount++;
                String message = "服务端推送消息 #" + mMsgCount;
                MyService.broadcastToAllClients(message);
                Toast.makeText(MainActivity.this, "已广播: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
