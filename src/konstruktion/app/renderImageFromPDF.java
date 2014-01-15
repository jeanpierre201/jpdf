package konstruktion.app;

import android.os.Debug;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import net.sf.andpdf.nio.ByteBuffer;
import net.sf.andpdf.refs.HardReference;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.decrypt.PDFAuthenticationFailureException;
import com.sun.pdfview.font.PDFFont;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Environment;
import android.util.Log;


public class renderImageFromPDF {
		
	public static PDFFile mPdfFile;
	public static int mPage;
    public static File mTmpFile;
    public static File mydir;
    public static int totNumberOfPages = 0;
    
    public static int imgWidth = 0;				// width of the rendered image
    public static int imgHeight = 0;			// height of the rendered image
    
    public static String imagePath = null;		// height of the rendered image
    public static Bitmap bi;
    private Boolean ready;
    
	public static String createJPEG(String pdf, int page, int width) {
		
		int stepper = 0;				// in case of crash, we will know how far we got
		imgWidth = width;
	    Boolean ready = false;
		
		
	    try {
	    	
	    	System.gc();
	    	Log.i("Seb", "=============================== (" + page + "/" + width + ") PDF  : " + pdf);
	    	logHeap();
	    	
	 		File rootsd = Environment.getExternalStorageDirectory();
   	 		String DIR = "/neleso/pdf2image";
   	 		File tmpDir = new File(rootsd + DIR);
   	 		if (!(tmpDir.exists())) {
   	 			tmpDir.mkdirs();
   	 		}

   	 		String shorty = pdf.replaceAll(".*/", "").replaceAll("pdf", "").replaceAll("[^A-Za-z0-9]", "");
   	 		
   	 		// -------------------------------------------------------
		
   	 //		imagePath =  rootsd.toString() + DIR + "/" + shorty + "_"  + page + "_"  + imgWidth + ".jpg";
   	   		imagePath =  rootsd.toString() + DIR + "/" + page + "_"  + imgWidth + ".jpg";


   	 		Log.i("Seb", "Create file path  : " + imagePath);
   	 		stepper = 100;
   	 		parsePDF(pdf);

   	 		stepper = 200;
   	 		PDFPage mPdfPage = mPdfFile.getPage(page+1, true);
	                
	        float pwidth 	= mPdfPage.getWidth();
	        float pheight 	= mPdfPage.getHeight();
	        
	        imgHeight = (int) (imgWidth * pheight / pwidth);
	        
	        Log.i("Seb", "Create file width : " + imgWidth + " height: " + imgHeight);
	        
	        // String pageInfo= "Pages: "+totNumberOfPages+ " Width: " + pwidth + " Height: " + pheight;
	        // Log.i("Seb", "PDF Info : " + pageInfo);
	        
	        // --------------------------------------------------------------------------------------
	        stepper = 300;
	        
	        RectF clip = null;	  
	        bi = mPdfPage.getImage(imgWidth, imgHeight, clip, true, true);  

	        stepper = 400;
		    File file = new File(imagePath);
		    if (file.exists()) {
		    	file.delete();
		    }
		    
		    OutputStream outStream = new FileOutputStream(file);
		    bi.compress(Bitmap.CompressFormat.JPEG, 99, outStream);
		    outStream.flush();
		    outStream.close();
		    
		    
		    Log.i("Seb", "PDF written : " + imagePath);
		    ready = true;
		    
		    /*
		    // System.gc()
   	 		// Create HTML file for debugging -------------------------------------------------------
		    stepper = 400;

	    	String dummy = "<html><body>";
	    	dummy += "Width/Height: " + imgWidth + "/" + imgHeight + "<br>";	    	 
	    	dummy += "Source: " + pdf + "<br>";	    	
	    	dummy += "Location: " + imagePath + "<br>";
	    	dummy += "Pages: "+totNumberOfPages+ " Width: " + pwidth + " Height: " + pheight + "<br>";
	    	

    		String imagePathX1 =  rootsd.toString() + DIR + "/" + shorty + "_" + page + "_"  + imgWidth + ".jpg";
    		File bigImageFile = new File(imagePathX1);
    		if (bigImageFile.exists()) {
    			dummy += "<img src=\"file:///" + imagePathX1 + "\" width=800><br>";
    		}
    		
	    	for (int i = 1; i<=totNumberOfPages; i++) {
	    		
	    		imagePathX1 =  rootsd.toString() + DIR + "/" + shorty + "_" + i + "_"  + imgWidth + ".jpg";
	    		
	    		dummy += "<a style=\"font-size:32px\" href=\"index" + i + ".html\">Image " + i + "</a> "+ imagePathX1 + "<br>";
	    		dummy += "<a href=\"file://" + imagePathX1 + "\">"+ imagePathX1 + "</a><br>";
	    		// dummy += "<img src=\"file:///" + imagePathX1 + "\" width=800><br>";
	    	}
    		
    		dummy += "</body></html>";

    		// Create toc
    		if (true) {
    			String outFileStr = rootsd.toString() + DIR + "/index.html";	    	 
    			java.io.FileOutputStream fout 		= new FileOutputStream(outFileStr);
    			java.io.OutputStream bout= new java.io.BufferedOutputStream(fout);
    			java.io.OutputStreamWriter dos = new java.io.OutputStreamWriter(bout, "UTF-8");
    			Log.i("Seb", "Create: " + outFileStr);
    			dos.write(dummy);
    			dos.flush();								// Zu mit der Verbindung
    			dos.close();								// Aus. Ende.
    		}
    		
    		// create html page that displays this image
    		if (true) {
    			String outFileStr = rootsd.toString() + DIR + "/index"  + page + ".html";	    	 
    			java.io.FileOutputStream fout 		= new FileOutputStream(outFileStr);
    			java.io.OutputStream bout= new java.io.BufferedOutputStream(fout);
    			java.io.OutputStreamWriter dos = new java.io.OutputStreamWriter(bout, "UTF-8");
    			Log.i("Seb", "Create: " + outFileStr);
    			dos.write(dummy);
    			dos.flush();								// Zu mit der Verbindung
    			dos.close();								// Aus. Ende.
    		}
    		*/
		    
			bi = null;
			mPdfPage = null;
			mPdfFile = null;
			System.gc();
			
	     } catch (Exception e) {
	    	 Log.i("Seb", "createJPEG error ("+stepper+"): " + e);
	    	 imagePath = null;
	     }

	     // -------------------------------------------------------		
	    Log.e("verify = "+ready, "++++");
		return imagePath;
	} // createJPEG


	
    // FOOTER ______________________________________________________________________
     
