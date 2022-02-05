package org.platon.lato;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.platon.wallet.engine.DownloadManagerPro;
import com.platon.wallet.engine.VersionUpdate;
import com.platon.wallet.utils.LogUtils;
/*
import org.platon.lato.jobs.UpdateApkJob;
import org.platon.lato.logging.Log;
import org.platon.lato.util.FileUtils;
import org.platon.lato.util.Hex;
import org.platon.lato.util.TextSecurePreferences;
import org.zackratos.ultimatebar.UltimateBar;
*/
import org.platon.lato.permissions.Permissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

public class UpdateApk {
    private static final String TAG = "UpdateApk";
    private Context mContext;
    public int versionCode;
    private static final String DOWNLOAD_FOLDER_NAME = Environment.DIRECTORY_DOWNLOADS;
    //private static final String DOWNLOAD_FOLDER_NAME = "lato";

    private final String notificationTitle = "PlatONFans";
    private final String notificationDescription = "版本升级";
    public String versionName;
    public String sha256sum;
    public String url;
    private boolean isForceUpdate = true;
    private String mVersionName;
    private DownloadManager mDownloadManager;
    private CompleteReceiver mCompleteReceiver;
    private DownloadManagerPro mDownloadManagerPro;
    private DownloadChangeObserver mDownloadChangeObserver;
    private String apkFilePath;
    private long lastDownloadId = -1;
    private Handler mHandler;
    private UpdateApkViewListener updateApkViewListener;
    public static final int MSG_DWSIZE = 1000;
    public static final int MSG_NULL = 2000;
    public UpdateApk(){}
    public UpdateApk(Context context, String url, String versionName, boolean isForceUpdate, UpdateApkViewListener _updateApkViewListener){
        this.mContext = context;
        this.url = url;
        this.isForceUpdate = isForceUpdate;
        mVersionName = "PlatONFans-android-" + versionName + ".apk";
        init(context);
        updateApkViewListener = _updateApkViewListener;
        mHandler =  new Handler(){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {

            }
        };
    }

