package com.ifit.sparky.fecp.communication;

import java.nio.ByteBuffer;

/**
 * Created by ryan.tensmeyer on 1/9/14.
 */
public interface CommReply {

    void stsMsgHandler(ByteBuffer buff);


}
