package industriekatalog.app;

import android.util.Log;

public class RenderOnTop extends Thread implements Runnable {
	
	private static final String TAG = "RenderOnTop";
	public int counterRenderOnTop=0;
//	PdfReaderFragment pdffrag;
	
	public RenderOnTop() { 
	}
	
	@Override
	public void run() {
		
		Log.i(TAG, "Start RenderOnTop THREAD :" + counterRenderOnTop);
		Log.i(TAG, "AddView Done :" + counterRenderOnTop);
		Log.i(TAG, "This Thread Is running :  " + RenderOnTop.currentThread().getName());
		Log.i(TAG, "Thread.activeCount() :  " + Thread.activeCount());
		
		
//		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
//		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_DISPLAY);
		
		try {
			counterRenderOnTop++;
//           if (PdfReader.isZoom) {
        	   
	
//        	   mResultsRegion.removeAllViews();				              						
//         	   mResultsRegion.requestLayout();
//			   mResultsRegion.addView(image);
	//		   PdfReader.mAdapter.notifyDataSetChanged();
			   
	//		   PdfReaderFragment.framelayout.removeAllViews();		
//			   PdfReaderFragment.framelayout.requestLayout();
//			   PdfReaderFragment.relativelayout.addView(PdfReaderFragment.mResultsRegion2);
//            } else {
//          
//				
//			    PdfReaderFragment.mResultsRegion2.removeAllViews();					
//            	PdfReaderFragment.mResultsRegion.removeAllViews();						
//            	PdfReaderFragment.mResultsRegion2.requestLayout();  
//            	PdfReaderFragment.mResultsRegion2.addView(PdfReaderFragment.imageZoom);
//            	PdfReader.mAdapter.notifyDataSetChanged();
//            }
		
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		}


	
	
}
