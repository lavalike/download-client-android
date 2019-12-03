package com.wangzhen.download.bean;

import com.wangzhen.download.callback.OnDownloadCallback;

/**
 * 下载参数构建
 * Created by wangzhen on 2019-09-12.
 */
public class ParamsBody {
    public Object tag;
    public String url;
    public String dir;
    public String fileName;
    public OnDownloadCallback callback;

    private ParamsBody(Builder builder) {
        this.tag = builder.tag;
        this.url = builder.url;
        this.dir = builder.dir;
        this.fileName = builder.fileName;
        this.callback = builder.callback;
    }

    public static class Builder {
        private Object tag;
        private String url;
        private String dir;
        private String fileName;
        private OnDownloadCallback callback;

        public Builder tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder dir(String dir) {
            this.dir = dir;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder callback(OnDownloadCallback callback) {
            this.callback = callback;
            return this;
        }

        public ParamsBody build() {
            return new ParamsBody(this);
        }
    }
}
