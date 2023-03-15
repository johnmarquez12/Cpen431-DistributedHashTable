package com.g10.CPEN431.A9;


class Codes {
    static class Commands {
        static final int PUT = 0x01;
        static final int GET = 0x02;
        static final int REMOVE = 0x03;
        static final int SHUTDOWN = 0x04;
        static final int WIPEOUT = 0x05;
        static final int IS_ALIVE = 0x06;
        static final int GET_PID = 0x07;
        static final int GET_MEMBERSHIP_COUNT = 0x08;
        static final int INTERNAL_REQUEST = 0x55;
    }

    static class Errs {
        static final int SUCCESS = 0x00;

        /**
         * Non-existent key requested in a get or delete operation
         */
        static final int KEY_DOES_NOT_EXIST = 0x01;

        /**
         * Returned when there is no space left to store data for an additional PUT.  Operations that do not consume new space (e.g., GET, REMOVE) would generally not (or ‘never’ if you can guarantee it) return this error code.
         */
        static final int OUT_OF_SPACE = 0x02;

        /**
         * Temporary system overload. The system is operational but decides to refuse
         * the operation due to temporary overload that consumes internal resources
         * (e.g., full internal buffers, too many in-flight requests).  Otherwise said,
         * this error code indicates that, if nothing happens and no new requests are accepted
         * for a while, the system will likely to return to a functioning state. This is
         * a signal so that a well-behaved client will wait for some overloadWaitTime
         * (in milliseconds) and either continue or retry the same operation.
         */
        static final int TEMP_SYS_OVERLOAD = 0x03;

        /**
         * Internal KVStore failure - a catch-all for all other situations where your KVStore has determined that something is wrong, and it can not recover from the failure.
         */
        static final int KVSTORE_FAIL = 0x04;

        /**
         * Unrecognized command.
         */
        static final int CMD_UNKNOWN = 0x05;

        /**
         * Invalid key: the key length is invalid (e.g., greater than the maximum allowed length).
         */
        static final int KEY_INVALID = 0x06;

        /**
         * Invalid value: the value length is invalid (e.g., greater than the maximum allowed length).
         */
        static final int VALUE_INVALID = 0x07;
    }

}