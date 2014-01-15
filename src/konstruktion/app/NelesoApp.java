package konstruktion.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import konstruktion.app.R;
import konstruktion.app.R.menu;


import java.awt.MenuBar;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import konstruktion.app.Consts.PurchaseState;
import konstruktion.app.Dungeons.CatalogEntry;
import konstruktion.app.Dungeons.Managed;
import konstruktion.app.PurchaseDatabase.DatabaseHelper;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.VideoView;
import android.widget.ZoomControls;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.os.Environment;
import android.provider.Settings;

import neleso.storefront.XMLHandler;
import neleso.storefront.XMLParsing;
import neleso.utils.StringUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONObject;

import konstruktion.app.CompatActionBarNavListener;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.ZoomDensity;
import android.widget.Toast;
import neleso.db.*;

// NeLeSo - Kiosk is now under version control
// 15. 1. 2014 Version 1

public class NelesoApp extends FragmentActivity    {
	
	
	// Public static final Strings	
	public static final String EXTRA_PDFFILENAME = "PDFFILENAME";
	public static final String LOG_TAG = "Download Log";
	
	// Device info
	public static String deviceId;
	public static String version;
	public static double screenInches;
	public static int width;
	public static int heigh;
	public static int sdk;
	
	
	// Initialize our progress dialog/bar
    private ProgressDialog mProgressDialog;
    ProgressDialog progressBar;
    
	// Get SD root
    File rootDir = Environment.getExternalStorageDirectory();
    String path;		

	// Data Base fields   
	public String guid;
	public String title;
	String subtitle;	
	String thumbnail;
	String pub_date;
	public String product_identifier;
	String uid;
	public String thumbnail_link;
	public String pdf_file;
	private DataBase_Adapter mDbHelper;
	
	// Thumbnail blob array - thumbnail portrait
	ByteArrayBuffer baf ;
	
	// purchase DB und data
	private PurchaseDatabase mPurchaseDatabase;
	int order_num = 0;
	CatalogEntry catalog;
	Dungeons buy;
	
	// File to save the magazine
	public File mydir;

	
	// GUI Declaration '''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
	
	// Define HTTP Client in the first screen
	HttpClient httpclient = new DefaultHttpClient(); 
	HttpGet httpget;  // Set the action you want to do
	
	// Static Menu
	static WebView webview;
	static ImageButton button;
  	public static ImageButton button2;		
 	static ImageButton button3;
 	static ImageButton button4;
 	static ImageButton button5;
 	
 	// create token
 	private String currentPage;
   	
    // set the url to load by the buttons
	private static String set_url_load;
	
	// GUI END '''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
	
	
	// in process - create a loading page progress bar
	ProgressBar pbar;
	public DisplayMetrics dm;

	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
              
        deviceId = Settings.System.getString(getContentResolver(),Settings.System.ANDROID_ID);
        
    	// Get Inches
		dm = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(dm);
	    double x = Math.pow(dm.widthPixels/dm.xdpi,2);
	    double y = Math.pow(dm.heightPixels/dm.ydpi,2);
	    screenInches = Math.sqrt(x+y);
	    Log.d("debug","Screen inches : " + screenInches);
		
