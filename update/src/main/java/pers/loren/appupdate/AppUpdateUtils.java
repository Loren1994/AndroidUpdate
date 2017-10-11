package pers.loren.appupdate;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;

@SuppressWarnings("unused")
public class AppUpdateUtils {
    private static CheckUpdateListener checkUpdateListener;
    private static ServiceConnection sc;

    private AppUpdateUtils() {
    }

    public static void bindDownloadService(Context context, CheckUpdateListener checkUpdate) {
        /*
        bind check for failed download
         */
        if (sc != null) {
            unbindDownloadService(context);
        }
        /*
        null check for illegal exit
         */
        if (sc == null) {
            sc = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    if (service != null) {
                        DownloadService ds = ((DownloadService.MyBinder) service).getService();
                        if (!ds.isDownloading) {
                            checkUpdateListener.checkUpdate();
                        }
                    } else {
                        checkUpdateListener.checkUpdate();
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            };
        }

        checkUpdateListener = checkUpdate;
        context.bindService(new Intent(context, DownloadService.class), sc, Context.BIND_AUTO_CREATE);
    }

    public static void unbindDownloadService(Context context) {
        if (sc != null) {
            context.unbindService(sc);
        }
        sc = null;
        checkUpdateListener = null;
    }

    public static int getVersionCode(Context context) {
        if (context != null) {
            try {
                return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
        return 0;
    }

    public static String getVersionName(Context context) {
        if (context != null) {
            try {
                return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
        return "";
    }

    public static void update(Context context, String downloadUrl) {
        Intent intent = new Intent(context.getApplicationContext(), DownloadService.class);
        intent.putExtra("url", downloadUrl);
        context.startService(intent);
    }

    public interface CheckUpdateListener {
        void checkUpdate();
    }

}
