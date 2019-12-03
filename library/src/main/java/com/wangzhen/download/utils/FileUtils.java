package com.wangzhen.download.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * 文件工具类
 * Created by wangzhen on 2019-09-12.
 */
public class FileUtils {

    /**
     * 从下载连接中解析出文件名,过滤掉webp带?的格式
     *
     * @param url url
     * @return 文件名
     */
    public static String getNameFromUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            String result = url.substring(url.lastIndexOf("/") + 1);
            if (result.contains("?")) {
                result = result.substring(0, result.lastIndexOf("?"));
            }
            return result;
        }
        return "";
    }

    /**
     * get default download path
     *
     * @return download path
     */
    public static String getDownloadPath() {
        File path = new File(Environment.getExternalStorageDirectory() + File.separator + "download");
        if (!path.exists()) {
            path.mkdirs();
        }
        return path.getAbsolutePath();
    }
}
