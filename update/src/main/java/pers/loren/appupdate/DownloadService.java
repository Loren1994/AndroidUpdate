package pers.loren.appupdate;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends IntentService {
    private static final int BUFFER_SIZE = 10 * 1024; // 8k ~ 32K
    private static final int NOTIFICATION_ID = 0;
    private static final String TAG = "DownloadService";
    private static final String NOTIFICATION_CHANNEL = "DownloadChannel";
    public static String APK_PATH = Environment.getExternalStorageDirectory() + "/update.apk";
    //    public static String AUTHORITY = BuildConfig.APPLICATION_ID + "appupdate.fileprovider";
    public boolean isDownloading = false;

    private Builder mBuilder;
    private NotificationManager mNotifyManager;

    private MyBinder myBinder;

    public DownloadService() {
        super("DownloadService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (myBinder == null) {
            myBinder = new MyBinder();
        }
        return myBinder;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, "下载",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("下载升级文件");
            channel.enableVibration(false);
            channel.enableLights(false);
            mNotifyManager.createNotificationChannel(channel);
        }
        mBuilder = new Builder(this, NOTIFICATION_CHANNEL);

        String appName = getString(getApplicationInfo().labelRes);
        int icon = getApplicationInfo().icon;

        mBuilder.setContentTitle(appName).setSmallIcon(icon);
        String urlStr = intent.getStringExtra("url");
        InputStream in = null;
        FileOutputStream out = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setConnectTimeout(10 * 1000);
            urlConnection.setReadTimeout(10 * 1000);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");

            urlConnection.connect();
            long byteTotal = urlConnection.getContentLength();
            long byteSum = 0;
            int byteRead;
            in = urlConnection.getInputStream();
//            File dir = StorageUtils.getCacheDirectory(this);
//            String apkName = urlStr.substring(urlStr.lastIndexOf("/") + 1, urlStr.length());
            File apkFile = new File(APK_PATH);
            if (!apkFile.exists()) {
                Log.i(TAG, "file create success:" + apkFile.createNewFile());
            }
//            File apkFile = new File(dir, apkName);
            out = new FileOutputStream(apkFile);
            byte[] buffer = new byte[BUFFER_SIZE];

            isDownloading = true;
            int oldProgress = 0;
            updateProgress(0);
            while ((byteRead = in.read(buffer)) != -1) {
                byteSum += byteRead;
                out.write(buffer, 0, byteRead);

                int progress = (int) (byteSum * 100L / byteTotal);
                // 如果进度与之前进度相等，则不更新，如果更新太频繁，否则会造成界面卡顿
                if (progress != oldProgress) {
                    updateProgress(progress);
                }
                oldProgress = progress;
            }
            // 下载完成
            isDownloading = false;
            installAPk(apkFile);

            mNotifyManager.cancel(NOTIFICATION_ID);

        } catch (Exception e) {
            isDownloading = false;
            e.printStackTrace();
            Toast.makeText(this, "下载更新失败", Toast.LENGTH_SHORT).show();
            mNotifyManager.cancel(NOTIFICATION_ID);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {

                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {

                }
            }
        }
    }

    private void updateProgress(int progress) {
        // "正在下载:" + progress + "%"
        mBuilder.setContentText(this.getString(R.string.android_auto_update_download_progress, progress)).setProgress(100, progress, false);
        // setContentIntent如果不设置在4.0+上没有问题，在4.0以下会报异常
        PendingIntent pendingintent = PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pendingintent);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
        mNotifyManager.notify(NOTIFICATION_ID, notification);
    }

    private void installAPk(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 如果没有设置SDCard写权限，或者没有sdcard,apk文件保存在内存中，需要授予权限才能安装
        try {
            String[] command = {"chmod", "777", apkFile.toString()};
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
        } catch (IOException ignored) {
            System.out.println(ignored.toString());
        }
        Uri uri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(apkFile);
        } else {
            uri = FileProvider.getUriForFile(getApplicationContext(), this.getPackageName() + ".appupdate.fileprovider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    class MyBinder extends Binder {
        DownloadService getService() {
            return DownloadService.this;
        }
    }
}
