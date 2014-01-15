package konstruktion.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import konstruktion.app.R;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class ThumbNailsGrid extends Activity implements OnItemClickListener{
	
	private GridView thumbNail;
	private ImageAdapter imageAdapter;
	private Display display; // width of the screen
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
     // Request progress bar
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.sdcard);

        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        setupViews();
        setProgressBarIndeterminateVisibility(true); 
        loadImages();
    }
	
	/**
     * Free up bitmap related resources.
     */
    protected void onDestroy() {
        super.onDestroy();
        final GridView grid = thumbNail;
        final int count = grid.getChildCount();
        ImageView v = null;
        for (int i = 0; i < count; i++) {
            v = (ImageView) grid.getChildAt(i);
            ((BitmapDrawable) v.getDrawable()).setCallback(null);
        }
    }
    
    /**
     * Setup the grid view.
     */
    private void setupViews() {
    	thumbNail = (GridView) findViewById(R.id.sdcard);
    	thumbNail.setNumColumns(display.getWidth()/95);
    	thumbNail.setClipToPadding(false);
    	thumbNail.setOnItemClickListener(ThumbNailsGrid.this);
        imageAdapter = new ImageAdapter(getApplicationContext()); 
        thumbNail.setAdapter(imageAdapter);
    }
    
    /**
     * Load images.
     */
    private void loadImages() {
        final Object data = getLastNonConfigurationInstance();
        if (data == null) {
            new LoadImagesFromSDCard().execute();
        } else {
            final LoadedImage[] photos = (LoadedImage[]) data;
            if (photos.length == 0) {
                new LoadImagesFromSDCard().execute();
            }
            for (LoadedImage photo : photos) {
                addImage(photo);
            }
        }
    }
    
    /**
     * Add image(s) to the grid view adapter.
     * 
     * @param value Array of LoadedImages references
     */
    private void addImage(LoadedImage... value) {
        for (LoadedImage image : value) {
            imageAdapter.addPhoto(image);
            imageAdapter.notifyDataSetChanged();
        }
    }
    
    /**
     * Save bitmap images into a list and return that list. 
     * 
     * @see android.app.Activity#onRetainNonConfigurationInstance()
     */
    @Override
    public Object onRetainNonConfigurationInstance() {
        final GridView grid = thumbNail;
        final int count = grid.getChildCount();
        final LoadedImage[] list = new LoadedImage[count];

        for (int i = 0; i < count; i++) {
            final ImageView v = (ImageView) grid.getChildAt(i);
            list[i] = new LoadedImage(((BitmapDrawable) v.getDrawable()).getBitmap());
        }

        return list;
    }
	
    class LoadImagesFromSDCard extends AsyncTask<Object, LoadedImage, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			    Bitmap bitmap = null;
	            Bitmap newBitmap = null;
	       
	            String temp = "/neleso/temp/";
	            Uri uri = Uri.parse(temp);            

	            // Set up an array of the Thumbnail Image ID column we want
	            String[] projection = {MediaStore.Images.Thumbnails._ID};
	            // Create the cursor pointing to the SDCard
	            Cursor cursor = managedQuery( MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
	                    projection, // Which columns to return
	                    null,       // Return all rows
	                    null,       
	                    null); 
	            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
	            int size = cursor.getCount();
	            // If size is 0, there are no images on the SD Card.
	            if (size == 0) {
	                //No Images available, post some message to the user
	            }
	            int imageID = 0;
	            for (int i = 0; i < size; i++) {
	                cursor.moveToPosition(i);
	                imageID = cursor.getInt(columnIndex);
	                uri = Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID);
	                try {
	                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
	                    if (bitmap != null) {
	                        newBitmap = Bitmap.createScaledBitmap(bitmap, 70, 70, true);
	                        bitmap.recycle();
	                        if (newBitmap != null) {
	                            publishProgress(new LoadedImage(newBitmap));
	                        }
	                    }
	                } catch (IOException e) {
	                    //Error fetching image, try to recover
	                }
	            }
	            cursor.close();				
			    return null;
		}
		
		/**
         * Add a new LoadedImage in the images grid.
         *
         * @param value The image.
         */
        @Override
        public void onProgressUpdate(LoadedImage... value) {
            addImage(value);
        }	
		
        /**
         * Set the visibility of the progress bar to false.
         * 
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Object result) {
            setProgressBarIndeterminateVisibility(false);
        }
    } // end class fromsd..
        
    class ImageAdapter extends BaseAdapter {

        private Context mContext; 
        private ArrayList<LoadedImage> photos = new ArrayList<LoadedImage>();

        public ImageAdapter(Context context) { 
            mContext = context; 
        } 

        public void addPhoto(LoadedImage photo) { 
            photos.add(photo); 
        } 

        public int getCount() { 
            return photos.size(); 
        } 

        public Object getItem(int position) { 
            return photos.get(position); 
        } 

        public long getItemId(int position) { 
            return position; 
        } 

        public View getView(int position, View convertView, ViewGroup parent) { 
            final ImageView imageView; 
            if (convertView == null) { 
                imageView = new ImageView(mContext); 
            } else { 
                imageView = (ImageView) convertView; 
            } 
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(8, 8, 8, 8);
            imageView.setImageBitmap(photos.get(position).getBitmap());
            return imageView; 
        } 
    }

    /**
     * A LoadedImage contains the Bitmap loaded for the image.
     */
    private static class LoadedImage {
        Bitmap mBitmap;

        LoadedImage(Bitmap bitmap) {
            mBitmap = bitmap;
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

}
