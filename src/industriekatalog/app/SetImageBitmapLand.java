package industriekatalog.app;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.LinearLayout;

public class SetImageBitmapLand extends Thread implements Runnable {

	  protected static final String TAG = "SetImageBitmapLand";
	  public int counterSendImagePosition=0;
	  RenderOnTop renderontop;
	  public static String pathFile;
	  public static LinearLayout.LayoutParams params;
	  PdfReader pdfreader;
	  PdfReaderFragment PDF;
	  
	  private int position;
	  private int width;
	  private int heigh;

	  
	  
	public SetImageBitmapLand(int pos, int width, int heigh) {
		this.position = pos;
		this.width = width;
		this.heigh = heigh;
	}

	@Override
	public void run() {
					
		Log.i(TAG, "SendImagePositionLand :  " + counterSendImagePosition);
		Log.i(TAG, "This Thread Is running :  " + SetImageBitmap.currentThread().getName());
		Log.i(TAG, "Thread.activeCount() :  " + Thread.activeCount());
		counterSendImagePosition++;
		
		
		try {
			
			if (!PdfReader.isZoom && position == PdfReader.mPagerLand.getCurrentItem()) {
				
					if (position == 0) {

						RenderPageThread rpt = new RenderPageThread(position, width, heigh);
				        rpt.run();
			            Log.i(TAG, "pathFile :  " + pathFile);		        			        	
			        	Bitmap bMap1 = PdfReader.mImageFetcher.processBitmap(pathFile);
				        Log.i(TAG, "pathFile dopo bitmap 1 :  " + pathFile);
				        RenderPageThread rpt2 = new RenderPageThread(position+1, width, heigh);
				        rpt2.run();
			            Log.i(TAG, "pathFile :  " + pathFile);		        			        	
			        	Bitmap bMap2 = PdfReader.mImageFetcher.processBitmap(pathFile);
				        Log.i(TAG, "pathFile dopo bitmap 1 :  " + pathFile);
				        				        
				        PdfReaderFragmentLand.refreshLand(combineImages(bMap1,bMap2));

					} else if (position*2+1 >= PdfReader.total_pages) {
						
						RenderPageThread rpt = new RenderPageThread(position*2-1, width, heigh);
				        rpt.run();
			            Log.i(TAG, "pathFile :  " + pathFile);		        			        	
			        	Bitmap bMap1 = PdfReader.mImageFetcher.processBitmap(pathFile);
				        Log.i(TAG, "pathFile dopo bitmap 1 :  " + pathFile);
				        PdfReaderFragmentLand.refreshLand(bMap1);
				      
			        } else {

    			RenderPageThread rpt = new RenderPageThread(position*2-1, width, heigh);
		        rpt.run();
	            Log.i(TAG, "pathFile :  " + pathFile);		        			        	
	        	Bitmap bMap1 = PdfReader.mImageFetcher.processBitmap(pathFile);
		        Log.i(TAG, "pathFile dopo bitmap 1 :  " + pathFile);
		        RenderPageThread rpt2 = new RenderPageThread(position*2, width, heigh);
		        rpt2.run();
	            Log.i(TAG, "pathFile :  " + pathFile);		        			        	
	        	Bitmap bMap2 = PdfReader.mImageFetcher.processBitmap(pathFile);
		        Log.i(TAG, "pathFile dopo bitmap 1 :  " + pathFile);
		        PdfReaderFragmentLand.refreshLand(combineImages(bMap1,bMap2));			        
			        
				}
			}
			
			else if (!PdfReader.isZoom && position != PdfReader.mPagerLand.getCurrentItem()) {
								
				if (PdfReader.mPagerLand.getCurrentItem() == 0) {

					RenderPageThread rpt = new RenderPageThread(PdfReader.mPagerLand.getCurrentItem(), width, heigh);
			        rpt.run();
		            Log.i(TAG, "pathFile :  " + pathFile);		        			        	
		        	Bitmap bMap1 = PdfReader.mImageFetcher.processBitmap(pathFile);
			        Log.i(TAG, "pathFile dopo bitmap 1 :  " + pathFile);
			        RenderPageThread rpt2 = new RenderPageThread(PdfReader.mPagerLand.getCurrentItem()+1, width, heigh);
			        rpt2.run();
		            Log.i(TAG, "pathFile :  " + pathFile);		        			        	
		        	Bitmap bMap2 = PdfReader.mImageFetcher.processBitmap(pathFile);
			        Log.i(TAG, "pathFile dopo bitmap 1 :  " + pathFile);
			        PdfReaderFragmentLand.refreshLand(combineImages(bMap1,bMap2));


				} else if (PdfReader.mPagerLand.getCurrentItem()*2+1 >= PdfReader.total_pages) {
					
					RenderPageThread rpt = new RenderPageThread(PdfReader.mPagerLand.getCurrentItem()*2-1, width, heigh);
			        rpt.run();
		            Log.i(TAG, "pathFile :  " + pathFile);		        			        	
		        	Bitmap bMap1 = PdfReader.mImageFetcher.processBitmap(pathFile);
			        Log.i(TAG, "pathFile dopo bitmap 1 :  " + pathFile);
			        PdfReaderFragmentLand.refreshLand(bMap1);
			      
		        } else {

			RenderPageThread rpt = new RenderPageThread(PdfReader.mPagerLand.getCurrentItem()*2-1, width, heigh);
	        rpt.run();
            Log.i(TAG, "pathFile :  " + pathFile);		        			        	
        	Bitmap bMap1 = PdfReader.mImageFetcher.processBitmap(pathFile);
	        Log.i(TAG, "pathFile dopo bitmap 1 :  " + pathFile);
	        RenderPageThread rpt2 = new RenderPageThread(PdfReader.mPagerLand.getCurrentItem()*2, width, heigh);
	        rpt2.run();
            Log.i(TAG, "pathFile :  " + pathFile);		        			        	
        	Bitmap bMap2 = PdfReader.mImageFetcher.processBitmap(pathFile);
	        Log.i(TAG, "pathFile dopo bitmap 1 :  " + pathFile);
	        PdfReaderFragmentLand.refreshLand(combineImages(bMap1,bMap2));			        
		    }
			
		}
			else if (PdfReader.isZoom && position == PdfReader.mPagerLand.getCurrentItem()) {
				
					if (position == 0) {

						RenderPageThread rpt = new RenderPageThread(position, width, heigh);
				        rpt.run();
			            Log.i(TAG, "pathFile :  " + pathFile);		        			        	
			        	Bitmap bMap1 = PdfReader.mImageFetcher.processBitmap(pathFile);
				        Log.i(TAG, "pathFile dopo bitmap 1 :  " + pathFile);
				        RenderPageThread rpt2 = new RenderPageThread(position+1, width, heigh);
				        rpt2.run();
			            Log.i(TAG, "pathFile :  " + pathFile);		        			        	
			        	Bitmap bMap2 = PdfReader.mImageFetcher.processBitmap(pathFile);
				        Log.i(TAG, "pathFile dopo bitmap 1 :  " + pathFile);
				        				        
				        PdfReaderFragmentLand.refreshLand(combineImages(bMap1,bMap2));

					} else if (position*2+1 >= PdfReader.total_pages) {
						
						RenderPageThread rpt = new RenderPageThread(position*2-1, width, heigh);
				        rpt.run();
			            Log.i(TAG, "pathFile :  " + pathFile);		        			        	
			        	Bitmap bMap1 = PdfReader.mImageFetcher.processBitmap(pathFile);
				        Log.i(TAG, "pathFile dopo bitmap 1 :  " + pathFile);
				        PdfReaderFragmentLand.refreshLand(bMap1);
				      
			        } else {

    			RenderPageThread rpt = new RenderPageThread(position*2-1, width, heigh);
		        rpt.run();
	            Log.i(TAG, "pathFile :  " + pathFile);		        			        	
	        	Bitmap bMap1 = PdfReader.mImageFetcher.processBitmap(pathFile);
		        Log.i(TAG, "pathFile dopo bitmap 1 :  " + pathFile);
		        RenderPageThread rpt2 = new RenderPageThread(position*2, width, heigh);
		        rpt2.run();
	            Log.i(TAG, "pathFile :  " + pathFile);		        			        	
	        	Bitmap bMap2 = PdfReader.mImageFetcher.processBitmap(pathFile);
		        Log.i(TAG, "pathFile dopo bitmap 1 :  " + pathFile);
		        PdfReaderFragmentLand.refreshLand(combineImages(bMap1,bMap2));			   
			        
				}
			
			} else {
				
				if (PdfReader.mPagerLand.getCurrentItem() == 0) {

					RenderPageThread rpt = new RenderPageThread(PdfReader.mPagerLand.getCurrentItem(), width, heigh);
			        rpt.run();
		            Log.i(TAG, "pathFile :  " + pathFile);		        			        	
		        	Bitmap bMap1 = PdfReader.mImageFetcher.processBitmap(pathFile);
			        Log.i(TAG, "pathFile dopo bitmap 1 :  " + pathFile);
			        RenderPageThread rpt2 = new RenderPageThread(PdfReader.mPagerLand.getCurrentItem()+1, width, heigh);
			        rpt2.run();
		            Log.i(TAG, "pathFile :  " + pathFile);		        			        	
		        	Bitmap bMap2 = PdfReader.mImageFetcher.processBitmap(pathFile);
			        Log.i(TAG, "pathFile dopo bitmap 1 :  " + pathFile);
			        PdfReaderFragmentLand.refreshLand(combineImages(bMap1,bMap2));


				} else if (PdfReader.mPagerLand.getCurrentItem()*2+1 >= PdfReader.total_pages) {
					
					RenderPageThread rpt = new RenderPageThread(PdfReader.mPagerLand.getCurrentItem()*2-1, width, heigh);
			        rpt.run();
		            Log.i(TAG, "pathFile :  " + pathFile);		        			        	
		        	Bitmap bMap1 = PdfReader.mImageFetcher.processBitmap(pathFile);
			        Log.i(TAG, "pathFile dopo bitmap 1 :  " + pathFile);
			        PdfReaderFragmentLand.refreshLand(bMap1);
			      
		        } else {

			RenderPageThread rpt = new RenderPageThread(PdfReader.mPagerLand.getCurrentItem()*2-1, width, heigh);
	        rpt.run();
            Log.i(TAG, "pathFile :  " + pathFile);		        			        	
        	Bitmap bMap1 = PdfReader.mImageFetcher.processBitmap(pathFile);
	        Log.i(TAG, "pathFile dopo bitmap 1 :  " + pathFile);
	        RenderPageThread rpt2 = new RenderPageThread(PdfReader.mPagerLand.getCurrentItem()*2, width, heigh);
	        rpt2.run();
            Log.i(TAG, "pathFile :  " + pathFile);		        			        	
        	Bitmap bMap2 = PdfReader.mImageFetcher.processBitmap(pathFile);
	        Log.i(TAG, "pathFile dopo bitmap 1 :  " + pathFile);
	        PdfReaderFragmentLand.refreshLand(combineImages(bMap1,bMap2));			        
		    }
				
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	 public Bitmap combineImages(Bitmap c, Bitmap s) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom 
         Bitmap cs = null; 

         int width, height = 0; 

         if(c.getWidth() > s.getWidth()) { 
           width = c.getWidth() + s.getWidth(); 
           height = c.getHeight(); 
         } else { 
           width = s.getWidth() + s.getWidth(); 
           height = c.getHeight(); 
         } 

         cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); 

         Canvas comboImage = new Canvas(cs); 

         comboImage.drawBitmap(c, 0f, 0f, null); 
         comboImage.drawBitmap(s, c.getWidth(), 0f, null); 
         return cs; 
       } 

	

	  
}

