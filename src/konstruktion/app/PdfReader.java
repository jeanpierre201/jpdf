package konstruktion.app;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.io.InputStream;


import neleso.db.DataBase_Adapter;
import neleso.storefront.XMLHandler;
import net.sf.andpdf.nio.ByteBuffer;
import net.sf.andpdf.refs.HardReference;


import konstruktion.app.Images;
import konstruktion.app.ImageFetcherPdf;
import konstruktion.app.ShowPdf.MyThread;

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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;





@SuppressLint("ValidFragment")
public class PdfReader extends FragmentActivity {
	
	private static final String TAG = "PdfReader";
	
	// using the Fragment	
	private static final String IMAGE_CACHE_DIR = "images";
    public static final String EXTRA_IMAGE = "extra_image";
    private ImageFetcherPdf mImageFetcher;
    public String[] thumbString;
    private ImagePagerAdapter mAdapter;
	public static ViewPager mPager;
    int longest;
    int sizeWidht;
	


	public Context cxt;
	NelesoApp neleso;
	
 // files - directory Variables	
	public String pdffilename;
	public PDFFile mPdfFile;
	public int mPage;
    public File mTmpFile;
    public File mydirThumb;
    public File mydirHD;
    public PDFPage mPdfPage; 
    public static Uri[] thumb;
    public String path;
    public File file;
    public String filename;
    
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
	static int or;
	float scale;
    public int pos;
	public static int total_pages;	
	public static int first_thumb;
	public int total_pages2;
	public int total_pages3;
	public static int imgWidth = 0;

    
	private MyAdapter_LandScape swipAdapter_LandScape;
	public static ViewPager viewPager_LandScape;
	private int half_width;
    
 // render PDF 
 
    float zoom = 1.0f;  //3   
    private int total;
	public int HD;
	boolean bigger;    

	
	// On Process ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	
	ImageButton toolbarheader;
	public int item;
	private static ProgressDialog mSpinnerProgress;
	public Bitmap[] bit; 
	
	
		

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
        
        
        // Declare and start the Thread Manager

        
              
        if (intent != null) {
        	if ("android.intent.action.VIEW".equals(intent.getAction())) {
    			pdffilename = storeUriContentToFile(intent.getData());    			
        	}
        	else {
        		pdffilename = getIntent().getStringExtra(NelesoApp.EXTRA_PDFFILENAME);        	}
        }
        
        try {
			parsePDF(pdffilename);
			
		} catch (PDFAuthenticationFailureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        if (or == 1) {
        	    	
        	setContentView(R.layout.pdfreader); 
        	mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), total_pages);
        	mPager = (ViewPager) findViewById(R.id.pager);
 //       	mPager.setPageMargin((int) getResources().getDimension(R.dimen.image_detail_pager_margin));
        	mPager.setAdapter(mAdapter);
        	mPager.setOffscreenPageLimit(2);
        	mImageFetcher.clearCacheInternal();
        	init();  
         
        	
        } else {
        	
        	setContentView(R.layout.pdfreader_landscape); 
     	    swipAdapter_LandScape = new MyAdapter_LandScape();
            viewPager_LandScape = (ViewPager) findViewById(R.id.pager_landscape);
            viewPager_LandScape.getBackground().setDither(true);
            viewPager_LandScape.setAdapter(swipAdapter_LandScape);
            swipAdapter_LandScape.getCount();
            swipAdapter_LandScape.notifyDataSetChanged();
            init();
           
        }

             
        }
    
    
    @Override
   	protected void onResume() {
       	super.onResume(); 	
             	
       	
           if (or == 1) {
           	
        	setContentView(R.layout.pdfreader);            	 
           	mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), total_pages);
           	mPager = (ViewPager) findViewById(R.id.pager);
 //         mPager.setPageMargin((int) getResources().getDimension(R.dimen.image_detail_pager_margin));
           	mPager.setAdapter(mAdapter);
           	mPager.setOffscreenPageLimit(2);
          	mImageFetcher.clearCacheInternal();
        	   
            	
           } else {
           	
           	    setContentView(R.layout.pdfreader_landscape); 
        	    swipAdapter_LandScape = new MyAdapter_LandScape();
                viewPager_LandScape = (ViewPager) findViewById(R.id.pager_landscape);
                viewPager_LandScape.getBackground().setDither(true);
                viewPager_LandScape.setAdapter(swipAdapter_LandScape);
                swipAdapter_LandScape.getCount();
           	    swipAdapter_LandScape.notifyDataSetChanged();
              
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
        	  mImageFetcher.closeCacheInternal();
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
	//	  mydir.delete();
	//	  mydir2.delete();
    	mImageFetcher.flushCache();
       	super.onPause(); 
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
      // Save UI state changes to the savedInstanceState.
      // This bundle will be passed to onCreate if the process is
      // killed and restarted. 
    	mAdapter.saveState();
      	mPager.saveHierarchyState(null);
    	swipAdapter_LandScape.saveState();  
    	viewPager_LandScape.saveHierarchyState(null);
    	    	
      super.onSaveInstanceState(savedInstanceState);
    }
    
    @Override
    public void onConfigurationChanged(Configuration savedInstanceState) { 
        super.onConfigurationChanged(savedInstanceState);     
        
        if (savedInstanceState.orientation == Configuration.ORIENTATION_PORTRAIT) {
     	   
  //      	awesomeAdapter2.destroyItem(imageFrame, pos, imageFrame);
        	
          	setContentView(R.layout.pdfreader);
            mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), total_pages);
            mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setAdapter(mAdapter);
  //          mPager.setPageMargin((int) getResources().getDimension(R.dimen.image_detail_pager_margin));
            mPager.setOffscreenPageLimit(2);
            mAdapter.getCount();
        	
  	   
       } else {

 //   	   awesomeAdapter.destroyItem(instantView, pos, instantView);
    	   setContentView(R.layout.pdfreader_landscape);
    	   swipAdapter_LandScape = new MyAdapter_LandScape();
           viewPager_LandScape = (ViewPager) findViewById(R.id.pager_landscape);
           viewPager_LandScape.getBackground().setDither(true);
           viewPager_LandScape.setAdapter(swipAdapter_LandScape);
           swipAdapter_LandScape.getCount();
 

       }
        }
  
    
