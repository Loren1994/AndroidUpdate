# AppUpdate

你可以通过它来升级你的App

## 简介

* 小巧便捷 , 使用方便
* 自带强制/非强制性升级提示框 , 可替换弹框的颜色
* HttpURLConnection下载 , 不引用额外的库
* 解决三方库之间FileProvider冲突问题
* 支持Android8.0

## 引用

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
     compile 'com.github.Loren1994:AndroidUpdate:1.2.0'
}
~~~~

# 用法
```java
AppUpdateUtils.bindDownloadService(this, new AppUpdateUtils.CheckUpdateListener() {
            @Override
            public void checkUpdate() {
                //模拟请求接口延时
                SystemClock.sleep(3000);
                //在这里请求你的检测更新接口
                //在接口成功回调里判断是否更新,更新则弹Dialog
                UpdateDialog.showUpdateDialog(MainActivity.this,
                        "update your app", new UpdateDialog.OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                                //下载方法
                                AppUpdateUtils.update(MainActivity.this,"URL");
                            }
                        });
            }
});
```
## 强制/非强制性升级提示框
```java
//强制
UpdateDialog.showUpdateForceDialog(MainActivity.this, 
      "update your app", new UpdateDialog.OnConfirmListener() {
        @Override
        public void onConfirm() {
              Toast.makeText(MainActivity.this, 
              "confirm", Toast.LENGTH_SHORT).show();
        }
});
//非强制
UpdateDialog.showUpdateDialog(MainActivity.this, "update your app", 
                               new UpdateDialog.OnConfirmListener() {
        @Override
        public void onConfirm() {
            Toast.makeText(MainActivity.this, 
                         "confirm",Toast.LENGTH_SHORT).show();
        }
});
```
### 替换提示框颜色

~~~~xml
<color name="download_indicator_color">XXXX</color>
~~~~

#### 关于FileProvider冲突的问题

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
| app模块:                                                                                           android:name="pers.loren.appupdate.ResolveConflictProvider"                                       update模块:                                                                                            android:name=".LorenProvider" | ✔编译通过✔TakePhoto调用正常✔update库正常 |
| app模块:                                                                                             tools:replace="android:authorities"   android:name="android.support.v4.content.FileProvider"                                                        update模块:                                                                          tools:replace="android:authorities"                                                                                       android:name="android.support.v4.content.FileProvider" | ✔编译通过✘TakePhoto调用崩溃           |
| app模块:                                                                                           android:name="pers.loren.appupdate.ResolveConflictProvider"                                       update模块:                                                                                            android:name=".ResolveConflictProvider" | ✘编译不通过                        |

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
    android:name="pers.loren.appupdate.ResolveConflictProvider"
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

#### 一点总结

* 同一App的多个库的provider的name都是android.support.v4.content.FileProvider时会引起冲突
* 上述情况可以通过写自定义FileProvider解决
* 同一App不同库的provider的name用同一个自定义FileProvider也会引起冲突
* 不同App之间provider的name可以相同 , 但authorities不可以重复,否则后者App不能安装
* 三方库的Manifest里引入tools , app里也引入了tools , 可能导致merge manifest fail

#### 一点题外总结

设置applicationId时 :

 "pers.loren.test" : ✔ 成功

 "pers.loren.test.1" : ✘ 编译通过 , install时提示解析安装包失败 (Android6.0)

 "pers.loren.test1" : ✔ 成功

# Tips

* 如果只引用这一个带有provider的库 , 则app里不需要写provider
* 如果库之间有冲突 , 则参考 [最终Manifest]( #最终Manifest )
* AppUpdateUtils.bindDownloadService( ) :  service不为null会自动解绑
* 不要忘记在onDestory( )里调用AppUpdateUtils.unbindDownloadService( )解绑Service

### 项目地址

GitHub:https://github.com/Loren1994/AndroidUpdate  欢迎star~ issue~




# <(▰˘◡˘▰)>


