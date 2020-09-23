package industriekatalog.app;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;
import java.nio.channels.FileChannel;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.io.InputStream;

import industriekatalog.app.R;

import net.sf.andpdf.nio.ByteBuffer;
import net.sf.andpdf.refs.HardReference;


import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.decrypt.PDFAuthenticationFailureException;
import com.sun.pdfview.font.PDFFont;

import android.R.array;
import android.R.bool;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;




public class ShowPdf extends Activity {
	

	private static final String TAG = "ShowPdf";
	public static final String EXTRA_USEFONTSUBSTITUTION = "net.sf.andpdf.extra.USEFONTSUBSTITUTION";
	
	private String pdffilename;
	public PDFFile mPdfFile;
	public int mPage;
    public File mTmpFile;
    public File mydir;
    public File mydir2;
    public PDFPage mPdfPage; 
    
    public Thread backgroundThread;
    public static Handler uiHandler;
    private static ProgressDialog dialog;
  
    public ImageView instantView;
    ImageView instantView2;
    ImageView instantView3;
    FrameLayout imageFrame;
    public Boolean temp;
    public int pos;
 
    float zoom = 1.0f;  //3
    // Limit zoomable/pannable image    
  
    public Uri[] mUrls;
    public Uri[] thumb;
    private int total;
    public Boolean pair;
    
	private Context cxt;
	
	public ViewPager scrollPager;
	public int totalNumberOfPages;	
	public AwesomePagerAdapter scrollAdapter;
	
	private AwesomePagerAdapter2 awesomeAdapter2;
	private ViewPager awesomePager2;
	private int half_width;
	NelesoApp neleso;
	
	
	// TRYING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	// Get Screen Size
//		public Display display = getWindowManager().getDefaultDisplay();
//		DisplayMetrics dm = new DisplayMetrics();
		public static final String LOG_TAG = "Download Of Ninja";
		private ProgressDialog mProgressDialog;

	
	
	ImageButton toolbarheader;
    public Uri header;
	private int HD = 1200;
	int or;

	// TRYING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {	
        super.onCreate(savedInstanceState);
        
        int sdk = Build.VERSION.SDK_INT;
        System.out.println("SDK "+sdk);
        String version = Build.VERSION.RELEASE;
        System.out.println("Version "+version);
        cxt = this;
        or = getResources().getConfiguration().orientation;
        
