package kr.coursemos.yonsei.a99util;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CMDBHelper extends SQLiteOpenHelper {
	private Context context = null;

	public CMDBHelper(Context context) {
		super(context, CMDB.DB, null, CMDB.DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			int size = CMDB.tables.length;
			StringBuffer sql = null;
			String table=null;
			for (int i = 0; i < size; i++) {
				sql = new StringBuffer("");
				table=CMDB.tables[i];
				sql.append("create table " + table + "(");
				sql.append("_id integer primary key autoincrement,");
				sql.append(CMDB.COLUMNtype1 + " text not null,");
				sql.append(CMDB.COLUMNtype2 + " text not null,");
				sql.append(CMDB.COLUMNtype3 + " text not null,");
				sql.append(CMDB.COLUMNjson + " text not null )");
				db.execSQL(sql.toString());
				db.execSQL("create index "+table+"_type1 on "+table+"("+CMDB.COLUMNtype1+")");
				db.execSQL("create index "+table+"_type2 on "+table+"("+CMDB.COLUMNtype2+")");
				db.execSQL("create index "+table+"_type3 on "+table+"("+CMDB.COLUMNtype3+")");
				db.execSQL("create index "+table+"_type12 on "+table+"("+CMDB.COLUMNtype1+","+CMDB.COLUMNtype2+")");
				db.execSQL("create index "+table+"_type123 on "+table+"("+CMDB.COLUMNtype1+","+CMDB.COLUMNtype2+","+CMDB.COLUMNtype3+")");


			}
		} catch (Exception e) {
			Env.error("", e);
		}

	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			int size = CMDB.tables.length;
			for (int i = 0; i < size; i++) {
				db.execSQL("drop table if exists " + CMDB.tables[i]);
			}
			Editor edit = context.getSharedPreferences(context.getPackageName(), Service.MODE_PRIVATE).edit();
			edit.clear();
			edit.commit();
			onCreate(db);
		} catch (Exception e) {
			Env.error("", e);
		}
	}

}
