/**
 * this strategy is to send the command that has been waiting the longest.
 * @author Levi.balling
 * @date 1/20/14
 * @version 1
 * this will send the last command that entered in. this runs with its own thread in the background.
 * It will send a command ever time that it receive a valid
 */
package com.ifit.sparky.fecp;

import com.ifit.sparky.fecp.communication.CommInterface;
import com.ifit.sparky.fecp.communication.CommReply;

public class FecpFifoCmdHandler extends FecpCmdHandler implements FecpCmdHandleInterface, CommReply {

    public FecpFifoCmdHandler(CommInterface commController)
    {
        super(commController);
    }

}