		// Get Resolution
	    
	    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
	    	width = dm.widthPixels;
		    heigh = dm.heightPixels;	    	
	    } else {
	    	heigh = dm.widthPixels;
	    	width = dm.heightPixels;
	    }
	    
	    Log.d("debug","Screen widht : " + width);
	    Log.d("debug","Screen heigh : " + heigh);
	    
	    // Get Version
	    sdk = Build.VERSION.SDK_INT;
        Log.d("SDK ", "SDK : " + String.valueOf(sdk));
        version = Build.VERSION.RELEASE;
        Log.d("Version ", "Version  : " +version);
        
        
        
        
        
        if (XMLHandler.url_kiosk == null) {
        	
        	 CopyAssets(XMLHandler.Magazine);
 	
        	try {
        		XMLParsing readXml = new XMLParsing();
        		readXml.getParsedMyXML(NelesoApp.this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } 
        
         
    //    getWindow().requestFeature(Window.FEATURE_PROGRESS); 
    //    pbar = (ProgressBar) findViewById(R.id.progressBar1);
        
        setContentView(R.layout.main);
             
        
        if (savedInstanceState != null) {
            ((WebView)findViewById(R.id.webView1)).restoreState(savedInstanceState); 
        } else {
        try {
			InitializeUI();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        }
    } // end onCreate ----------------------------------------------------------------------------------------------   
    
    
    
    
    @Override
	protected void onStart() {
		
    	super.onStart();
    	ImageButton button  = (ImageButton) findViewById(R.id.kiosk);
    	button.setSelected(true);
    	
    	webview.loadUrl(XMLHandler.url_kiosk);
    }
    
    
    
    
    
    @Override
	protected void onResume() {
    	super.onResume();
    	
    	button2.setSelected(false);
    	if (getUrl_load() == XMLHandler.url_kiosk) {
    		button.setSelected(true);   		
    	} else if (getUrl_load() == XMLHandler.url_news) {
    		button3.setSelected(true);
    		button.setSelected(false);
    	} else if (getUrl_load() == XMLHandler.url_website) {
    		button4.setSelected(true);
    		button.setSelected(false);
    	} else if (getUrl_load() == XMLHandler.url_help) {
    		button5.setSelected(true);
    		button.setSelected(false);
    	} 
     	webview.loadUrl(getUrl_load());
 	
    }
    
    
    
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
    }
    
    
	
	
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
    }
    
    
    
    
    
    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }
    
    
    
    // More activities states....
    
    protected void onSaveInstanceState(Bundle outState) {
    	webview.saveState(outState);
        webview.getOriginalUrl();
   	}
    
  
 //   @Override
 //   public void onConfigurationChanged(Configuration savedInstanceState)
 //   {
 //       super.onConfigurationChanged(savedInstanceState);
 //       setContentView(R.layout.main); 
 //       InitializeUI();
        
 //   }
    
      
	    @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	        // Check if the key event was the BACK key and if there's history
		 
		   
	//		   currentPage = webview.getOriginalUrl();
	     //	   Toast.makeText(this, currentPage, Toast.LENGTH_SHORT).show();
	//     	   if (currentPage.equals(XMLHandler.url_kiosk)) {
		    		button.setSelected(false); 		
	//	    	} else if (currentPage.equals(XMLHandler.url_news)) {
		    		button3.setSelected(false);
	//	    	} else if (currentPage.equals(XMLHandler.url_website)) {
		    		button4.setSelected(false);
	//	    	} else if (currentPage.equals(XMLHandler.url_help)) {
		    		button5.setSelected(false);
	//	    	}	  
	     	   
	   
	     	   
	        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) { 
	        	
	        	   webview.goBack();
	        	   currentPage = webview.getOriginalUrl();
	     //   	   Toast.makeText(this, currentPage3, Toast.LENGTH_SHORT).show();
	        	   if (currentPage.equals(XMLHandler.url_kiosk)) {
			    		button.setSelected(true); 		
			    	} else if (currentPage.equals(XMLHandler.url_news)) {
			    		button3.setSelected(true);
			    	} else if (currentPage.equals(XMLHandler.url_website)) {
			    		button4.setSelected(true);
			    	} else if (currentPage.equals(XMLHandler.url_help)) {
			    		button5.setSelected(true);
			    	}
	        		        	       	
	            return true;
	                    
	        }
	     

	        // If it wasn't the BACK key or there's no web page history, bubble up to the default
	        // system behavior (probably exit the activity)
	   //     Toast.makeText(getApplicationContext(), "No hay vuelta atras", Toast.LENGTH_LONG).show();    

	        return super.onKeyDown(keyCode, event);
	    }
	

    
 ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////   
    
    
    
    
    
    private void CopyAssets(String magazine) {
    	
    	File rootsd = Environment.getExternalStorageDirectory();
        Log.d("ROOT PATH", "" + rootsd.getAbsolutePath());
        
        // check neleso first 
        String DIR = "/neleso/";
        mydir = new File(rootsd.toString() + DIR);
        if (!mydir.exists()) {
       	 mydir.mkdir();
       }
        
        String DIR2 = "/neleso/"+magazine;
        mydir = new File(rootsd.toString() + DIR2);
        if (!mydir.exists()) {
       	 mydir.mkdir();
       }
            		
            AssetManager assetManager = getAssets();  
            
            String[] files = null;  
            try {  
                files = assetManager.list(magazine);  
            } catch (IOException e) {  
                Log.e("tag", e.getMessage());  
            }  
              
            for(String filename : files) {  
                          
                System.out.println("File name => "+filename);  
                InputStream in = null;  
                OutputStream out = null;  
                try {  
                  in = assetManager.open(magazine+"/"+filename);   // if files resides inside the "Magazine" directory itself  
                  out = new FileOutputStream(Environment.getExternalStorageDirectory().toString() +"/neleso/"+magazine+"/"+filename);  
                  copyFile(in, out);  
                  in.close();  
                  in = null;  
                  out.flush();  
                  out.close();  
                  out = null;  
                } catch(Exception e) {  
                    Log.e("tag", e.getMessage());  
                }         
            }
        }  
    	
    	
    	private void copyFile(InputStream in, OutputStream out) throws IOException {  
            byte[] buffer = new byte[1024];  
            int read;  
            while((read = in.read(buffer)) != -1){  
              out.write(buffer, 0, read);  
            }  
          
		
	}


	@SuppressLint("SetJavaScriptEnabled")
	private void InitializeUI() throws IOException {

        
       //    getWindow().requestFeature(Window.FEATURE_PROGRESS);
    	   
           webview = (WebView) findViewById(R.id.webView1);
           webview.getSettings().setJavaScriptEnabled(true);
           webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
           webview.getSettings().setBuiltInZoomControls(true);
           webview.setVerticalScrollBarEnabled(true);
           webview.setHorizontalScrollBarEnabled(true);
           webview.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);  
           webview.loadUrl(getUrl_load());
           Log.d("url", "" + getUrl_load());

           webview.setWebViewClient(new myWebViewClient());
         
           
        // Buttons
       	button  = (ImageButton) findViewById(R.id.kiosk);
