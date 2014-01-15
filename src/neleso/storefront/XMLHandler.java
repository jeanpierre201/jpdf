package neleso.storefront;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandler extends DefaultHandler{
	

     // ===========================================================
     // Define the magazine and version
	
	      public static String Magazine = "konstruktion";
	      public static int Version = 4;
	       
     // ===========================================================
     
     private boolean magazine = false;
     private boolean in_mytag = false;
 
     
    
     public String mag;
     public static String url_kiosk;
     public static String url_website;
     public static String url_news;
     public static String url_help;
   
       
     private XMLDataSet myParsedExampleDataSet = new XMLDataSet();

     // ===========================================================
     // Getter & Setter
     // ===========================================================

     public XMLDataSet getParsedData() {
          return this.myParsedExampleDataSet;
     }

     // ===========================================================
     // Methods
     // ===========================================================
     @Override
     public void startDocument() throws SAXException {
          this.myParsedExampleDataSet = new XMLDataSet();
     }

     @Override
     public void endDocument() throws SAXException {
          // Nothing to do
     }
     
     

     /** Gets be called on opening tags like:
      * <tag>
      * Can provide attribute(s), when xml was like:
      * <tag attribute="attributeValue">*/
     
     @Override
     public void startElement(String namespaceURI, String localName,
               String qName, Attributes atts) throws SAXException {
          if (localName.equals("storefront")) {
          }else if (localName.equals("magazine")) {
        	  mag = atts.getValue("name");
        	  if (mag.equals(Magazine)) {
        		  this.magazine = true; }
          }else if (localName.equals("url_kiosk") && magazine == true) {
            url_kiosk = atts.getValue("url_kiosk"); 
          }else if (localName.equals("url_news") && magazine == true) {
            url_news = atts.getValue("url_news");             
          }else if (localName.equals("url_website") && magazine == true) {
             url_website = atts.getValue("url_website");             
          }else if (localName.equals("url_help") && magazine == true) {
             url_help = atts.getValue("url_help");
    //       myParsedExampleDataSet.setExtractedString(url_help);
          } 
     }       
               

     /** Gets be called on closing tags like:
      * </tag> */
     @Override
     public void endElement(String namespaceURI, String localName, String qName)
               throws SAXException {
          if (localName.equals("storefront")) {
          }else if (localName.equals("magazine")) {
               this.magazine = false;
          }
     }
     
     /** Gets be called on the following structure:
      * <tag>characters</tag> */
     @Override
    public void characters(char ch[], int start, int length) {
          if(this.in_mytag){
          myParsedExampleDataSet.setExtractedString(new String(ch, start, length));
     }
    }
}