        if (or == 1) {
        	
        	setContentView(R.layout.pdfreader); 
     	    scrollAdapter = new AwesomePagerAdapter();
            scrollPager = (ViewPager) findViewById(R.id.pager);
            scrollPager.getBackground().setDither(true);
            scrollPager.setAdapter(scrollAdapter);
            scrollAdapter.getCount();
            scrollAdapter.notifyDataSetChanged();
            init();
        	
        } else {
        	
        	setContentView(R.layout.pdfreader_landscape); 
     	    awesomeAdapter2 = new AwesomePagerAdapter2();
            awesomePager2 = (ViewPager) findViewById(R.id.pager_landscape);
            awesomePager2.getBackground().setDither(true);
            awesomePager2.setAdapter(awesomeAdapter2);
            awesomeAdapter2.getCount();
            awesomeAdapter2.notifyDataSetChanged();
            init();
           
        }

             
        }
    
    
    @Override
   	protected void onResume() {
       	super.onResume(); 	
       	
           if (or == 1) {
           	
           	setContentView(R.layout.pdfreader); 
        	    scrollAdapter = new AwesomePagerAdapter();
               scrollPager = (ViewPager) findViewById(R.id.pager);
               scrollPager.getBackground().setDither(true);
               scrollPager.setAdapter(scrollAdapter);
               scrollAdapter.getCount();
           	scrollAdapter.notifyDataSetChanged();
       
           	
           } else {
           	
           	setContentView(R.layout.pdfreader_landscape); 
        	    awesomeAdapter2 = new AwesomePagerAdapter2();
               awesomePager2 = (ViewPager) findViewById(R.id.pager_landscape);
               awesomePager2.getBackground().setDither(true);
               awesomePager2.setAdapter(awesomeAdapter2);
               awesomeAdapter2.getCount();
           	awesomeAdapter2.notifyDataSetChanged();
   
              
           }
       }
    
    
    // Save the thread
    @Override
    public Object onRetainNonConfigurationInstance() { 	 
           return backgroundThread;
    }
    
 // dismiss dialog if activity is destroyed
    @Override
    protected void onDestroy() {
           if (dialog != null && dialog.isShowing())
           {
                  dialog.dismiss();
                  dialog = null;
           }
           
           if (mTmpFile != null) {
        		mTmpFile.delete();
        		mTmpFile = null;
        	}
        	
        	  if (mydir != null) {
        		  mydir.delete();
        		  mydir = null;
          	}
           super.onDestroy();
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
      // Save UI state changes to the savedInstanceState.
      // This bundle will be passed to onCreate if the process is
      // killed and restarted. 
    	scrollAdapter.saveState();
    	awesomeAdapter2.saveState();
    	    	
      super.onSaveInstanceState(savedInstanceState);
    }
    
    @Override
    public void onConfigurationChanged(Configuration savedInstanceState) { 
        super.onConfigurationChanged(savedInstanceState);     
        
        if (savedInstanceState.orientation == Configuration.ORIENTATION_PORTRAIT) {
     	   
  //      	awesomeAdapter2.destroyItem(imageFrame, pos, imageFrame);
        	setContentView(R.layout.pdfreader); 
     	    scrollAdapter = new AwesomePagerAdapter();
            scrollPager = (ViewPager) findViewById(R.id.pager);
            scrollPager.getBackground().setDither(true);
            scrollPager.setAdapter(scrollAdapter);
            scrollAdapter.getCount();
    
    	   
       } else {

 //   	   awesomeAdapter.destroyItem(instantView, pos, instantView);
    	   setContentView(R.layout.pdfreader_landscape); 
    	   awesomeAdapter2 = new AwesomePagerAdapter2();
           awesomePager2 = (ViewPager) findViewById(R.id.pager_landscape);
           awesomePager2.getBackground().setDither(true);
           awesomePager2.setAdapter(awesomeAdapter2);
           awesomeAdapter2.getCount();
       }
        }
    
        
              
    private void init() {  
    	
    	temp = false;
         
        Intent intent = getIntent();       
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
        
                   
        // Check if the thread is already running
        backgroundThread = (Thread) getLastNonConfigurationInstance();
        if (backgroundThread != null && backgroundThread.isAlive()) 
             dialog = ProgressDialog.show(this, "Load", "creating pages");
    	
    	
    	mUrls = new Uri[totalNumberOfPages];
        thumb = new Uri[totalNumberOfPages];
 //       new CreateThumbnails().execute();
      
//        renderHDPage(0);
//        renderHDPage(1);
        
        dialog = ProgressDialog.show(this, "Thumbnail", "creating");
    	
        	uiHandler = new Handler();
        	backgroundThread = new MyThread();
        	backgroundThread.start();
                
        }



	public class MyThread extends Thread { 	
  
        @Override
        public void run() {
               try
			 {
			        new Thread();
			        Thread.sleep(5000);
			 }
			 catch (InterruptedException e)
			 {
			        e.printStackTrace();
			 }    
               
          //   new CreateThumbnailsAsync().execute();
  			 
  			 for(int i = 0 ; i < totalNumberOfPages ; i++)  {
  			
  				 addPage(i,zoom); 	
  				 
  			 }       
  
  			dialog.dismiss();
		//	 uiHandler.post(new MyRunnable());       
    }       
 } 
    
    public class MyRunnable implements Runnable
    {
           public void run()
           {
                  backgroundPagesHD();
                  dialog.dismiss();
           }

		private void backgroundPagesHD() {

		    	final Handler handle = new Handler();
		 
		    		Thread t = new Thread(){

		    			public void run(){
		    				
		    				for(int i = 2 ; i < 5; i++)  {
		    					
		    					renderHDPage(i);
		    					  dialog.dismiss();
		      			     }
		    				handle.post(proceso);
		    			}
		    		};
		      
		   		t.start();
		    	} 
		     
		    	final Runnable proceso = new Runnable(){
		   		public void run(){
		   			
			        Toast.makeText(getApplicationContext(), "Finito Pages",Toast.LENGTH_SHORT).show();
			        dialog.dismiss();

		   		}
		    	};
    
   } 
    
 // nostra implementazione di showPage - chiamato per ogni pagina
    protected void addPage(int page, float zoom) {
    	
    	try {
    		
	        // remember: free memory from previous page
	       
	        mPdfPage = mPdfFile.getPage(page+1, true);
	        int num = mPdfPage.getPageNumber();
	                
	        float wi = mPdfPage.getWidth();
	        float hei = mPdfPage.getHeight();
	        String pageInfo= new File(pdffilename).getName() + " - " + num +"/"+totalNumberOfPages+ ": " + wi + "x" + hei;

	        Log.d("showPage()", pageInfo);
	        RectF clip = null;	  

	        Bitmap bi = mPdfPage.getImageT((int)(180*zoom), (int)(300*zoom), clip, true, true);
            createJpg(page, bi);
            bi.isRecycled();
            bi = null;
	
	        
		} catch (Throwable e) {
			Log.e("showPage()", e.getMessage(), e);
		}
    	
    	
    }
    
  
 private void createJpg(int page, Bitmap bi) {
	 
	 String path;
     String filename;
     Date date = new Date(0);
     SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMMddHHmmss");
     filename =  sdf.format(date);

     String storage = Environment.getExternalStorageState();
     Log.i("System out", "" + storage);
     File rootsd = Environment.getExternalStorageDirectory();
     Log.d("ROOT PATH", "" + rootsd.getAbsolutePath());


     String DIR = "/neleso/temp";
     String DIR2 = "/neleso/pdf2image";
     
     
    
     mydir = new File(rootsd.toString() + DIR);
     mydir2 = new File(rootsd.toString() + DIR2);


if (!mydir.exists()) {

	 mydir.mkdir();
}

OutputStream outStream = null;  
File file = new File(mydir + "/"+filename+"_"+page+".JPG");
path = file.getAbsolutePath();
try {
 outStream = new FileOutputStream(file);
 bi.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
 outStream.flush();
 outStream.close();
}
catch(Exception e)
{}

thumb[page] = Uri.parse(path);

}
 
 private void renderHDPage(int i) {
      
//		 renderImageFromPDF rndpdf = new renderImageFromPDF();
	     String imagePath = renderImageFromPDF.createJPEG(pdffilename, i, HD );
	     mUrls[i] = Uri.parse(imagePath);
			
		}
	 
	
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
        totalNumberOfPages = mPdfFile.getNumPages(); // * TOTAL NUMBER OF PAGES of the pdf
       // String pages = getString(totNumberOfPages);
        Log.i(TAG, "total pages from pdf " + totalNumberOfPages);
        
        total = totalNumberOfPages/2 + 1;
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
    
   

		
		private android.view.View.OnClickListener myListener = new android.view.View.OnClickListener(){
	    	@Override
			public void onClick(View v) {
						
				CustomizeDialog customizeDialog = new CustomizeDialog(ShowPdf.this);
				customizeDialog.setCanceledOnTouchOutside(true);
				customizeDialog.getWindow().setGravity(Gravity.BOTTOM);
				customizeDialog.getWindow().setLayout(LayoutParams.FILL_PARENT, 200);
				customizeDialog.show();
				
			}
	    };  // end myListener

    
    
    
    
    public class CustomizeDialog extends Dialog {
    	
    	Boolean even;
    	
    	public void analyseDigits(int number) {
    		
    		  if (number % 2 == 0) {
    	        	even = true; 	 		
   	    	} else {
    	    		pair = false;  		
    	    	}    		
    		}
    	
    	 public CustomizeDialog(Context context) {
			super(context);
			
			/** It will hide the title */
			requestWindowFeature(Window.FEATURE_NO_TITLE);		
			setContentView(R.layout.thumbnails);
			Gallery g = (Gallery) findViewById(R.id.gallery);
			g.setAdapter(new ImageAdapter(context));
			g.setOnItemClickListener(new OnItemClickListener() {
	              public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            	  
	            	  if (or == 1) {
	            		             	
	            			scrollPager.setCurrentItem(position);
	            		  
	            	  } else {
	            	    
	//            		  analyseDigits(position);
//		            		if (even){            			
		            			int posi = position/2;
		            			awesomePager2.setCurrentItem(posi);
//		            		} else {
		            			
//		            			int pos = position+1/2;
//		            			awesomePager2.setCurrentItem(pos);
//		            		}
	            
	            	  }
	              }
				
	          });

		}
    
 	 
	
    	 
    	  public class ImageAdapter extends BaseAdapter {
    	       
    	        int mGalleryItemBackground;
    	        private Context mContext;


    	        public ImageAdapter(Context c) {
    	            mContext = c;
    	            TypedArray a = obtainStyledAttributes(R.styleable.Theme);
    	            mGalleryItemBackground = a.getResourceId(
    	            		R.styleable.Theme_android_galleryItemBackground, 0);
    	            a.recycle();
    	        }

    	        public int getCount() {
    	            return totalNumberOfPages;
    	        }

    	        public Object getItem(int position) {
    	            return position;
    	        }

    	        public long getItemId(int position) {
    	            return position;
    	        }

    	        public View getView(int position, View convertView, ViewGroup parent) {
    	            ImageView i = new ImageView(mContext);

    	            i.setImageURI(thumb[position]);
    	            i.setLayoutParams(new Gallery.LayoutParams(160, 200));
    	            i.setScaleType(ImageView.ScaleType.FIT_XY);
    	            i.setBackgroundResource(mGalleryItemBackground);

    	            return i;
    	        }
    	    }
    
    }
    
    
   
    // ****************************************************************************************************************************************
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
   
    
    public class AwesomePagerAdapter extends PagerAdapter implements OnTouchListener {	
     
    	ImageView image;
    	Boolean done = false;
    	
    	
    		@Override
    		public int getCount() {
    			return totalNumberOfPages;
    		}		
    		
    		@Override
    		public Object instantiateItem(View collection, int position) {
    			
    			pos = position;
    			
    			if (position > 1) {
    				done = true;
    			}
    			
    			switch (position) {  
    			
    			case 0: // Full ExpandableList code here
    			
    				if (!done) {		
    				 image = new ImageView(cxt);
    				 image.setImageURI(mUrls[position]);
    				 image.setOnClickListener(myListener);
    			     ((ViewPager) collection).addView(image,0);
    			     return image;
    			     
    				}
    				
    			case 1: // Full ExpandableList code here
        			
    				if (!done) { 
    				 image = new ImageView(cxt);
    				 image.setImageURI(mUrls[position]); 
    				 image.setOnClickListener(myListener);
    				
    			     ((ViewPager) collection).addView(image,0);
    			     return image;
    			     
    				}
    			 
			default:
    				    instantView = new ImageView(cxt);
    	    			Bitmap bMap = BitmapFactory.decodeFile(mydir2 +"/"+position+"_"+HD+".jpg");
    	    			instantView.setImageBitmap(bMap);
 //   	             	instantView.setImageBitmapReset(bMap, true);
//    	             	instantView.setScaleType(ImageView.ScaleType.MATRIX);
    	    		    ((ViewPager) collection).addView(instantView,0);
    	    		    return instantView;
    		    }
    		}

    		public void destroyItem(View collection, int position, Object view) {
    			((ViewPager) collection).removeView((ImageView) view);
    		}	
    		
    		public boolean isViewFromObject(View view, Object object) {
    			return view==((ImageView)object);
    		}
    		
    		public void blockPage(View collection) {
    			((ViewPager) collection).setScrollbarFadingEnabled(false);
    		}
    		
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

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				 // We can be in one of these 3 states
		    	   final int NONE = 0;
		    	   final int DRAG = 1;
		    	   int mode = NONE;
				
				image = (ImageView) v;
				image.setScaleType(ImageView.ScaleType.MATRIX);
				
				 // Dump touch event to log
				   dumpEvent(event);
			
				// Handle touch events here...
			      switch (event.getAction() & MotionEvent.ACTION_MASK) {
			      case MotionEvent.ACTION_DOWN:
			    	  image.setClickable(isRestricted());
			      case MotionEvent.ACTION_UP:
			    	  image.setClickable(isRestricted());
			      case MotionEvent.ACTION_POINTER_DOWN:
			    	  image.setClickable(isRestricted());
			         break;			      
			      case MotionEvent.ACTION_POINTER_UP:
			    	  image.setClickable(isRestricted());
				         break;	
			      case MotionEvent.ACTION_MOVE:
			         if (mode == DRAG) {}		         
			         break;
			      } 						
				return true;
			}
			
		
			
			 /** Show an event in the LogCat view, for debugging */
			   private void dumpEvent(MotionEvent event) {
			      // ...
			      String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
			            "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
			      StringBuilder sb = new StringBuilder();
			      int action = event.getAction();
			      int actionCode = action & MotionEvent.ACTION_MASK;
			      sb.append("event ACTION_").append(names[actionCode]);
			      if (actionCode == MotionEvent.ACTION_POINTER_DOWN
			            || actionCode == MotionEvent.ACTION_POINTER_UP) {
			         sb.append("(pid ").append(
			               action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			         sb.append(")");
			      }
			      sb.append("[");
			      for (int i = 0; i < event.getPointerCount(); i++) {
			         sb.append("#").append(i);
			         sb.append("(pid ").append(event.getPointerId(i));
			         sb.append(")=").append((int) event.getX(i));
			         sb.append(",").append((int) event.getY(i));
			         if (i + 1 < event.getPointerCount())
			            sb.append(";");
			      }
			      sb.append("]");
			      Log.d(TAG, sb.toString());
			   }
    		
    }
    
