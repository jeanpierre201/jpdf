package com.sun.pdfview.action;

import java.io.IOException;

import android.util.Log;

import com.sun.pdfview.PDFDestination;
import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.PDFVideo;

public class LinkAction extends PDFAction {
	
	private int i = 0;
	private String [] uri;

	public LinkAction(String type) {
		super(type);
		// TODO Auto-generated constructor stub
	}

	public LinkAction(PDFObject obj, PDFObject root) throws IOException {
        super("Link");
        
        PDFObject stringObj = obj.getDictRef("S");
        if (stringObj == null) {
            throw new PDFParseException("No link in LinkAction to show " + obj);
        }
        
        // parse it
        uri[i] = PDFVideo.getLinks(stringObj, root);
        Log.d("Link", "" + uri[i]);
    }
    
	

}
