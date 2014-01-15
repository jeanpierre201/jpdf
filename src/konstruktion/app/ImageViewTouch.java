package konstruktion.app;

import android.content.Context;
import android.library.imagezoom.ImageViewTouchBase;
import android.library.imagezoom.RotateBitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

public class ImageViewTouch extends ImageViewTouchBase {
	
	private static final String TAG = "ImageViewTouch";
	
	static final float					MIN_ZOOM	= 0.9f;
	public ScaleGestureDetector	mScaleDetector;
	public GestureDetector			mGestureDetector;
	protected int							mTouchSlop;
	protected float						mCurrentScaleFactor;
	protected float						mScaleFactor;
	protected int							mDoubleTapDirection;
	protected GestureListener			mGestureListener;
	protected ScaleListener				mScaleListener;
	protected OnClickListener           mOnclickClickListener;


//	AwesomePagerAdapter pagerAdapter;
	
//	AwesomePagerAdapter view;
	public boolean singleTap;
	public boolean zoom = false;
	
	
	PdfReader pdfreader;
	PdfReaderFragment pdfreaderfragment;
	
	// Try
		
	public ImageViewTouch( Context context )
	{
		super(context);
	}
	
	public ImageViewTouch( Context context, AttributeSet attrs )
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
		
		singleTap = false;
		Log.d("ini " +singleTap, "inicio");
	}
	
	@Override
	public void setImageRotateBitmapReset( RotateBitmap bitmap, boolean reset )
	{
		super.setImageRotateBitmapReset( bitmap, reset );
		mScaleFactor = getMaxZoom() / 3;
		
	} 
	
	
	@Override
	public boolean onTouchEvent( MotionEvent event )
	{
		mScaleDetector.onTouchEvent( event );
		
		if ( !mScaleDetector.isInProgress() ) mGestureDetector.onTouchEvent( event );
		int action = event.getAction();
		switch ( action & MotionEvent.ACTION_MASK ) {
			case MotionEvent.ACTION_UP:
				if ( getScale() < 1f ) {
					zoomTo( 1f, 50 );
				}
				break;
		}
		return true;
	}
	
	@Override
	public void onZoom( float scale )
	{
		Log.d("vediamo " +scale, "scale");
		
		super.onZoom( scale );
		if ( !mScaleDetector.isInProgress() ) 		
			mCurrentScaleFactor = scale;
		
        if (scale > 1.3f) {
			
			Log.d("Inicia la Scala > 1f " +scale, "scale");
		}
		
		if (scale < 1f ||scale == 1f ) {
			
			Log.d(TAG + "Termina la Scala  " +scale, "scale");
			
		}
	}
		
	protected float onDoubleTapPost( float scale, float maxZoom )
	{
		if ( mDoubleTapDirection == 1 ) {
			if ( ( scale + ( mScaleFactor * 2 ) ) <= maxZoom ) {
//				view.blockPage(getRootView());
				return scale + mScaleFactor;
			} else {
				mDoubleTapDirection = -1;
				return maxZoom;
			}
		} else {
			mDoubleTapDirection = 1;
//			view.diblockPage(getRootView());
			return 1f;
			
		}
	}
	
	class GestureListener extends GestureDetector.SimpleOnGestureListener {
		

		@Override
		public boolean onDoubleTap( MotionEvent e )
		{
			float scale = getScale();
			float targetScale = scale;
			targetScale = onDoubleTapPost( scale, getMaxZoom() );
			targetScale = Math.min( getMaxZoom(), Math.max( targetScale, MIN_ZOOM ) );
			mCurrentScaleFactor = targetScale;
			zoomTo( targetScale, e.getX(), e.getY(), 200 );
			invalidate();
			return super.onDoubleTap( e );
		}
		
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			ThumbnailsMenu customizeDialog = new ThumbnailsMenu(PdfReaderFragment.context);
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
