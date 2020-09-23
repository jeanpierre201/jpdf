package industriekatalog.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class RenderPageThread extends Thread {
	
	private int page;
	private int width;
	private int height;
	public static String imagePath = null;
    public static Bitmap bi;
    public static volatile Boolean ready;
    ThumbnailsMenu thumbnailsmenu;
	private String TAG = "RenderPageThread";
	private int counterRenderPageThread=0;
	public static int targetHeight;
	
	
	
	public RenderPageThread(int page, int width, int height) {
		this.page = page;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void run() {
		
		Log.i(TAG , "RenderPageThread :  " + counterRenderPageThread);
		Log.i(TAG, "This Thread Is running :  " + RenderPageThread.currentThread().getName());

		counterRenderPageThread++;
		
//		readFile = new FileDealer(pdfPath);
	    PDFFile PdfFile = PdfReader.mPdfFile;
		PDFPage mPdfPage = PdfFile.getPage(page+1, true);
//		Log.i(TAG , "width :  " + width);
		targetHeight = (int) (mPdfPage.getHeight() * width / (double) mPdfPage.getWidth());
		
		imagePath =  PdfReader.mydirHD.toString() + "/" + page + "_"  + width + ".jpg";	
		
        Log.i(TAG , "mPdfPage :  " + mPdfPage);
        RectF clip = null;	 
        bi = mPdfPage.getImageT(width, targetHeight, clip, true, true);
        File file = new File(imagePath);
        
	    if (file.exists()) {
	    	file.delete();
	    }
	    

		try {
			OutputStream outStream = new FileOutputStream(file);
		    bi.compress(Bitmap.CompressFormat.JPEG, 99, outStream);
		    outStream.flush();
		    outStream.close();
		    ready = true;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SetImageBitmap.pathFile = imagePath;
		SetImageBitmapLand.pathFile = imagePath;
		PdfReader.pageString[page] = imagePath;
		
//   	bi.recycle();
		bi = null;
		mPdfPage = null;
		PdfFile = null;
		System.gc();
	    Log.i("Seb", "PDF written : " + imagePath);
	    Log.e("verify = "+ready, "++++");
	    
	    return;
	}
	

}
