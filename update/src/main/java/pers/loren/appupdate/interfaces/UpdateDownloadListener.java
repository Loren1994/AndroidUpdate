package pers.loren.appupdate.interfaces;

/**
 * Copyright © 2018/12/29 by loren
 */
public interface UpdateDownloadListener {
    void onDownloading(int process);

    void onDownloadSuccess();

    void onDownloadFail(String reason);
}
