package com.wangzhen.download.task;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.wangzhen.download.bean.ParamsBody;
import com.wangzhen.download.callback.OnDownloadCallback;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.wangzhen.download.utils.FileUtils.getDownloadPath;
import static com.wangzhen.download.utils.FileUtils.getNameFromUrl;

/**
 * 执行下载任务
 * Created by wangzhen on 2019-09-12.
 */
public final class DownloadTask extends Thread {
    private static final int MSG_SUCCESS = 0x1;
    private static final int MSG_FAIL = 0x2;
    private static final int MSG_LOADING = 0x3;
    private String url;
    private String dir;
    private String fileName;
    private OnDownloadCallback callback;
    private Call mCall;

    public DownloadTask(ParamsBody body) {
        this.url = body.url;
        this.dir = body.dir;
        this.fileName = body.fileName;
        this.callback = body.callback;
    }

    @Override
    public void run() {
        if (TextUtils.isEmpty(url)) {
            onFail("url is empty");
            return;
        }
        if (TextUtils.isEmpty(fileName))
            fileName = getNameFromUrl(url);
        if (TextUtils.isEmpty(fileName)) {
            onFail("fail getting a file name");
            return;
        }
        if (TextUtils.isEmpty(dir))
            dir = getDownloadPath();
        //创建目录
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        if (url.startsWith("/")) {
            File file = new File(url);
            if (file.exists()) {
                copy2Folder(url, dir);
            } else {
                onFail("local file does not exist");
            }
        } else if (url.startsWith("http") || url.startsWith("https")) {
            download(url, dir, fileName);
        } else {
            onFail("unsupported url");
        }
    }

    private void download(String url, final String dir, final String fileName) {
        // 服务器会随机的对下发的资源做GZip操作，而此时就没有相应的content-length,responsebody.contentLength()获取到的值为-1
        // 强迫服务器不走压缩，在Header中加入：Request.Builder().addHeader("Accept-Encoding", "identity")
        Request request = new Request.Builder().addHeader("Accept-Encoding", "identity").url(url).build();
        mCall = new OkHttpClient().newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String error;
                if (e instanceof SocketTimeoutException) {
                    error = "连接超时";
                } else {
                    error = e.getMessage();
                }
                onFail(error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                FileOutputStream fos = null;
                byte[] buff = new byte[2048];
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(dir, fileName);
                    if (!file.exists())
                        file.createNewFile();
                    fos = new FileOutputStream(file);
                    int len;
                    long sum = 0;
                    while ((len = is.read(buff)) != -1) {
                        fos.write(buff, 0, len);
                        sum += len;
                        int progress = (int) ((sum * 1.0f) / total * 100);
                        onLoading(progress);
                    }
                    fos.flush();
                    onSuccess(dir + File.separator + fileName);
                } catch (Exception e) {
                    onFail(e.getMessage());
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 将文件复制到指定目录
     *
     * @param url 文件路径
     * @param dir 指定目录
     */
    public void copy2Folder(String url, String dir) {
        String destinationPath = dir + File.separator + getNameFromUrl(url);
        try {
            FileInputStream fis = new FileInputStream(url);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fis);
            FileOutputStream fos = new FileOutputStream(destinationPath);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fos);
            int len;
            while ((len = bufferedInputStream.read()) != -1) {
                bufferedOutputStream.write(len);
            }
            bufferedInputStream.close();
            bufferedOutputStream.close();
            onSuccess(destinationPath);
        } catch (Exception e) {
            onFail(e.getMessage());
        }
    }

    private void onSuccess(String url) {
        Message message = Message.obtain();
        message.what = MSG_SUCCESS;
        message.obj = url;
        handler.sendMessage(message);
    }

    private void onLoading(int progress) {
        Message message = Message.obtain();
        message.what = MSG_LOADING;
        message.obj = progress;
        handler.sendMessage(message);
    }

    private void onFail(String err) {
        Message message = Message.obtain();
        message.what = MSG_FAIL;
        message.obj = err;
        handler.sendMessage(message);
    }

    public Call getCall() {
        return mCall;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUCCESS:
                    String path = msg.obj != null ? String.valueOf(msg.obj) : "";
                    if (callback != null) {
                        callback.onSuccess(path);
                    }
                    break;
                case MSG_FAIL:
                    String err = msg.obj != null ? String.valueOf(msg.obj) : "";
                    if (callback != null) {
                        callback.onFail(err);
                    }
                    break;
                case MSG_LOADING:
                    int progress = msg.obj != null ? (int) msg.obj : 0;
                    if (callback != null) {
                        callback.onLoading(progress);
                    }
                    break;
            }
        }
    };
}
