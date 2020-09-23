package industriekatalog.app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import industriekatalog.app.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bitmap.util.ImageWorker;
import android.bitmap.util.RecyclingImageView;
import android.bitmap.util.Utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.library.imagezoom.ImageViewTouch;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * This fragment will populate the children of the ViewPager from {@link ImageDetailActivity}.
 */
public class PdfReaderFragmentLand extends Fragment  {
	
    private static final String IMAGE_DATA_EXTRA = "extra_image_data";
    private static final String IMAGE_DATA_EXTRA2 = "extra_image_data2";
	private static final String TAG = "PdfReaderFragmentLand";
    private String mImageUri1;
    private String mImageUri2;
    public static Context context;
    public RecyclingImageView page1;
    public RecyclingImageView page2;
    public static ImageFetcherPdf mImageFetcher1;
    public static ImageFetcherPdf mImageFetcher2;
    PdfReader pdfreader;
	public static RenderOnTop renderonTop;
	public static FrameLayout framelayout;
	public static Bitmap bitmap;
	public static int position;
	public LinearLayout mResultsRegion;
	public LinearLayout mResultsRegion2;
	public ZoomPageLand imageZoom;
	public View v;
	public Boolean mbReloadForm;
	public static android.widget.LinearLayout.LayoutParams params;
	public static android.widget.LinearLayout.LayoutParams params2;
	public static int half_width;
	RelativeLayout mRelativeLayout;
	RelativeLayout mRelativeLayout2;
	static ProgressBar mProgress;
	
	public static Fragment newInstance(String mImageUri1, String mImageUri2) {
		final PdfReaderFragmentLand f = new PdfReaderFragmentLand();
		final Bundle args = new Bundle();
		args.putString(IMAGE_DATA_EXTRA, mImageUri1);
		args.putString(IMAGE_DATA_EXTRA2, mImageUri2);
	    f.setArguments(args);
	    return f;
	}

    /**
     * Empty constructor as per the Fragment documentation
     */
    public PdfReaderFragmentLand() {}

