package industriekatalog.app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import industriekatalog.app.R;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

public class ThumbnailsMenu extends Dialog {
	
	protected static final String TAG = "ThumbnailsMenu";
	public static int CurrentPage;
	public Boolean even;
	public Boolean pair;
	PdfReader pdfreader;
	public int counterOnItemClick=0;
	public int counterRenderPageBack=0;
	public static ExecutorService taskList;
	
	public void analyseDigits(int number) {
		
		  if (number % 2 == 0) {
	        	even = true; 	 		
	    	} else {
	    		pair = false;  		
	    	}    		
		}
	
	 public ThumbnailsMenu(Context context) {
		super(context);
		
		/** It will hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.thumbnails);
		Gallery g = (Gallery) findViewById(R.id.gallery);
		g.setAdapter(new ImageAdapter(context));
		g.setOnItemClickListener(new OnItemClickListener() {
              public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	           	             		             	
            		  
            		  PdfReader.mPager.setCurrentItem(position);           		  
            		  Log.i(TAG, "counterOnItemClick :  " + counterOnItemClick);
      				
      				try {		
      					CurrentPage = position;
      					Log.i(TAG, "CurrentPage :  " + CurrentPage);
      					Log.i(TAG, "position :  " + position);
      			//		renderPageBackground(position);		
      					counterOnItemClick++;
      				} catch (Exception e) {
      					// TODO Auto-generated catch block
      					e.printStackTrace();
      				}
              }
			
          });

	}
	 
	  public void renderPageBackground(int pos) {
	      
	 
	      try {
	    	        PdfReader.isZoom = false;
	    	  
	//    	        SetImageBitmap.currentThread().interrupt();	
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

	 

	 
	  private class ImageAdapter extends BaseAdapter {
	       
	        int mGalleryItemBackground;
	        private Context mContext;


	        public ImageAdapter(Context c) {
	            mContext = c;

	            TypedArray a = mContext.obtainStyledAttributes(R.styleable.Theme);
	            mGalleryItemBackground = a.getResourceId(R.styleable.Theme_android_galleryItemBackground, 0);
	            a.recycle();
	        }

	        public int getCount() {
	            return PdfReader.total_pages;
	        }

	        public Object getItem(int position) {
	            return position;
	        }

	        public long getItemId(int position) {
	            return position;
	        }

	        public View getView(int position, View convertView, ViewGroup parent) {
	        	
	        	
	            ImageView i = new ImageView(mContext);           	
	            i.setImageURI(PdfReader.thumb[position]);
    	           	            	
//	            } else if (position < 20 ) {
//	            	
//	            	  Bitmap bit = mImageFetcher.processBitmap(thumbString[position]);
//	            	  i.setImageBitmap(bit);    

 	            	  i.setLayoutParams(new Gallery.LayoutParams(160, 200));
	    	          i.setScaleType(ImageView.ScaleType.FIT_XY);
	    	          i.setBackgroundResource(mGalleryItemBackground);
	            	  return i;
	           
	        }
	    }

}
