/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 2/6/14
 * @version 1
 * Details.
 */
package com.ifit.fecptestapp;

import android.content.AsyncTaskLoader;
import android.os.AsyncTask;
import android.os.Looper;
import android.widget.TextView;

import com.ifit.sparky.fecp.CommandCallback;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ByteConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.SpeedConverter;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.status.WriteReadDataSts;

import java.util.TreeMap;

public class HandleInfo implements CommandCallback, Runnable {


    private TextView mInfoView;
    private String mResultStr = "";
    private MainActivity mAct;
    public HandleInfo(MainActivity act, TextView infoView)
    {
        mInfoView = infoView;
        this.mAct = act;
    }
    /**
     * Handles the reply from the device
     *
     * @param cmd the command that was sent.
     */
    @Override
    public void msgHandler(Command cmd) {

        //check command type
        TreeMap<BitFieldId, BitfieldDataConverter> commandData;
        if(cmd.getCmdId() == CommandId.WRITE_READ_DATA)
        {
            commandData = ((WriteReadDataSts)cmd.getStatus()).getResultData();

            if(commandData.containsKey(BitFieldId.KPH))
            {

                try {
                    mResultStr = "Speed="+((SpeedConverter)commandData.get(BitFieldId.KPH).getData()).getSpeed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(commandData.containsKey(BitFieldId.WORKOUT_MODE))
            {

                try {
                    mResultStr += "Mode=" + ((ByteConverter)commandData.get(BitFieldId.WORKOUT_MODE).getData()).getValue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

           // this.mInfoView.setText(resultStr);
            this.mAct.runOnUiThread(new Thread(this));
        }
    }

    @Override
    public void run() {
        this.mInfoView.setText(this.mResultStr);
    }
}
