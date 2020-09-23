package neleso.utils;


    
  

public class ArrayUtils {
	
	public static final String[] EMPTY_STRING_ARRAY = new String[0];

	  public static String[] nullToEmpty(String[] array) {
		            if (array == null || array.length == 0) {
		                  return EMPTY_STRING_ARRAY;
		              }
		              return array;
		          }
}
