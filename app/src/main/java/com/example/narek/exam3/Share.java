package com.example.narek.exam3;

import android.app.NotificationManager;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;

/**
 * Created by Narek on 4/4/16.
 */
public class Share extends AsyncTask<Void, Integer, Integer> {
    private NotificationManager mNotifyManager;
    private android.support.v7.app.NotificationCompat.Builder mBuilder;
    int id = 1;

    public void setmNotifyManager(NotificationManager mNotifyManager) {
        this.mNotifyManager = mNotifyManager;
    }

    public void setmBuilder(NotificationCompat.Builder mBuilder) {
        this.mBuilder = mBuilder;
    }

    public NotificationCompat.Builder getmBuilder() {
        return mBuilder;
    }

    public int getId() {
        return id;
    }

    @Override
    protected void onPreExecute () {
        super.onPreExecute();

        mBuilder.setProgress(100, 0, false);
        mNotifyManager.notify(id, mBuilder.build());

    }

    @Override
    protected void onProgressUpdate (Integer...values){
        mBuilder.setProgress(100, values[0], false);
        mNotifyManager.notify(id, mBuilder.build());
        super.onProgressUpdate(values);
    }

    @Override
    protected Integer doInBackground (Void...params){
        int i;
        for (i = 0; i <= 100; i += 2) {
            publishProgress(Math.min(i, 100));
            try {
                Thread.sleep(150);
            } catch (InterruptedException ignored) {

            }
        }
        return null;
    }

    @Override
    public void onPostExecute (Integer result){
        super.onPostExecute(result);
        mBuilder.setContentText("Download complete");

        mBuilder.setProgress(0, 0, false);
        mNotifyManager.notify(id, mBuilder.build());
    }



}