    /**
     * Populate image using a uri from extras, use the convenience factory method
     * {@link PdfReaderFragment#newInstance(String)} to create this fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUri1 = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA) : null;
        mImageUri2 = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA2) : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState) {
    	 v = inflater.inflate(R.layout.image_fragment_land, container, false);
    	 PopulateForm();
    	 return v;
    }

    public void PopulateForm() {
    	
    	float width = PdfReader.mPagerLand.getMeasuredWidth();
    	half_width = (int) (width/2);
 	
        framelayout = (FrameLayout) v.findViewById(R.id.framelayoutLand);
        
        mResultsRegion = new LinearLayout(PdfReader.cxt);  
		mResultsRegion = (LinearLayout)v.findViewById(R.id.linearLayoutland);
		
	//	mProgress=(ProgressBar) v. findViewById(R.id.ProgressLoading);
 	   
		
		mResultsRegion2 = new LinearLayout(PdfReader.cxt);  
		mResultsRegion2 = (LinearLayout) v. findViewById(R.id.linearLayoutland2);

        
        page1 = new RecyclingImageView(PdfReader.cxt);
        page1.setScaleType(ImageView.ScaleType.FIT_END);
        page2 = new RecyclingImageView(PdfReader.cxt);
        page2.setScaleType(ImageView.ScaleType.FIT_START);

        
        mRelativeLayout = new RelativeLayout(PdfReader.cxt);
        params = new LinearLayout.LayoutParams(half_width, LayoutParams.MATCH_PARENT);
    	params.leftMargin = 0;
    	params.topMargin = 0;
    	mRelativeLayout.addView(page1, params);
 
    	
    	mRelativeLayout2 = new RelativeLayout(PdfReader.cxt);
    	params2 = new LinearLayout.LayoutParams(half_width, LayoutParams.MATCH_PARENT);
    	params2.leftMargin = half_width;
    	params2.topMargin = 0;
//    	params2.gravity = Gravity.CENTER_VERTICAL;
//    	params2.gravity = Gravity.CENTER_HORIZONTAL;
    	mRelativeLayout2.addView(page2, params2);
    	
       	mResultsRegion.addView(mRelativeLayout);
    	mResultsRegion.addView(mRelativeLayout2); 

	     return;
		
	}


	@SuppressLint("NewApi")
	@Override
	public void onResume() {
       	super.onResume();
       	
    }
    


	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Use the parent activity to load the image asynchronously into the ImageView (so a single
        // cache can be used over all pages in the ViewPager
        if (PdfReader.class.isInstance(getActivity())) {
            mImageFetcher1 = ((PdfReader) getActivity()).getImageFetcher();
            mImageFetcher2 = ((PdfReader) getActivity()).getImageFetcher();
   	        mImageFetcher1.loadImage(mImageUri1, page1);   
   	        mImageFetcher2.loadImage(mImageUri2, page2);
   	        
   	        if (PdfReader.mPagerLand.getCurrentItem() == 0) {
   	        	
      	     new Thread(new Runnable() {
     	      public void run() {  	    	  
  
     	   	     Log.d(TAG, "CurrentPage CurrentPageOnpageChange " + PdfReader.CurrentPageOnpageChange);
     	         renderPageBackground(0);
     	      }
     	    }).start();
   	        }
   	        
   	        page1.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View view) {
    				ThumbnailsMenuLand customizeDialog = new ThumbnailsMenuLand(PdfReader.cxt);
    				customizeDialog.setCanceledOnTouchOutside(true);
    				customizeDialog.getWindow().setGravity(Gravity.BOTTOM);
    				customizeDialog.getWindow().setLayout(LayoutParams.FILL_PARENT, 200);
    				customizeDialog.show(); 				
    			}
    		});
   	        
   	        page2.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View view) {
    				ThumbnailsMenuLand customizeDialog = new ThumbnailsMenuLand(PdfReader.cxt);
    				customizeDialog.setCanceledOnTouchOutside(true);
    				customizeDialog.getWindow().setGravity(Gravity.BOTTOM);
    				customizeDialog.getWindow().setLayout(LayoutParams.FILL_PARENT, 200);
    				customizeDialog.show(); 				
    			}
    		});
        }
       

        // Pass clicks on the ImageView to the parent activity to handle
//        if (OnClickListener.class.isInstance(getActivity()) && Utils.hasHoneycomb()) {
//            mImageView.setOnClickListener((OnClickListener) getActivity());
//        }
        

    }


	@Override
    public void onDestroy() {
        super.onDestroy();
        if (page1 != null) {
            // Cancel any pending image work
            ImageWorker.cancelWork(page1);
            page1.setImageDrawable(null);
        } else if (page2 != null) {
            // Cancel any pending image work
            ImageWorker.cancelWork(page2);
            page2.setImageDrawable(null);
        } 
    }

	public static void refreshLand(Bitmap bMap1) {
		
        final Bitmap bitmap1 = bMap1;
        
      
        
        int index = PdfReader.mPagerLand.getCurrentItem();
        PdfReader.ImagePagerAdapterLand adapter = ((PdfReader.ImagePagerAdapterLand)PdfReader.mPagerLand.getAdapter());
        final PdfReaderFragmentLand fragment = adapter.getFragment(index);
        
        Log.d(TAG, "CurrentPage Refresh " + index);
        
  //      mProgress.setVisibility(View.INVISIBLE);
        
        fragment.mResultsRegion.post(new Runnable() {
        	
        	
        
        	public void run() {
        		     		
        		if (!PdfReader.isZoom) {
        			
        			fragment.imageZoom = new ZoomPageLand(PdfReader.cxt);
        			fragment.imageZoom.setImageBitmap(bitmap1);
        			fragment.imageZoom.setImageBitmapReset(bitmap1, true);
        			fragment.imageZoom.setScaleType(ImageView.ScaleType.MATRIX);
        			
        			fragment.mResultsRegion.removeAllViews();
        	        fragment.mResultsRegion2.removeAllViews();
        	        fragment.mResultsRegion2.requestLayout();
        	        fragment.mResultsRegion2.addView(fragment.imageZoom);
//        	        fragment.mResultsRegion.addView(fragment.mRelativeLayout2);
      			
        		} else {
        			    			
        			fragment.imageZoom.setImageBitmap(bitmap1);
        			fragment.mResultsRegion2.removeAllViews();											
        			fragment.mResultsRegion2.requestLayout();  
        			fragment.mResultsRegion2.addView(fragment.imageZoom);  

        		}

        }
    });
	
	}
	
	  public void renderPageBackground(int pos) {  
			 
	      try {
	    	        PdfReader.isZoom = false;    	  
	    	        SetImageBitmapLand.currentThread().interrupt();	
	    	        Log.i(TAG, "position antes:  " + pos);
	    	        SetImageBitmapLand SIB = new SetImageBitmapLand(pos, NelesoApp.width, NelesoApp.heigh);
	    	        SIB.run();
	    	        Log.i(TAG, "position despues:  " + pos);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

	}

}


