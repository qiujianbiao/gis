package com.ericsson.fms.utils;

public class BlockUtils {

	/*
A:	55.36813196624758
	24.92666303374804

B:	55.10793623413088
	24.92666303374804
	
C:	55.36813196624758
	25.249022488631727

Dis AB:	26.2667
Dis AC:	35.8849

10米 : 经度:	26.2667 * 1000 : (55.36813196624758 - 55.10793623413088) = 10: 0.000099059163167318
10米 : 纬度:	35.8849 * 1000 : (25.249022488631727 - 24.92666303374804) = 10: 0.00008983150430506663
	 * 
	 * 
	 * 
	 * */
	
	//10: 0.000099059163167318
	//10: 0.00008983150430506663
	/**
	 * 
	 * @param longitude 经度值
	 * @param latitude	纬度值
	 * @param loblock	经度分块长
	 * @param lablock	纬度分块长
	 * @return
	 * String 块Id： %7.0X_%7.0Y
	 */
	public static String getBlockId(double longitude, double latitude, double loblock, double lablock) {
		if (loblock > 0 && lablock > 0) {
			return String.format("%07.0f_%07.0f", longitude / loblock, latitude / lablock);
		}
		return null;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double longitude = 55.10793623413088;
		double Latitude = 24.92666303374804;
		double loblock = 0.0001;
		double lablock = 0.0001;
		
		for(int i = 0; i < 5;i++) {
			for(int j = 0; j < 5;j++) {
				System.out.println(String.format("X:%10f    Y:%10f :", longitude + loblock * j,Latitude + lablock * i) + getBlockId(longitude + loblock * j,Latitude + lablock * i,loblock,lablock));
			}
		}
		//25.0824610000,55.2877590000
		System.out.println(String.format("X:%10f    Y:%10f :", 55.2877590000,25.0824610000) + getBlockId(55.2877590000,25.0824610000,loblock,lablock));
		
		//25.0803010000,55.2620310000
		System.out.println(String.format("X:%10f    Y:%10f :", 55.2620310000,25.0803010000) + getBlockId(55.2620310000,25.0803010000,loblock,lablock));
	}
}
