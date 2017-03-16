# AppUpdate
You can use it to update your App. Support Android 7.1

Step 1. Add the JitPack repository to your build file 
<pre>
<code>
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
</code>
</pre>
Step 2. Add the dependency
<pre>
<code>
dependencies {
	 compile 'com.github.Loren1994:AndroidUpdate:1.0.0'
}
</code>
</pre>

# How to Use it ?
<pre><code>
 AppUpdateUtils.checkUpdate(this, new AppUpdateUtils.CheckUpdateListener() {
            @Override
            public void checkUpdate() {
                //add your check update interface
                //showDialog in http callback
                UpdateDialog.ShowUpdateDialog(MainActivity.this, "update your App", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UpdateDialog.dismissDialog(); 
                        Toast.makeText(MainActivity.this, "confirm button", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
</code></pre>

## ShowUpdateForceDialog &  ShowUpdateDialog
<pre>
<code>
UpdateDialog.ShowUpdateForceDialog(MainActivity.this, 
	"update your app", new UpdateDialog.OnConfirmListener() {
             @Override
             public void onConfirm() {
                   Toast.makeText(MainActivity.this, 
                   "confirm", Toast.LENGTH_SHORT).show();
             }
 });
</code>
</pre>

# APIs Instructions

#### AppUpdateUtils.checkUpdate() : 

Auto bind ServiceConnection. If App is downloading , this function can not run .

##### You need run unbindService() on Destory()


# ╭∩╮(︶︿︶）╭∩╮


