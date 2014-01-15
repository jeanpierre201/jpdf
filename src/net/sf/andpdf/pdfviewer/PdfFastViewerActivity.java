package net.sf.andpdf.pdfviewer;

import konstruktion.app.ShowPdf;
import android.os.Bundle;


/**
 * @author ferenc.hechler
 */
public class PdfFastViewerActivity extends ShowPdf {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	getIntent().putExtra(ShowPdf.EXTRA_USEFONTSUBSTITUTION, true);
        super.onCreate(savedInstanceState);
    }

}