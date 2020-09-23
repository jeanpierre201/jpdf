package neleso.storefront;


import java.io.InputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import industriekatalog.app.R;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.os.Bundle;

public class XMLParsing {
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle icicle) {
	}

	/**
	 * This methos is called to begin parse XML file content
	 * @return
	 * @throws Exception
	 */
	
	public void getParsedMyXML(Context cxt) throws Exception {
		StringBuffer inLine = new StringBuffer();
		/* Get a SAXParser from the SAXPArserFactory. */
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();

		/* Get the XMLReader of the SAXParser we created. */
		XMLReader xr = sp.getXMLReader();
		/* Create a new ContentHandler and apply it to the XML-Reader */
		XMLHandler myExampleHandler = new XMLHandler();
		xr.setContentHandler(myExampleHandler);
		/* Load xml file from raw folder*/
        InputStream in = cxt.getResources().openRawResource(R.raw.urls); 
    /* Begin parsing */
		xr.parse(new InputSource(in));
		XMLDataSet parsedExampleDataSet = myExampleHandler.getParsedData();
		inLine.append(parsedExampleDataSet.toString());
		in.close();
		return;
	}
}