public class AwesomePagerAdapter2 extends PagerAdapter {
	
    	
		Bitmap blank;
		
		    	        
		@Override
		public int getCount() {
			return total;
		}
	
		
		
		@Override
		public Object instantiateItem(View collection, int position) {
			
			pos = position;
			
			
		        	float width = awesomePager2.getMeasuredWidth();
			 	    Log.d("width "+width, "width yeah");
			 	    half_width = (int) (width/2);
			 	    float height = awesomePager2.getMeasuredHeight();
			        Log.d("height "+height, "height yeah");
				    imageFrame = new FrameLayout(cxt);
                imageFrame.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                imageFrame.setOnClickListener(myListener);
                instantView2 = new ImageView(cxt);
                instantView2.setScaleType(ImageView.ScaleType.FIT_END);
                instantView3 = new ImageView(cxt);
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
			    
    		                instantView2.setImageURI(mUrls[0]);
         	                instantView3.setImageURI(mUrls[1]);
    		                imageFrame.addView(mRelativeLayout);
    		                imageFrame.addView(mRelativeLayout2); 
 
    		            ((ViewPager) collection).addView(imageFrame,0);
    		            return imageFrame;
   					
    				}
    				
    			default:
           
                instantView2.setImageURI(mUrls[position*2-1]);
             
