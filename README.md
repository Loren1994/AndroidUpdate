# AppUpdate

##### 👉🏼[前往1.x文档](https://github.com/Loren1994/AndroidUpdate/blob/master/README-1.x.md)

你可以通过它来升级你的App。

##### [![](https://jitpack.io/v/Loren1994/AndroidUpdate.svg)](https://jitpack.io/#Loren1994/AndroidUpdate) [![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)

### 简介

* 小巧便捷 , 使用方便
* 自带强制/非强制性升级提示框 , 可替换弹框的颜色
* HttpURLConnection下载 , 不引用额外的库
* 解决三方库之间FileProvider冲突问题
* 支持Android 8

### 🔥2.x版本🔥

- [x] 重构API，调用方式更加简单

- [x] 支持使用自定义弹框

- [x] 升级库的support依赖

- [x] 支持进度回调，对话框进度条，通知栏进度条展示

- [x] 支持后台下载

- [x] 支持强制更新

- [x] 支持弹出框颜色替换

### Gradle引入

```Java
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
~~~~Java
dependencies {
	implementation 'com.github.Loren1994:AndroidUpdate:2.0.2'
}
~~~~

### API

```java
//首先请求你的检测更新接口 - 判断是否更新
new AppUpdateManager.Builder()
                .bind(this) //必须调用
                .setDownloadUrl(url) //必须设置
                .setDownloadListener(new UpdateDownloadListener() { //必须设置
                    @Override
                    public void onDownloading(int process) {
                        Log.d("update", "onProcess: " + process);
                    }

                    @Override
                    public void onDownloadSuccess() {
                        Log.d("update", "下载完成");
                    }

                    @Override
                    public void onDownloadFail(String reason) {
                        Log.d("update", reason);
                    }
                })
                .setUpdateMessage("检测到有新的版本,请下载升级.") //自带弹框显示的内容
                .setShowDialog(true) //是否显示自带的弹框
                .setForceUpdate(true) //是否显示自带的强制弹框
                .build();
```
### 说明

> ⚠️ 所需相关权限需要在自己的App中申请。
>
> ⚠️ 库中带有两种提示框，分为强制性更新框和非强制性更新框，可通过setForceUpdate设置。
>
> ⚠️ 若不使用自带的提示框，可设置setShowDialog(false)不显示弹框，则AppUpdateManager可看做只是下载的方法。在这之前加入自己的弹框和页面逻辑即可。

* 强制弹框

![image01](https://github.com/Loren1994/AndroidUpdate/blob/master/images/image01.png)

* 非强制弹框

![image02](https://github.com/Loren1994/AndroidUpdate/blob/master/images/image02.png)

### 替换提示框颜色

~~~~xml
<color name="download_indicator_color">XXXX</color>
~~~~

### 关于FileProvider冲突的问题

以下用TakePhoto 4.0.3测试冲突问题

##### TakePhoto库的Provider

~~~~Xml
<provider
   android:name="android.support.v4.content.FileProvider"
   android:authorities="${applicationId}.fileprovider"
   android:grantUriPermissions="true"
   android:exported="false">
   <meta-data
     android:name="android.support.FILE_PROVIDER_PATHS"
     android:resource="@xml/file_paths" />
</provider>
~~~~

##### 测试过程

| 条件                                       | 结果                            |
| ---------------------------------------- | ----------------------------- |
| app模块:                                                                                             tools:replace="android:authorities"   android:name="android.support.v4.content.FileProvider"                                                        update模块:                                                                                            android:name="android.support.v4.content.FileProvider" | ✘编译不通过                        |
| app模块:                                                                                             tools:replace="android:authorities"   android:name="android.support.v4.content.FileProvider"                                                        update模块:                                                                                            android:name=".LorenProvider" | ✔编译通过✘TakePhoto调用崩溃           |
| app模块:                                                                                             android:name="android.support.v4.content.FileProvider"                                                        update模块:                                                                                            android:name=".LorenProvider" | ✘编译不通过                        |
| app模块:                                                                                           android:name="pers.loren.appupdate.providers.ResolveConflictProvider"                                       update模块:                                                                                            android:name=".LorenProvider" | ✔编译通过✔TakePhoto调用正常✔update库正常 |
| app模块:                                                                                             tools:replace="android:authorities"   android:name="android.support.v4.content.FileProvider"                                                        update模块:                                                                          tools:replace="android:authorities"                                                                                       android:name="android.support.v4.content.FileProvider" | ✔编译通过✘TakePhoto调用崩溃           |
| app模块:                                                                                           android:name="pers.loren.appupdate.providers.ResolveConflictProvider"                                       update模块:                                                                                            android:name=".ResolveConflictProvider" | ✘编译不通过                        |

##### 冲突报错

~~~~Java
Error:Execution failed for task ':app:processDebugManifest'.
> Manifest merger failed : Attribute provider#android.support.v4.content.FileProvider@authorities value=(pers.loren.test.app.fileprovider) from AndroidManifest.xml:26:13-68
  	is also present at [com.jph.takephoto:takephoto_library:4.0.3] AndroidManifest.xml:19:13-64 value=(pers.loren.test.fileprovider).
  	Suggestion: add 'tools:replace="android:authorities"' to <provider> element at AndroidManifest.xml:24:9-32:20 to override.
~~~~

##### 最终Manifest

~~~~xml
<!--app模块-->
<provider
    android:name="pers.loren.appupdate.providers.ResolveConflictProvider"
    android:authorities="${applicationId}.app.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
       <meta-data
           android:name="android.support.FILE_PROVIDER_PATHS"
           android:resource="@xml/file_paths" />
</provider>

<!--update模块-->
<provider
    android:name=".LorenProvider"
    android:authorities="${applicationId}.appupdate.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
      <meta-data
           android:name="android.support.FILE_PROVIDER_PATHS"
           android:resource="@xml/file_paths" />
</provider>
~~~~

##### 最终解决方案

update库里采用自定义的fileprovider , app模块里也需要写provider标签, update模块提供ResolveConflictProvider以便app模块使用 , app模块里也可以自行建立fileprovider类

### 一点总结

* 同一App的多个库的provider的name都是android.support.v4.content.FileProvider时会引起冲突
* 上述情况可以通过写自定义FileProvider解决
* 同一App不同库的provider的name用同一个自定义FileProvider也会引起冲突
* 不同App之间provider的name可以相同 , 但authorities不可以重复,否则后者App不能安装
* 三方库的Manifest里引入tools , app里也引入了tools , 可能导致merge manifest fail

### Tips

* 如果只引用这一个带有provider的库 , 则app里不需要写provider
* 如果库之间有冲突 , 则参考 [最终Manifest]( #最终Manifest )
* build()方法里已判断ServiceConnection不为null时首先解绑
* 可以调用AppUpdateManager.unbindDownloadService(this)解绑Service

### 项目地址

GitHub:https://github.com/Loren1994/AndroidUpdate  欢迎star~ issue~




# <(▰˘◡˘▰)>


