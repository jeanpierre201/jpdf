package industriekatalog.app;


import industriekatalog.app.ImageFetcherPdf;
import industriekatalog.app.Images;
import industriekatalog.app.ShowPdf.MyThread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.io.InputStream;

import neleso.db.DataBase_Adapter;
import neleso.storefront.XMLHandler;
import neleso.utils.StringUtils;
import net.sf.andpdf.nio.ByteBuffer;
import net.sf.andpdf.refs.HardReference;


import industriekatalog.app.R;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.decrypt.PDFAuthenticationFailureException;
import com.sun.pdfview.font.PDFFont;
import android.view.View;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bitmap.util.ImageCache;
import android.bitmap.util.ImageWorker;
import android.bitmap.util.Utils;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.library.imagezoom.ImageViewTouchBase;
import android.library.imagezoom.RotateBitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.util.LruCache;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;


@SuppressLint("ValidFragment")
public class PdfReader extends FragmentActivity {
	
	private static final String TAG = "PdfReader";
	
	// using the Fragment	
	private static final String IMAGE_CACHE_DIR = "images";
    public static final String EXTRA_IMAGE = "extra_image";
    public static ImageFetcherPdf mImageFetcher;
    public static String[] thumbString;
    static ImagePagerAdapter mAdapter;
    static ImagePagerAdapterLand mAdapterLand;
	public static CustomViewPager mPager;
	public static CustomViewPager mPagerLand;
    int longest;
    int sizeWidht;

	public static Context cxt;
	NelesoApp neleso;
	
 // files - directory Variables	
	public String pdffilename;
	public static PDFFile mPdfFile;
	public int mPage;
    public File mTmpFile;
    public static File mydirThumb;
    public static File mydirHD;
    public PDFPage mPdfPage; 
    public static Uri[] thumb;
    public String path;
    
 // threads - procceses
    public Thread backgroundThread;   
    public static Handler uihandler;
    public static Handler updateBarHandler;
    private static ProgressDialog barProgressDialog;
      
 // UI - ViewPager   
    
    ImageView instantView2;
    ImageView instantView3;
    FrameLayout imageFrame;
    ImageView instantView;
    public Boolean temp;
	public static int or;
	float scale;
    public int pos;
	public static int total_pages;	
	public static int pages_created;
	public static int total_pages_land;	
	public static int first_thumb;
	public static int imgWidth = 0;

	public static ViewPager viewPager_LandScape;
	// render PDF 
 
    float zoom = 1.0f;  //3   
    public int HD;
	boolean bigger; 

	
	// On Process ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	
	ImageButton toolbarheader;
	public int item;
	public Bitmap[] bit; 
	
	public static float thmbWidht;
    public static float thmbHeight;

	public static boolean isZoom;
	public static Boolean start;
	public static int CurrentPage;
	public static int CurrentPageOnpageChange;

	public static String[] pageString;	
	public String DIR;
	public File rootsd;
	public static String folder;
	public String[] OrderedFiles;
	public File[] files;
		

	// TRYING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {	
        super.onCreate(savedInstanceState);
        
        cxt = this;
        or = getResources().getConfiguration().orientation;
        Intent intent = getIntent();
   
        // Fetch screen height and width, to use as our max size when loading images as this
        // activity runs full screen

        scale = getApplicationContext().getResources().getDisplayMetrics().density; 
        Log.i(TAG, "width " + NelesoApp.width);
        HD = NelesoApp.width;      
        
