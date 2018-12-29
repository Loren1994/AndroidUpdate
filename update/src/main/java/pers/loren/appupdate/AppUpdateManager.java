package pers.loren.appupdate;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import pers.loren.appupdate.interfaces.UpdateDownloadListener;

/**
 * Copyright © 2018/12/28 by loren
 */
public class AppUpdateManager {

    private static ServiceConnection sc = null;
    private CheckUpdateListener checkUpdateListener;

    private AppUpdateManager() {
    }

    private static void unbindDownloadService(Context context) {
        if (sc != null) {
            context.unbindService(sc);
        }
        sc = null;
    }

    private static void bindDownloadService(final Context context, final CheckUpdateListener checkUpdateListener) {
        if (sc != null) {
            unbindDownloadService(context);
        }
        if (sc == null) {
            sc = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    if (service != null) {
                        DownloadService.DownloadBinder binder = ((DownloadService.DownloadBinder) service);
                        if (!binder.getService().isDownloading) {
                            checkUpdateListener.onCheckUpdate();
                        }
                    } else {
                        checkUpdateListener.onCheckUpdate();
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                }
            };
        }
        context.bindService(new Intent(context, DownloadService.class), sc, Context.BIND_AUTO_CREATE);
    }

    public static String getUpdateApkPath() {
        return DownloadService.APK_PATH;
    }

    interface CheckUpdateListener {
        void onCheckUpdate();
    }

    public static class Builder {
        private Context context = null;
        //设置下载回调
        private UpdateDownloadListener updateDownloadListener = null;
        //dialog中升级提示信息
        private String updateMessage = "检测到有新的版本";
        //下载的url
        private String downloadUrl = null;
        //是否是强制更新dialog
        private boolean forceUpdate = false;
        //是否显示自带的dialog
        private boolean isShowDialog = true;

        public Builder bind(Context context) {
            this.context = context;
            return this;
        }

        public Builder setDownloadUrl(String url) {
            this.downloadUrl = url;
            return this;
        }

        public Builder setDownloadListener(UpdateDownloadListener updateDownloadListener) {
            this.updateDownloadListener = updateDownloadListener;
            return this;
        }

        public Builder setUpdateMessage(String msg) {
            this.updateMessage = msg;
            return this;
        }

        public Builder setForceUpdate(boolean force) {
            this.forceUpdate = force;
            return this;
        }

        public Builder setShowDialog(boolean show) {
            this.isShowDialog = show;
            return this;
        }

        public void build() {
            bindDownloadService(context, new CheckUpdateListener() {
                @Override
                public void onCheckUpdate() {
                    if (!isShowDialog) {
                        startDownload();
                    } else {
                        if (forceUpdate) {
                            UpdateDialog.showUpdateForceDialog(context, updateMessage, new UpdateDialog.OnConfirmListener() {
                                @Override
                                public void onConfirm() {
                                    startDownload();
                                }
                            });
                        } else {
                            UpdateDialog.showUpdateDialog(context, updateMessage, new UpdateDialog.OnConfirmListener() {
                                @Override
                                public void onConfirm() {
                                    startDownload();
                                }
                            });
                        }
                    }
                }
            });
        }

        private void startDownload() {
            if (null == context) {
                throw new NullPointerException("don't call Builder.bind(context).");
            }
            if (null == downloadUrl) {
                throw new NullPointerException("please call Builder.setDownloadUrl(url).");
            }
            if (null == updateDownloadListener) {
                throw new NullPointerException("please call Builder.setDownloadListener(listener).");
            }
            DownloadService.updateDownloadListener = this.updateDownloadListener;
            Intent intent = new Intent(context.getApplicationContext(), DownloadService.class);
            intent.putExtra("url", downloadUrl);
            context.startService(intent);
        }

    }
}
