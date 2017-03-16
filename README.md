# AppUpdate
You can use it to update your App. Support Android 7.1
#How to Use it ?
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
#####You can find