        // For this sample we'll use half of the longest width to resize our images. As the
        // image scaling ensures the image is larger than this, we should be left with a
        // resolution that is appropriate for both portrait and landscape. For best image quality
        // we shouldn't divide by 2, but this will use more memory and require a larger memory
        // cache.
        longest = (NelesoApp.heigh > NelesoApp.width ? NelesoApp.heigh : NelesoApp.width);

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.50f); // Set memory cache to 50% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcherPdf(this, longest);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(false);
        
      	mImageFetcher.clearCacheInternal();
               
        // Declare and start the Thread Manager
        int numCpus;
  		numCpus = Runtime.getRuntime().availableProcessors();
  		Log.i(TAG, "numCpus " + numCpus);
             
        if (intent != null) {
        	if ("android.intent.action.VIEW".equals(intent.getAction())) {
    			pdffilename = storeUriContentToFile(intent.getData());    			
        	}
        	else {
        		pdffilename = getIntent().getStringExtra(NelesoApp.EXTRA_PDFFILENAME);
                Log.d(TAG, "pdffilename " + pdffilename);
        		}
        }
        
        try {
			parsePDF(pdffilename);
			
		} catch (PDFAuthenticationFailureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        thumb = new Uri[total_pages];
        thumbString = new String[total_pages];
        pageString = new String[total_pages];
        
        mPdfPage = mPdfFile.getPage(1, true); 
        thmbWidht = mPdfPage.getWidth()/3;
        thmbHeight = mPdfPage.getHeight()/3;
        
        // Creates thumbnails folder and pdf2image temporal buffer
        
        String[] splits = pdffilename.split("/");
        String[] folderWithFormat = splits[4].split("\\.");
        folder = folderWithFormat[0];
        Log.d(TAG, "folder " + folder);     
        DIR = "/neleso/"+folder;
        Log.d(TAG, "DIR " + DIR);

        
       
        String DIR2 = "/neleso/pdf2image";
        rootsd = Environment.getExternalStorageDirectory();	
        mydirHD = new File(rootsd.toString() + DIR2);
        if (!mydirHD.exists()) {
        	mydirHD.mkdir();
        }  
        
        
        if (or == 1) {
            
        	setContentView(R.layout.pdfreader);
         	mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), total_pages, cxt);
        	mPager = (CustomViewPager) findViewById(R.id.pager);   	
        	mPager.setAdapter(mAdapter);
            init();
            
        } else {

        	setContentView(R.layout.pdfreaderland);
         	mAdapterLand = new ImagePagerAdapterLand(getSupportFragmentManager(), total_pages_land, cxt);
        	mPagerLand = (CustomViewPager) findViewById(R.id.pagerland);
//        	mPager.setPageMargin((int) getResources().getDimension(R.dimen.image_detail_pager_margin)); 
//        	mPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.viewpager_margin));
//          mPager.setOffscreenPageLimit(0);
        	mPagerLand.setAdapter(mAdapterLand);
        	mImageFetcher.clearCacheInternal();
            init();          
        }
                   
        }
    
    
    @Override
   	protected void onResume() {
       	super.onResume(); 	
             	
       	
           if (or == 1) {
           	
        	    setContentView(R.layout.pdfreader);
            	mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), total_pages, cxt);
            	mPager = (CustomViewPager) findViewById(R.id.pager);
            	mPager.setAdapter(mAdapter);
            	mPager.setOnPageChangeListener(new OnPageChangeListener() {
            	   
					@Override
					public void onPageScrollStateChanged(int arg0) {}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {}

					@Override
					public void onPageSelected(int arg0) {
						
						start = true;
						
						CurrentPageOnpageChange = arg0;
						Log.d(TAG, "CurrentPage onPageScrollStateChanged " + CurrentPageOnpageChange);  
						
						 new Thread(new Runnable() {
					   	      public void run() {  	    	  

					   	   	     Log.d(TAG, "CurrentPage CurrentPageOnpageChange " + CurrentPageOnpageChange);
					   	         renderPageBackground(CurrentPageOnpageChange);
					   	      }

							private void renderPageBackground(
									int currentPageOnpageChange) {
							      try {
						    	        PdfReader.isZoom = false;    	  
						    	        SetImageBitmap.currentThread().interrupt();	
						    	        Log.i(TAG, "position antes:  " + pos);
						    	        SetImageBitmap SIB = new SetImageBitmap(pos, NelesoApp.width, NelesoApp.heigh);
						    	        SIB.run();
						    	        Log.i(TAG, "position despues:  " + pos);
								
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
								
							}
					   	    }).start();
						
					}
            	});          	
             	mImageFetcher.clearCacheInternal();
          	
            	
           } else {
        	            setContentView(R.layout.pdfreaderland);
        	            mAdapterLand = new ImagePagerAdapterLand(getSupportFragmentManager(), total_pages_land, cxt);
        	         	mPagerLand = (CustomViewPager) findViewById(R.id.pagerland);
        	          	mPagerLand.setAdapter(mAdapterLand);
        	          	mPagerLand.setOnPageChangeListener(new OnPageChangeListener() {
        	            	   
        					@Override
        					public void onPageScrollStateChanged(int arg0) {}

        					@Override
        					public void onPageScrolled(int arg0, float arg1, int arg2) {
        			//			PdfReaderFragmentLand.mProgress.setVisibility(View.VISIBLE);
        					}

        					@Override
        					public void onPageSelected(int arg0) {
        						
        						start = true;
        						SetImageBitmapLand.currentThread().interrupt();	
        						
        						 
        						CurrentPageOnpageChange = arg0;
        						Log.d(TAG, "CurrentPage onPageScrollStateChanged " + CurrentPageOnpageChange);  
        						
        						 new Thread(new Runnable() {
        					   	      public void run() {  	    	  

        					   	   	     Log.d(TAG, "CurrentPage CurrentPageOnpageChange " + CurrentPageOnpageChange);
        					   	         renderPageBackground(CurrentPageOnpageChange);
        					   	      }

        							private void renderPageBackground(
        									int currentPageOnpageChange) {
        							      try {
        							    	
        							    	  PdfReader.isZoom = false;    	  
      						    	          SetImageBitmapLand.currentThread().interrupt();	
      						    	          Log.i(TAG, "position antes:  " + currentPageOnpageChange);
      						    	          SetImageBitmapLand SIB = new SetImageBitmapLand(currentPageOnpageChange, NelesoApp.width, NelesoApp.heigh);
      						    	          SIB.run();
      						    	          Log.i(TAG, "position despues:  " + currentPageOnpageChange);
        								
        							} catch (Exception e) {
        								// TODO Auto-generated catch block
        								e.printStackTrace();
        							}
        								
        							}
        					   	    }).start();
        						
        					}
                    	});          	
                     	mImageFetcher.clearCacheInternal();
              
           }
       }
    
    
