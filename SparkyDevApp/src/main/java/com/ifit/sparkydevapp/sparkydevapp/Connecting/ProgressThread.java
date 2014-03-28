/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 3/27/2014
 * @version 1
 * Details.
 */
package com.ifit.sparkydevapp.sparkydevapp.Connecting;

import android.app.ProgressDialog;

public class ProgressThread extends Thread {

    private boolean stopThread;
    private final int totalProgressTime = 100;
    private ProgressDialog mProgress;
    public ProgressThread(ProgressDialog progressObj)
    {
        super();
        stopThread = false;
        mProgress = progressObj;
    }
    public  void stopProgress()
    {
        stopThread = true;
    }
    @Override
    public void run(){
        stopThread = false;
        int jumpTime = 0;
        while(jumpTime < totalProgressTime && !stopThread){
            try {
                sleep(100);
                jumpTime += 5;
                if(jumpTime == totalProgressTime)
                {
                    jumpTime = 0;
                }
                mProgress.setProgress(jumpTime);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        mProgress.dismiss();

    }
}
