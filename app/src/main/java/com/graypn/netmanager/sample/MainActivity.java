package com.graypn.netmanager.sample;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.graypn.netmanager.NetManager;
import com.graypn.netmanager.callback.DownloadCallBack;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetManager.init(this);

        btn = (Button) findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testDownload();
            }
        });
    }

    private void testDownload() {
        NetManager.download("http://download.voicecloud.cn/100IME/iFlyIME_v7.0.4405.apk",
                Environment.getExternalStorageDirectory().toString(), "test.apk", new DownloadCallBack() {
                    @Override
                    public void onFinish(File file) {
                        Toast.makeText(MainActivity.this, "finished", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(long currentBytes, long totalBytes) {
                        int progress = (int) ((currentBytes * 100) / totalBytes);
                        Log.i("progress", "progress" + progress);
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
