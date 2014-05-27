/**
 * This interface will handle all the different sends and receives to the device.
 * @author Levi.balling
 * @date 1/20/14
 * @version 1
 * This will have the requirements for what to send and receive for each command.
 */
package com.ifit.sparky.fecp;

import com.ifit.sparky.fecp.communication.CommInterface;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.testingUtil.CmdInterceptor;

public interface FecpCmdHandleInterface {

    /**
     * Gets the comm controller used for the system.
     * @return the comm controller
     */
    CommInterface getCommController();

    /**
     * Adds the command to the list to be sent
     * @param cmd the command to be sent.
     */
    void addFecpCommand(FecpCommand cmd)throws Exception;

    /**
     * Adds the command to the list to be sent
     *
     * @param cmd the command to be sent.
     * @param highPriority the command to be sent.
     */
    void addFecpCommand(FecpCommand cmd, boolean highPriority) throws Exception;


    /**
     * adds the command to the queue, in order to be ready to send.
     * @param cmd the command to be sent.
     */
    void processFecpCommand(FecpCommand cmd);

    /**
     * adds the command to the queue, in order to be ready to send.
     * @param cmd the command to be sent.
     */
    void processFecpCommand(FecpCommand cmd, boolean highPriority);

    /**
     * Removes the command if it matches the Command id and the Device ID.
     * If there are multiples in the command list it will remove both of them.
     * @return true if it removed the element
     */
    boolean removeFecpCommand(DeviceId devId, CommandId cmdId);

    /**
     * Removes the command if it matches the Command id and the Device ID.
     * If there are multiples in the command list it will remove both of them.
     * @return true if it removed the element
     */
    boolean removeFecpCommand(FecpCommand cmd);

    /**
     * Sends the command to the Fecp Communication Controller
     * @param cmd the command to the Device
     */
    void sendCommand(FecpCommand cmd)throws Exception;

    /**
     * This will add the interceptor for testing to validate commands going to the
     * System.
     * @param interceptor interceptor for the system
     */
    void addInterceptor(CmdInterceptor interceptor);

}
