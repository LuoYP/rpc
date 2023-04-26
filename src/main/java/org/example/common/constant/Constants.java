package org.example.common.constant;

import java.util.concurrent.atomic.AtomicLong;

public class Constants {

    public static final String DOT = ".";

    public static final String DASHED = "-";

    public static final AtomicLong ID = new AtomicLong(0);

    /** RPC消息类型,封装在RpcHeader中 */
    public static final byte COMMENT = 0x01;

    public static final byte FILE = 0x02;

    public static final byte HEART_BEAT = 0x03;
}
