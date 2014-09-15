package net.scratchforfun.xioco.clock;

import java.util.Calendar;

public class Clock {

	public String hour;
	public String minute;
	public String second;
	
	public Clock(String hours, String minutes, String seconds){
		this.hour = hours;
		this.minute = minutes;
		this.second = seconds;
		
		if(this.hour.length() == 1) this.hour = "0"+this.hour;
		if(this.minute.length() == 1) this.minute = "0"+this.minute;
		if(this.second.length() == 1) this.second = "0"+this.second;
	}
	
	public static Clock getClock(){
		Calendar currDate = Calendar.getInstance();
		
		return new Clock(""+currDate.get(Calendar.HOUR_OF_DAY), ""+currDate.get(Calendar.MINUTE), ""+currDate.get(Calendar.SECOND));
	}
	
	public static Date getDate(){
		Calendar currDate = Calendar.getInstance();
		
		return new Date(""+currDate.get(Calendar.DATE), ""+(currDate.get(Calendar.MONTH)+1), ""+currDate.get(Calendar.YEAR));
	}
	
	public static class Date {
		public String day;
		public String month;
		public String year;
		
		/** 
		 * Must be string so that the time 12:07 is not recognized as 12:7 (there is no number 07 so it writes it down as 7)
		 * @param day
		 * @param month
		 * @param year
		 */
		public Date(String day, String month, String year){
			this.day = day;
			this.month = month;
			this.year = year;
			
			if(this.day.length() == 1) this.day = "0"+this.day;
			if(this.month.length() == 1) this.month = "0"+this.month;
		}
	}
	
}