    private void init(Context context) {
        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        mDownloadManagerPro = new DownloadManagerPro(mDownloadManager);

        mDownloadChangeObserver = new DownloadChangeObserver();
        context.getContentResolver().registerContentObserver(DownloadManagerPro.CONTENT_URI, true,
                mDownloadChangeObserver);

        mCompleteReceiver = new CompleteReceiver();
        context.registerReceiver(mCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        /*apkFilePath = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath())
                .append(File.separator).append(DOWNLOAD_FOLDER_NAME)
                .append(File.separator).append(mVersionName)
                .toString();*/
        apkFilePath = new StringBuilder(Environment.getExternalStoragePublicDirectory(DOWNLOAD_FOLDER_NAME).getAbsolutePath())

                .append(File.separator).append(mVersionName)
                .toString();
        System.out.println("apkFilePath = " + apkFilePath);

    }

    public void execute() {
        // lastDownloadId = PreferenceTool.getLong(Constants.Preference.KEY_DOWNLOAD_MANAGER_ID, -1);
        if (lastDownloadId != -1 && !invalidDownLoadManager()) {
            mDownloadManager.remove(lastDownloadId);
            // PreferenceTool.remove(Constants.Preference.KEY_DOWNLOAD_MANAGER_ID);
        }
        initDownLoadPath();
        //2.创建下载请求对象，并且把下载的地址放进去
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(notificationTitle);
        //设置显示在文件下载Notification（通知栏）中显示的文字。6.0的手机Description不显示
        request.setDescription(notificationDescription);
        //设置在什么连接状态下执行下载操作
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        // 设置为可被媒体扫描器找到
        request.allowScanningByMediaScanner();
        //设置为可见和可管理
        request.setVisibleInDownloadsUi(false);
        //给下载的文件指定路径
        request.setDestinationInExternalPublicDir(DOWNLOAD_FOLDER_NAME, mVersionName);

       //request.setDestinationInExternalFilesDir(mContext, null, "lato-update.apk");

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        request.setMimeType(mimeString);
        System.out.println("url1111 = " + url);
        //检查系统管理器知是否正常状态
        if (invalidDownLoadManager()) {//不可用

            System.out.println("invalidDownLoadManager 1" );
            // ToastUtil.showLongToast(mContext, "当前下载器被禁用，请在设置中开启");
        } else {

            System.out.println("invalidDownLoadManager 2");
            lastDownloadId = mDownloadManager.enqueue(request);
            //PreferenceTool.putLong(Constants.Preference.KEY_DOWNLOAD_MANAGER_ID, lastDownloadId);
            updateView();

        }
    }
    public int getVersionCode(){
        return this.versionCode;
    }
    public String getUrl(){
        return this.url;
    }
    public String getVersionName(){
        return this.versionName;
    }
    public String getDigest() {
        return sha256sum;
    }
    /*public DownloadStatus getDownloadStatus(String uri, byte[] theirDigest) {
        mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query           = new DownloadManager.Query();
        query.setFilterByStatus(DownloadManager.STATUS_PAUSED | DownloadManager.STATUS_PENDING | DownloadManager.STATUS_RUNNING | DownloadManager.STATUS_SUCCESSFUL);
        long   pendingDownloadId = TextSecurePreferences.getUpdateApkDownloadId(mContext);
        byte[] pendingDigest     = getPendingDigest(mContext);
        Cursor cursor            = mDownloadManager.query(query);
        try {
            DownloadStatus status = new DownloadStatus(DownloadStatus.Status.MISSING, -1);

            while (cursor != null && cursor.moveToNext()) {
                int    jobStatus         = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                String jobRemoteUri      = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_URI));
                long   downloadId        = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_ID));

                byte[] digest            = getDigestForDownloadId(downloadId);

                if (jobRemoteUri != null && jobRemoteUri.equals(uri) && downloadId == pendingDownloadId) {

                    if (jobStatus == DownloadManager.STATUS_SUCCESSFUL    &&
                            digest != null && pendingDigest != null           &&
                            MessageDigest.isEqual(pendingDigest, theirDigest) &&
                            MessageDigest.isEqual(digest, theirDigest))
                    {
                        return new UpdateApk.DownloadStatus(UpdateApk.DownloadStatus.Status.COMPLETE, downloadId);
                    } else if (jobStatus != DownloadManager.STATUS_SUCCESSFUL) {
                        status = new UpdateApk.DownloadStatus(UpdateApk.DownloadStatus.Status.PENDING, downloadId);
                    }
                }
            }

            return status;
        } finally {
            if (cursor != null) cursor.close();
        }
    }*/

    static class DownloadStatus {
        enum Status {
            PENDING,
            COMPLETE,
            MISSING
        }

        private final DownloadStatus.Status status;
        private final long   downloadId;

        DownloadStatus(DownloadStatus.Status status, long downloadId) {
            this.status     = status;
            this.downloadId = downloadId;
        }

        public DownloadStatus.Status getStatus() {
            return status;
        }

        public long getDownloadId() {
            return downloadId;
        }
    }


    /*public @Nullable byte[] getDigestForDownloadId(long downloadId) {
        try {
            DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            FileInputStream fin             = new FileInputStream(downloadManager.openDownloadedFile(downloadId).getFileDescriptor());
            byte[]          digest          = FileUtils.getFileDigest(fin);

            fin.close();

            return digest;
        } catch (IOException e) {
            //Log.w(TAG, e);
            return null;
        }
    }
   /* private @Nullable byte[] getPendingDigest(Context context) {
        try {
            String encodedDigest = TextSecurePreferences.getUpdateApkDigest(context);

            if (encodedDigest == null) return null;

            return Hex.fromStringCondensed(encodedDigest);
        } catch (IOException e) {
            //Log.w(TAG, e);
            return null;
        }
    }*/
    public boolean invalidDownLoadManager() {
        int state = mContext.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
        boolean invalid = state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED;
        return invalid;
    }

    private void initDownLoadPath() {

        Environment.getExternalStorageDirectory();
        File folder = Environment.getExternalStoragePublicDirectory( DOWNLOAD_FOLDER_NAME);
        if (!folder.exists() || !folder.isDirectory()) {
            boolean rs = folder.mkdirs();
            System.out.println("initDownLoadPath rs = "+ rs);
        }

        //如果已经存在该版本app，则删除之前的。
        File file = new File(apkFilePath);
        if (file.exists()) {
            System.out.println("initDownLoadPath rs = 1");
            if(!file.delete()){
                System.out.println("initDownLoadPath rs = 2");
                LogUtils.d("delete  file fail");
            }
        }
    }
    private void updateView() {
        if (updateApkViewListener != null) {
            if (isForceUpdate) {
                int[] bytesAndStatus = mDownloadManagerPro.getBytesAndStatus(lastDownloadId);
                int downloadedBytes = bytesAndStatus[0];
                int totalBytes = bytesAndStatus[1];
                int downloadStatus = bytesAndStatus[2];
                LogUtils.d("[cursize,total,status]" + "[" + downloadedBytes + "," + totalBytes + "," + downloadStatus + "]");
                double i = (double)downloadedBytes / (double)totalBytes;
                updateApkViewListener.onChange(i);
                   mHandler.sendMessage(mHandler.obtainMessage(MSG_DWSIZE, downloadedBytes, totalBytes, downloadStatus));
            } else {
                mHandler.sendEmptyMessage(MSG_NULL);
            }
        }
    }
    class CompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.d("[CompleteReceiver]");

            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            if (completeDownloadId == lastDownloadId) {
                updateView();
                if (mDownloadManagerPro.getStatusById(lastDownloadId) == DownloadManager.STATUS_SUCCESSFUL) {
                    installProxy(context);
                }
            }
        }
    }
    private void installProxy(Context context) {
        unregisterReceiver(context);
        /*DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(lastDownloadId);
        Cursor cursor    = mDownloadManager.query(query);
        try {
            while (cursor != null && cursor.moveToNext()) {
                String local_uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                if(local_uri!=null){
                    apkFilePath = Uri.parse(local_uri).getPath();
                    System.out.println("apkFilePath = " + apkFilePath);
                }else{
                    System.out.println("apkFilePath = null");
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }*/
        install(context, apkFilePath);
        try {
            Thread.sleep(2000);
            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

       /* if (isForceUpdate) {
            System.exit(0);
        }*/
    }
    private void unregisterReceiver(Context context) {
        if (mCompleteReceiver != null) {
            context.unregisterReceiver(mCompleteReceiver);
        }

        if (mDownloadChangeObserver != null) {
            context.getContentResolver().unregisterContentObserver(mDownloadChangeObserver);
        }

    }
    private void install(Context context, String filePath) {
        System.out.println("install111111");
        System.out.println("install111111 filePath ="+filePath);
        Uri packageURI = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
        Intent intent = new Intent(Intent.ACTION_VIEW, packageURI);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri contentUri = FileProvider.getUriForFile(context, "org.platon.lato.fileProvider", new File(filePath));
            System.out.println("/"+contentUri);
            System.out.println("instal33333333333"+contentUri.getPath());
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                boolean hasInstallPermission = mContext.getPackageManager().canRequestPackageInstalls();
                System.out.println("instal5555555555 hasInstallPermission"+hasInstallPermission);
                if (!hasInstallPermission) {
                    System.out.println("instal5566666666 hasInstallPermission"+hasInstallPermission);
                    Uri packageURI = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                    Intent intent1 = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES , packageURI);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent1);
                }else{
                    context.startActivity(intent);
                }
            }*/
        } else {
            System.out.println("instal22222222222223");
            intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }


        System.out.println("instal44444444444333");

        context.startActivity(intent);


        //return false;
    }

    class DownloadChangeObserver extends ContentObserver {

        public DownloadChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {

            LogUtils.d("[DownloadChangeObserver]");
            if (isForceUpdate) {
                updateView();
            }
        }

    }
    public interface UpdateApkViewListener{
        void onChange(double i);
    };

}
