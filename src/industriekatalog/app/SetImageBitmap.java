package industriekatalog.app;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SetImageBitmap extends Thread implements Runnable {

	  protected static final String TAG = "SendImagePosition";
	  public int counterSendImagePosition=0;
	  RenderOnTop renderontop;
	  public static String pathFile;
	  public static LinearLayout.LayoutParams params;
	  PdfReader pdfreader;
	  PdfReaderFragment PDF;
	  
	  public static int positionRender;
	  private int width;
	  private int heigh;

	  
	  
	public SetImageBitmap(int pos, int width, int heigh) {
		SetImageBitmap.positionRender = pos;
		this.width = width;
		this.heigh = heigh;
	}

	@Override
	public void run() {
			
		Log.i(TAG, "SendImagePosition :  " + counterSendImagePosition);
		Log.i(TAG, "This Thread Is running :  " + SetImageBitmap.currentThread().getName());
		Log.i(TAG, "Thread.activeCount() :  " + Thread.activeCount());
		counterSendImagePosition++;
		
		
		try {
			
			if (!PdfReader.isZoom && positionRender == PdfReader.mPager.getCurrentItem()) {
				
				        RenderPageThread rpt = new RenderPageThread(positionRender, width, heigh);
				        rpt.run();
			            Log.i(TAG, "pathFile :  " + pathFile);		        			        	
			        	Bitmap bMap = PdfReader.mImageFetcher.processBitmap(pathFile);
				        Log.i(TAG, "pathFile dopo bitmap :  " + pathFile);				        
				        PdfReaderFragment.refresh(bMap);		        	
			        
			} else if (!PdfReader.isZoom && positionRender != PdfReader.mPager.getCurrentItem()) {
				
				RenderPageThread rpt = new RenderPageThread(PdfReader.mPager.getCurrentItem(), width, heigh);
		        rpt.run();
	            Log.i(TAG, "pathFile :  " + pathFile);		        			        	
	        	Bitmap bMap = PdfReader.mImageFetcher.processBitmap(pathFile);
		        Log.i(TAG, "pathFile dopo bitmap :  " + pathFile);				        
		        PdfReaderFragment.refresh(bMap);
			
			} else if (PdfReader.isZoom && positionRender == PdfReader.mPager.getCurrentItem()) {
				
				RenderPageThread rpt = new RenderPageThread(positionRender, width, heigh);
		        rpt.run();
                Bitmap bMap = PdfReader.mImageFetcher.processBitmap(pathFile);
                Log.i(TAG, "pathFile dopo bitmap zoom :  " + pathFile);				        
                PdfReaderFragment.refresh(bMap);
			
			} else {
				
				    RenderPageThread rpt = new RenderPageThread(PdfReader.mPager.getCurrentItem(), width, heigh);
			        rpt.run();
                    Bitmap bMap = PdfReader.mImageFetcher.processBitmap(pathFile);
                    Log.i(TAG, "pathFile dopo bitmap zoom :  " + pathFile);				        
                    PdfReaderFragment.refresh(bMap);
			}
//			

//			}
		
//			this.notify();
//			renderontop.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	

	  
}