/////////// MULTICORE PROCESSOR///////////////////////////////////////////////////////////////////////    
     
   
    
    
    
/////////// MULTICORE PROCESSOR///////////////////////////////////////////////////////////////////////    

    
              
    private void init() {  
    	
    	temp = false;
         
          thumb = new Uri[total_pages];
          thumbString = new String[total_pages];
          
        updateBarHandler = new Handler();
        launchBarDialog(null);

    	
    	
        	uihandler = new Handler();
 //       	backgroundThread = new MyThread();
 //       	backgroundThread.start();
        
// creates 20 thumbnails
        
        
        
        
        // Set the current item based on the extra passed in to this activity
        final int extraCurrentItem = getIntent().getIntExtra(EXTRA_IMAGE, -1);
        if (extraCurrentItem != -1) {
            mPager.setCurrentItem(extraCurrentItem);
        }
    
        
    }            

	public void launchBarDialog(View view) {
		
		if (total_pages > 10) {
			first_thumb = 10;
			bigger = true;
		} else {
			first_thumb = total_pages;
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
    	                	
    	                        for(int i = 0 ; i < barProgressDialog.getMax() ; i++)  {	
    	          				createThumbnails(i); 
    	          				Thread.sleep(100);
    	                        updateBarHandler.post(new Runnable() {

    	                            public void run() {
                                    barProgressDialog.incrementProgressBy(1);
    	                            }

   	                          });
    	          			 
    	                        }    	                        
    	                            barProgressDialog.dismiss();
    	                            if (bigger) {
    	                            	for(int i = 10 ; i < 20 ; i++)  {	
    	        	          				createThumbnails(i); }
    	                            }

    	                } catch (Exception e) {
    	                	e.printStackTrace();
    	                }

    	            }
               
    	        }).start();
    }
	
	
	
	
	
