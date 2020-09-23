package industriekatalog.app;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class RenderThumbPageThread extends Thread implements Runnable {
	
	private int page;
	private PDFFile pdffile;
    public PDFPage mPdfPage;
    PdfReader pdfreader;
    PdfReaderFragment prf;

	public RenderThumbPageThread(PDFFile mPdfFile, int page) {
		
	this.page = page;
	this.pdffile = mPdfFile;	
	
//	String storage = Environment.getExternalStorageState();
//    Log.i("System out", "" + storage);
//    File rootsd = Environment.getExternalStorageDirectory();
//    Log.d("ROOT PATH", "" + rootsd.getAbsolutePath());
	
//    String DIR = "/neleso/"+NelesoApp.pdf_file_name;
//    String DIR2 = "/neleso/pdf2image";
    
   
 //     PdfReader.mydirThumb = new File(rootsd.toString() + DIR );
//      PdfReader.mydirHD = new File(rootsd.toString() + DIR2);


//if (!PdfReader.mydirThumb.exists()) {
//	PdfReader.mydirThumb.mkdir();
//}

	
	}

	
	@Override
	public void run() {	
	
		 try {
	    		
		        // remember: free memory from previous page
			 
			
		       
			    mPdfPage = pdffile.getPage(page+1, true);
		        RectF clip = null;	
	            Bitmap bi = mPdfPage.getImageT((int)PdfReader.thmbWidht, (int)PdfReader.thmbHeight, clip, true, true);	  	               
//		        bi.setDensity((int) scale);

	            OutputStream outStream = null;
	    		File file = new File(PdfReader.mydirThumb + "/"+PdfReader.folder+"_"+page+".JPG");
	    		String path = file.getAbsolutePath();
	        	
	        	try {
	        	 outStream = new FileOutputStream(file);
	        	 bi.compress(Bitmap.CompressFormat.JPEG, 90, outStream);  	 
	        	 bi.isRecycled();
	        	 outStream.flush();
	        	 outStream.close();
	        	}
	        	catch(Exception e)
	        	{}
	        	
	        	PdfReader.thumb[page] = Uri.parse(path);
	        	PdfReader.thumbString[page] = path;
	     
	            bi.isRecycled();
	            bi = null;
	            	            
			} catch (Throwable e) {
				Log.e("createThumb()", e.getMessage(), e);
			}
		
	}
	
	@Override
	public void interrupt() {
		super.interrupt();
	}
}
