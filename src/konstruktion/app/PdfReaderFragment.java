package konstruktion.app;

import android.bitmap.util.ImageWorker;
import android.bitmap.util.Utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * This fragment will populate the children of the ViewPager from {@link ImageDetailActivity}.
 */
public class PdfReaderFragment extends Fragment {
	
    private static final String IMAGE_DATA_EXTRA = "extra_image_data";
    private String mImageUri;
    public static Context context;
    private ImageView mImageView;
    private ImageFetcherPdf mImageFetcher;
    private ImageViewTouch instantView;
    PdfReader pdfreader;
	public FrameLayout framelayout;
	public static Bitmap bitmap;
    /**
     * Factory method to generate a new instance of the fragment given an image number.
     *
     * @param imageUri The image uri to load
     * @param cxt 
     * @param bMap2 
     * @return A new instance of ImageDetailFragment with imageNum extras
     */
    public static PdfReaderFragment newInstance(String imageUri, Context cxt, Bitmap bMap2) {
        final PdfReaderFragment f = new PdfReaderFragment();
        context = cxt;
        bitmap = bMap2;
        final Bundle args = new Bundle();
        args.putString(IMAGE_DATA_EXTRA, imageUri);
        f.setArguments(args);
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
   	    	
        View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
        framelayout = (FrameLayout) v.findViewById(R.id.framelayout); 
        instantView = new ImageViewTouch(context);
        instantView.setImageBitmapReset(bitmap, true);
        framelayout.addView(instantView);
        
 //       mImageView = (ImageView) v.findViewById(R.id.imageView);
//        instantView = (ImageViewTouch) v.findViewById(R.id.imageView);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Use the parent activity to load the image asynchronously into the ImageView (so a single
        // cache can be used over all pages in the ViewPager
        if (PdfReader.class.isInstance(getActivity())) {
            mImageFetcher = ((PdfReader) getActivity()).getImageFetcher();
            mImageFetcher.loadImage(mImageUri, instantView);
        }

        // Pass clicks on the ImageView to the parent activity to handle
//        if (OnClickListener.class.isInstance(getActivity()) && Utils.hasHoneycomb()) {
//            mImageView.setOnClickListener((OnClickListener) getActivity());
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (instantView != null) {
            // Cancel any pending image work
            ImageWorker.cancelWork(instantView);
            instantView.setImageDrawable(null);
        }
    }

	public Context getContext() {
		// TODO Auto-generated method stub
		return context;
	}
}