    protected static void logHeap() {
        Double allocated = new Double(Debug.getNativeHeapAllocatedSize())/new Double((1048576));
        Double available = new Double(Debug.getNativeHeapSize())/1048576.0;
        Double free = new Double(Debug.getNativeHeapFreeSize())/1048576.0;
        java.text.DecimalFormat df = new java.text.DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        Log.d("Seb", "debug. =================================");
        Log.d("Seb", "debug.heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free)");
        Log.d("Seb", "debug.memory: allocated: " + df.format(new Double(Runtime.getRuntime().totalMemory()/1048576)) + "MB of " + df.format(new Double(Runtime.getRuntime().maxMemory()/1048576))+ "MB (" + df.format(new Double(Runtime.getRuntime().freeMemory()/1048576)) +"MB free)");
    }       
    
	// Nostra implementazione di parsePDF - ONE TIME ONLY per pdf
    private static void parsePDF(String filename) throws PDFAuthenticationFailureException { 
    	
         PDFImage.sShowImages = true;
         PDFPaint.s_doAntiAlias = true;
         PDFFont.sUseFontSubstitution = false;
         HardReference.sKeepCaches = false;
	
        try {
        	File f = new File(filename);
        	long len = f.length();
        	if (len == 0) {
        		Log.e("parsePDF", "Lengt of file = 0 :(");
        	}
        	else {
        		openFile(f);
        	}
    	}
        catch (PDFAuthenticationFailureException e) {
        	e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
    
    // nostra versione del metodo openFile
    private  static void openFile(File file) throws IOException {
        // first open the file for random access
        RandomAccessFile raf = new RandomAccessFile(file, "r");

        // extract a file channel
        FileChannel channel = raf.getChannel();

        // now memory-map a byte-buffer
         ByteBuffer bb = ByteBuffer.NEW(channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()));
         
        // create a PDFFile from the data
        mPdfFile = new PDFFile(bb);  
        totNumberOfPages = mPdfFile.getNumPages(); // * TOTAL NUMBER OF PAGES of the pdf

    }



	public Boolean getReady() {
		return ready;
	}   
    
    // eof
}