//    // Save the thread
//    @Override
//    public Object onRetainNonConfigurationInstance() { 	 
//           return backgroundThread;
//    }
    
 // dismiss dialog if activity is destroyed
    @Override
    protected void onDestroy() {
    	
           
           if (mTmpFile != null) {
        		mTmpFile.delete();
        		mTmpFile = null;
        	}
        	
        	  if (mydirThumb != null) {
        		  mydirThumb.delete();
        		  mydirThumb = null;
          	}
        	  if (mydirHD != null) {
        		  mydirHD.delete();
        		  mydirHD = null;
        		  
          	} 
        	  
        	  mImageFetcher.clearCacheInternal();
        	  mImageFetcher.closeCache();
           super.onDestroy();
    }
    
    @Override
   	protected void onStop() {
	//	  mydir.delete();
	//	  mydir2.delete();
       	super.onStop(); 
    }
    
    @Override
   	protected void onPause() {
    	SetImageBitmap.currentThread().interrupt();
    	SetImageBitmapLand.currentThread().interrupt();
    	mImageFetcher.flushCache();
    	mImageFetcher.flushCache();
       	super.onPause(); 
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
      // Save UI state changes to the savedInstanceState.
      // This bundle will be passed to onCreate if the process is
      // killed and restarted. 
    	SetImageBitmap.currentThread().interrupt();
    	SetImageBitmapLand.currentThread().interrupt();
 //   	mAdapter.saveState();
 //   	mAdapterLand.saveState();
 //     	mPager.saveHierarchyState(null);

      	//    	swipAdapter_LandScape.saveState();  
//    	viewPager_LandScape.saveHierarchyState(null);
    	    	
      super.onSaveInstanceState(savedInstanceState);
    }
    
    @Override
    public void onConfigurationChanged(Configuration savedInstanceState) { 
        super.onConfigurationChanged(savedInstanceState);     
        
        if (savedInstanceState.orientation == Configuration.ORIENTATION_PORTRAIT) {
      	   
        	setContentView(R.layout.pdfreader);
        	mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), total_pages, cxt);
        	mPager = (CustomViewPager) findViewById(R.id.pager);
        	mPager.setAdapter(mAdapter);
        	mAdapter.getItemPosition(mAdapter.saveState());
 //           mAdapter.notifyDataSetChanged();
            mAdapter.getCount();
        	mPager.setOnPageChangeListener(new OnPageChangeListener() {
         	   
				@Override
				public void onPageScrollStateChanged(int arg0) {}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {}

				@Override
				public void onPageSelected(int arg0) {
					
					start = true;
					
					CurrentPageOnpageChange = arg0;
					Log.d(TAG, "CurrentPage onPageScrollStateChanged " + CurrentPageOnpageChange);  
					
					 new Thread(new Runnable() {
				   	      public void run() {  	    	  

				   	   	     Log.d(TAG, "CurrentPage CurrentPageOnpageChange " + CurrentPageOnpageChange);
				   	         renderPageBackground(CurrentPageOnpageChange);
				   	      }

						private void renderPageBackground(
								int currentPageOnpageChange) {
						      try {
					    	        PdfReader.isZoom = false;    	  
					    	        SetImageBitmap.currentThread().interrupt();	
					    	        Log.i(TAG, "position antes:  " + pos);
					    	        SetImageBitmap SIB = new SetImageBitmap(pos, NelesoApp.width, NelesoApp.heigh);
					    	        SIB.run();
					    	        Log.i(TAG, "position despues:  " + pos);
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							
						}
				   	    }).start();
					
				}
        	});          	
         	mImageFetcher.clearCacheInternal();
        	
  	   
       } else {

    	    setContentView(R.layout.pdfreaderland);
    	    mAdapterLand = new ImagePagerAdapterLand(getSupportFragmentManager(), total_pages_land, cxt);
    	    mPagerLand = (CustomViewPager) findViewById(R.id.pagerland);
    	    mPagerLand.setAdapter(mAdapterLand);
    	    mAdapterLand.getItemPosition(mAdapterLand.saveState());
 //        	mAdapterLand.notifyDataSetChanged();
         	mAdapterLand.getCount();
         	mPagerLand.setOnPageChangeListener(new OnPageChangeListener() {
         	   
				@Override
				public void onPageScrollStateChanged(int arg0) {}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {}

				@Override
				public void onPageSelected(int arg0) {
					
					start = true;
					SetImageBitmapLand.currentThread().interrupt();	
					
					CurrentPageOnpageChange = arg0;
					Log.d(TAG, "CurrentPage onPageScrollStateChanged " + CurrentPageOnpageChange);  
					
					 new Thread(new Runnable() {
				   	      public void run() {  	    	  

				   	   	     Log.d(TAG, "CurrentPage CurrentPageOnpageChange " + CurrentPageOnpageChange);
				   	         renderPageBackground(CurrentPageOnpageChange);
				   	      }

						private void renderPageBackground(
								int currentPageOnpageChange) {
						      try {
						    	
						    	  PdfReader.isZoom = false;    	  
					    	          SetImageBitmapLand.currentThread().interrupt();	
					    	          Log.i(TAG, "position antes:  " + currentPageOnpageChange);
					    	          SetImageBitmapLand SIB = new SetImageBitmapLand(currentPageOnpageChange, NelesoApp.width, NelesoApp.heigh);
					    	          SIB.run();
					    	          Log.i(TAG, "position despues:  " + currentPageOnpageChange);
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							
						}
				   	    }).start();
					
				}
        	});          	
         	mImageFetcher.clearCacheInternal();

       }
        }
  
  
    
