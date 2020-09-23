package industriekatalog.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.library.imagezoom.ImageViewTouchBase;
import android.library.imagezoom.RotateBitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager.BadTokenException;
import android.widget.ProgressBar;

public class ZoomPage extends ImageViewTouchBase  {
	
	static final float					MIN_ZOOM	= 1.0f;
	public ScaleGestureDetector	        mScaleDetector;
	public GestureDetector			    mGestureDetector;
	protected int						mTouchSlop;
	protected float						mCurrentScaleFactor;
	protected float						mScaleFactor;
	protected int						mDoubleTapDirection;
	protected GestureListener			mGestureListener;
	protected ScaleListener				mScaleListener;


	public boolean fingerZoomIn = false;	
	private static final String TAG = "ZoomPage";
	public int width;
	private int height;
	ThumbnailsMenu menu;
	private int position=0;	
	private int maxWidth = (int) (NelesoApp.width * MAX_ZOOM);
	private int maxHeight = (int) (RenderPageThread.targetHeight * MAX_ZOOM);
	public static ProgressDialog progressDialog;
	volatile boolean startZoomDialog = false;
	volatile boolean maxPageSize = false;
	volatile boolean doubleTap = false;

	

	public ZoomPage(Context context) {
		super(context);
	}
	
	public ZoomPage( Context context, AttributeSet attrs )
	{
		super(context, attrs);
	}
	
	@Override
	protected void init()
	{
		super.init();
		mTouchSlop = ViewConfiguration.getTouchSlop();
		mGestureListener = new GestureListener();
		mScaleListener = new ScaleListener();
		
		mScaleDetector = new ScaleGestureDetector( getContext(), mScaleListener );
		mGestureDetector = new GestureDetector( getContext(), mGestureListener, null, true );
		mCurrentScaleFactor = 1f;
		mDoubleTapDirection = 1;		
		
	}
	
	@Override
	public void setImageRotateBitmapReset( RotateBitmap bitmap, boolean reset )
	{
		super.setImageRotateBitmapReset( bitmap, reset );
		mScaleFactor = getMaxZoom() / 2;
		
	} 
	
	
	@Override
	public boolean onTouchEvent( MotionEvent event )
	{
		mScaleDetector.onTouchEvent( event );
		
		if ( !mScaleDetector.isInProgress() ) mGestureDetector.onTouchEvent( event );
		int action = event.getAction();
		switch ( action & MotionEvent.ACTION_MASK ) {
		
			case MotionEvent.ACTION_POINTER_UP:               
				
				if ( getScale() < 1f ) {
					
					zoomTo( 1f, 50 );
					
				} else if (getScale() > 1.01f && !maxPageSize ) {
					
					PdfReader.mPager.setPagingEnabled(false);
					
					if (getScale() == MAX_ZOOM) {
						 maxPageSize = true;
					}
					
					try {	
						new Thread(new Runnable() {
							   public void run() {
								   
								    PdfReader.isZoom = true;
								    position =  PdfReader.mPager.getCurrentItem();						   
								    SetImageBitmap.currentThread().interrupt();	    	        
								    SetImageBitmap sib = new SetImageBitmap(position, width, height);  // is changed in the max calculation on ImageViewTouchBase
					    	        sib.run();			    	       
								        }
								  }).start();
						
					} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				}					
				break;
		}
		
		return true;
	}


	@Override
	public void onZoom( float scale )
	{	
		Log.d("vediamo " +scale, "scale");
		width = (int) (NelesoApp.width * scale);
		height = (int) (RenderPageThread.targetHeight * scale);
		Log.d("width " +width, "scale");
		Log.d("height " +height, "scale");
		
		super.onZoom( scale );
		if ( !mScaleDetector.isInProgress() ) 		
			mCurrentScaleFactor = scale;	
		    
        if (scale > 1.01f) {
      	
			Log.d("Inicia la Scala > 1f " +scale, "scale");
			fingerZoomIn = true;
		
            if (scale < MAX_ZOOM) {
				maxPageSize = false;
			}
		}
		
		if (fingerZoomIn && scale == 1f ) {			
			Log.d(TAG + "Termina la Scala  " +scale, "scale");
			fingerZoomIn = false;
			PdfReader.mPager.setPagingEnabled(true);
		}
		
	}	
	protected float onDoubleTapPost( float scale, float maxZoom )
	{
		
		
		if ( mDoubleTapDirection == 1 ) {
			if ( ( scale + ( mScaleFactor * maxZoom ) ) <= maxZoom ) {
				PdfReader.mPager.setPagingEnabled(false);
	//			mDoubleTapDirection = 1;
				return scale + mScaleFactor;
			} else   {
				mDoubleTapDirection = -1;	
				PdfReader.mPager.setPagingEnabled(false);
				return maxZoom;
			}
		} else {
			mDoubleTapDirection = 1;
			PdfReader.mPager.setPagingEnabled(true);
			return 1f;
			
		}
	}
	
