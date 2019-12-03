package com.wangzhen.download.callback;

/**
 * 下载回调接口
 * Created by wangzhen on 2019-09-12.
 */
public interface OnDownloadCallback {
    /**
     * 下载进度
     *
     * @param progress progress
     */
    void onLoading(int progress);

    /**
     * 下载成功
     *
     * @param path path
     */
    void onSuccess(String path);

    /**
     * 错误回调
     *
     * @param err err msg
     */
    void onFail(String err);
}
