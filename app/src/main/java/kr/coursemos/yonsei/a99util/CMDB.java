package kr.coursemos.yonsei.a99util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CMDB {
	private Context context=null;
	private CMDBHelper dbhepler=null;
	private SQLiteDatabase db=null;
	private Cursor cursor=null;
	public final static String DB = "CRON";
	public final static int DB_VERSION = 21;
	public final static String TABLE01_common = "TABLE01_common";
	public final static String TABLE01_03SubjectList = "TABLE01_03SubjectList";
	public final static String TABLE01_03z01TimeTable = "TABLE01_03z01TimeTable";
	public final static String TABLE01_04SubjectDetail = "TABLE01_04SubjectDetail";
	public final static String TABLE01_04z01BoardInfo = "TABLE01_04z01BoardInfo";
	public final static String TABLE01_05ProfessorList = "TABLE01_05ProfessorList";
	public final static String TABLE01_06StudentList = "TABLE01_06StudentList";
	public final static String TABLE01_07AttendanceTeacher = "TABLE01_07AttendanceTeacher";
	public final static String TABLE01_07Z91AutoAttendanceTeacher = "TABLE01_07Z91AutoAttendanceTeacher";
	public final static String TABLE01_08AttendanceStudent = "TABLE01_08AttendanceStudent";
	public final static String TABLE01_10BoardList = "TABLE01_10BoardList";
	public final static String TABLE01_10z01BoardDetail = "TABLE01_10z01BoardDetail";
	public final static String TABLE01_weekly = "TABLE01_weekly";
	public final static String TABLE01_11ChattingDetail = "TABLE01_11ChattingDetail";
	public final static String TABLE01_15Schedule = "TABLE01_15Schedule";
	public final static String TABLE01_16ChattingList = "TABLE01_16ChattingList";
	public final static String TABLE01_18MyReport = "TABLE01_18MyReport";
	public final static String TABLE01_20Alarm = "TABLE01_20Alarm";
	public final static String COLUMN_id = "_id";
	public final static String COLUMNtype1 = "type1";
	public final static String COLUMNtype2 = "type2";
	public final static String COLUMNtype3 = "type3";
	public final static String COLUMNjson = "json";

	public final static String[] tables = { TABLE01_common,TABLE01_03SubjectList,TABLE01_03z01TimeTable,TABLE01_04SubjectDetail, TABLE01_04z01BoardInfo,TABLE01_weekly,TABLE01_07AttendanceTeacher,TABLE01_07Z91AutoAttendanceTeacher,TABLE01_08AttendanceStudent,TABLE01_10BoardList,TABLE01_10z01BoardDetail,TABLE01_05ProfessorList,TABLE01_06StudentList, TABLE01_15Schedule,TABLE01_16ChattingList,TABLE01_11ChattingDetail,TABLE01_18MyReport,TABLE01_20Alarm};

	private CMDB(Context context) {
		this.context = context;
		dbhepler = new CMDBHelper(this.context);
		db = dbhepler.getWritableDatabase();

	}

	public static CMDB open(Context context) {
		if (StaticClass.cmdb instanceof CMDB) {

		} else {
			StaticClass.cmdb = new CMDB(context);
		}
		return StaticClass.cmdb;
	}



	public Cursor selectChattingFirst(String userid)  throws Exception{
		cursorCheck();
		//상대아이디, (자신 또는 상대아이디), 시간
		cursor = db.query(false, TABLE01_11ChattingDetail, new String[] {COLUMN_id,COLUMNtype1,COLUMNtype2,COLUMNtype3, COLUMNjson }, " "+COLUMNtype1+" = '"+userid+"' " , null, null, null," _id DESC ", " 0, 10 ");
		return cursor;
	}
	public Cursor selectChattingNew(String userid,int _id)  throws Exception{
		cursorCheck();
		//상대아이디, (자신 또는 상대아이디), 시간
		cursor = db.query(false, TABLE01_11ChattingDetail, new String[] {COLUMN_id,COLUMNtype1,COLUMNtype2,COLUMNtype3, COLUMNjson }, " "+COLUMNtype1+" = '"+userid+"' AND _id > "+_id , null, null, null," _id ASC ", " 0, 10 ");
		return cursor;
	}

	public Cursor selectChattingHistory(String userid,int _id)  throws Exception{
		cursorCheck();
		//상대아이디, (자신 또는 상대아이디), 시간
		cursor = db.query(false, TABLE01_11ChattingDetail, new String[] {COLUMN_id,COLUMNtype1,COLUMNtype2,COLUMNtype3, COLUMNjson }, " "+COLUMNtype1+" = '"+userid+"' AND _id < "+_id , null, null, null," _id DESC ", " 0, 10 ");
		return cursor;
	}



	public String selectString(String type1, String type2, String type3, String table)  throws Exception{
		cursorCheck();
		cursor = db.query(false, table, new String[] { COLUMN_id, COLUMNtype1,COLUMNtype2,COLUMNtype3, COLUMNjson }, " " + COLUMNtype1 + " = '" + type1 + "' AND " + COLUMNtype2 + " = '" + type2 + "' AND " + COLUMNtype3 + " = '" + type3 + "' ", null, null, null, null, null);
		String result = "";
		if (cursor.moveToNext()) {
			result = cursor.getString(cursor.getColumnIndex(COLUMNjson));
		}
		cursorCheck();
		return result;
	}

	public Cursor select(String query, String[] params)  throws Exception{
		cursorCheck();
		cursor = db.rawQuery(query,params);
		return cursor;
	}

	public Cursor select(String type1, String type2, String type3, String table)  throws Exception{
		return select(type1,type2,type3,table," _id ASC ");
	}

	public Cursor select(String type1, String type2, String type3, String table,String orderby)  throws Exception{
		cursorCheck();
		cursor = db.query(false, table, new String[] { COLUMN_id, COLUMNtype1,COLUMNtype2,COLUMNtype3, COLUMNjson }, getWhere(type1, type2, type3), null, null, null, orderby, null);
		return cursor;
	}


	public Cursor select(String[] type1List, String[] type2List, String[] type3List, String table)  throws Exception{
		StringBuilder sb=new StringBuilder();
		int size=0;
		if(type1List!=null){
			if(sb.length()>0){
				sb.append(" AND ( ");
			}else{
				sb.append(" ( ");
			}
			size=type1List.length;
			for(int i=0;i<size;i++){
				if(i==0){
				}else{
					sb.append(" OR ");
				}
				sb.append(" " + COLUMNtype1 + " = '" + type1List[i] + "' ");
			}
			sb.append(" ) ");
		}

		if(type2List!=null){
			if(sb.length()>0){
				sb.append(" AND ( ");
			}else{
				sb.append(" ( ");
			}
			size=type2List.length;
			for(int i=0;i<size;i++){
				if(i==0){
				}else{
					sb.append(" OR ");
				}
				sb.append(" " + COLUMNtype2 + " = '" + type2List[i] + "' ");
			}
			sb.append(" ) ");
		}

		if(type3List!=null){
			if(sb.length()>0){
				sb.append(" AND ( ");
			}else{
				sb.append(" ( ");
			}
			size=type3List.length;
			for(int i=0;i<size;i++){
				if(i==0){
				}else{
					sb.append(" OR ");
				}
				sb.append(" " + COLUMNtype3 + " = '" + type3List[i] + "' ");
			}
			sb.append(" ) ");
		}




		cursorCheck();
		cursor = db.query(false, table, new String[] { COLUMN_id, COLUMNtype1,COLUMNtype2,COLUMNtype3, COLUMNjson },sb.toString() , null,COLUMN_id,null," _id ASC ",null);
		return cursor;
	}
	public long insertORupdate(String type1, String type2, String type3, String json, String table) throws Exception{
		ContentValues values = new ContentValues();
		values.put(COLUMNtype1, type1);
		values.put(COLUMNtype2, type2);
		values.put(COLUMNtype3, type3);
		values.put(COLUMNjson, json);
		int result = db.update(table, values, COLUMNtype1 + "='" + type1 + "' AND " + COLUMNtype2 + "='" + type2 + "' AND " + COLUMNtype3 + "='" + type3 + "'", null);
		if (result == 0) {
			return db.insert(table, null, values);
		}
		return result;
	}

	public long update(String type1, String type2, String type3, String json, String table)  throws Exception{
		ContentValues values = new ContentValues();
		values.put(COLUMNjson, json);
		return db.update(table, values, getWhere(type1, type2, type3), null);
	}

	public long insert(String type1, String type2, String type3, String json, String table)  throws Exception{
		ContentValues values = new ContentValues();
		values.put(COLUMNtype1, type1);
		values.put(COLUMNtype2, type2);
		values.put(COLUMNtype3, type3);
		values.put(COLUMNjson, json);
		return db.insert(table, null, values);
	}

	public void DBClear(boolean isLogout)  throws Exception{
		int size = CMDB.tables.length;
		for (int i = 0; i < size; i++) {
			if (isLogout) {
				db.delete(tables[i], null, null);
			} else {
				if (CMDB.tables[i].equals(CMDB.TABLE01_common) ) {

				} else {
					db.delete(tables[i], null, null);
				}
			}

		}
	}

	public void DBClear(String table) throws Exception {
		db.delete(table, null, null);
	}



	public long delete(String type1, String type2, String type3, String table)  throws Exception{
		long result = db.delete(table, getWhere(type1, type2, type3), null);
		return result;
	}
	public String getWhere(String type1, String type2, String type3)  throws Exception{
		StringBuilder sb=new StringBuilder();
		if(type1!=null){
			if(sb.length()>0){
				sb.append(" AND ");
			}else{
				sb.append(" ");
			}
			sb.append(COLUMNtype1);
			sb.append(" = '");
			sb.append(type1);
			sb.append("'");
		}
		if(type2!=null){
			if(sb.length()>0){
				sb.append(" AND ");
			}else{
				sb.append(" ");
			}
			sb.append(COLUMNtype2);
			sb.append(" = '");
			sb.append(type2);
			sb.append("'");
		}
		if(type3!=null){
			if(sb.length()>0){
				sb.append(" AND ");
			}else{
				sb.append(" ");
			}
			sb.append(COLUMNtype3);
			sb.append(" = '");
			sb.append(type3);
			sb.append("'");
		}
		if(sb.length()>0){
			sb.append(" ");
		}
		return sb.toString();
	}


	public synchronized void cursorCheck()  throws Exception{
		if (cursor instanceof Cursor) {
			cursor.close();
		}
	}

	public void close()  throws Exception{
		db.close();
		dbhepler.close();
		if (cursor instanceof Cursor) {
			cursor.close();
			cursor = null;
		}

	}
}