                if (position*2+1 >= totalNumberOfPages) {
                	instantView3.setImageBitmap(blank);
	            } else {
	            	instantView3.setImageURI(mUrls[position*2]);
	             }
              
            	imageFrame.addView(mRelativeLayout);  
            	imageFrame.addView(mRelativeLayout2);
            	
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
    
public class CreateThumbnails extends AsyncTask<String, String, String> {

	@Override
    protected void onPreExecute() {
        super.onPreExecute();
        showDialog(0);
    }

    @Override
    protected String doInBackground(String... params) {
    	
    	int firstthumbs = 20;
    	int i;
    	long total = 0;
    	
    	try {
    		
    		if (totalNumberOfPages > 20) {
    			for( i = 0 ; i < firstthumbs ; i++)  {   			
   				 addPage(i,zoom); 
    	    if (totalNumberOfPages < 20) {
    		    for( i = 0 ; i < totalNumberOfPages ; i++)  {   			
    	   				 addPage(i,zoom); 
    			}
    		}
       }
   	
    		//here's the download code
            
			while (i > 0) {
				total += i; //total = total + i
				publishProgress("" + (int)((total*100)/totalNumberOfPages));
			   }
    		}
   
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
        }
        
        return null;
    }
	
    protected void onProgressUpdate(String... progress) {
        Log.d(LOG_TAG,progress[0]);
        mProgressDialog.setProgress(Integer.parseInt(progress[0]));
   }
    
    @Override
    protected void onPostExecute(String unused) {
        //dismiss the dialog after the file was downloaded
        dismissDialog(0);
        mProgressDialog.dismiss();
    }
	

}


//Creating the BarDialog  ******************************************************************************************************

//our progress bar settings
@Override
protected Dialog onCreateDialog(int id) {
switch (id) {
   case 0: //we set this to 0
       mProgressDialog = new ProgressDialog(ShowPdf.this);
       mProgressDialog.setMessage("Creating Thumbnails...");
       mProgressDialog.setIndeterminate(false);
       mProgressDialog.setMax(100);
       mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
       mProgressDialog.setCancelable(true);
       mProgressDialog.show();
       return mProgressDialog;
default:
	return null;
}
}

//Creating the BarDialog  ******************************************************************************************************



		  

   
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
		
	
    
    // FOOTER ______________________________________________________________________
     
}


