# AppUpdate

你可以通过它来升级你的App

## 简介

* 小巧便捷 , 使用方便
* 自带强制/非强制性升级提示框 , 可替换弹框的颜色
* HttpURLConnection下载 , 不引用额外的库
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
     compile 'com.github.Loren1994:AndroidUpdate:1.1.0'
}
~~~~

# 用法
```java
 AppUpdateUtils.checkUpdate(this, new AppUpdateUtils.CheckUpdateListener() {

       @Override
       public void checkUpdate() {
           //add your check update interface
           //showDialog in http callback
           UpdateDialog.showUpdateDialog(MainActivity.this, "update your App", new              View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   UpdateDialog.dismissDialog(); 
                   Toast.makeText(MainActivity.this, "confirm                  button",Toast.LENGTH_SHORT).show();
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

* AppUpdateUtils.checkUpdate() :  会自动绑定Service,如果app正在下载,则此方法不会再运行
* 最后不要忘记在onDestory( )里用AppUpdateUtils.unbindService( )解绑Service

### 项目地址

GitHub:https://github.com/Loren1994/AndroidUpdate  欢迎star~




# <(▰˘◡˘▰)>


