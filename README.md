# AppUpdate

你可以通过它来升级你的App

## 简介

* 小巧便捷 , 使用方便
* 自带强制/非强制性升级提示框 , 可替换弹框的颜色
* HttpURLConnection下载 , 不引用额外的库
* ~~支持Android8.0~~

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
     compile 'com.github.Loren1994:AndroidUpdate:v1.1.2'
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

# Tips

* AppUpdateUtils.bindDownloadService( ) :  service不为null会自动解绑
* 不要忘记在onDestory( )里调用AppUpdateUtils.unbindDownloadService( )解绑Service

### 项目地址

GitHub:https://github.com/Loren1994/AndroidUpdate  欢迎star~




# <(▰˘◡˘▰)>