/////////// MULTICORE PROCESSOR///////////////////////////////////////////////////////////////////////    

/////////// MULTICORE PROCESSOR///////////////////////////////////////////////////////////////////////    

    
	private void init() {  
    	
    	temp = false;
    	String[] dirListing = null;

    	
        mydirThumb = new File(rootsd.toString() + DIR );
        Log.d(TAG, "mydirThumb " + mydirThumb);
        
    	if (mydirThumb.exists()) {
    		
    		 File[] files = mydirThumb.listFiles();             
    		 
    		 if (files.length > 0) {  			 
                 dirListing = mydirThumb.list();
                 ordenedList(dirListing);
    	    		 
    		 } else {
    		
    			    updateBarHandler = new Handler();
    	            launchBarDialog(null,0);
    		 }
          
    	} else {
 		 
    		mydirThumb.mkdir();
    		updateBarHandler = new Handler();
	        launchBarDialog(null,0);
    		
    	}
    
        // Set the current item based on the extra passed in to this activity
//        final int extraCurrentItem = getIntent().getIntExtra(EXTRA_IMAGE, -1);
//        if (extraCurrentItem != -1) {
//            mPager.setCurrentItem(extraCurrentItem);
//        }
    
        
    }            


	private void ordenedList(String[] dirListing) {
		
		String pageEins;
		
		 for(int i = 0 ; i < dirListing.length  ; i++)  {	
			 
			 pageEins = dirListing[i].substring(dirListing[i].indexOf("_") + 1,dirListing[i].indexOf("."));			 
			 Log.d(TAG, "pageEins " + pageEins);	 
			 int page1 = Integer.parseInt(pageEins);
			 Log.d(TAG, "page1 " + page1);
			 Log.d(TAG, "OrderedFiles[page1] " + mydirThumb + "/" + dirListing[i]);
			 
			 thumbString[page1] = mydirThumb + "/" + dirListing[i];	
			 thumb[page1] = Uri.parse(thumbString[page1]); 			 	 
		 }
		
		 return;
	}


	public void launchBarDialog(View view, final int start) {
		
		if (total_pages > 20) {
			first_thumb = 20;
			pages_created = 20;
			bigger = true;
		} else {
			first_thumb = total_pages;
			pages_created = total_pages;
		}
    	
    	        barProgressDialog = new ProgressDialog(PdfReader.this);
    	        barProgressDialog.setTitle("Creating thumbnails ...");
    	        barProgressDialog.setMessage("Create in progress ...");  
    	        barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    	        barProgressDialog.setProgress(0);
    	        barProgressDialog.setMax(first_thumb);
    	        barProgressDialog.setCancelable(false);
    	        barProgressDialog.show();
                new Thread(new Runnable() {
    	            
                	@Override
    	            public void run() {
    	                try {

    	                    // Here you should write your time consuming task...
    	                	
    	                        for(int i = start ; i < barProgressDialog.getMax() ; i++)  {	
    	          				createThumbnails(i); 
    	          				Thread.sleep(500);
    	                        updateBarHandler.post(new Runnable() {

    	                            public void run() {
                                    barProgressDialog.incrementProgressBy(1);
    	                            }

   	                          });
    	          			 
    	                        }    	                        
    	                            barProgressDialog.dismiss();
    	                            
//    	                            if (bigger) {
//    	                            	for(int i = 10 ; i < 20 ; i++)  {	
//    	        	          				createThumbnails(i); }
//    	                            }

    	                } catch (Exception e) {
    	                	e.printStackTrace();
    	                }

    	            }
               
    	        }).start();
    }
	
	
    
 // Create thumbnails
    protected void createThumbnails(int page) {
    	
    	 ExecutorService taskListThumb = Executors.newFixedThreadPool(total_pages);
	
    		try {
    			
    			taskListThumb.execute(new CreateThumbThread(page));
                
                } catch (Exception e) {
    	       // TODO Auto-generated catch block
    	       e.printStackTrace();
               }	

         }
    	
    public static class CreateThumbThread extends Thread implements Runnable {	
		 
		 private int page;
		 
		 public CreateThumbThread(int page) {
				this.page = page;
			}
		 
		 @Override
		 public void run() {
			 
				try {
					
					        RenderThumbPageThread newThread = new RenderThumbPageThread(mPdfFile, page);
					        newThread.run();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				
				}
		 }
		 
	 }	
  
    
