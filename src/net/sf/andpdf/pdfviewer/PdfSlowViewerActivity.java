package net.sf.andpdf.pdfviewer;


import jpdf.app.ShowPdf;
import android.os.Bundle;


/**
 * @author ferenc.hechler
 */
public class PdfSlowViewerActivity extends ShowPdf {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	getIntent().putExtra(ShowPdf.EXTRA_USEFONTSUBSTITUTION, false);
        super.onCreate(savedInstanceState);
    }

}