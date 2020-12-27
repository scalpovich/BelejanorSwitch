package com.belejanor.switcher.parser;

import java.text.DateFormat;
import java.util.Date;

class CrashThread extends Thread {
	   public static void main(String[] a) {
	      Thread t;
	      int m = 16;
	      Date now;
	      DateFormat df = DateFormat.getTimeInstance();
	      for (int n=1; n<=m; n++) {
	      	 now = new Date();
	         t = new CrashThread();
	         t.start();
	         System.out.println(df.format(now) + " Launched threed "+n);
	      }
	   }
	   public void run() {
	      while (true);
	   }
}
