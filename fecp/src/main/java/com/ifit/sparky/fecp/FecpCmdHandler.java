/**
 * This is the super class for handling which commands to send and to receive.
 * @author Levi.balling
 * @date 1/20/14
 * @version 1
 * handles the common items as far as what to send and what to receive.
 */
package com.ifit.sparky.fecp;

import com.ifit.sparky.fecp.communication.CommInterface;
import com.ifit.sparky.fecp.communication.CommReply;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.Status;
import com.ifit.sparky.fecp.interpreter.status.StatusId;

import java.nio.ByteBuffer;

public class FecpCmdHandler extends Thread implements FecpCmdHandleInterface, CommReply {

    private CommInterface mCommController;
    private FecpCommand mLastestSentCmd;
    private FecpCmdList mCmds;

    public FecpCmdHandler(CommInterface commController)
    {
        super();//initializes the thread
        this.mCommController = commController;
        this.mCmds = new FecpCmdList();
    }

    @Override
    public void run() {
        super.run();
        //start my own process handling
    }

    /**
     * Sets the Comm controller for the system.
     * @param CommController the comm controller
     */
    @Override
    public void setCommController(CommInterface CommController) {
        //initialized outside of  this function
        this.mCommController = CommController;
        this.mCommController.setStsHandler(this);
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
    public void addFecpCommand(FecpCommand cmd)
    {
        this.mCmds.add(cmd);
    }

    /**
     * Removes the command if it matches the Command id and the Device ID.
     * If there are multiples in the command list it will remove both of them.
     *
     * @param devId
     * @param cmdId
     * @return true if it removed the element
     */
    @Override
    public boolean removeFecpCommand(DeviceId devId, CommandId cmdId) {

        boolean result = false;
        for(FecpCommand cmd : this.mCmds)
        {
            if(cmd.getCommand().getCmdId() == cmdId && cmd.getCommand().getDevId() == devId)
            {
                this.mCmds.remove(cmd);
                result = true;
            }
        }
        return result;
    }

    /**
     * Sends the command to the Fecp Communication Controller
     *
     * @param cmd the command to the Device
     */
    @Override
    public void sendCommand(FecpCommand cmd) throws Exception{
        this.mLastestSentCmd = cmd;
        this.mLastestSentCmd.incrementCmdSentCounter();
        this.mCommController.sendCmdBuffer(cmd.getCommand().getCmdMsg());
    }

    @Override
    public void stsMsgHandler(ByteBuffer buff) {
        Status msgStatus;
        CommandCallback cmdCallback;
        try
        {
            msgStatus = this.mLastestSentCmd.getCommand().getStatus();

            msgStatus.handleStsMsg(buff);
            this.mLastestSentCmd.incrementCmdReceivedCounter();
            //check if we need to call the callback
            if(msgStatus.getStsId()!= StatusId.IN_PROGRESS)
            {
                //call callback
                cmdCallback = this.mLastestSentCmd.getCallback();
                cmdCallback.msgHandler(this.mLastestSentCmd.getCommand());
                //set flag that it was sent
            }
        }
        catch (Exception ex)
        {
            //check the exception and determine whether to throw it or hold it
        }
        this.run();//call the next command

    }

    //
//    /**
//     * implements runnable
//     */
//    @Override
//    public void run() {
//
//        //go through the list of commands and make the function calls to them.
//        //yes this is an infinite loop.
//        try
//        {
//            this.sendCommand(this.mCmds.get(0));
//        }
//        catch (Exception ex)
//        {
//
//        }
//
//    }
}
