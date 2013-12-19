/**
 * A command to issue to the System.
 * @author Levi.Balling
 * @date 12/19/13
 * @version 1
 * This will hold all the values of the fecp command, that are needed to send and receive data.
 */
package com.ifit.sparky.fecp;

import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.device.Device;

public class FecpCommand {

    //cmd and device
    private Device mDevice;
    private Command mCommand;//this holds the command and the status.
    private int mTimeout;
    private int mFrequency;//time in between each call.
    private int mCmdSentCounter;
    private int mCmdReceivedCounter;
    //todo add a callback

    /**
     * default constructor
     */
    public FecpCommand()
    {
        this.mDevice = new Device();
        this.mCommand = new Command();
        this.mTimeout = 0;
        this.mFrequency = 0;
        this.mCmdSentCounter = 0;
        this.mCmdReceivedCounter = 0;
    }

    public FecpCommand(Device dev, Command cmd)
}
