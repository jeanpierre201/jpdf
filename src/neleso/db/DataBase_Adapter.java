package neleso.db;

import java.io.ByteArrayInputStream;
import java.util.LinkedList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DataBase_Adapter {
	
	private static final String TAG = "DataBase_Adapter";

	// Database fields
	public static final String KEY_ROWID = "_id";
	public static final String KEY_GUID = "guid";
	public static final String KEY_TITLE = "title";
	public static final String KEY_SUBTITLE = "subtitle";
	public static final String KEY_THUMBNAIL_LINK = "thumbnail_link";
	public static final String KEY_THUMBNAIL = "thumbnail";
	public static final String KEY_PDF_LINK = "pdf_link";
	public static final String KEY_PDF_FILE = "pdf_file";
	public static final String KEY_PUBDATE = "pub_date";
	public static final String KEY_PRODUCT_IDENTIFIER = "product_identifier";
	public static final String KEY_UID = "uid";
	
	private static final String DB_TABLE = "pdf";
	
	
	private Context context;
	private SQLiteDatabase db;
	private DataBase_Helper dbHelper;

	public DataBase_Adapter(Context context) {
		this.context = context;
	}

	public DataBase_Adapter open() throws SQLException {
		dbHelper = new DataBase_Helper(context);
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * Create a new todo If the todo is successfully created return the new
	 * rowId for that note, otherwise return a -1 to indicate failure.
	 */
	public long createTodo(String guid, String title, String subtitle, String thumbnail_link, byte[] thumbnail, String pdf_link, String pdf_file, String pub_date, String product_identifier, String uid) {
		ContentValues values = createContentValues(guid, title, subtitle, thumbnail_link, thumbnail, pdf_link, pdf_file, pub_date, product_identifier, uid);	
		return db.insert(DB_TABLE, null, values);
	}

	/**
	 * Update the todo
	 */
	public boolean updateTodo(long rowId, String guid, String title, String subtitle, String thumbnail_link, byte[] thumbnail, String pdf_link, String pdf_file, String pub_date, String product_identifier, String uid) {
		ContentValues values = createContentValues(guid, title, subtitle, thumbnail_link, thumbnail, pdf_link, pdf_file, pub_date, product_identifier, uid);

		return db.update(DB_TABLE, values, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * Deletes todo
	 */
	public boolean deleteTodo(long rowId) {
		return db.delete(DB_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * Return a Cursor over the list of all todo in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllTodos() {
		return db.query(DB_TABLE, new String[] { KEY_ROWID, KEY_GUID, KEY_TITLE, KEY_SUBTITLE,
				KEY_THUMBNAIL_LINK, KEY_THUMBNAIL, KEY_PDF_LINK, KEY_PDF_FILE, KEY_PUBDATE, KEY_PRODUCT_IDENTIFIER, KEY_UID }, null, null, null, null, null);
	}

	/**
	 * Return a Cursor positioned at the defined todo
	 */
	public Cursor fetchTodo(long rowId) throws SQLException {
		Cursor mCursor = db.query(true, DB_TABLE, new String[] { KEY_ROWID, KEY_GUID, KEY_TITLE, KEY_SUBTITLE,
				KEY_THUMBNAIL_LINK, KEY_THUMBNAIL, KEY_PDF_LINK, KEY_PDF_FILE, KEY_PUBDATE, KEY_PRODUCT_IDENTIFIER, KEY_UID }, KEY_ROWID + "="
				+ rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	/**
	 * The search method on 3 columns
	 */
	
	 public LinkedList<String> search(String search) {
	        
	        LinkedList<String> results = new LinkedList<String>();
	        Cursor mCursor = null;
	        try{
	        	mCursor = this.db.query(true, DB_TABLE, new String[] { KEY_GUID }, DB_TABLE + " MATCH ?", new String[] { search },null, null, null, null);
	            
	            if(mCursor!=null && mCursor.getCount() > 0 && mCursor.moveToFirst()){
	                
	            	int iGuid = mCursor.getColumnIndex(KEY_GUID);
	        //        int iTitle = mCursor.getColumnIndex(KEY_TITLE);
	        //        int iProductIdentifier = mCursor.getColumnIndex(KEY_PRODUCT_IDENTIFIER);
	               

	                do{
	                    results.add(
	                        new String(
	                            "Guid : "+mCursor.getString(iGuid)
	          //                  ", Title: "+mCursor.getString(iTitle) +
	          //                  ", Product_identifier: "+mCursor.getString(iProductIdentifier)
	                        )
	                    );
	                }while(mCursor.moveToNext());
	            }
	            
	        } catch(Exception e) {
	            Log.e ("Searching", "An error occurred while searching for "+ search +": "+e.toString(), e);
	        } finally {
	        	
	            if(mCursor != null && !mCursor.isClosed()){
	            	mCursor.close();
	            }
	        }
	        
	        return results;
	    }
	
	public String[] getColumn(String keyTitle) {
		
		 Cursor mCursor = null;
		 mCursor = this.db.query(DB_TABLE, new String[] { KEY_ROWID, KEY_GUID, KEY_TITLE, KEY_SUBTITLE,
					KEY_THUMBNAIL_LINK, KEY_THUMBNAIL, KEY_PDF_LINK, KEY_PDF_FILE, KEY_PUBDATE, KEY_PRODUCT_IDENTIFIER, KEY_UID }, null, null, null, null, null);
		 
              String[] result = new String[mCursor.getCount()];
              if(mCursor.moveToFirst()){
                   for (int i = 0; i < mCursor.getCount(); i++){
                       result[i] = mCursor.getString(mCursor.getColumnIndex(keyTitle));
                       mCursor.moveToNext();
                     }//end of for
              }
             mCursor.close();
           
		
		return result;
		
	}
	
	public String getKeyRowId(String keyTitle, String Title) {
		
		 String RowId = null;
		 Cursor mCursor = null;
		 mCursor = this.db.query(DB_TABLE, new String[] { KEY_ROWID, KEY_GUID, KEY_TITLE, KEY_SUBTITLE,
					KEY_THUMBNAIL_LINK, KEY_THUMBNAIL, KEY_PDF_LINK, KEY_PDF_FILE, KEY_PUBDATE, KEY_PRODUCT_IDENTIFIER, KEY_UID }, null, null, null, null, null);
		 
             String[] result = new String[mCursor.getCount()];
             if(mCursor.moveToFirst()){
                  for (int i = 0; i < mCursor.getCount(); i++){
                      result[i] = mCursor.getString(mCursor.getColumnIndex(keyTitle));
                      if (result[i].contains(Title)) {
                    	  RowId = mCursor.getString(mCursor.getColumnIndex(KEY_ROWID));
                    	  Log.i(TAG, "numero row id " + RowId);
                      }                   	  
                      mCursor.moveToNext();
                    }//end of for
             }
            mCursor.close();
          
		
		return RowId;
		
	}
	
	
	public Bitmap[] getColumnThumb(String keyTitle) {
		
		 Cursor mCursor = null;
		 mCursor = this.db.query(DB_TABLE, new String[] { KEY_ROWID, KEY_GUID, KEY_TITLE, KEY_SUBTITLE,
					KEY_THUMBNAIL_LINK, KEY_THUMBNAIL, KEY_PDF_LINK, KEY_PDF_FILE, KEY_PUBDATE, KEY_PRODUCT_IDENTIFIER, KEY_UID }, null, null, null, null, null);
		 
             Bitmap[] result = new Bitmap[mCursor.getCount()];
             
             if(mCursor.moveToFirst()){
                  
            	 for (int i = 0; i < mCursor.getCount(); i++){
               	 
            		 byte[]  blob;
					 blob = mCursor.getBlob(mCursor.getColumnIndex(keyTitle));
					 ByteArrayInputStream imageStream = new ByteArrayInputStream(blob);     
					 result[i] = BitmapFactory.decodeStream(imageStream);
                     mCursor.moveToNext();                      
                    }//end of for
             }
            mCursor.close();
          
		
		return result;
		
	}
	
	
	private ContentValues createContentValues(String guid, String title, String subtitle, String thumbnail_link, byte[] thumbnail, String pdf_link, String pdf_file, String pub_date, String product_identifier, String uid) {
	    byte yes[] = thumbnail;
		ContentValues values = new ContentValues();
		values.put(KEY_GUID, guid);
		values.put(KEY_TITLE, title);
		values.put(KEY_SUBTITLE, subtitle);
		values.put(KEY_THUMBNAIL_LINK, thumbnail_link);
		values.put(KEY_THUMBNAIL, yes);
		values.put(KEY_PDF_LINK, pdf_link);
		values.put(KEY_PDF_FILE, pdf_file);
		values.put(KEY_PUBDATE, pub_date);
		values.put(KEY_PRODUCT_IDENTIFIER, product_identifier);
		values.put(KEY_UID, uid);
		return values;
	}


	
	



}