//
//	private class renderPageAsync extends AsyncTask<String, String, String> {
//		
//		String imagePath;
//		Bitmap bMap;
//		
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mSpinnerProgress.show();
//            mImageFetcher = PdfReader.this.getImageFetcher();
//		        
//        }
//		
//		@Override
//		protected String doInBackground(String... arg0) {
//			
//			if (or == 1) {			
//				
//				imagePath = renderImageFromPDF.createJPEG(pdffilename, item, HD );      
//			    bMap = mImageFetcher.processBitmap(imagePath);
//			    instantView.setImageBitmap(bMap);
////			    instantView.setImageBitmapReset(bMap, true);       // ImageViewTouch     zoom default 
//			
//			} else {
//							
//			}
//			return null;
//				
//		}
//		
//		protected void onProgressUpdate(String... progress) {
////			 Log.d(LOG_TAG,progress[0]);
////             mProgressDialog.setProgress(Integer.parseInt(progress[0]));
//			mSpinnerProgress.dismiss();
//       }
//
//       @Override
//       protected void onPostExecute(String unused) {
//    	   //dismiss the dialog after the file was downloaded
//    	   mSpinnerProgress.dismiss();
////           openPdf(path);
//       }
//		
//	}
//	
	
	
	
	
	
    
 // Create thumbnails
    protected void createThumbnails(int page) {
    	
    	try {
    		
	        // remember: free memory from previous page
	       
	        mPdfPage = mPdfFile.getPage(page+1, true);
	                
	        float wi = mPdfPage.getWidth()/2;
	        float hei = mPdfPage.getHeight()/2;
	        
//	        int imgHeight = (int) (HD * hei / wi)/2;
//	        int imgWidth = HD/2;
	        
	        RectF clip = null;	
//          Bitmap bi = mPdfPage.getImage((int)wi, (int)hei, clip, true, true);	  	               
            Bitmap bi = mPdfPage.getImageT((int)wi, (int)hei, clip, true, true);	  	               
//	        bi.setDensity((int) scale);
	        createThumbJpg(page, bi);
            bi.isRecycled();
            bi = null;
        
		} catch (Throwable e) {
			Log.e("showPage()", e.getMessage(), e);
		}
    	
    	
    }
    
//___________________________________________________________________________________________________________   
    
    
 //___________________________________________________________________________________________________________   
    
  
 @SuppressLint("SimpleDateFormat")
private void createThumbJpg(int page, Bitmap bi) {
	 
	 
    
     Date date = new Date(0);
     SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMMddHHmmss");
     filename =  sdf.format(date);

     String storage = Environment.getExternalStorageState();
     Log.i("System out", "" + storage);
     File rootsd = Environment.getExternalStorageDirectory();
     Log.d("ROOT PATH", "" + rootsd.getAbsolutePath());


     String DIR = "/neleso/temp";
     String DIR2 = "/neleso/pdf2image";
     
    
       mydirThumb = new File(rootsd.toString() + DIR );
       mydirHD = new File(rootsd.toString() + DIR2);


if (!mydirThumb.exists()) {
	 mydirThumb.mkdir();
}

OutputStream outStream = null;  
file = new File(mydirThumb + "/"+filename+"_"+page+".JPG");
path = file.getAbsolutePath();
try {
 outStream = new FileOutputStream(file);
 bi.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
 outStream.flush();
 outStream.close();
}
catch(Exception e)
{}

thumb[page] = Uri.parse(path);
thumbString[page] = path;


}
 
// private void storeHDPageSD(int i) {
//      
////		 renderImageFromPDF rndpdf = new renderImageFromPDF();
//	     String imagePath = renderImageFromPDF.createJPEG(pdffilename, i, HD );
//	     mUrls[i] = Uri.parse(imagePath);
//			
//		}
//	 
	
//Start ThumbImages  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	
//End ThumbImage %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



// ±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±± parsePDF - openFile - store temporal uri content ±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±

	// Nostra implementazione di parsePDF - ONE TIME ONLY per pdf
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
       // String pages = getString(totNumberOfPages);
        Log.i(TAG, "total pages from pdf " + total_pages);
        
        total = total_pages/2 + 1;
        Log.i(TAG, "total pages from pdf - 2 " + total);
        
//        if (totNumberOfPages % 2 == 0) {
//        	pair = false;
// 		
//    	} else {
//    		pair = true;  		
//    	}
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
    
   

		
		public android.view.View.OnClickListener myListener = new android.view.View.OnClickListener(){
	    	@Override
			public void onClick(View v) {
				
//	    		a = obtainStyledAttributes(R.styleable.Theme);
				ThumbnailsMenu customizeDialog = new ThumbnailsMenu(PdfReader.this);
				customizeDialog.setCanceledOnTouchOutside(true);
				customizeDialog.getWindow().setGravity(Gravity.BOTTOM);
				customizeDialog.getWindow().setLayout(LayoutParams.FILL_PARENT, 200);
				customizeDialog.show();
				
			}
	    };  // end myListener

   
    // ****************************************************************************************************************************************
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
       
public class MyAdapter_LandScape extends PagerAdapter {
	
    	
		Bitmap blank;

               			        	        
		@Override
		public int getCount() {
			return total;
		}
	
		
		