//___________________________________________________________________________________________________________   
    
    
 //___________________________________________________________________________________________________________   
    
  

    private void parsePDF(String filename) throws PDFAuthenticationFailureException { 
    	
         PDFImage.sShowImages = true;
         PDFPaint.s_doAntiAlias = true;
         PDFFont.sUseFontSubstitution = false;
         HardReference.sKeepCaches = false;

//		pdffilename = getIntent().getStringExtra(PdfFileSelectActivity.EXTRA_PDFFILENAME);
//		Log.d("file exists?", (new File(pdffilename).exists())? "yes" : "no" );
	
        try {
        	File f = new File(filename);
        	long len = f.length();
        	if (len == 0) {
        		Log.e("parsePDF", "Lengt of file = 0 :(");
        	}
        	else {
        		openFile(f);
        	}
    	}
        catch (PDFAuthenticationFailureException e) {
        	e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
    
    // nostra versione del metodo openFile
    private  void openFile(File file) throws IOException {
        // first open the file for random access
        RandomAccessFile raf = new RandomAccessFile(file, "r");

        // extract a file channel
        FileChannel channel = raf.getChannel();

        // now memory-map a byte-buffer
         ByteBuffer bb =
                ByteBuffer.NEW(channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()));
        // create a PDFFile from the data
        mPdfFile = new PDFFile(bb);  
        total_pages = mPdfFile.getNumPages(); // * TOTAL NUMBER OF PAGES of the pdf
        total_pages_land = total_pages/2 + 1;
       // String pages = getString(totNumberOfPages);
        Log.i(TAG, "total pages from pdf " + total_pages);
        Log.i(TAG, "total pages from pdf - 2 " + total_pages_land);

    }
    
    private String storeUriContentToFile(Uri uri) {
    	String result = null;
    	try {
	    	if (mTmpFile == null) {
				File root = Environment.getExternalStorageDirectory();
				if (root == null)
					throw new Exception("external storage dir not found");
				mTmpFile = new File(root,"neleso/Pdf_temp.pdf");
				mTmpFile.getParentFile().mkdirs();
	    		mTmpFile.delete();
	    	}
	    	else {
	    		mTmpFile.delete();
	    	}
	    	InputStream is = getContentResolver().openInputStream(uri);
	    	OutputStream os = new FileOutputStream(mTmpFile);
	    	byte[] buf = new byte[1024];
	    	int cnt = is.read(buf);
	    	while (cnt > 0) {
	    		os.write(buf, 0, cnt);
		    	cnt = is.read(buf);
	    	}
	    	os.close();
	    	is.close();
	    	result = mTmpFile.getCanonicalPath();
	    	mTmpFile.deleteOnExit();
    	}
    	catch (Exception e) {
    		Log.e(TAG, e.getMessage(), e);
		}
		return result;
	}
    
    public int getScreenOrientation() {


    	  // Query what the orientation currently really is.
    	  if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)                {
    	      // The following message is only displayed once.
    	       return 1; // Portrait Mode
    	   
    	  }else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
    	      // The following message is only displayed once.
    	       return 2;   // Landscape mode
    	  }
    	  return 0;
    	 }

    // ±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±± parsePDF - openFile - store temporal uri content ±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±    
    
    
   
   
    // BUTTONS ******************************************************************************************************************
    
   

		
