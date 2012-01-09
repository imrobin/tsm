

package com.justinmobile.core.utils;




public class DateUtils {
	public static int maxDay(int year,int month)
	 {
	  //初始化12个月份的天数
	        int[] months={31,0,31,30,31,30,31,31,30,31,30,31};
	        //如果不是2月则返回该月份的天数
	        if(month!=2){         
	           return months[month-1];
	         }
	         else{
	              //如果是闰年返回为29天，否则28天     
	           if(isLeapYear(year)){      
	           return 29;
	           }
	           else{       
	            return 28;
	          }
	      }      
	 }
	public static boolean isLeapYear(int year){
		if(year%400==0 || year%4==0 && year%100!=0){
			return true;
		}else{
			return false;
		}
	}


}



