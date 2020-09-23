package industriekatalog.app;


import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Vector;

import industriekatalog.app.PdfReader.CreateThumbThread;
import industriekatalog.app.R;
import neleso.db.DataBase_Adapter;
import neleso.storefront.XMLHandler;
import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ArchiveList extends ListActivity {
	
	public static final String EXTRA_PDFFILENAME = "PDFFILENAME";
	protected static final String TAG = "ArchiveList";
	
	private LayoutInflater mInflater;
	private Vector<RowData> data;
	private DataBase_Adapter dbHelper;
	RowData rd;
	private Bitmap[] thumbnail;
	String[] pdf_name;
	String[] title;
	String[] subtitle;
	NelesoApp neleso;
	String url_help3;
    Long rowId;
    int position;
    Long ide;
    ImageButton toolbarheader;
    public Uri header;
    public static ImageButton button2;	
    boolean nexus;
  
    
    
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	
	
	setContentView(R.layout.archive_list);

    
    
// insert the toolbarhader	

	toolbarheader  = (ImageButton) findViewById(R.id.header);
	if (NelesoApp.screenInches > 9 && NelesoApp.heigh > 1280 ) {
	header = Uri.parse(Environment.getExternalStorageDirectory().toString() +"/neleso/"+XMLHandler.Magazine+"/toolbarheader_10.png");
	} else if (NelesoApp.screenInches >= 7) {	
	header = Uri.parse(Environment.getExternalStorageDirectory().toString() +"/neleso/"+XMLHandler.Magazine+"/toolbarheader_7.png");
	} else {
	header = Uri.parse(Environment.getExternalStorageDirectory().toString() +"/neleso/"+XMLHandler.Magazine+"/toolbarheader_7.png");	
	}
	
	toolbarheader.setImageURI(header);
	mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	data = new Vector<RowData>();
	dbHelper = new DataBase_Adapter(this);
	ImageButton button2  = (ImageButton) findViewById(R.id.archive);
	button2.setSelected(true);
	
	try {
		fillData();
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  // Save UI state changes to the savedInstanceState.
	  // This bundle will be passed to onCreate if the process is
	  // killed and restarted.

	  savedInstanceState.putStringArray(null, title);
	  savedInstanceState.putStringArray(null, subtitle);
	  savedInstanceState.putStringArray(null, pdf_name);
//	  savedInstanceState.putParcelableArray(null, thumbnail);
	  thumbnail = null;
	  // etc.
	  super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  // Restore UI state from the savedInstanceState.
	  // This bundle has also been passed to onCreate.
//	  thumbnail = (Bitmap[]) savedInstanceState.getParcelableArray(null);
	  title = savedInstanceState.getStringArray(null);
	  subtitle = savedInstanceState.getStringArray(null);
	  pdf_name = savedInstanceState.getStringArray(null);
	}
	
	
	
//	 @Override
//	    public boolean onKeyDown(int keyCode, KeyEvent event) {
//	        // Check if the key event was the BACK key and if there's history
//     	    button2.setSelected(false);	        	
//	        return super.onKeyDown(keyCode, event);
//	    }
	
	
	   private void fillData() throws UnsupportedEncodingException {
		   
			dbHelper.open();
										
			title = new String[] { DataBase_Adapter.KEY_TITLE };
			title =  dbHelper.getColumn(DataBase_Adapter.KEY_TITLE);
			subtitle = new String[] { DataBase_Adapter.KEY_SUBTITLE };
			subtitle =  dbHelper.getColumn(DataBase_Adapter.KEY_SUBTITLE);
			pdf_name = new String[] { DataBase_Adapter.KEY_PDF_FILE };
			pdf_name =  dbHelper.getColumn(DataBase_Adapter.KEY_PDF_FILE);
			thumbnail =  dbHelper.getColumnThumb(DataBase_Adapter.KEY_THUMBNAIL);
			
						    	
			for(int i = 0; i < title.length; i++){
						
				try {				
					 
					rd = new RowData(i,title[i],subtitle[i]);
				 	
				    } catch (ParseException e) {
				    	e.printStackTrace();
				   }
				   data.add(rd);
				}		
			
			       dbHelper.close();
			       CustomAdapter adapter = new CustomAdapter(this, R.layout.list, R.id.title, data);
				   setListAdapter(adapter);
				   getListView().setTextFilterEnabled(true);
				   getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					    @Override
					    public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
					        return onLongListItemClick(v,pos,id);
					    }
					});


	}
	   
	   protected boolean onLongListItemClick(View v, int pos, long id) {
		  
		      position = pos;
		  
               //set up dialog
               final Dialog dialog = new Dialog(this);
               dialog.setContentView(R.layout.deletedialog);
               dialog.setCancelable(true);

               //set up text
               TextView text = (TextView) dialog.findViewById(R.id.Delete01);
               text.setText(R.string.menu_delete);

               //set up button
               Button button = (Button) dialog.findViewById(R.id.delete_ok);
               Button button2 = (Button) dialog.findViewById(R.id.delete_cancel);
               button.setOnClickListener(new OnClickListener() {
               @Override
                   public void onClick(View v) {
            	   
         //   	   String path = "/neleso" + pdf_name[pos] ;
       	//		   String filePath = android.os.Environment.getExternalStorageDirectory().getPath() + path;
       //	       File file = new File(filePath);
       //	       file.delete();
       	           dbHelper.open();
       	           String GetrowId = dbHelper.getKeyRowId(DataBase_Adapter.KEY_TITLE, title[position]);
       	           Log.i(TAG, "title " + title[position]);
       	           Log.i(TAG, "key title " + DataBase_Adapter.KEY_TITLE);
       	           Log.i(TAG, "numero row id " + GetrowId);      	           
       	           rowId = Long.parseLong(GetrowId);
       	           Log.i(TAG, "numero row id " + rowId);
       	           dbHelper.deleteTodo(rowId);
       	           dbHelper.close();
       	           rd = new RowData(position,title[position],subtitle[position]);
    	           data.removeElement(rd);
       	           dialog.dismiss();
                   }
               });
               
               button2.setOnClickListener(new OnClickListener() {
                   @Override
                       public void onClick(View v) {               	   
                	  dialog.cancel();
                       }
                   });
                    
               dialog.show();
               return true;
           }

	   
	   public void onListItemClick(ListView parent, View v, int position,long id) {        	
		 		
	        String path = "/neleso" + pdf_name[position] ;
			String file = android.os.Environment.getExternalStorageDirectory().getPath() + path;
			Intent intent = new Intent(ArchiveList.this, PdfReader.class).putExtra(EXTRA_PDFFILENAME, file);
	    	startActivity(intent);
		}
	   

	   
	   private class RowData {
		   
	       protected int mId;
	       protected String mTitle;
	       protected String mDetail;
	       RowData(int id,String title,String detail){
	      
	       mId=id;
	       mTitle = title;
	       mDetail=detail;
	    }
	       
	       @Override
	       public String toString() {
	               return mId+" "+mTitle+" "+mDetail;
	       }
	}
	   
	  private class CustomAdapter extends ArrayAdapter<RowData> {

	  public CustomAdapter(Context context, int resource, int textViewResourceId, List<RowData> objects) {               

	  super(context, resource, textViewResourceId, objects);
	  
	  }
	  
	      @Override
	       public View getView(int position, View convertView, ViewGroup parent) {   

	       ViewHolder holder = null;
	       TextView title = null;
	       TextView subtitle = null;
	       ImageView i11 = null;
	       RowData rowData= getItem(position);
	       
	       if (null == convertView) {
	    	   
	            convertView = mInflater.inflate(R.layout.list, null);
	            holder = new ViewHolder(convertView);
	            convertView.setTag(holder);
	 }
	             holder = (ViewHolder) convertView.getTag();
	             title = holder.gettitle();
	             title.setText(rowData.mTitle);
	             subtitle = holder.getdetail();
	             subtitle.setText(rowData.mDetail);                                                     

	             i11 = holder.getImage();
	             i11.setImageBitmap(thumbnail[rowData.mId]);
	             return convertView;
	}
	          
	      
	      private class ViewHolder {
	    	  
	            private View mRow;
	            private TextView title = null;
	            private TextView detail = null;
	            private ImageView i11 = null; 

	            public ViewHolder(View row) {
	            mRow = row;
	 }
	         public TextView gettitle() {
	             if(null == title){
	                 title = (TextView) mRow.findViewById(R.id.title);
	                }
	            return title;
	         }     

	         public TextView getdetail() {
	             if(null == detail){
	                  detail = (TextView) mRow.findViewById(R.id.textView2);
	                    }
	           return detail;
	           
	         }
	        public ImageView getImage() {
	             if(null == i11){
	                  i11 = (ImageView) mRow.findViewById(R.id.img);
	                                      }
	                return i11;
	        }
	     }
	     
	   } 
	  
	  
	  // FOOTER ______________________________________________________________________
	    
	    
	  public void callIntent(View view) throws Exception {
			
	   	    ImageButton button  = (ImageButton) findViewById(R.id.kiosk);
			ImageButton button2  = (ImageButton) findViewById(R.id.archive);
			ImageButton button3  = (ImageButton) findViewById(R.id.news);
			ImageButton button4  = (ImageButton) findViewById(R.id.website);
			ImageButton button5  = (ImageButton) findViewById(R.id.help);
			
		   MenuManager menu = new MenuManager(this, view);
		   menu.callIntent(this, view, button, button2, button3, button4, button5);
		   
		   
	  }  
		          
//		    	if (button.setSelected(true)) {
//		    		button.setSelected(true);   }		
////		    	} else if (getUrl_load() == XMLHandler.url_news) {
//		    		button3.setSelected(true);
//		    		button.setSelected(false);
//		    	} else if (getUrl_load() == XMLHandler.url_website) {
//		    		button4.setSelected(true);
//		    		button.setSelected(false);
//		    	} else if (getUrl_load() == XMLHandler.url_help) {
//		    		button5.setSelected(true);
//		    		button.setSelected(false);
//		    	} 
//		     	webview.loadUrl(getUrl_load());
//		 	
//		    }
		   
		 
	    
	    // FOOTER ______________________________________________________________________

	
	  


}

