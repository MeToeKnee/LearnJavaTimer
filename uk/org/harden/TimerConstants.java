package uk.org.harden;

final class TimerConstants {

    static final String ACTION_DELETE = "Delete";
    static final String ACTION_RESET = "Reset";
    static final String ACTION_START = "Start";
    static final String ACTION_STOP = "Stop";
    static final String ACTION_ADD = "Add";

    static final String ACTION_DONE = "Done";

    static final String NEW_LINE = System.getProperty("line.separator");

    static final int DB_RETRIES = 3;
    static final int DB_RETRY_DELAY = 2000;

    static final int TCP_RETRIES = 3;
    static final int TCP_RETRY_DELAY = 2000;

    static final int TCPPortNumber = 2020;
}
