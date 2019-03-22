package com.peihou.willgood2.utils;

public class TenTwoUtil {

	public static int[] changeToTwo (int x){

		int[] result = new int[8];
		result[0]=x/128;
		result[1]=(x%128)/64;
		result[2]=(x%64)/32;
		result[3]=(x%32)/16;
		result[4]=(x%16)/8;
		result[5]=(x%8)/4;
		result[6]=(x%4)/2;
		result[7]=(x%2)/1;
		return result;
	}
	
	public static int changeToTen(int[] x){
		int result =0;
		for (int i =0; i <8; i++) {
			result+=x[i]<<(i);
		}
		return result;
	}

	public static int changeToTen2(int[] x){
		int result =0;
		for (int i =0; i <8; i++) {
			result+=x[i]<<(7-i);
		}
		return result;
	}
	
}
