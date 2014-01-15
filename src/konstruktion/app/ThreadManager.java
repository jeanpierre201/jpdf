package konstruktion.app;

import java.net.URL;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.bitmap.util.ImageCache;;

public class ThreadManager {
	
	ImageCache imagecache;
	PdfReader pdfreader;
	
	/*
     * Status indicators
     */
    static final int RENDER_FAILED = -1;
    static final int RENDER_STARTED = 1;
    static final int RENDER_COMPLETE = 2;
    static final int CREATE_FAILED = 3;
    static final int CREATE_STARTED = 4;
    static final int CREATE_COMPLETE = 5;
    static final int SAVE_FAILED = 6;
    static final int SAVE_STARTED = 7;
    static final int SAVE_COMPLETE = 8;
    static final int DECODE_STARTED = 9;
    static final int TASK_COMPLETE = 10;
    
 // Sets the size of the storage that's used to cache images
    private static final int IMAGE_CACHE_SIZE = 1024 * 1024 * 4;
    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 1;
    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;
    // Sets the initial threadpool size to 8
    private static final int CORE_POOL_SIZE = 8;
    // Sets the maximum threadpool size to 8
    private static final int MAXIMUM_POOL_SIZE = 8;       
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
 // A queue of Runnables for the image render pool
    private final BlockingQueue<Runnable> mRenderWorkQueue;
    // A queue of Runnables for the image store pool
    private final BlockingQueue<Runnable> mStoreWorkQueue;
    // A queue of ThreadManager tasks. Tasks are handed to a ThreadPool.
//    private final Queue<ThreadTask> mThreadTaskWorkQueue;
    // A managed pool of background render threads
    private final ThreadPoolExecutor mRenderThreadPool;
    // A managed pool of background store threads
    private final ThreadPoolExecutor mStoreThreadPool;
    // An object that manages Messages in a Thread
    private Handler mHandler;
    // A single instance of ThreadManager, used to implement the singleton pattern
    private static ThreadManager sInstance = null;

    // A static block that sets class fields
    static {
        
        // The time unit for "keep alive" is in seconds
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        
        // Creates a single static instance of ThreadManager
        sInstance = new ThreadManager();
    }
    
    /**
     * Constructs the work queues and thread pools used to download and decode images.
     */
    private ThreadManager() {

        /*
         * Creates a work queue for the pool of Thread objects used for downloading, using a linked
         * list queue that blocks when the queue is empty.
         */
    	mRenderWorkQueue = new LinkedBlockingQueue<Runnable>();

        /*
         * Creates a work queue for the pool of Thread objects used for decoding, using a linked
         * list queue that blocks when the queue is empty.
         */
    	mStoreWorkQueue = new LinkedBlockingQueue<Runnable>();

        /*
         * Creates a work queue for the set of of task objects that control Rendering and
         * decoding, using a linked list queue that blocks when the queue is empty.
         */
 //   	mThreadTaskWorkQueue = new LinkedBlockingQueue<ThreadTask>();

        /*
         * Creates a new pool of Thread objects for the Render work queue
         */
        mRenderThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mRenderWorkQueue);

        /*
         * Creates a new pool of Thread objects for the decoding work queue
         */
        mStoreThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mStoreWorkQueue);

        // Instantiates a new cache based on the cache size estimate
        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(pdfreader.cxt, "images");
        cacheParams.setMemCacheSizePercent(0.50f); // Set memory cache to 50% of app memory
        
        /*
         * Instantiates a new anonymous Handler object and defines its
         * handleMessage() method. The Handler *must* run on the UI thread, because it moves photo
         * Bitmaps from the PhotoTask object to the View object.
         * To force the Handler to run on the UI thread, it's defined as part of the PhotoManager
         * constructor. The constructor is invoked when the class is first referenced, and that
         * happens when the View invokes startRender. Since the View runs on the UI Thread, so
         * does the constructor and the Handler.
         */
        mHandler = new Handler(Looper.getMainLooper()) {

            /*
             * handleMessage() defines the operations to perform when the
             * Handler receives a new Message to process.
             */
            @Override
            public void handleMessage(Message inputMessage) {

                // Gets the thread task from the incoming Message object.
 //               ThreadTask ThreadTask = (ThreadTask) inputMessage.obj;

                // Sets an PhotoView that's a weak reference to the
                // input ImageView
  //              PhotoView localView = ThreadTask.getPhotoView();

                // If this input view isn't null
  //              if (localView != null) {

                    /*
                     * Gets the URL of the *weak reference* to the input
                     * ImageView. The weak reference won't have changed, even if
                     * the input ImageView has.
                     */
 //                   URL localURL = localView.getLocation();

                    /*
                     * Compares the URL of the input ImageView to the URL of the
                     * weak reference. Only updates the bitmap in the ImageView
                     * if this particular Thread is supposed to be serving the
                     * ImageView.
                     */
    //                if (photoTask.getImageURL() == localURL)

                        /*
                         * Chooses the action to take, based on the incoming message
                         */
                        switch (inputMessage.what) {

                            // If the Render has started, sets background color to dark green
                            case RENDER_STARTED:
  //                              localView.setStatusResource(R.drawable.imagerender);
                                break;

                            /*
                             * If the Render is complete, but the decode is waiting, sets the
                             * background color to golden yellow
                             */
                            case RENDER_COMPLETE:
                                // Sets background color to golden yellow
  //                              localView.setStatusResource(R.drawable.decodequeued);
                                break;
                            // If the decode has started, sets background color to orange
                            case DECODE_STARTED:
 //                               localView.setStatusResource(R.drawable.decodedecoding);
                                break;
                            /*
                             * The decoding is done, so this sets the
                             * ImageView's bitmap to the bitmap in the
                             * incoming message
                             */
                            case TASK_COMPLETE:
  //                              localView.setImageBitmap(photoTask.getImage());
  //                              recycleTask(photoTask);
                                break;
                            // The Render failed, sets the background color to dark red
                            case RENDER_FAILED:
 //                               localView.setStatusResource(R.drawable.imagedownloadfailed);
                                
                                // Attempts to re-use the Task object
  //                              recycleTask(photoTask);
                                break;
                            default:
                                // Otherwise, calls the super method
                                super.handleMessage(inputMessage);
                        }
                }
            
        };

}

}
