package com.wangzhen.download.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.wangzhen.download.DownloadClient;
import com.wangzhen.download.bean.ParamsBody;
import com.wangzhen.download.callback.AbsDownloadCallback;

/**
 * MainActivity
 * Created by wangzhen on 2019-12-03.
 */
public class MainActivity extends AppCompatActivity {

    private TextView mTvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvResult = findViewById(R.id.tv_result);
    }

    public void start_download(View view) {
        mTvResult.setText("-> 开始下载\n");
        download(1, "http://10.100.119.192:8080/wangzhen/audio/missing_is_a_knife.mp3");
        download(2, "http://10.100.119.192:8080/wangzhen/audio/moonlight.mp3");
    }

    public void download(Object tag, final String url) {
        DownloadClient.get().enqueue(new ParamsBody.Builder()
                .tag(tag)
                .url(url)
                .callback(new AbsDownloadCallback() {
                    @Override
                    public void onLoading(int progress) {

                    }

                    @Override
                    public void onSuccess(String path) {
                        mTvResult.append("-> 下载成功\n");
                        mTvResult.append("-> " + path + "\n");
                    }

                    @Override
                    public void onFail(String err) {
                        mTvResult.append("-> onFail " + err + "\n");
                        mTvResult.append("-> " + url + "\n");
                    }
                })
                .build());
    }
}
