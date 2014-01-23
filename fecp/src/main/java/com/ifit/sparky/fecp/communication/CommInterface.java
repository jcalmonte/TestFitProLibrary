/**
 * Interface for all communication types (e.g. usb, uart, blue tooth).
 * @author Ryan.Tensmeyer
 * @date 12/10/13
 * @version 1
 * Release Date
 * @date 12/10/13
 */

package com.ifit.sparky.fecp.communication;

import java.nio.ByteBuffer;

public interface CommInterface {

    void sendCmdBuffer(ByteBuffer buff);
    ByteBuffer getStsBuffer();

    /**
     * sends the command and waits for the reply to handle the buffer
     * @param buff the command buffer to send
     * @return
     */
    ByteBuffer sendAndRecieveCmd(ByteBuffer buff);
    void setStsHandler(CommReply handler);

}
