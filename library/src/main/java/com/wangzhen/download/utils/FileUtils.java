package com.wangzhen.download.utils;

import android.text.TextUtils;

/**
 * file utils
 * Created by wangzhen on 2019-09-12.
 */
public class FileUtils {

    /**
     * retrieve file name from url
     *
     * @param url url
     * @return name
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
}