//      	images[1] = Uri.parse(Environment.getExternalStorageDirectory().toString() +"/neleso/"+XMLHandler.Magazine+"/icon_01_kiosk.png");
//    	button.setImageURI(images[1]);
       	button2  = (ImageButton) findViewById(R.id.archive);
//       	images[2] = Uri.parse(Environment.getExternalStorageDirectory().toString() +"/neleso/"+XMLHandler.Magazine+"/icon_02_archive.png");
//    	button2.setImageURI(images[2]);
       	button3  = (ImageButton) findViewById(R.id.news);
//       	images[3] = Uri.parse(Environment.getExternalStorageDirectory().toString() +"/neleso/"+XMLHandler.Magazine+"/icon_03_news.png");
//    	button3.setImageURI(images[3]);
       	button4  = (ImageButton) findViewById(R.id.website);
//       	images[4] = Uri.parse(Environment.getExternalStorageDirectory().toString() +"/neleso/"+XMLHandler.Magazine+"/icon_04_www.png");
//    	button4.setImageURI(images[4]);
       	button5  = (ImageButton) findViewById(R.id.help);
//       	images[5] = Uri.parse(Environment.getExternalStorageDirectory().toString() +"/neleso/"+XMLHandler.Magazine+"/icon_05_help.png");
//    	button5.setImageURI(images[5]);
    	
	}
    
	
	
	
   
   
