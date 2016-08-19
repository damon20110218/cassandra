package com.cfets.ts.cassandra.util;

public class CommonUtil {
	public static boolean isNullOrSpace(String param) {
	    boolean tof = false;
	    if (param == null)
	      tof = true;
	    else if (replace(param, " ", "").equalsIgnoreCase("")) {
	      tof = true;
	    }

	    return tof;
	  }

	  public static boolean isNullOrSpace(Object param)
	  {
	    boolean tof = false;
	    if (param == null)
	      tof = true;
	    else if (replace(param.toString(), " ", "").equalsIgnoreCase("")) {
	      tof = true;
	    }

	    return tof;
	  }
	  
	  public static String replace(String sSource, String sReplace, String sNew) {
		    if ((sSource == null) || (sReplace == null) || (sNew == null))
		      return "";
		    int nLen = sSource.length();
		    int nLen1 = sReplace.length();
		    int nLocate = 0;

		    nLocate = sSource.indexOf(sReplace);
		    while (nLocate >= 0) {
		      nLen = sSource.length();
		      sSource = sSource.substring(0, nLocate) + sNew + sSource.substring(nLocate + nLen1, nLen);

		      nLocate = sSource.indexOf(sReplace, nLocate + sNew.length());
		    }

		    return sSource;
	 }

	public static String replace(String sSource, String sReplace, String sNew, String suffix)
		  {
		    String s = replace(sSource, sReplace, sNew);
		    return s + suffix;
		  }

		  public static String replace(String sSource) {
		    return replace(sSource, " ", "");
	}
}
