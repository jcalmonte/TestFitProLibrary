/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.interpreter.command;

import java.nio.ByteBuffer;

public interface CommandInterface {
    /**
     * Gets the command message for all commands that extend this class.
     * When they want to get the command they have to get the command.
     * @return the Command structured to be ready to send over the usb.
     */
     ByteBuffer getCmdMsg() throws Exception;
}
