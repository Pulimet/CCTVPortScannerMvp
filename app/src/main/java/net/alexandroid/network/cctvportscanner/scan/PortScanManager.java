package net.alexandroid.network.cctvportscanner.scan;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PortScanManager {

    private static PortScanManager sInstance;

    // Gets the number of available cores (not always the same as the maximum number of cores)
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 5;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    // A queue of Runnables
    private final BlockingQueue<Runnable> mBlockingQueue;


    private ThreadPoolExecutor mThreadPoolExecutor;


    static {
        // Creates a single static instance of PhotoManager
        sInstance = new PortScanManager();
    }

    private PortScanManager() {
        // Instantiates the queue of Runnables as a LinkedBlockingQueue
        mBlockingQueue = new LinkedBlockingQueue<>();

        // Creates a thread pool manager
        mThreadPoolExecutor = new ThreadPoolExecutor(
                NUMBER_OF_CORES,       // Initial pool size
                NUMBER_OF_CORES,       // Max pool size
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                mBlockingQueue);
    }


    public void startScanTask(String host, int port, ScanResult mScanResult) {
        // Adds a download task to the thread pool for execution
        mThreadPoolExecutor.execute(new PortScanRunnable(host, port, mScanResult));
    }

    public static PortScanManager getInstance() {
        return sInstance;
    }
}