// Web View Settings ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^    
 
  

   /**        
           final Activity activity = this;
           webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
             // Activities and WebViews measure progress with different scales.
             // The progress meter will automatically disappear when we reach 100%
             activity.setProgress(progress * 100);
              d 
            }});     
      
    */        
	 
	
    
    // Handle myWebViewClient #######################################################################################
    
    private class myWebViewClient extends WebViewClient {
    	
    	public String pdf_link;
    
    	 
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
       
         	Log.d("Veamos Url", url); 
         	
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs     	    	
        	// Split the string url to create a newUrl --------------------------------------------------------------
        	
        	if (url.contains("action://mserver/null?")) {   
        	
         	String arr[] = url.split("/");
            for(int i = 0; i < arr.length;){
             	
        	// System.out.println("arr["+i+"] = " + arr[i].trim());
        	 String newUrl = "http://" + arr[5] + "/"+ arr[6] + "/" + arr[7];
        	 Log.d("Veamos newUrl", newUrl);
        	//  view.loadUrl(newUrl);
        	//  Toast.makeText(getApplicationContext(), newUrl, Toast.LENGTH_LONG).show();      	
        	// the newUrl has been created -------------------------------------------------------------------------
        	// Conditional for the contains in the newUrl ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        	
        	if(newUrl.contains("preview.php")==true)  
        	{  
        		Log.d("Veamos PreviewUrl", newUrl);
        		try {
					getParse(newUrl);
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			    
	           // url_pdf has been download and showed it ==================================================================================================
          	           	 
        	}  else if(newUrl.contains("buy.php")==true) {
        		
        		// for macwelt
        		showAlertDialog(newUrl);
        		
                // for others
        		
        	//	String newUrlBuy = "http://" + arr[5] + "/"+ arr[6] + "/" + arr[8];
        	//	 Log.e("Veamos newUrlBuy", newUrlBuy);
        	//	showAlertDialog(newUrlBuy);
        		
        	}  else if(newUrl.contains("download.php")==true) {
        		
        		Log.d("Veamos DownloadUrl", newUrl);
        		try {
					getParse(newUrl);
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			    
	           // url_pdf has been download and showed it ==================================================================================================
          
        		
        		
        	
        	} // end Conditional-----------------------------------------------------------------------------
        	   
			    return true;       
        }
      }
			return false;	
        }
        

        
        
        private void showAlertDialog(final String newUrl) {
       	 // Create an AlertDialog
   		
 		  AlertDialog.Builder builder = new AlertDialog.Builder(NelesoApp.this);
 		  builder.setMessage("Do you want to but it for 3,99 Euros Kaufen?").setCancelable(false)
 		  .setPositiveButton("Kaufen", new DialogInterface.OnClickListener() {
 			  
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					getValues(newUrl);
		//			new CatalogEntry(guid, 12 , Managed.MANAGED);
		//			CatalogEntry(guid, pdf_title, Dungeons.Managed.MANAGED);
			    	Intent intent = new Intent(NelesoApp.this, Dungeons.class);
			    	startActivity(intent);				    				
				}
			})
 	      .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					
				}
			});
 		  
 		  AlertDialog alert = builder.create();
		  alert.show();
			
		}

       protected void getValues(String newUrlBuy) {
			try {
	  	         
				Log.i("Veamos el link", newUrlBuy);
        		httpget = new HttpGet(newUrlBuy); 
				HttpResponse response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity(); 
				
				InputStream is = entity.getContent(); // Create an InputStream with the response
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				
				while ((line = reader.readLine()) != null) // Read line by line
				    sb.append(line + "\n");

				String html_content = sb.toString(); // Result is here
		    //  Log.e("Device id", deviceId);
				
				
				guid = StringUtils.substringBetween(html_content, "<guid>", "</guid>");
			    Log.d("Veamos el guid", guid);
	//			Toast.makeText(getApplicationContext(), guid, Toast.LENGTH_LONG).show();
				title = StringUtils.substringBetween(html_content, "<title>", "</title>");
				Log.d("Veamos el title", title);
	//			Toast.makeText(getApplicationContext(), title, Toast.LENGTH_LONG).show();
				subtitle = StringUtils.substringBetween(html_content, "<subtitle>", "</subtitle>");
				Log.d("Veamos el subtitle", subtitle);
				thumbnail_link = StringUtils.substringBetween(html_content, "<thumbnail>", "</thumbnail>");
				Log.d("Veamos el thumbnail_link", thumbnail_link);
				pdf_link = StringUtils.substringBetween(html_content, "<link>", "</link>");
				Log.d("Veamos el pdf_link", pdf_link);
				pdf_file = pdf_link.substring(pdf_link.lastIndexOf('/'));
				Log.d("Veamos el pdf_file", pdf_file);
	//			Toast.makeText(getApplicationContext(), pdf_link, Toast.LENGTH_LONG).show();
				pub_date = StringUtils.substringBetween(html_content, "<pubDate>", "</pubDate>");
				Log.d("Veamos el pub_date", pub_date);
				product_identifier = StringUtils.substringBetween(html_content, "<productIdentifier>", "</productIdentifier>");
	//			Log.d("Veamos el product_identifier", product_identifier);
	//			Toast.makeText(getApplicationContext(), product_identifier, Toast.LENGTH_LONG).show();
			
				// DB - BLOB
				new BLOB_Thumbnail().execute();
				
				is.close();
				
				mPurchaseDatabase = new PurchaseDatabase(NelesoApp.this);
		  		mPurchaseDatabase.open();
		  		long purchaseTime = new Date().getTime();
		  		PurchaseState state = Consts.PurchaseState.PURCHASED;
		  		order_num = order_num++;
		  		String order_id = Integer.toString(order_num);
		  		mPurchaseDatabase.updatePurchasedItem(product_identifier, 1);
		  		Dungeons.addToCatalog(order_id, title);
		  		mPurchaseDatabase.insertOrder(order_id, product_identifier, state, purchaseTime, null);
		  		mPurchaseDatabase.close();
		  		mPurchaseDatabase.close();
				
				mDbHelper = new DataBase_Adapter(NelesoApp.this);				
		  		mDbHelper.open();
		  	
		  		
		  		
		  		String[] pdf_column = new String[] { DataBase_Adapter.KEY_PDF_FILE };
		  		pdf_column =  mDbHelper.getColumn(DataBase_Adapter.KEY_PDF_FILE);
		  		boolean exist = false;
		  		
				  	     
		  	    String fileUrl = "/neleso" + pdf_file ;
				Log.e("Veamos el fileUrl", fileUrl);
		  	    String file = android.os.Environment.getExternalStorageDirectory().getPath() + fileUrl;
		  	    File f = new File(file);
	    
	
	    	    for (int i = 0; i < pdf_column.length; i++) {
	    	    	if ( pdf_column[i].matches(pdf_file)) {
	    	    		exist = true;
	    	    	}
	    	    }

	    	    /*
	  	    // ask user if he warts to overwrite the existing file.
		  	    // for now, we delete
		  	  if (f.exists()) {
		  		  f.delete();		  		  
		  	  }
				 
	    	    // seb - make sure the pdf is downloaded again
	    	    exist = false; 
	    	       
	    	     */
	    		     if (f.exists() && exist == true ) {
	    					 
		    				 Toast.makeText(NelesoApp.this, product_identifier + "Already Exist in folder and db", Toast.LENGTH_LONG).show();
			  			     Log.i("Veamos el product ID ", product_identifier);
			  			     mDbHelper.close();
			  			//     openPdf(file); 
			  			     
	    						 
	    			 } else if (f.exists() && exist == false ) {
	    			 
	    		    Toast.makeText(NelesoApp.this, product_identifier + "Already Exist in folder but not in db", Toast.LENGTH_LONG).show();	 
	    		    mDbHelper.createTodo(guid, title, subtitle, thumbnail_link, baf.toByteArray(), pdf_link, pdf_file, pub_date, product_identifier, deviceId);
	    		    mDbHelper.close();
	  		//	    openPdf(file); 
		  	        
	    			} else {
	    				
	    		        checkAndCreateDirectory("/neleso");
	    		        mDbHelper.close();
			  	      //  new DownloadFileAsync().execute(pdf_link);
	    			}
   		
   	
   		} catch (MalformedURLException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		        Log.i("System out","malformed :"+e.getMessage());
		        Toast.makeText(getApplicationContext(), "System out",Toast.LENGTH_SHORT).show();
		        Log.i("System out","malformed :"+e.toString());
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		        Log.i("System out","io: "+e.getMessage());
		        Log.i("System out","io: "+e.toString());
		    } // Execute it
			return;
			
		}



	// parse the xml and insert in DB and call the download 
		private void getParse(String newUrl) throws URISyntaxException {
			try {
  	         
		
				Log.d("Veamos el link getParse", newUrl);
		
				httpget = new HttpGet(newUrl); 
				HttpResponse response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity(); 
				
				InputStream is = entity.getContent(); // Create an InputStream with the response
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				
				while ((line = reader.readLine()) != null) // Read line by line
				    sb.append(line + "\n");

				String html_content = sb.toString(); // Result is here
		    //  Log.e("Device id", deviceId);

				
				guid = StringUtils.substringBetween(html_content, "<guid>", "</guid>");
			    Log.d("Veamos el guid", guid);
	//			Toast.makeText(getApplicationContext(), guid, Toast.LENGTH_LONG).show();
				title = StringUtils.substringBetween(html_content, "<title>", "</title>");
				Log.d("Veamos el title", title);
	//			Toast.makeText(getApplicationContext(), title, Toast.LENGTH_LONG).show();
				subtitle = StringUtils.substringBetween(html_content, "<subtitle>", "</subtitle>");
				Log.d("Veamos el subtitle", subtitle);
				thumbnail_link = StringUtils.substringBetween(html_content, "<thumbnail>", "</thumbnail>");
				Log.d("Veamos el thumbnail_link", thumbnail_link);
				pdf_link = StringUtils.substringBetween(html_content, "<link>", "</link>");
				Log.d("Veamos el pdf_link", pdf_link);
				pdf_file = pdf_link.substring(pdf_link.lastIndexOf('/'));
				Log.d("Veamos el pdf_file", pdf_file);
	//			Toast.makeText(getApplicationContext(), pdf_link, Toast.LENGTH_LONG).show();
				pub_date = StringUtils.substringBetween(html_content, "<pubDate>", "</pubDate>");
				Log.d("Veamos el pub_date", pub_date);
				product_identifier = StringUtils.substringBetween(html_content, "<productIdentifier>", "</productIdentifier>");
				Log.d("Veamos el product_identifier", product_identifier);
	//			Toast.makeText(getApplicationContext(), product_identifier, Toast.LENGTH_LONG).show();
			
				// DB - BLOB
				new BLOB_Thumbnail().execute();
				
				is.close();
				
				mDbHelper = new DataBase_Adapter(NelesoApp.this);
		  		mDbHelper.open();

		  		String[] pdf_column = new String[] { DataBase_Adapter.KEY_PDF_FILE };
		  		pdf_column =  mDbHelper.getColumn(DataBase_Adapter.KEY_PDF_FILE);
		  		boolean exist = false;
		  		
				  	     
		  	    String fileUrl = "/neleso" + pdf_file ;
				Log.e("Veamos el fileUrl", fileUrl);
		  	    String file = android.os.Environment.getExternalStorageDirectory().getPath() + fileUrl;
		  	    File f = new File(file);
	    			 
	    	    for (int i = 0; i < pdf_column.length; i++) {
	    	    	if ( pdf_column[i].matches(pdf_file)) {
	    	    		exist = true;
	    	    	}
	    	    }

		  	    // ask user if he warts to overwrite the existing file.
		  	    // for now, we delete
	    	    /*
		  	  if (f.exists()) {
		  		  f.delete();		  		  
		  	  }
		  	  exist = false;
		  	  */
		  	  
	    		     if (f.exists() && exist == true ) {
	    					 
		    				 Toast.makeText(NelesoApp.this, product_identifier + "Already Exist in folder and db", Toast.LENGTH_LONG).show();
			  			     Log.i("Veamos el product ID ", product_identifier);
			  			     mDbHelper.close();
			  			     openPdf(file); 
			  			     
	    						 
	    			 } else if (f.exists() && exist == false ) {
	    			 
	    		    Toast.makeText(NelesoApp.this, product_identifier + "Already Exist in folder but not in db", Toast.LENGTH_LONG).show();	 
	    		    mDbHelper.createTodo(guid, title, subtitle, thumbnail_link, baf.toByteArray(), pdf_link, pdf_file, pub_date, product_identifier, deviceId);
	    		    mDbHelper.close();
	  			    openPdf(file); 
		  	        
	    			} else {
	    				
	    		        checkAndCreateDirectory("/neleso");
			  	        new DownloadFileAsync().execute(pdf_link);
	    			}
   		
   	
   		} catch (MalformedURLException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		        Log.i("System out","malformed :"+e.getMessage());
		        Toast.makeText(getApplicationContext(), "System out",Toast.LENGTH_SHORT).show();
		        Log.i("System out","malformed :"+e.toString());
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		        Log.i("System out","io: "+e.getMessage());
		        Log.i("System out","io: "+e.toString());
		    } // Execute it
			return;
		}

        
        // startDownloadImage and call openPDF method %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		
	
	    //this is our download file asynctask
	   public class DownloadFileAsync extends AsyncTask<String, String, String> {
	        
	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            showDialog(0);
	        }

	        
	        @Override
	        protected String doInBackground(String... aurl) {

	            try {
	                //connecting to url
	                URL u = new URL(pdf_link);
	                HttpURLConnection c = (HttpURLConnection) u.openConnection();
	                c.setRequestMethod("GET");
	                c.setDoOutput(true);
	                c.connect();
	                
	                //lenghtOfFile is used for calculating download progress
	                int lenghtOfFile = c.getContentLength();
	                
	                //this is where the file will be seen after the download
	                FileOutputStream f = new FileOutputStream(new File(rootDir + "/neleso/", pdf_file));
	                //file input is from the url
	                InputStream in = c.getInputStream();

	                //here's the download code
	                byte[] buffer = new byte[1024];
	                int len1 = 0;
	                long total = 0;
	                
	                while ((len1 = in.read(buffer)) > 0) {
	                    total += len1; //total = total + len1
	                    publishProgress("" + (int)((total*100)/lenghtOfFile));
	                    f.write(buffer, 0, len1);
	                }
	                f.close();
	                c.disconnect();
	      	        mDbHelper.createTodo(guid, title, subtitle, thumbnail_link, baf.toByteArray(), pdf_link, pdf_file, pub_date, product_identifier, deviceId);
	      	        mDbHelper.close();
	            } catch (Exception e) {
	                Log.d(LOG_TAG, e.getMessage());
	            }
	            
	            return null;
	        }
	        
	        protected void onProgressUpdate(String... progress) {
	             Log.d(LOG_TAG,progress[0]);
	             mProgressDialog.setProgress(Integer.parseInt(progress[0]));
	        }

	        @Override
	        protected void onPostExecute(String unused) {
	            //dismiss the dialog after the file was downloaded
	            dismissDialog(0);
	            openPdf(path);
	        }
	    }
	    
	    //function to verify if directory exists
	    public void checkAndCreateDirectory(String dirName){
	        File new_dir = new File( rootDir + dirName );
	        path = new_dir + "/" + pdf_file;
	        if( !new_dir.exists() ){
	            new_dir.mkdirs();
	        }
	    }
	   

		// end startDownloadImage %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


		private void openPdf(String path) {
			
			if (XMLHandler.Version == 4) {
				
	    	Intent intent = new Intent(NelesoApp.this, PdfReader.class)
			.putExtra(EXTRA_PDFFILENAME, path);
	    	startActivity(intent);
		} else if (XMLHandler.Version == 3) {
				
	    	Intent intent = new Intent(NelesoApp.this, PdfReader.class)
			.putExtra(EXTRA_PDFFILENAME, path);
	    	startActivity(intent);
		} else {
			
			Intent intent = new Intent(NelesoApp.this, PdfReader.class)
			.putExtra(EXTRA_PDFFILENAME, path);
	    	startActivity(intent);
			
		}
	  }
    }  // end webViewClient  --------------------------------------------------------------------------------------
    
    
    //-------Buttons----------------------------------------------------------------------------------------------
    
    
    
    public void callIntent(View view) throws Exception {
		
   MenuManager menu = new MenuManager(this, view);
   menu.callIntent(this, view, button, button2, button3, button4, button5);
	
} 
    
    //-------Buttons----------------------------------------------------------------------------------------------

