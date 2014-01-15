

package neleso.app;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import neleso.app.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import android.app.Activity;
import android.app.Dialog;
import android.os.Message;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import android.os.Environment;
import android.util.Log;

import neleso.utils.StringUtils;
import net.sf.andpdf.pdfviewer.PdfFileSelectActivity;
import net.sf.andpdf.pdfviewer.PdfViewerActivity;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



import wrongs.Download;
import wrongs.FileManager;


import examples.HttpDownload;
import examples.Show_Image;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class NelesoApp extends Activity {

	// Used Temporally 
	
	public static final String EXTRA_PDFFILENAME = "net.sf.andpdf.extra.PDFFILENAME";
	
    HttpDownload takeTitleXml;
    
    ProgressThread progressThread;
    ProgressDialog progressDialog;
	
	
	ProgressThread progThread;
    ProgressDialog progDialog;
    int typeBar;                     // Determines type progress bar: 0 = spinner, 1 = horizontal
    int delay = 40;                  // Milliseconds of delay in the update loop
    int maxBarValue;                 // Maximum value of horizontal progress bar
    int total;
	
	
	// GUI '''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
	
	WebView webview; // Simple Web View
	Button myButton; // used for testing
	Button myButton2; // used for testing
	
	HttpClient httpclient = new DefaultHttpClient(); // Create HTTP Client in the first screen
	HttpGet httpget;  // Set the action you want to do
	
	// GUI END '''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        myButton = (Button) findViewById(R.id.button1);
        myButton.setOnClickListener(myListener);
        myButton2 = (Button) findViewById(R.id.button2);
        myButton2.setOnClickListener(myListener2);
        
        webview = (WebView) findViewById(R.id.webView1);
        webview.setWebViewClient(new myWebViewClient());
        webview.loadUrl("http://pcwelt.mserver.net/m_storefront/"); 
            
       
        
    } // end onCreate ----------------------------------------------------------------------------------------------   
      
    
      
    // Handle myWebViewClient #######################################################################################
    
    private class myWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	     	
        	
        	if (Uri.parse(url).getHost().equals("action://mserver/")) {   		
        		// This is my web site, so do not override; let my WebView load the page
                return false;
            }
        	
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
           
               	    	
        	// Split the string url to create a newUrl --------------------------------------------------------------
        	
         	String arr[] = url.split("/");
            for(int i = 0; i < arr.length;){
            	
        	//System.out.println("arr["+i+"] = " + arr[i].trim());
        	String newUrl = "http://" + arr[5] + "/"+ arr[6] + "/" + arr[7];
        
        	//  view.loadUrl(newUrl);
        	//  Toast.makeText(getApplicationContext(), newUrl, Toast.LENGTH_LONG).show();
        	
        	// the newUrl has been created -------------------------------------------------------------------------
            
        	
        	
        	// Conditional for the contains in the newUrl ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        	
        	if(newUrl.contains("preview.php")==true)  
        	{  
            	
                // Get the url_pdf, call method startDownloadImage(url_pdf) ================================================================================================== 

            	 try {
            		            		 
         
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
					String url_pdf = StringUtils.substringBetween(html_content, "<link>", "</link>");
					//URL_PDF = url_pdf;
					startDownloadImage(url_pdf);
	            	is.close();
					
	            	
	                
	            	Toast.makeText(getApplicationContext(), "Downloaded", Toast.LENGTH_SHORT).show();
	                        
			      
			    } catch (MalformedURLException e) {
			        // TODO Auto-generated catch block
			        e.printStackTrace();
			        Log.i("System out","malformed :"+e.getMessage());
			        Log.i("System out","malformed :"+e.toString());
			    } catch (IOException e) {
			        // TODO Auto-generated catch block
			        e.printStackTrace();
			        Log.i("System out","io: "+e.getMessage());
			        Log.i("System out","io: "+e.toString());
			    } // Executeit
			    
	           // url_pdf has been download and showed it ==================================================================================================
            	
            	 
        	}  else if(newUrl.contains("buy.php")==true) {
            	Toast.makeText(getApplicationContext(), "BuyApp", Toast.LENGTH_SHORT).show();

        	}  else if(newUrl.contains("download.php")==true) {
        		
        		try {
           		 
        	         
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
					String url_pdf = StringUtils.substringBetween(html_content, "<link>", "</link>");
					//URL_PDF = url_pdf;
					 
				    startDownloadImage(url_pdf);
					is.close();
					
	              
		            
        		
            	//Toast.makeText(getApplicationContext(), "Downloaded", Toast.LENGTH_SHORT).show(); 
        	
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
			    } // Executeit
        	
        	} // end Conditional-----------------------------------------------------------------------------
        	   
			    return true;       
        }
			return false;	
        }
        

        // startDownloadImage and call openPDF method %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
		private void startDownloadImage(String url) throws IOException {
			
			
							
			// TODO Auto-generated method stub
			String storage = Environment.getExternalStorageState();
		    Log.i("System out", "" + storage);
		   // Toast.makeText(getApplicationContext(), storage, Toast.LENGTH_LONG).show();
		    File rootsd = Environment.getExternalStorageDirectory();
		    Log.d("ROOT PATH", "" + rootsd.getAbsolutePath());
		   // Toast.makeText(getApplicationContext(), rootsd.getAbsolutePath(), Toast.LENGTH_LONG).show();
		    String DIR = "/neleso";
		    File mydir = new File(rootsd.toString() + DIR);
		    Log.i("DIR PATH", "" + mydir.getAbsolutePath());
		    mydir.mkdir();
		    
		    String name_pdf = url.substring(url.lastIndexOf('/'));
		    
		    URL u; 
		    u = new URL(url);
		    HttpURLConnection c = (HttpURLConnection) u.openConnection();
	        c.setRequestMethod("GET");
	        c.setDoOutput(true);
//	        Toast.makeText(getApplicationContext(), maxBarValue, Toast.LENGTH_LONG).show();
	        c.connect();
	        maxBarValue = c.getContentLength();
	        typeBar = 2;
	        showDialog(typeBar);
	        
	        FileOutputStream f = new FileOutputStream(new File(mydir + "/"
	                + name_pdf));
	                
	        InputStream in = c.getInputStream();
	               
	        byte[] buffer = new byte[2048];
	        int len1 = 0;
	        while ((len1 = in.read(buffer)) > 0) {
	              
	            f.write(buffer, 0, len1);
	        }

	       
	        f.close();
	        String path = mydir + "/" + name_pdf;
	        c.disconnect(); 
	    	        
	        openPdf(path);
            
		}

		// end startDownloadImage %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%





		private void openPdf(String path) {
			
			
	    	Intent intent = new Intent(NelesoApp.this, PdfViewer.class)
			.putExtra(EXTRA_PDFFILENAME, path);
	    	startActivity(intent);
		}
    
    }  // end webViewClient  --------------------------------------------------------------------------------------
    
  
    
 // Used Temporally    
    
    
    
    
    
	//public String getURL_Image() {
		// TODO Auto-generated method stub
		//return URL_PDF;
