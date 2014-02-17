/**
 * This is the super class for handling which commands to send and to receive.
 * @author Levi.balling
 * @date 1/20/14
 * @version 1
 * handles the common items as far as what to send and what to receive.
 */
package com.ifit.sparky.fecp;
import android.util.Log;

import com.ifit.sparky.fecp.communication.CommInterface;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FecpCmdHandler implements FecpCmdHandleInterface, Runnable{

    private CommInterface mCommController;
    private Vector<FecpCommand> mProcessCmds;
    private Vector<FecpCommand> mPeriodicCmds;//this will use the thread scheduler
    private ScheduledExecutorService mThreadManager = Executors.newSingleThreadScheduledExecutor();//this will keep track of all the threads
    private Thread mCurrentThread;//this thread will be recreated when needed.

    public FecpCmdHandler(CommInterface commController)
    {
        this.mCommController = commController;
        this.mProcessCmds =new Vector<FecpCommand>();
        this.mPeriodicCmds = new Vector<FecpCommand>();
    }

    /**
     * Gets the comm controller used for the system.
     *
     * @return the comm controller
     */
    @Override
    public CommInterface getCommController() {
        return this.mCommController;
    }

    /**
     * Adds the command to the list to be sent
     *
     * @param cmd the command to be sent.
     */
    @Override
    public void addFecpCommand(FecpCommand cmd) throws Exception
    {
        if(this.mPeriodicCmds.contains(cmd))
        {
            return;//already in the list. don't add
        }

        //check if thread is set
        cmd.setSendHandler(this);
        //check if the thread is running
        if(cmd.getFrequency() != 0)
        {
            this.mPeriodicCmds.add(cmd);
            cmd.setFutureScheduleTask(this.mThreadManager.scheduleAtFixedRate(cmd, 0, cmd.getFrequency(), TimeUnit.MILLISECONDS));
        }
        else
        {
            this.processFecpCommand(cmd);
        }

    }

    /**
     * Removes the command if it matches the Command id and the Device ID.
     * If there are multiples in the command list it will remove both of them.
     *
     * @param devId Device id to check if the command matches
     * @param cmdId the command to be removed
     * @return true if it removed the element
     */
    @Override
    public boolean removeFecpCommand(DeviceId devId, CommandId cmdId) {

        boolean result = false;
        for(FecpCommand cmd : this.mPeriodicCmds)
        {
            if(cmd.getCommand().getCmdId() == cmdId && cmd.getCommand().getDevId() == devId)
            {
                cmd.getFutureScheduleTask().cancel(false);//cancels
                this.mPeriodicCmds.remove(cmd);
                result = true;
            }
        }
        return result;
    }

    /**
     * Removes the command
     * @param cmd the fecpCommand to remove
     * @return true if it removed the element
     */
    @Override
    public boolean removeFecpCommand(FecpCommand cmd) {

        if(this.mPeriodicCmds.contains(cmd))
        {
            this.mPeriodicCmds.remove(cmd);
            cmd.getFutureScheduleTask().cancel(false);//stop calling it
            return true;
        }
        return false;
    }

    /**
     * Sends the command to the Fecp Communication Controller
     *
     * @param cmd the command to the Device
     */
    @Override
    public void sendCommand(FecpCommand cmd) throws Exception{
        long startTime;
        long endTime;
        cmd.incrementCmdSentCounter();
        ByteBuffer tempBuffer = cmd.getCommand().getCmdMsg();
        //send the command and handle the response.
        if(cmd.getTimeout() == 0)
        {
            startTime = System.nanoTime();
            tempBuffer = this.mCommController.sendAndReceiveCmd(tempBuffer);
            endTime = System.nanoTime();
        }
        else
        {
            startTime = System.nanoTime();
            tempBuffer = this.mCommController.sendAndReceiveCmd(tempBuffer, cmd.getTimeout());
            endTime = System.nanoTime();
        }
        cmd.setCommSendReceiveTime(endTime - startTime);
        //check if there was an error with the send. if so return Failed
        if(tempBuffer.get(0)== 0)
        {
            //message failed
            cmd.getCommand().getStatus().setStsId(StatusId.FAILED);
            return;
        }
        cmd.getCommand().getStatus().handleStsMsg(tempBuffer);
        cmd.incrementCmdReceivedCounter();
    }

    /**
     * adds the command to the queue, in order to be ready to send.
     *
     * @param cmd the command to be sent.
     */
    @Override
    public void processFecpCommand(FecpCommand cmd) {
        //add to list of commands to send as soon as possible
        //check if already in the list
        if(!this.mProcessCmds.contains(cmd))
        {
            this.mProcessCmds.add(cmd);
        }
        if(this.mCurrentThread == null || !this.mCurrentThread.isAlive())
        {
            this.mCurrentThread = new Thread(this);
            this.mCurrentThread.start();
        }
    }

    /**
     * implements runnable
     */
    @Override
    public void run() {
        //go through the list of commands and make the function calls to them.
        //yes this is an infinite loop.
        try
        {
            while(this.mProcessCmds.size() > 0)
            {
                FecpCommand tempCmd = this.mProcessCmds.get(0);
                this.sendCommand(tempCmd);
                //if there is a callback call it
                if(tempCmd.getCallback() != null
                        && (tempCmd.getCommand().getStatus().getStsId() == StatusId.DONE
                        || tempCmd.getCommand().getStatus().getStsId() == StatusId.FAILED))
                {
                    tempCmd.getCallback().msgHandler(tempCmd.getCommand());
                }
                //remove from this it will add it later when it needs to.
                this.mProcessCmds.remove(0);
            }
        }
        catch (Exception ex)
        {
            Log.e("thread error", ex.getMessage());
        }
    }
}
