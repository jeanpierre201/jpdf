package industriekatalog.app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import industriekatalog.app.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bitmap.util.ImageWorker;
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
import android.view.WindowManager.BadTokenException;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * This fragment will populate the children of the ViewPager from {@link ImageDetailActivity}.
 */
public class PdfReaderFragment extends Fragment {
	
    private static final String IMAGE_DATA_EXTRA = "extra_image_data";
	private static final String TAG = "PdfReaderFragment";
    private String mImageUri;
    public static Context context;
    public ImageView image;
    public ImageView image2;
    public static ImageFetcherPdf mImageFetcher;
    PdfReader pdfreader;
	public static RenderOnTop renderonTop;
	public static FrameLayout framelayout;
	public static Bitmap bitmap;
	public int position;
	public LinearLayout mResultsRegion;
	public LinearLayout mResultsRegion2;
	public ZoomPage imageZoom;
	public View v;
	public Boolean mbReloadForm;
	public String Position;
	public static ProgressDialog progressDialog;
	
    
	/**
     * Factory method to generate a new instance of the fragment given an image number.
	 * @param mContext 
     *
     * @param imageUri The image uri to load
     * @param cxt 
     * @param bMap2 
     * @return A new instance of ImageDetailFragment with imageNum extras
     */
	
	public static Fragment newInstance(String mImageUri, Context mContext) {
		final PdfReaderFragment f = new PdfReaderFragment();
		final Bundle args = new Bundle();
		args.putString(IMAGE_DATA_EXTRA, mImageUri);
	    f.setArguments(args);
	    context = mContext; 
	    return f;
	}

    /**
     * Empty constructor as per the Fragment documentation
     */
    public PdfReaderFragment() {}

    /**
     * Populate image using a uri from extras, use the convenience factory method
     * {@link PdfReaderFragment#newInstance(String)} to create this fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUri = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA) : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate and locate the main ImageView  	
    	 v = inflater.inflate(R.layout.image_fragment, container, false);
    	 PopulateForm();
    	 return v;
    }

    public void PopulateForm() {
 	
        framelayout = (FrameLayout) v.findViewById(R.id.framelayout); 
        
        mResultsRegion = new LinearLayout(context);  
		mResultsRegion = (LinearLayout)v.findViewById(R.id.linearLayout);
		
		mResultsRegion2 = new LinearLayout(context);  
		mResultsRegion2 = (LinearLayout) v. findViewById(R.id.linearLayout2);
		
		image = (ImageView)v.findViewById(R.id.imageView);	

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
            mImageFetcher = ((PdfReader) getActivity()).getImageFetcher();
   	        mImageFetcher.loadImage(mImageUri, image);

   	        if (PdfReader.mPager.getCurrentItem() == 0) {
   	        	
      	     new Thread(new Runnable() {
     	      public void run() {  	    	  
  
     	   	     Log.d(TAG, "CurrentPage CurrentPageOnpageChange " + PdfReader.CurrentPageOnpageChange);
     	         renderPageBackground(0);
     	      }
     	    }).start();
   	        }
   	        

	        
   	        image.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View view) {
    				ThumbnailsMenu customizeDialog = new ThumbnailsMenu(PdfReader.cxt);
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
        if (image != null) {
            // Cancel any pending image work
            ImageWorker.cancelWork(image);
            image.setImageDrawable(null);
        }
    }

	public static void refresh(Bitmap bMap) {
		
        final Bitmap bitmap = bMap;
        int index = PdfReader.mPager.getCurrentItem();
        PdfReader.ImagePagerAdapter adapter = ((PdfReader.ImagePagerAdapter)PdfReader.mPager.getAdapter());
        final PdfReaderFragment fragment = adapter.getFragment(index);
        
        Log.d(TAG, "CurrentPage Refresh " + index);
        
  //      fragment.mResultsRegion.removeAllViews();
        fragment.mResultsRegion.post(new Runnable() {
        	
            
        
        	public void run() {
        		     		
        		if (!PdfReader.isZoom) {
        			
        			fragment.imageZoom = new ZoomPage(context);
        			fragment.imageZoom.setImageBitmap(bitmap);
        			fragment.imageZoom.setImageBitmapReset(bitmap, true);
        			fragment.imageZoom.setScaleType(ImageView.ScaleType.MATRIX);
        			fragment.mResultsRegion.removeAllViews();
        	        fragment.mResultsRegion2.removeAllViews();
        	        fragment.mResultsRegion2.requestLayout();
        	        fragment.mResultsRegion2.addView(fragment.imageZoom);

        		} else {
        			
        			fragment.imageZoom.setImageBitmap(bitmap);
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
	    	        SetImageBitmap.currentThread().interrupt();	
	    	        Log.i(TAG, "position antes:  " + pos);
	    	        SetImageBitmap SIB = new SetImageBitmap(pos, NelesoApp.width, NelesoApp.heigh);
	    	        SIB.run();
	    	        Log.i(TAG, "position despues:  " + pos);
//					Log.i(TAG, "VOIDrenderPageBackground :  " + counterRenderPageBack);
//					counterRenderPageBack++;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

	}
	  
		public static ProgressDialog createProgressDialog(Context mContext) {
	        ProgressDialog dialog = new ProgressDialog(mContext);
	        try {
	                dialog.show();
	        } catch (BadTokenException e) {

	        }
	        dialog.setCancelable(true);
	        dialog.setContentView(R.layout.progressdialog);
	        // dialog.setMessage(Message);
	        return dialog;
	}

}

