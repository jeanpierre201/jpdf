package industriekatalog.app;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import industriekatalog.app.BuildConfig;


import android.bitmap.util.DiskLruCache;
import android.bitmap.util.ImageCache;
import android.bitmap.util.ImageResizer;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;


public class ImageFetcherPdf extends ImageResizer {
	
	private static final String TAG = "ImageFetcherPdf";
    private static final int CACHE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final String CACHE_DIR = "/neleso/temp";

    private DiskLruCache mDiskCache;
    private File mCacheDir;
    private boolean mDiskCacheStarting = true;
    private final Object mDiskCacheLock = new Object();
    private static final int DISK_CACHE_INDEX = 0;
    
    NelesoApp neleso;
    /**
     * Initialize providing a target image width and height for the processing images.
     *
     * @param context
     * @param imageWidth
     * @param imageHeight
     */
	public ImageFetcherPdf(Context context, int imageWidth, int imageHeight) {
		super(context, imageWidth, imageHeight);
		init(context);
	}
	
	
	/**
     * Initialize providing a single target image size (used for both width and height);
     *
     * @param context
     * @param imageSize
     */	
	public ImageFetcherPdf(Context context, int imageSize) {
		super(context, imageSize);
	    init(context);
	}
	
	 private void init(Context context) {
		 
		 String storage = Environment.getExternalStorageState();
	     Log.i("System out", "" + storage);
	     File rootsd = Environment.getExternalStorageDirectory();
	     Log.d("ROOT PATH", "" + rootsd.getAbsolutePath());
		 
		 mCacheDir = ImageCache.getDiskCacheDir(context, rootsd.toString() + CACHE_DIR);	
	}
	 
	 @Override
	    protected void initDiskCacheInternal() {
	        super.initDiskCacheInternal();
	        initDiskCache();
	    }


	
	private void initDiskCache() {
        if (!mCacheDir.exists()) {
            mCacheDir.mkdirs();
        }
        synchronized (mDiskCacheLock) {
            if (ImageCache.getUsableSpace(mCacheDir) > CACHE_SIZE) {
                try {
                    mDiskCache = DiskLruCache.open(mCacheDir, 1, 1, CACHE_SIZE);
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "cache initialized");
                    }
                } catch (IOException e) {
                    mDiskCache = null;
                }
            }
            mDiskCacheStarting = false;
            mDiskCacheLock.notifyAll();
        }
    }
	
    @Override
    protected void clearCacheInternal() {
        super.clearCacheInternal();
        synchronized (mDiskCacheLock) {
            if (mDiskCache != null && !mDiskCache.isClosed()) {
                try {
                    mDiskCache.delete();
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "cache cleared");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "clearCacheInternal - " + e);
                }
                mDiskCache = null;
                mDiskCacheStarting = true;
                initDiskCache();
            }
        }
    }
    
    @Override
    protected void flushCacheInternal() {
        super.flushCacheInternal();
        synchronized (mDiskCacheLock) {
            if (mDiskCache != null) {
                try {
                    mDiskCache.flush();
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "cache flushed");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "flush - " + e);
                }
            }
        }
    }
    
    @Override
    protected void closeCacheInternal() {
        super.closeCacheInternal();
        synchronized (mDiskCacheLock) {
            if (mDiskCache != null) {
                try {
                    if (!mDiskCache.isClosed()) {
                        mDiskCache.close();
                        mDiskCache = null;
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "cache closed");
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "closeCacheInternal - " + e);
                }
            }
        }
    }
	
    /**
     * The main process method, which will be called by the ImageWorker in the AsyncTask background
     * thread.
     *
     * @param data The data to load the bitmap, in this case, a regular Uri address
     * @return The downloaded and resized bitmap
     */
    private Bitmap processBitmap(String data) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "processBitmap - " + data);
        }

        final String key = ImageCache.hashKeyForDisk(data);
        FileDescriptor fileDescriptor = null;
        FileInputStream fileInputStream = null;
        DiskLruCache.Snapshot snapshot;
        synchronized (mDiskCacheLock) {
            // Wait for disk cache to initialize
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException e) {}
            }

            if (mDiskCache != null) {
                try {
                    snapshot = mDiskCache.get(key);
                    if (snapshot == null) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "processBitmap, not found in cache, reading...");
                        }
                        DiskLruCache.Editor editor = mDiskCache.edit(key);
                        if (editor != null) {
                            if (readUriToStream(data,
                                    editor.newOutputStream(DISK_CACHE_INDEX))) {
                                editor.commit();
                            } else {
                                editor.abort();
                            }
                        }
                        snapshot = mDiskCache.get(key);
                    }
                    if (snapshot != null) {
                        fileInputStream =
                                (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
                        fileDescriptor = fileInputStream.getFD();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "processBitmap - " + e);
                } catch (IllegalStateException e) {
                    Log.e(TAG, "processBitmap - " + e);
                } finally {
                    if (fileDescriptor == null && fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {}
                    }
                }
            }
        }

        Bitmap bitmap = null;
        if (fileDescriptor != null) {
            bitmap = decodeSampledBitmapFromDescriptor(fileDescriptor, mImageWidth,
                    mImageHeight, getImageCache());
        }
        if (fileInputStream != null) {
            try {
                fileInputStream.close();
            } catch (IOException e) {}
        }
        return bitmap;
    }
    
    @Override
	public Bitmap processBitmap(Object data) {
        return processBitmap(String.valueOf(data));
    }

    /**
     * Read a bitmap from a Uri and write the content to an output stream.
     *
     * @param uriString The Uri to fetch
     * @return true if successful, false otherwise
     */
	
    public boolean readUriToStream(String data, OutputStream outputStream) {

        BufferedOutputStream out = null;
        BufferedInputStream in = null;

        try {
        	
        	InputStream isFile = new FileInputStream(data.toString());
            in = new BufferedInputStream(isFile, IO_BUFFER_SIZE);
            out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);

            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (final IOException e) {
            Log.e(TAG, "Error in readBitmap - " + e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {}
        }
        return false;
    }
	 



}
