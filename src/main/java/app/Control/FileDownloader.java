package app.Control;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class FileDownloader {

    int mDownloadedFileSize = 0;
    int mTotalFileSize = 0;
    String inputUrl;
    String fileExtention;
    String downloadedFileName;
    String outputFilePath;
    Handler mHandler;
    private static final String TAG = FileDownloader.class.getSimpleName();

    private OnFileDownloadProgressChangedListener mDownloadChangedListener;
    public boolean mStopDownload = false;


    public FileDownloader(String filepath, OnFileDownloadProgressChangedListener listener, String outputFilePath) {
        this.mHandler = new Handler(Looper.getMainLooper());
        this.inputUrl = filepath;
        this.fileExtention = this.inputUrl.substring(filepath.lastIndexOf("."), filepath.length());
        this.outputFilePath = outputFilePath;
        this.mDownloadChangedListener = listener;
    }

    /**
     * Start doownloading of the file
     */
    public void startDownload() {
        /**start downloading file*/
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDownloadChangedListener.onDownloadStart();
            }
        });
        downloadFile();
    }

    public void stopDownloading() {
        this.mStopDownload = true;
    }

    int downloadFile() {

        try {
            URL url = new URL(inputUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
            urlConnection.connect();

            FileOutputStream fileOutput = new FileOutputStream(outputFilePath);

            Log.d(TAG, "Response: " + urlConnection.getResponseCode() + urlConnection.getResponseMessage());
            Log.d(TAG, "Getting input stream");
            InputStream inputStream = urlConnection.getInputStream();

            mTotalFileSize = urlConnection.getContentLength();
            //create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
                mDownloadedFileSize += bufferLength;
                /**Check whether to cancel this download or not*/
                if (mStopDownload) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mDownloadChangedListener.onFailure();

                        }
                    });
                    break;
                }

                /**
                 * Post the current progress on the service*/
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        float per = ((float) mDownloadedFileSize / mTotalFileSize) * 100;
                        mDownloadChangedListener.onProgressChanged(per);
                    }
                });
            }
            //close the output stream when complete //
            fileOutput.close();
            if (!mStopDownload) {
                /** Notify through the listener that the file is downloaded*/
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDownloadChangedListener.onFileDownloaded(outputFilePath);
                    }
                });
            }
        } catch (Exception e) {
            showError("Error : Please check your internet connection " + e);
        }
        return 0;
    }


    void showError(final String err) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "" + err);
            }
        });
    }


    /**
     * This is to track the downloading status
     */
    public interface OnFileDownloadProgressChangedListener {
        public void onDownloadStart();

        public void onProgressChanged(float currentProgress);

        public void onFileDownloaded(String currentPath);

        public void onFailure();
    }
}


