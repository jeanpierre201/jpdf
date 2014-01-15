package com.sun.pdfview;

import java.io.IOException;

public class PDFVideo {

	   /** The known types of destination */
    public static final int URI = 0;
    public static final int malito = 1;
    public static final int Link = 2;
    public static final int Screen = 3;
    public static final int JavaScript = 4;
    /** the page we refer to */
    private PDFObject pageObj;
      /** the type of this links (from the list above) */
    private int type;
    /** the left coordinate of the fit area, if applicable */
    private float left;
    /** the right coordinate of the fit area, if applicable */
    private float right;
    /** the top coordinate of the fit area, if applicable */
    private float top;
    /** the bottom coordinate of the fit area, if applicable */
    private float bottom;
    /** the zoom, if applicable */
    private float zoom;
    

    /** 
     * Creates a new instance of PDFDestination
     * @param destArray 
     *
     * @param pageObj the page object this destination refers to
     * @param type the type of page this object refers to
     */
    protected PDFVideo(PDFObject pageObj, int type) {
        this.pageObj = pageObj;
        this.type = type;
    }

    
    /**
     * Get a video from either an array (explicit link), a 
     * name (named link) or a string (name tree links).
     *
     * @param obj the PDFObject representing this destination
     * @param root the root of the PDF object tree
     */
    public static PDFVideo getVideo(PDFObject obj, PDFObject root)
            throws IOException {
        // resolve string and name issues
        if (obj.getType() == PDFObject.NAME) {
            obj = getVideoFromName(obj, root);
        } else if (obj.getType() == PDFObject.STRING) {
            obj = getVideoFromString(obj, root);
        }

        // make sure we have the right kind of object
        if (obj == null || obj.getType() != PDFObject.ARRAY) {
            throw new PDFParseException("Can't create video from: " + obj);
        }

        // the array is in the form [video type args ... ]
        PDFObject[] videoArray = obj.getArray();

        // create the video based on the type
        PDFVideo video = null;
        String type = videoArray[1].getStringValue();
        if (type.equals("URI")) {
            video = new PDFVideo(videoArray[0], URI);
        } else if (type.equals("malito")) {
        	video = new PDFVideo(videoArray[0], malito);
        } else if (type.equals("Link")) {
        	video = new PDFVideo(videoArray[0], Link);
        } else if (type.equals("Screen")) {
        	video = new PDFVideo(videoArray[0], Screen);
        } else if (type.equals("JavaScript")) {
        	video = new PDFVideo(videoArray[0], JavaScript);
        } else {
            throw new PDFParseException("Unknown video type: " + type);
        }

        // now fill in the arguments based on the type
        switch (video.getType()) {
            case URI:
       //         dest.setLeft(destArray[2].getFloatValue());
       //         dest.setTop(destArray[3].getFloatValue());
       //         dest.setZoom(destArray[4].getFloatValue());
                break;
            case malito:
       //         dest.setTop(destArray[2].getFloatValue());
                break;
            case Link:
        //        dest.setLeft(destArray[2].getFloatValue());
                break;
            case Screen:
     //           dest.setLeft(destArray[2].getFloatValue());
     //           dest.setBottom(destArray[3].getFloatValue());
     //           dest.setRight(destArray[4].getFloatValue());
     //           dest.setTop(destArray[5].getFloatValue());
                break;
            case JavaScript:
         //       dest.setTop(destArray[2].getFloatValue());
                break;
         }

        return video;
    }
    
    /**
     * Get the type of this video
     */
    public int getType() {
        return type;
    }


	public static String getLinks(PDFObject stringObj, PDFObject root) {
		// TODO Auto-generated method stub
		return null;
	}
	
    /**
     * Get the left coordinate value
     */
    public float getLeft() {
        return left;
    }

    /** 
     * Set the left coordinate value
     */
    public void setLeft(float left) {
        this.left = left;
    }

    /**
     * Get the right coordinate value
     */
    public float getRight() {
        return right;
    }

    /** 
     * Set the right coordinate value
     */
    public void setRight(float right) {
        this.right = right;
    }

    /**
     * Get the top coordinate value
     */
    public float getTop() {
        return top;
    }

    /** 
     * Set the top coordinate value
     */
    public void setTop(float top) {
        this.top = top;
    }

    /**
     * Get the bottom coordinate value
     */
    public float getBottom() {
        return bottom;
    }

    /** 
     * Set the bottom coordinate value
     */
    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    /**
     * Get the zoom value
     */
    public float getZoom() {
        return zoom;
    }

    /** 
     * Set the zoom value
     */
    public void setZoom(float zoom) {
        this.zoom = zoom;
    }
	
	
	/**
     * Get a destination, given a name.  This means the destination is in
     * the root node's dests dictionary.
     */
    private static PDFObject getVideoFromName(PDFObject name, PDFObject root)
            throws IOException {
        // find the dests object in the root node
        PDFObject videos = root.getDictRef("Subtype");
        if (videos != null) {
            // find this name in the dests dictionary
            return videos.getDictRef(name.getStringValue());
        }

        // not found
        return null;
    }

    /**
     * Get a destination, given a string.  This means the destination is in
     * the root node's names dictionary.
     */
    private static PDFObject getVideoFromString(PDFObject str, PDFObject root)
            throws IOException {
        // find the names object in the root node
        PDFObject names = root.getDictRef("Annots");
        if (names != null) {
            // find the dests entry in the names dictionary
            PDFObject videos = names.getDictRef("Subtype");
            if (videos != null) {
                // create a name tree object
                NameTree tree = new NameTree(videos);

                // find the value we're looking for
                PDFObject obj = tree.find(str.getStringValue());

                // if we get back a dictionary, look for the /D value
                if (obj != null && obj.getType() == PDFObject.DICTIONARY) {
                    obj = obj.getDictRef("D");
                }

                // found it
                return obj;
            }
        }

        // not found
        return null;
    }
	
}
