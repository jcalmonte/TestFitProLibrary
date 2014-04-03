/**
 * BriefDiscription.
 * @author Levi.Balling
 * @date 4/2/2014
 * @version 1
 * Details.
 */
package com.ifit.sparky.fecp.error;

import java.nio.ByteBuffer;

public interface ErrorReporting {

    //sends error Function

    /**
     * Sends the buffer that matches the online profile for Error messages
     * @param buffer buffer that is pointing to the start of the message.
     */
    void sendErrorObject(ByteBuffer buffer);

}