//		public android.view.View.OnClickListener myListener = new android.view.View.OnClickListener(){
//	    	@Override
//			public void onClick(View v) {
//				
////	    		a = obtainStyledAttributes(R.styleable.Theme);
//				ThumbnailsMenu customizeDialog = new ThumbnailsMenu(PdfReader.this);
//				customizeDialog.setCanceledOnTouchOutside(true);
//				customizeDialog.getWindow().setGravity(Gravity.BOTTOM);
//				customizeDialog.getWindow().setLayout(LayoutParams.FILL_PARENT, 200);
//				customizeDialog.show();
//				
//			}
//	    };  // end myListener

   
    // ****************************************************************************************************************************************
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
            
// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Multi Touch @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@		


/**
 * The main adapter that backs the ViewPager. A subclass of FragmentStatePagerAdapter as there
 * could be a large number of items in the ViewPager and we don't want to retain them all in
 * memory at once but create/destroy them on the fly.
 */
public static class ImagePagerAdapter extends FragmentStatePagerAdapter {
	
    private final int mSize;
    public final Context mContext;
    private SparseArray<WeakReference<PdfReaderFragment>> mPageReferenceMap = new SparseArray<WeakReference<PdfReaderFragment>>();

    public ImagePagerAdapter(FragmentManager fm, int size, Context context) {
        super(fm);        
        mSize = size;
        mContext = context;
    }

	@Override
    public int getCount() {
        return mSize;
    }

    @Override
    public Fragment getItem(int position) {   	
    	CurrentPage = position;  	   	
    	Log.d(TAG, "CurrentPage ImagePagerAdapter " + CurrentPage); 
    	return getFragment(position);   
    }
	
