package kr.coursemos.yonsei.a51custometc;

import android.graphics.Color;

public class A901Color extends Color {
	public static int r =0x1b;
	public static int g =0xbc;
	public static int b =0x9b;
	public static int rDown =0x1b;
	public static int gDown =0x9c;
	public static int bDown =0x7b;
	public static void setMainColor(String colorString) throws Exception{
		if(colorString.equals("")){
			r =0x1b;
			g =0xbc;
			b =0x9b;
			rDown =0x1b;
			gDown =0x9c;
			bDown =0x7b;
			return;
		}
		r=Integer.parseInt(colorString.substring(0,2),16);
		g=Integer.parseInt(colorString.substring(2,4),16);
		b=Integer.parseInt(colorString.substring(4,6),16);

		rDown=r-0x20;
		gDown=g-0x20;
		bDown=b-0x20;
		if(rDown<0){
			rDown=0;
		}

		if(gDown<0){
			gDown=0;
		}

		if(bDown<0){
			bDown=0;
		}

	}
	public static int getCustomOrange(){
		return A901Color.rgb( 0xf1, 0x75, 0x4c);
	}
	public static int getCustomLTGreen(){
		return A901Color.rgb( 0x1b, 0xbc, 0x9b);
	}
	public static int getLoginBackground(){
		return A901Color.rgb( 0x00, 0x00, 0x00);
	}
	public static int getInputBackground(){
		return A901Color.rgb( 0xeb, 0xeb, 0xeb);
	}
	public static int get34ad81(){
		return A901Color.rgb( 0x34, 0xad, 0x81);
	}
	public static int get6a6c73(){
		return A901Color.rgb( 0x6a, 0x6c, 0x73);
	}
	public static int getIhwaColor(){
		return A901Color.rgb( r, g, b);
	}
	public static int getIhwaColorDown(){
		return A901Color.rgb( rDown, gDown, bDown);
	}
	public static int getHeaderBackground(){return A901Color.rgb(0x69,0x6c,0x71);}
	public static int getDarkHeaderBackground(){return A901Color.rgb(0x53,0x56,0x58);}
	public static int getDarkHeaderBackgroundDown(){return A901Color.rgb(0x66,0x66,0x66);}
	public static int getDefaultBackground(){
		return A901Color.rgb( 0xef, 0xee, 0xf4);
	}
	public static int getOrange(){
		return A901Color.rgb( 0xe8, 0x80, 0x5b);
	}
	public static int getOrangeDown(){
		return A901Color.rgb( 0xd8, 0x60, 0x3b);
	}
	public static int getTouchDownColor(){return A901Color.argb(0x80, 0x00, 0x00, 0x00);}
	public static int getTextColor(){return A901Color.rgb( 0x66, 0x66, 0x66);}

	public static int getAttendanceAbsence(){return A901Color.rgb(0xef, 0x83, 0x60);}
	public static int getAttendanceLateness(){return A901Color.rgb(0xff, 0xa2, 0x44);}
	public static int getAttendanceEtc(){return A901Color.rgb(0x01, 0x8c, 0xfe);}

}