//  Used Temporally   


// Creating the BarDialog  ******************************************************************************************************

//our progress bar settings
@Override
protected Dialog onCreateDialog(int id) {
  switch (id) {
      case 0: //we set this to 0
          mProgressDialog = new ProgressDialog(NelesoApp.this);
          mProgressDialog.setMessage("Downloading file...");
          mProgressDialog.setIndeterminate(false);
          mProgressDialog.setMax(100);
          mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
          mProgressDialog.setCancelable(true);
          mProgressDialog.show();
          return mProgressDialog;
default:
	return null;
}
}

// Creating the BarDialog  ******************************************************************************************************




//  Downloading tthe BLOB image to SQLite --------------------------------------------------------------------------------------

public class BLOB_Thumbnail  {
	
    public void execute() {
    	URL url = null;
        try {
            url = new URL(thumbnail_link);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  //http://example.com/image.jpg
        //open the connection
        URLConnection ucon = null;
        try {
            ucon = url.openConnection();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //buffer the download
        InputStream is = null;
        try {
            is = ucon.getInputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        BufferedInputStream bis = new BufferedInputStream(is,128);
        baf = new ByteArrayBuffer(128);
        //get the bytes one by one
        int current = 0;
        try {
            while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    		
	}

  }

//Downloading tthe BLOB image to SQLite --------------------------------------------------------------------------------------


public static String getUrl() {
	return getUrl_load();
}

public static String getUrl_load() {
	return set_url_load;
}

public static void setUrl_load(String url_load) {
	NelesoApp.set_url_load = url_load;
}




 
}


