/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 2/6/14
 * @version 1
 * Details.
 */
package com.ifit.fecptestapp;

import android.widget.TextView;

import com.ifit.sparky.fecp.CommandCallback;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.SpeedConverter;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.GetTaskInfoCmd;
import com.ifit.sparky.fecp.interpreter.status.CpuTask;
import com.ifit.sparky.fecp.interpreter.status.GetTaskInfoSts;
import com.ifit.sparky.fecp.interpreter.status.WriteReadDataSts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;

public class HandleTaskInfo implements CommandCallback, Runnable {

    private int mNumOfTasks;
    private int mCurrentTask;//this will be used to keep track of which task we still need to query.
    private ArrayList<CpuTask> taskList;
    private String taskString;

    private TextView mInfoView;
    private MainActivity mAct;
    public HandleTaskInfo(MainActivity act, TextView infoView)
    {
        mInfoView = infoView;
        this.mAct = act;
        this.taskList = new ArrayList<CpuTask>();
        this.mNumOfTasks = 0;
        this.mCurrentTask = 0;
    }
    /**
     * Handles the reply from the device
     *
     * @param cmd the command that was sent.
     */
    @Override
    public void msgHandler(Command cmd) {

        //check command type
        CpuTask task;
        if(cmd.getCmdId() == CommandId.GET_TASK_INFO)
        {

            task = new CpuTask(((GetTaskInfoSts) cmd.getStatus()).getTask());
 
            for(CpuTask tempTask : this.taskList)
            {
                if(tempTask.getTaskIndex() == task.getTaskIndex())
                {
                    //remove it
                    this.taskList.remove(tempTask);
                    break;
                }
            }

            this.taskList.add(task);
            this.mCurrentTask++;
            if(this.mCurrentTask == this.mNumOfTasks)
            {
                this.mCurrentTask = 0;//reset to 0

            }

            //set the task to be the task that we want next time.
            ((GetTaskInfoCmd)cmd).setTaskIndex(this.mCurrentTask);

            //sort based on the task index
            Collections.sort(this.taskList);
            String taskString_temp = "";
            for (Iterator<CpuTask> it = this.taskList.iterator(); it.hasNext();) {
                taskString_temp += it.next().toString();
            }
            taskString = taskString_temp;

            this.mAct.runOnUiThread(new Thread(this));//update the gui
        }
    }

    @Override
    public void run() {
        //add to the data text
        this.mInfoView.setText(taskString);
    }

    public int getNumOfTasks() {
        return mNumOfTasks;
    }


    public int getCurrentTask() {
        return mCurrentTask;
    }

    public void setCurrentTask(int currentTask) {
        this.mCurrentTask = currentTask;
    }

    public void setNumOfTasks(int numOfTasks) {
        this.mNumOfTasks = numOfTasks;
    }

}
