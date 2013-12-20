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
    private CommandCallback mCallback;
    private int mTimeout;
    private int mFrequency;//time in between each call.
    private int mCmdSentCounter;
    private int mCmdReceivedCounter;

    /**
     * default constructor
     */
    public FecpCommand() throws Exception
    {
        this.fecpInitializer(new Device(), new Command(), null, 0,0);
    }

    /**
     * Constructor for a simple command.
     * @param dev the device to send the command to.
     * @param cmd the command for the device.
     * @param callback the callback after it is done.
     */
    public FecpCommand(Device dev, Command cmd, CommandCallback callback)
    {
        this.fecpInitializer(dev, cmd, callback, 0,0);
    }

    /**
     * Constructs a command with a timeout
     * @param dev the device to send the command to.
     * @param cmd the command for the device.
     * @param callback the callback after it is done.
     * @param timeout the length of time it should receive a response.
     */
    public FecpCommand(Device dev, Command cmd, CommandCallback callback, int timeout)
    {
        this.fecpInitializer(dev, cmd, callback, timeout,0);
    }
    /**
     * Constructs a command with a timeout
     * @param dev the device to send the command to.
     * @param cmd the command for the device.
     * @param callback the callback after it is done.
     * @param timeout the length of time it should receive a response.
     * @param frequency How frequent messages should be sent.
     */
    public FecpCommand(Device dev, Command cmd, CommandCallback callback, int timeout, int frequency)
    {
        this.fecpInitializer(dev, cmd, callback, timeout, frequency);
    }


    /**
     * Initializes all the values
     * @param dev the device to send the command to.
     * @param cmd the command for the device.
     * @param callback the callback after it is done.
     * @param timeout the length of time it should receive a response.
     * @param frequency How frequent messages should be sent.
     */
    private void fecpInitializer(Device dev,
                                 Command cmd,
                                 CommandCallback callback,
                                 int timeout,
                                 int frequency)
    {
        this.mDevice = dev;
        this.mCommand = cmd;
        this.mCallback = callback;
        this.mTimeout = timeout;
        this.mFrequency = frequency;
        this.mCmdSentCounter = 0;
        this.mCmdReceivedCounter = 0;
    }

    /*************
     *  GETTERS
     ************/

    /**
     * Gets the commands device
     * @return the device of the command
     */
    public Device getDevice()
    {
        return this.mDevice;
    }

    /**
     * Gets the Command to be sent
     * @return the command
     */
    public Command getCommand()
    {
        return this.mCommand;
    }

    /**
     * Gets the callback for the command
     * @return the callback
     */
    public CommandCallback getCallback()
    {
        return this.mCallback;
    }

    /**
     * Gets the timeout of the command
     * @return the timeout
     */
    public int getTimeout()
    {
        return this.mTimeout;
    }

    /**
     * Gets the frequency of the command
     * @return the frequency
     */
    public int getFrequency() {
        return this.mFrequency;
    }

    /**
     * Gets the number of times the command was sent.
     * @return the number of sends
     */
    public int getCmdSentCounter() {
        return this.mCmdSentCounter;
    }

    /**
     * gets the number of responds to this message
     * @return the receive count
     */
    public int getCmdReceivedCounter() {
        return this.mCmdReceivedCounter;
    }

    /*************
     *  SETTERS
     ************/

    /**
     * Sets the device for the command
     * @param mDevice the device
     */
    public void setDevice(Device mDevice) {
        this.mDevice = mDevice;
    }

    /**
     * Sets the command for the send
     * @param mCommand the command
     */
    public void setCommand(Command mCommand) {
        this.mCommand = mCommand;
    }

    /**
     * Sets the callback for the send
     * @param mCallback the callback
     */
    public void setCallback(CommandCallback mCallback) {
        this.mCallback = mCallback;
    }

    /**
     * Sets the timeout for the command
     * @param mTimeout the timeout
     */
    public void setTimeout(int mTimeout) {
        this.mTimeout = mTimeout;
    }

    /**
     * sets the Frequency of the command sends
     * @param mFrequency the frequency of sends
     */
    public void setFrequency(int mFrequency) {
        this.mFrequency = mFrequency;
    }

    /**
     * sets the number of times the command was sent.
     * @param mCmdSentCounter the sent counter
     */
    public void setCmdSentCounter(int mCmdSentCounter) {
        this.mCmdSentCounter = mCmdSentCounter;
    }

    /**
     * Sets the number of times the command received a response
     * @param mCmdReceivedCounter the receive counter
     */
    public void setCmdReceivedCounter(int mCmdReceivedCounter) {
        this.mCmdReceivedCounter = mCmdReceivedCounter;
    }
}
