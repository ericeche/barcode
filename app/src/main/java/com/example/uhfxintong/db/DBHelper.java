package com.example.uhfxintong.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * 创建数据库
 * @author zhaojing
 *
 */
public class DBHelper extends SQLiteOpenHelper{
	
	private static final String TAG = "DBHelper";
    private String dbName;
    private String tableName;
    private String createTableSql;
    private String createTableSql2;
    public DBHelper(Context context, String dbName,int version,String tableName,String createTableSql,String tableName2, String createTableSql2) {
        
        super(context, dbName, null, version); 
        this.dbName = dbName;
        this.tableName = tableName;
        this.createTableSql = createTableSql;
        this.createTableSql2 = createTableSql2;
}
    
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(createTableSql);
		db.execSQL(createTableSql2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
		String sql="DROP TABLE IF EXISTS "+tableName;
		db.execSQL(sql);
		onCreate(db);
	}

}
