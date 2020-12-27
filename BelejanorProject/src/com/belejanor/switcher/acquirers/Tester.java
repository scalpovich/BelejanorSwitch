package com.belejanor.switcher.acquirers;


public class Tester {

	public static void main(String[] args) {
		
		
//		IVRIsAcq aa = new IVRIsAcq();
//		
//		String[] cc = aa.getPreguntasDesafioRandom(3,7);
//		for (String s : cc) {
//			System.out.println(s);
//		}
		//System.out.println(obtenerAleatorio2(0,9));
		
		//orElse
		
		String valores = "1,2,3,4";
		String buscar = "2";
		int a = valores.indexOf(buscar);
		System.out.println(a);

	}
	public static int obtenerAleatorio2(int desde, int hasta){
		
		  String acum = "";
		  int num = 0;
		  for (int i = 0; i < 3; i++) {
			  num = (int) (Math.random() * ( hasta - desde + 1 ) ) + desde;
			  if(i == 0 && num == 0) {
				  num = 1;
			  }
			  acum+=String.valueOf(num);
		  }
		  return Integer.parseInt(acum);
	} 
	public static boolean validaRepetidos(long number) {
		
		 boolean res = false;
	     int[] cont = new int[10]; 
	     int digito;
	     while(number > 0) {
	         digito = (int) (number % 10);
	         cont[digito]++;
	         number /= 10;
	     }
	     for(digito=0;digito<10;digito++) {
	         if(cont[digito]>1) {
	            res = true;
	            break;
	         }
	     }
	     return res;
	}
	
	public static int obtenerAleatorio(int desde, int hasta){
	      return (int)( Math.random() * ( hasta - desde + 1 ) ) + desde;
	} 

}