	public Object instantiateItem(ViewGroup container, int position) {

		PdfReaderFragment myFragment = (PdfReaderFragment) PdfReaderFragment.newInstance(thumbString[position], mContext);
		mPageReferenceMap.put(Integer.valueOf(position), new WeakReference<PdfReaderFragment>(myFragment));
		return super.instantiateItem(container, position);
	}
	
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {

		super.destroyItem(container, position, object);
		mPageReferenceMap.remove(Integer.valueOf(position));
	}
	
	/*
	 * Overriding this method in conjunction with calling
	 * notifyDataSetChanged removes a page from the pager.
	 */
	@Override
	public int getItemPosition(Object object) {

		return POSITION_NONE;
	}
	
	public PdfReaderFragment getFragment(int key) {

		WeakReference<PdfReaderFragment> weakReference = mPageReferenceMap.get(key);

		if (null != weakReference) {

			return (PdfReaderFragment) weakReference.get();
		}
		else {

			return null;
		}
	}
	
	public void blockPage(View collection) {
	      mPager.setScrollbarFadingEnabled(false);
	}
    
}

/**
 * Called by the ViewPager child fragments to load images via the one ImageFetcher
 */
public ImageFetcherPdf getImageFetcher() {
    return mImageFetcher;
}

public static class ImagePagerAdapterLand extends FragmentStatePagerAdapter {
	
	private final int mSize;
    public final Context mContext;
    
    private SparseArray<WeakReference<PdfReaderFragmentLand>> mPageReferenceMapLand = new SparseArray<WeakReference<PdfReaderFragmentLand>>();

    public ImagePagerAdapterLand(FragmentManager fm, int size, Context context) {
        super(fm);        
        mSize = size;
        mContext = context;
    }

	@Override
    public int getCount() {
        return mSize;
    }
	
	@Override
    public Fragment getItem(int position) {   		       
    	return getFragment(position);   
    }
	
	public Object instantiateItem(ViewGroup container, int position) {
		
		switch (position) {
		
		case 0: // Full ExpandableList code here
		
			if (true) {

				PdfReaderFragmentLand myFragment = (PdfReaderFragmentLand) PdfReaderFragmentLand.newInstance(thumbString[position],thumbString[position+1]);
				mPageReferenceMapLand.put(Integer.valueOf(position), new WeakReference<PdfReaderFragmentLand>(myFragment));
				return super.instantiateItem(container, position);
			}
			
		default:
			
			if (position*2+1 >= total_pages) {
				PdfReaderFragmentLand myFragment = (PdfReaderFragmentLand) PdfReaderFragmentLand.newInstance(thumbString[position*2-1], null);
				mPageReferenceMapLand.put(Integer.valueOf(position), new WeakReference<PdfReaderFragmentLand>(myFragment));
				return super.instantiateItem(container, position);
	          }

		PdfReaderFragmentLand myFragment = (PdfReaderFragmentLand) PdfReaderFragmentLand.newInstance(thumbString[position*2-1],thumbString[position*2]);
		mPageReferenceMapLand.put(Integer.valueOf(position), new WeakReference<PdfReaderFragmentLand>(myFragment));
		return super.instantiateItem(container, position);
	   }
	}

	public PdfReaderFragmentLand getFragment(int key) {

		WeakReference<PdfReaderFragmentLand> weakReference = mPageReferenceMapLand.get(key);

		if (null != weakReference) {

			return (PdfReaderFragmentLand) weakReference.get();
		}
		else {

			return null;
		}
	}

}


    
   
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Multi Touch @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@		
		   
 
 
    // FOOTER ______________________________________________________________________


	public void callIntent(View view) throws Exception {
		
    	ImageButton button  = (ImageButton) findViewById(R.id.kiosk);
    	ImageButton button2  = (ImageButton) findViewById(R.id.archive);
    	ImageButton button3  = (ImageButton) findViewById(R.id.news);
    	ImageButton button4  = (ImageButton) findViewById(R.id.website);
    	ImageButton button5  = (ImageButton) findViewById(R.id.help);
    	
       MenuManager menu = new MenuManager(this, view);
       menu.callIntent(this, view, button, button2, button3, button4, button5);
    		
    	}


	public void check() {
 	          Toast.makeText(getApplicationContext(), "Sono qui",Toast.LENGTH_SHORT).show();    
 	       
		
	}

	
// IMAGE VIEW TOUCH FEATURES ==================================================================================================	

    
    // FOOTER ______________________________________________________________________
	
    
}