//	}
	
	
	
    
    
    
    //-------Buttons----------------------------------------------------------------------------------------------
    
    //
    private android.view.View.OnClickListener myListener = new android.view.View.OnClickListener(){
		@Override
		public void onClick(View v) {

			typeBar = 2;
            showDialog(typeBar);
        }
    };  // end myListener
    
    
    private android.view.View.OnClickListener myListener2 = new android.view.View.OnClickListener(){

		@Override
		public void onClick(View v) {
			typeBar = 0;
            showDialog(typeBar);
        }
    
			//Intent myIntent = new Intent(getApplicationContext(), PdfFileSelectActivity.class); 
			//NelesoApp.this.startActivity(myIntent); 
				
				
}; // end myListener2


    
    //-------Buttons----------------------------------------------------------------------------------------------

//  Used Temporally   


// Creating the BarDialog  ******************************************************************************************************

//Method to create a progress bar dialog of either spinner or horizontal type
@Override
protected Dialog onCreateDialog(int id) {
    switch(id) {
    case 0:                      // Spinner
        progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setMessage("Loading...");
        progThread = new ProgressThread(handler);
        progThread.start();
        return progDialog;
    case 1:                      // Horizontal
        progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progDialog.setMax(maxBarValue);
        progDialog.setMessage("Downloading the pdf");
 //       progThread = new ProgressThread(handler);
        progThread.start();
        return progDialog;
    case 2:
    	 progressDialog = new ProgressDialog(NelesoApp.this);
         progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
         progressDialog.setMessage("Downloading the Pdf");
         return progressDialog;
    default:
        return null;
    }
}

