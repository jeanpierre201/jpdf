package konstruktion.app;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

public class ThumbnailsMenu extends Dialog {
	
	public Boolean even;
	public Boolean pair;
	PdfReader pdfreader;
	
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
            	  
  //          	  PdfReader.or = pdfreader.getResources().getConfiguration().orientation;            	  
            	  if (PdfReader.or == 1) {	            		             	
            		  PdfReader.mPager.setCurrentItem(position);	            		  
            	  } else {           			
	            			int posi = position/2;
	            			PdfReader.viewPager_LandScape.setCurrentItem(posi);            
            	  }
              }
			
          });

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
	        	
	        //  //  	            bit[position] = mImageFetcher.processBitmap(thumbString[position]);
	        //  //  	            Log.d(TAG, "vediamo" + thumbString[position] );
	        //  //  	            i.setImageBitmap(bit[position]);
//	            	            i.setImageURI(thumb[position]);
//	            	            i.setLayoutParams(new Gallery.LayoutParams(160, 200));
//	            	            i.setScaleType(ImageView.ScaleType.FIT_XY);
//	            	            i.setBackgroundResource(mGalleryItemBackground);
	        	
	            ImageView i = new ImageView(mContext);
	            
	            if (position < 10) {  	            	
	            	i.setImageURI(PdfReader.thumb[position]);
    	           	            	
//	            } else if (position < 20 ) {
//	            	
//	            	  Bitmap bit = mImageFetcher.processBitmap(thumbString[position]);
//	            	  i.setImageBitmap(bit);    
//
//	            } else {
//	            	
//	            	 createThumbnails(position);
//	            	 Bitmap bit = mImageFetcher.processBitmap(thumbString[position]);
//	            	     i.setImageBitmap(bit); 
	            }

 	            	  i.setLayoutParams(new Gallery.LayoutParams(160, 200));
	    	          i.setScaleType(ImageView.ScaleType.FIT_XY);
	    	          i.setBackgroundResource(mGalleryItemBackground);
	            	  return i;
	           
	        }
	    }

}
