/**
 * Command handler type, specifies the type of communication that the system will do.
 * @author Levi.balling
 * @date 1/20/14
 * @version 1
 * The type specifies the way the commands will be handled,
 * and a specific system may have a better option.
 */
package com.ifit.sparky.fecp;

public enum CmdHandlerType {
    FIFO_PRIORITY();

    /**
     * Constructor for the command handler type.
     * this will be the type of command handling that will go on for a specific system
     */
    CmdHandlerType()
    {

    }
}