// Handler on the main (UI) thread that will receive messages from the 
// second thread and update the progress.

/*

final Handler handler = new Handler() {
    public void handleMessage(Message msg) {
        // Get the current value of the variable total from the message data
        // and update the progress bar.
        int total = msg.getData().getInt("total");
        progDialog.setProgress(total);
        if (total <= 0){
            dismissDialog(typeBar);
            progThread.setState(ProgressThread.DONE);
        }
    }
};

// Inner class that performs progress calculations on a second thread.  Implement
// the thread by subclassing Thread and overriding its run() method.  Also provide
// a setState(state) method to stop the thread gracefully.

private class ProgressThread extends Thread {	
    
    // Class constants defining state of the thread
    final static int DONE = 0;
    final static int RUNNING = 1;
    
    Handler mHandler;
    int mState;
    int total;

    // Constructor with an argument that specifies Handler on main thread
    // to which messages will be sent by this thread.
    
    ProgressThread(Handler h) {
        mHandler = h;
    }
    
    // Override the run() method that will be invoked automatically when 
    // the Thread starts.  Do the work required to update the progress bar on this
    // thread but send a message to the Handler on the main UI thread to actually
    // change the visual representation of the progress. In this example we count
    // the index total down to zero, so the horizontal progress bar will start full and
    // count down.
    
    @Override
    public void run() {
        mState = RUNNING;   
        total = maxBarValue;
        while (mState == RUNNING) {
            // The method Thread.sleep throws an InterruptedException if Thread.interrupt() 
            // were to be issued while thread is sleeping; the exception must be caught.
            try {
                // Control speed of update (but precision of delay not guaranteed)
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Log.e("ERROR", "Thread was Interrupted");
            }
            
            // Send message (with current value of  total as data) to Handler on UI thread
            // so that it can update the progress bar.
            
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putInt("total", total);
            msg.setData(b);
            mHandler.sendMessage(msg);
            
            total--;    // Count down
        }
    }
    
    // Set current state of thread (use state=ProgressThread.DONE to stop thread)
    public void setState(int state) {
        mState = state;
    }
}   */

@Override
protected void onPrepareDialog(int id, Dialog dialog) {
    switch(id) {
    case 2:
        progressDialog.setProgress(0);
        progressThread = new ProgressThread(handler);
        progressThread.start();
}
}

// Define the Handler that receives messages from the thread and update the progress
final Handler handler = new Handler() {
    public void handleMessage(Message msg) {
        int total = msg.arg1;
        progressDialog.setProgress(total);
        if (total >= 100){
            dismissDialog(2);
            progressThread.setState(ProgressThread.STATE_DONE);
        }
    }
};

/** Nested class that performs progress calculations (counting) */
private class ProgressThread extends Thread {
    Handler mHandler;
    final static int STATE_DONE = 0;
    final static int STATE_RUNNING = 1;
    int mState;
    int total;
   
    ProgressThread(Handler h) {
        mHandler = h;
    }
   
    public void run() {
        mState = STATE_RUNNING;   
        total = maxBarValue;
        while (mState == STATE_RUNNING) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.e("ERROR", "Thread Interrupted");
            }
            Message msg = mHandler.obtainMessage();
            msg.arg1 = total;
            mHandler.sendMessage(msg);
            total++;
        }
    }
    
    /* sets the current state for the thread,
     * used to stop the thread */
    public void setState(int state) {
        mState = state;
    }
}

    
} 