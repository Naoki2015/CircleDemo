package com.yiw.qupai.result;

import android.content.Intent;
import android.os.Bundle;

public class RecordResult {

    private final Bundle _Bundle;
    public static final String RESULT_KEY = "qupai.edit.result";
    public static final String XTRA_PATH = "path";
    public static final String XTRA_THUMBNAIL = "thumbnail";


    public RecordResult(Intent intent) {
        _Bundle = intent.getBundleExtra(RESULT_KEY);
    }

    public String getPath() {
        return _Bundle.getString(XTRA_PATH);
    }

    public String[] getThumbnail() {
        return _Bundle.getStringArray(XTRA_THUMBNAIL);
    }


    public static final String XTRA_DURATION = "duration";
    public long getDuration() {
        return _Bundle.getLong(XTRA_DURATION);
    }
}