	class GestureListener extends GestureDetector.SimpleOnGestureListener {
		

		@Override
		public boolean onDoubleTap( MotionEvent e )
		{
			doubleTap = true;
			float scale = getScale();
			float targetScale = scale;
			if (scale == MAX_ZOOM ) {
				
				targetScale = onDoubleTapPost( scale, getMaxZoom() );
				targetScale = Math.min( getMaxZoom(), Math.max( targetScale, MIN_ZOOM ) );
				mCurrentScaleFactor = targetScale;
				zoomTo( targetScale, e.getX(), e.getY(), 200 );
				invalidate();
				return super.onDoubleTap( e );
				
			} else {
				
		    doubleTap = true;
			targetScale = onDoubleTapPost( scale, getMaxZoom() );
			targetScale = Math.min( getMaxZoom(), Math.max( targetScale, MIN_ZOOM ) );
			mCurrentScaleFactor = targetScale;
			zoomTo( targetScale, e.getX(), e.getY(), 200 );
			invalidate();
		
			new Thread(new Runnable() {
				   public void run() {
					   
					    PdfReader.isZoom = true;
					    position =  PdfReader.mPager.getCurrentItem();						   
		    	        SetImageBitmap.currentThread().interrupt();	    	        
		    	        SetImageBitmap sib = new SetImageBitmap(position, maxWidth, maxHeight);  // is changed in the max calculation on ImageViewTouchBase
		    	        sib.run();			    	       
					        }
					  }).start();
			
			return super.onDoubleTap( e );
			}
		}
		
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			ThumbnailsMenu customizeDialog = new ThumbnailsMenu(PdfReader.cxt);
			customizeDialog.setCanceledOnTouchOutside(true);
			customizeDialog.getWindow().setGravity(Gravity.BOTTOM);
			customizeDialog.getWindow().setLayout(LayoutParams.FILL_PARENT, 200);
			customizeDialog.show();
		return super.onSingleTapConfirmed(e);	
		}
		
		
		@Override
		public boolean onScroll( MotionEvent e1, MotionEvent e2, float distanceX, float distanceY )
		{
			if ( e1 == null || e2 == null ) return false;
			if ( e1.getPointerCount() > 1 || e2.getPointerCount() > 1 ) return false;
			if ( mScaleDetector.isInProgress() ) return false;
			if ( getScale() == 1f ) return false;
			scrollBy( -distanceX, -distanceY );
			invalidate();
			return super.onScroll( e1, e2, distanceX, distanceY );
		}
		
		@Override
		public boolean onFling( MotionEvent e1, MotionEvent e2, float velocityX, float velocityY )
		{
			if ( e1.getPointerCount() > 1 || e2.getPointerCount() > 1 ) return false;
			if ( mScaleDetector.isInProgress() ) return false;
			
			float diffX = e2.getX() - e1.getX();
			float diffY = e2.getY() - e1.getY();
			
			if ( Math.abs( velocityX ) > 800 || Math.abs( velocityY ) > 800 ) {
				scrollBy( diffX / 2, diffY / 2, 300 );
				invalidate();
			}
			return super.onFling( e1, e2, velocityX, velocityY );
		}
	}
	
	class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		
		@SuppressWarnings( "unused" )
		@Override
		public boolean onScale( ScaleGestureDetector detector )
		{
			float span = detector.getCurrentSpan() - detector.getPreviousSpan();
			float targetScale = mCurrentScaleFactor * detector.getScaleFactor();
			if ( true ) {
				targetScale = Math.min( getMaxZoom(), Math.max( targetScale, MIN_ZOOM ) );
				zoomTo( targetScale, detector.getFocusX(), detector.getFocusY() );
				mCurrentScaleFactor = Math.min( getMaxZoom(), Math.max( targetScale, MIN_ZOOM ) );
				mDoubleTapDirection = 1;
				invalidate();
				return true;
			}
			return false;
		}
	}
}