		@Override
		public Object instantiateItem(View collection, int position) {
			
			instantView2 = new ImageView(cxt);
			instantView3 = new ImageView(cxt);
			
			item = position;
			
			
		        	float width = viewPager_LandScape.getMeasuredWidth();
//			 	    Log.d("width "+width, "width yeah");
			 	    half_width = (int) (width/2);
			 	    float height = viewPager_LandScape.getMeasuredHeight();
//			        Log.d("height "+height, "height yeah");
				    imageFrame = new FrameLayout(cxt);
                    imageFrame.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                

                instantView2.setScaleType(ImageView.ScaleType.FIT_END);
                instantView3.setScaleType(ImageView.ScaleType.FIT_START);
                
                RelativeLayout mRelativeLayout = new RelativeLayout(cxt);
            	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(half_width, LayoutParams.MATCH_PARENT);
            	params.leftMargin = 0;
            	params.topMargin = 0;
            	mRelativeLayout.addView(instantView2, params);
            	RelativeLayout mRelativeLayout2 = new RelativeLayout(cxt);
            	RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(half_width, LayoutParams.MATCH_PARENT);
            	params2.leftMargin = half_width;
            	params2.topMargin = 0;
            	mRelativeLayout2.addView(instantView3, params2);
                
            	
            	
                
                switch (position) {
    			
    			case 0: // Full ExpandableList code here
    			
    				if (true) {
			    
    					    
    					    instantView2.setImageURI(thumb[0]);
         	                instantView3.setImageURI(thumb[1]);
                            imageFrame.addView(mRelativeLayout);
    		                imageFrame.addView(mRelativeLayout2); 
    		                imageFrame.setOnClickListener(myListener);
    		            ((ViewPager) collection).addView(imageFrame,0);
    		            return imageFrame;
   					
    				}
    				
    			default:
           
    				instantView2.setImageURI(thumb[position*2-1]);
    				
                if (position*2+1 >= total_pages) {
                	instantView3.setImageBitmap(blank);
	            } else {
	            	instantView3.setImageURI(thumb[position*2]);
	             }
              
            	imageFrame.addView(mRelativeLayout);  
            	imageFrame.addView(mRelativeLayout2);
            	imageFrame.setOnClickListener(myListener);
            ((ViewPager) collection).addView(imageFrame,0);
            return imageFrame;

		}
	}	
						
	
		/**
	     * Remove a page for the given position.  The adapter is responsible
	     * for removing the view from its container, although it only must ensure
	     * this is done by the time it returns from {@link #finishUpdate()}.
	     *
	     * @param container The containing View from which the page will be removed.
	     * @param position The page position to be removed.
	     * @param object The same object that was returned by
	     * {@link #instantiateItem(View, int)}.
	     */
		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((View) view);
		}	
		
		public boolean isViewFromObject(View view, Object object) {
			return view==((View)object);
		}
		
		public void blockPage(View collection) {
			 ((CustomViewPager) collection).setPagingEnabled(false);
		}
		
		public void diblockPage(View collection) {
			 ((CustomViewPager) collection).setPagingEnabled(true);
		}

		
		
		
	    /**
	     * Called when the a change in the shown pages has been completed.  At this
	     * point you must ensure that all of the pages have actually been added or
	     * removed from the container as appropriate.
	     * @param container The containing View which is displaying this adapter's
	     * page views.
	     */
		@Override
		public void finishUpdate(View arg0) {}
		

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {}
		      
// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Multi Touch @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@		

	





}

/**
 * The main adapter that backs the ViewPager. A subclass of FragmentStatePagerAdapter as there
 * could be a large number of items in the ViewPager and we don't want to retain them all in
 * memory at once but create/destroy them on the fly.
 */
public class ImagePagerAdapter extends FragmentStatePagerAdapter {
    private final int mSize;

    public ImagePagerAdapter(FragmentManager fm, int size) {
        super(fm);
        mSize = size;
    }

    @Override
    public int getCount() {
        return mSize;
    }

    @Override
    public Fragment getItem(int position) {
    	
    	Log.i(TAG, "fragment position" + position);
    	String path = renderImageFromPDF.createJPEG(pdffilename, position, HD );
    	Bitmap bMap = mImageFetcher.processBitmap(path);
     	return PdfReaderFragment.newInstance(path,cxt,bMap);
    }

}

/**
 * Called by the ViewPager child fragments to load images via the one ImageFetcher
 */
public ImageFetcherPdf getImageFetcher() {
    return mImageFetcher;
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


