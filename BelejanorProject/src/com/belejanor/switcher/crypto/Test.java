package com.belejanor.switcher.crypto;

public class Test {

	public static void main(String[] args) {
		
		GetPinBlock u = new GetPinBlock();
		String a = null;
		try {
			a = u.getPinBlock("12345");
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		System.out.println(a);
	}

}
