package com.android.adbkeyboard;

/**
 * Created by andrewleo on 2017/11/1.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProcessInfoService extends IntentService {

    private static final String PROCESS_INFO_ACTION = "com.android.adbkeyboard.ProcessInfo";
    private static final String DEFAULT_FILE_PATH = "/data/local/tmp/appinfos";

    public ProcessInfoService() {
        super("ProcessInfoService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            String fileToSaved = (bundle != null) ? bundle.getString("fileToSave",
                    DEFAULT_FILE_PATH) : DEFAULT_FILE_PATH;
            try {
                switch (intent.getAction()) {
                    case PROCESS_INFO_ACTION:
                        JSONArray jsonArray = new JSONArray();
                        PackageManager pm = this.getPackageManager();
                        for (ApplicationInfo appinfo : getAppInfos()) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put(appinfo.loadLabel(pm).toString(), appinfo.processName);
                            jsonArray.put(jsonObject);
                        }
                        saveToFile(fileToSaved, jsonArray.toString());
                }
            } catch (Exception e) {
                Log.e("adbKeyBoard", e.getMessage());
                e.printStackTrace();
            }
        }

    }

    private void saveToFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content);
        bw.close();
    }

    private List<ApplicationInfo> getAppInfos() {
        PackageManager pm = getApplicationContext().getPackageManager();
        List<ApplicationInfo> appList = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        return appList;
    }
}
