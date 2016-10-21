package jpdf.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.decrypt.PDFAuthenticationFailureException;
import com.sun.pdfview.font.PDFFont;


import net.sf.andpdf.pdfviewer.PdfFileSelectActivity;
import net.sf.andpdf.pdfviewer.PdfViewerActivity;
import net.sf.andpdf.pdfviewer.gui.FullScrollView;
import net.sf.andpdf.refs.HardReference;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

public class PdfViewer extends Activity {
	
	private GraphView mOldGraphView;
	private GraphView mGraphView;
	private String pdffilename;
	private PDFFile mPdfFile;
	private int mPage;
    private File mTmpFile;
    
    private PDFPage mPdfPage; 
    
    private Thread backgroundThread;
    private Handler uiHandler;
    
    /**
	 * restore member variables from previously saved instance
	 * @see onRetainNonConfigurationInstance
	 * @return true if instance to restore from was found
	 */
	private boolean restoreInstance() {
		mOldGraphView = null;
		if (getLastNonConfigurationInstance()==null)
			return false;
		PdfViewer inst =(PdfViewer)getLastNonConfigurationInstance();
		if (inst != this) {
		
			mOldGraphView = inst.mGraphView;
			mPage = inst.mPage;
			mPdfFile = inst.mPdfFile;
			mPdfPage = inst.mPdfPage;
			mTmpFile = inst.mTmpFile;
			pdffilename = inst.pdffilename;
			backgroundThread = inst.backgroundThread; 
			// mGraphView.invalidate();
		}	
		return true;
	}

    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHandler = new Handler();
        restoreInstance();
        if (mOldGraphView != null) {
	        mGraphView = new GraphView(this);
	        mGraphView.mBi = mOldGraphView.mBi;
	        mOldGraphView = null;
	        mGraphView.mImageView.setImageBitmap(mGraphView.mBi);
	        setContentView(mGraphView);
        }
        else {
	        mGraphView = new GraphView(this);	        
	        Intent intent = getIntent();
		        
	        if (intent != null) {
	        	if ("android.intent.action.VIEW".equals(intent.getAction())) {
        			pdffilename = storeUriContentToFile(intent.getData());
	        	}
	        	else {
	                pdffilename = getIntent().getStringExtra(PdfFileSelectActivity.EXTRA_PDFFILENAME);
	        	}
	        }
	        
	        if (pdffilename == null)
	        	pdffilename = "no file selected";

			mPage = 1;

			setContent(null);
	        
        }
    }
    
    private void setContent(String password) {
        //parsePDF(pdffilename);
		setContentView(mGraphView);
		startRenderThread(mPage);
	}
	
    
    private synchronized void startRenderThread(final int page) {
		if (backgroundThread != null)
			return;

        backgroundThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
			        if (mPdfFile != null) {
//			        	File f = new File("/sdcard/andpdf.trace");
//			        	f.delete();
//			        	Log.e(TAG, "DEBUG.START");
//			        	Debug.startMethodTracing("andpdf");
			        	showPage(page);
//			        	Debug.stopMethodTracing();
//			        	Log.e(TAG, "DEBUG.STOP");
			        }
				} catch (Exception e) {
				
				}
		        backgroundThread = null;
			}

			private void showPage(int page) {
		         long startTime = System.currentTimeMillis();
		            long middleTime = startTime;
		        	try {
		    	        // free memory from previous page
		    	        mGraphView.setPageBitmap(null);
		    	        mGraphView.updateImage();
		    	        
		    	        mPdfPage = mPdfFile.getPage(page, true);
		    	        int num = mPdfPage.getPageNumber();
		    	        int maxNum = mPdfFile.getNumPages();
		    	        float wi = mPdfPage.getWidth();
		    	        float hei = mPdfPage.getHeight();
		    	        String pageInfo= new File(pdffilename).getName() + " - " + num +"/"+maxNum+ ": " + wi + "x" + hei;
		    	   
		    	    
		    	        RectF clip = null;
		    	        middleTime = System.currentTimeMillis();
		    	        Bitmap bi = mPdfPage.getImage((int)(wi*1), (int)(hei*1), clip, true, true);
		    	        mGraphView.setPageBitmap(bi);
		    	        mGraphView.updateImage();
		    		} catch (Throwable e) {
		    			
		    		}
		            long stopTime = System.currentTimeMillis();
				
			}
		});
        updateImageStatus();
        backgroundThread.start();
	}
	
	
	
	private void updateImageStatus() {
//		Log.i(TAG, "updateImageStatus: " +  (System.currentTimeMillis()&0xffff));
		if (backgroundThread == null) {
			mGraphView.updateUi();
			return;
		}
		mGraphView.updateUi();
		mGraphView.postDelayed(new Runnable() {
			@Override public void run() {
				updateImageStatus();
			}
		}, 1000);
	}



	private String storeUriContentToFile(Uri uri) {
		String result = null;
    	try {
	    	if (mTmpFile == null) {
				File root = Environment.getExternalStorageDirectory();
				if (root == null)
					throw new Exception("external storage dir not found");
				mTmpFile = new File(root,"AndroidPdfViewer/AndroidPdfViewer_temp.pdf");
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
    		
		}
		return result;
	}




	private class GraphView extends FullScrollView {
    
    	private ImageView mImageView;
    	private Bitmap mBi;
        
        public GraphView(Context context) {
            super(context);

            // layout params
			LinearLayout.LayoutParams lpWrap1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,1);
			LinearLayout.LayoutParams lpWrap10 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,10);

            // vertical layout
			LinearLayout vl=new LinearLayout(context);
			vl.setLayoutParams(lpWrap10);
			vl.setOrientation(LinearLayout.VERTICAL);
			
		        
		        mImageView = new ImageView(context);
		        setPageBitmap(null);
		        updateImage();
		        mImageView.setLayoutParams(lpWrap1);
		        mImageView.setPadding(5, 5, 5, 5);
		        vl.addView(mImageView);	
			    
			setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT,
					100));
			setBackgroundColor(Color.LTGRAY);
			setHorizontalScrollBarEnabled(true);
			setHorizontalFadingEdgeEnabled(true);
			setVerticalScrollBarEnabled(true);
			setVerticalFadingEdgeEnabled(true);
			addView(vl);
        }
        
        private void setPageBitmap(Bitmap bi) {
			if (bi != null)
				mBi = bi;
			else {
				mBi = Bitmap.createBitmap(100, 100, Config.RGB_565);
	            Canvas can = new Canvas(mBi);
	            can.drawColor(Color.RED);
	            
				Paint paint = new Paint();
	            paint.setColor(Color.BLUE);
	            can.drawCircle(50, 50, 50, paint);
	            
	            paint.setStrokeWidth(0);
	            paint.setColor(Color.BLACK);
	            can.drawText("Bitmap", 10, 50, paint);
			}
		}
        
        private void updateImage() {
        	uiHandler.post(new Runnable() {
				@Override
				public void run() {
		        	mImageView.setImageBitmap(mBi);
				}
			});
		}
        
        private void updateUi() {
        	uiHandler.post(new Runnable() {
				@Override
				public void run() {
		        	
				}
			});
		}
        
     
       
        
	}     

}
