# download-client-android
> 基于OKHttp的文件下载器

[![Platform](https://img.shields.io/badge/Platform-Android-00CC00.svg?style=flat)](https://www.android.com)
[![](https://jitpack.io/v/lavalike/download-client-android.svg)](https://jitpack.io/#lavalike/download-client-android)

### 依赖导入

项目根目录

``` gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

模块目录

``` gradle
dependencies {
	implementation 'com.github.lavalike:download-client-android:0.0.2'
}
```

### 代码示例

``` java
DownloadClient.get().enqueue(new ParamsBody.Builder()
        .tag(tag)
        .fileName(name)
        .url(url)
        .callback(new AbsDownloadCallback() {
            @Override
            public void onLoading(int progress) {

            }

            @Override
            public void onSuccess(String path) {

            }

            @Override
            public void onFail(String err) {

            }
        })
        .build());
```