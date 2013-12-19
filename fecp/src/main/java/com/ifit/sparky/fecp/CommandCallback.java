/**
 * Callback to call after a reply is received.
 * @author Levi.Balling
 * @date 12/19/13
 * @version 1
 * you have to pass a callback in to receive.
 */
package com.ifit.sparky.fecp;

import com.ifit.sparky.fecp.interpreter.command.Command;

public interface CommandCallback {

    /**
     * Handles the reply from the device
     * @param cmd the command that was sent.
     */
    void msgHandler(Command cmd);
}
