# jpdf
# Autor:Jean Pierre De la Torre

JPDF is a free version of one of my projects from 2014. It was created as a customised kiosk magazine app for different vendors. The app was developed using the "andpdf" open source library. The app contains three activities: one is the online KIOSK (hybrid function whiting a web view showing the kiosk as a backend of the app), the second one is the LIBRARY or file folder where the pdf are stored and the third one is the PDF VIEWER which render any PDF format with the fancy touch functionalities. One pinch touch will show the thumbnails in the bottom. Two pinch zoom in / out. Two finger close/open zoom function. The project was the first release and may still has some bugs to fix.

(Eclipse) Android DeveloperTools 23.0.2
Android SDK version 4.2.
andpdf.jar
Local Sqlite


JAVA Classes -------------------------

Main Activity Launch (KIOSK)
src/jpdf.app/JpdfApp.java

Main Activity (LIBRARY)
src/jpdf.app/ArchiveList.java

Main Activity (PDF VIEWER)
src/jpdf.app/ShowPdf.java

---------------------------------------

Customized java/files


res/raw/urls.tx - contains the urls to be read in the main kiosk activity.

<magazine name ="jpdf">
	    <url_kiosk url_kiosk = "http://jpspotit.orgfree.com/wordpress/pdf-magazine-shop/" />
	    <url_news url_news = "http://jpspotit.orgfree.com/wordpress/pdf-magazine-news/" />
	    <url_website url_website = "http://jpspotit.orgfree.com/wordpress/" />
		<url_help url_help= "http://jpspotit.orgfree.com/wordpress/pdf-magazine-info/" />	
</magazine>

You can have more kiosks with their current urls just inserting more tags <magazine name ="new_name_kiosk"..... /> and changing the public static variable, public static String Magazine = "jpdf"; in src/jpdf.storefront/XMLHandler.java.
In that way you can customized your kiosks as you want.
____________________________________________________________________________________________________________________________

The assets/jpdf/ contains the tool bar header in the given Kiosk. Here you can add more folder wit the name of the ne kiosk and insert the toolbarheader image depending the different size of the devices.

____________________________________________________________________________________________________________________________

You can try the free version installing the jpdf.apk
https://github.com/jeanpierre201/jpdf/blob/master/jpdf.apk

JP






