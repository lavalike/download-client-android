package com.wangzhen.download;

import com.wangzhen.download.bean.ParamsBody;
import com.wangzhen.download.task.DownloadTask;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 多线程下载器
 * Created by wangzhen on 2019-09-12.
 */
public final class DownloadClient {
    private static DownloadClient mInstance;
    private static final Object lock = new Object();
    private ExecutorService mExecutorService;
    private Map<Object, SoftReference<DownloadTask>> mTaskCache = new HashMap<>();

    public static DownloadClient get() {
        if (mInstance == null) {
            synchronized (lock) {
                if (mInstance == null) {
                    mInstance = new DownloadClient();
                }
            }
        }
        return mInstance;
    }

    /**
     * 自定义ExecutorService
     *
     * @param service ExecutorService
     * @return this
     */
    public DownloadClient executor(ExecutorService service) {
        this.mExecutorService = service;
        return this;
    }

    /**
     * 获取线程池
     *
     * @return ExecutorService
     */
    private ExecutorService getExecutor() {
        if (mExecutorService == null) {
            mExecutorService = Executors.newCachedThreadPool();
        }
        return mExecutorService;
    }

    /**
     * 提交下载任务
     *
     * @param body params body
     */
    public void enqueue(ParamsBody body) {
        if (body != null) {
            cancel(body.tag);
            DownloadTask task = new DownloadTask(body);
            mTaskCache.put(body.tag, new SoftReference<>(task));
            getExecutor().submit(task);
        }
    }

    /**
     * 取消全部任务
     */
    public void cancel() {
        for (SoftReference<DownloadTask> reference : mTaskCache.values()) {
            if (reference != null && reference.get() != null) {
                if (reference.get().getCall() != null) {
                    reference.get().getCall().cancel();
                }
            }
        }
        mTaskCache.clear();
    }

    /**
     * 取消指定任务
     *
     * @param tag tag
     */
    public void cancel(Object tag) {
        SoftReference<DownloadTask> reference = mTaskCache.remove(tag);
        if (reference != null && reference.get() != null) {
            if (reference.get().getCall() != null) {
                reference.get().getCall().cancel();
            }
        }
    }
}
