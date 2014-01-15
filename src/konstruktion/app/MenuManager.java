package konstruktion.app;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import konstruktion.app.R;
import neleso.storefront.XMLHandler;
import neleso.utils.StringUtils;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MenuManager extends Activity {
	
    private Context cxt;
    private View viewId;
    String clase;
    NelesoApp neleso;

	
	 public MenuManager(Context context, View view) throws Exception {
	        
	        this.cxt = context;
	        this.viewId = view;
            clase = cxt.toString();	
   
	 }
	 
	 
@SuppressWarnings("static-access")
public void callIntent(Context context, View view, ImageButton button, ImageButton button2, ImageButton button3, ImageButton button4, ImageButton button5) {
	
	    Intent intent = new Intent();
	    
 if (clase.contains("NelesoApp") == true) {
	    	
	 switch (view.getId()) { 
	    case R.id.kiosk:	
			button.setSelected(true);
			button2.setSelected(false);
			button3.setSelected(false);
			button4.setSelected(false);
			button5.setSelected(false);
			NelesoApp.webview.loadUrl(XMLHandler.url_kiosk);
//			Toast.makeText(cxt, XMLHandler.url_kiosk, Toast.LENGTH_LONG).show();
			break;
		case R.id.archive:
			button.setSelected(false);
			button2.setSelected(true);
			button3.setSelected(false);
			button4.setSelected(false);
			button5.setSelected(false);
			intent.setClass(cxt, ArchiveList.class);
		    cxt.startActivity(intent);
			break;
		case R.id.news:
			button.setSelected(false);
			button2.setSelected(false);
			button3.setSelected(true);
			button4.setSelected(false);
			button5.setSelected(false);
			NelesoApp.webview.loadUrl(XMLHandler.url_news);
//			Toast.makeText(cxt, XMLHandler.url_news, Toast.LENGTH_LONG).show();
			break;
		case R.id.website:			
			button.setSelected(false);
			button2.setSelected(false);
			button3.setSelected(false);
			button4.setSelected(true);
			button5.setSelected(false);
			NelesoApp.webview.loadUrl(XMLHandler.url_website);
//			Toast.makeText(cxt, XMLHandler.url_website, Toast.LENGTH_LONG).show();
			break;
		case R.id.help:		
			button.setSelected(false);
			button2.setSelected(false);
			button3.setSelected(false);
			button4.setSelected(false);
			button5.setSelected(true);
			NelesoApp.webview.loadUrl(XMLHandler.url_help);
//			Toast.makeText(cxt, XMLHandler.url_help, Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
	}   if (clase.contains("ArchiveList") == true) {
    	
 switch (view.getId()) { 
    case R.id.kiosk:	
		intent.setClass(cxt, NelesoApp.class);
		neleso.setUrl_load(XMLHandler.url_kiosk);
	    cxt.startActivity(intent);
//	    Toast.makeText(cxt, XMLHandler.url_kiosk, Toast.LENGTH_LONG).show();
		break;
	case R.id.archive:
		intent.setClass(cxt, ArchiveList.class);
	    cxt.startActivity(intent);
		break;
	case R.id.news:
		intent = new Intent(cxt, NelesoApp.class);
		neleso.setUrl_load(XMLHandler.url_news);
		cxt.startActivity(intent);	
		break;
	case R.id.website:			
		intent = new Intent(cxt, NelesoApp.class);
		neleso.setUrl_load(XMLHandler.url_website);
		cxt.startActivity(intent);	
		break;
	case R.id.help:		
		intent = new Intent(cxt, NelesoApp.class);
		neleso.setUrl_load(XMLHandler.url_help);
		cxt.startActivity(intent);	
//		Toast.makeText(cxt, XMLHandler.url_help, Toast.LENGTH_SHORT).show();
		break;
	default:
		break;
	}
    	    	
    	
    }  if (clase.contains("ShowPdf") == true )  {
    	
    	
    	switch (view.getId()) {
		case R.id.kiosk:
			intent = new Intent(cxt, NelesoApp.class);
			neleso.setUrl_load(XMLHandler.url_kiosk);
			cxt.startActivity(intent);	
			break;
		case R.id.archive:
			intent = new Intent(cxt, ArchiveList.class);
	    	cxt.startActivity(intent);
			break;
		case R.id.news:
			intent = new Intent(cxt, NelesoApp.class);
			neleso.setUrl_load(XMLHandler.url_news);
			cxt.startActivity(intent);		
			break;
		case R.id.website:
			intent = new Intent(cxt, NelesoApp.class);
			neleso.setUrl_load(XMLHandler.url_website);
			cxt.startActivity(intent);	
			break;
		case R.id.help:
			intent = new Intent(cxt, NelesoApp.class);
			neleso.setUrl_load(XMLHandler.url_help);
			cxt.startActivity(intent);		
			break;
		default:
			break;
		}
	}
    }
	    
	
}




