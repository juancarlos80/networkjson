package jc.personal.networkjson;

import android.net.NetworkInfo;

public interface DownloadRetorno<T> {

    interface Progreso {
        int ERROR = -1;
        int CONNECT_SUCCESS = 0;
        int GET_INPUT_STREAM_SUCCESS = 1;
        int PROCESS_INPUT_STREAM_IN_PROGESS = 2;
        int PROCESS_INPUT_STREAM_SUCCESS = 3;
    }

    void updateFromDownload(T result);
    void updateProgreso(String result);
    NetworkInfo getActiveNetworkInfo();
    void finishDownloading(T message);
}

