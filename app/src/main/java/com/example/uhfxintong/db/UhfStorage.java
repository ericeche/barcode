package com.example.uhfxintong.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class UhfStorage {

	
	 private static final String TAG = "UhfStorage";
	 
	 	private DBHelper helper;
	 	private DBHelper historyHelper;
	    private static final String DB_NAME = "uhf_data.db";
	    public static final String TABLE_NAME = "uhf_data";
	    public static final String TABLE_NAME2 = "history_tb";
	    private static final int DB_VERSION = 1;
	    
	    static final String COLUM_ID = "_id";
	    static final String COLUM_NAME = "name";
	    static final String COLUM_TIME = "time";
	    static final String COLUM_FACTORY="factory";
	    
	    static final String COLUM_OPERATOR="operator";
	    static final String COLUM_VOLT="voltgrade";
	    static final String COLUM_LINE="linespace";
	    static final String COLUM_DEFECT = "defect";
	    static final String COLUM_NOTES = "notes";
	    static final String COLUM_PHOTO = "photos";
	    //创建数据表语句
	    private String mCreateTableSql="create table if not exists "+
	    TABLE_NAME+"("+COLUM_ID+" text primary key not null,"
	    		+ COLUM_NAME+" text,"+COLUM_TIME+" text,"+COLUM_OPERATOR+
	    		" text,"+COLUM_VOLT+" text,"+COLUM_LINE+" text,"
	    		+ COLUM_FACTORY+" text"+")";
		private String mCreateHistoryTable = "create table if not exists "
			+ TABLE_NAME2 + "(" + COLUM_ID + " text not null,"
			+ COLUM_NAME + " text," + COLUM_TIME + " text," + COLUM_OPERATOR
			+ " text," + COLUM_VOLT + " text," + COLUM_LINE + " text,"
			+ COLUM_FACTORY + " text,"+ COLUM_DEFECT + " text,"+ COLUM_NOTES + " text," +  COLUM_PHOTO + " text "+ ")";
	   //创建数据库和数据表
	    public UhfStorage(Context context){
	    	
	    	helper = new DBHelper(context,DB_NAME, DB_VERSION,TABLE_NAME,mCreateTableSql, TABLE_NAME2, mCreateHistoryTable);
	    }
	    
	    
	    /**
	     * 插入数据
	     * @param cv
	     * @return
	     */
	    public long insert(ContentValues cv){
	    	SQLiteDatabase db = helper.getWritableDatabase();
	    	long result = 0;
	    	try{
	    	 result=db.insert(TABLE_NAME, null, cv);
	    	db.close();//操作完毕关闭数据库
	    	Log.i(TAG, "insert success"+result);	
    		
	    	}catch(SQLException e){
	    		e.printStackTrace();
	    		Log.e(TAG, "Failed to insert into "+TABLE_NAME);
	    	}
			return result;
			}
	    public long insertHistoryTb(ContentValues cv){
	    	SQLiteDatabase db = helper.getWritableDatabase();
	    	long result = 0;
	    	try{
		    	result=db.insert(TABLE_NAME2, null, cv);
		    	db.close();//操作完毕关闭数据库
		    	Log.i(TAG, "insert success"+result);	
    		
	    	}catch(SQLException e){
	    		e.printStackTrace();
	    		Log.e(TAG, "Failed to insert into "+TABLE_NAME);
	    	}
			return result;
			}
	    /**
	     * 更新数据
	     * @param cv
	     * @param id
	     */
	    public int update(ContentValues cv,String id){
	    	SQLiteDatabase db = helper.getWritableDatabase();
	    	String where = "_id=?";
			String[] args=new String[]{id};
	    	int i = db.update(TABLE_NAME, cv, where, args);
	    	db.close();
			return i;
	    }
	    
	    public String[] getColumns(){
			return new String[]{COLUM_ID,COLUM_NAME,COLUM_TIME, COLUM_FACTORY,COLUM_OPERATOR,COLUM_VOLT,COLUM_LINE};
	    	
	    }
	    public String[] getHistoryColumns(){
			return new String[]{COLUM_ID,COLUM_NAME,COLUM_TIME, COLUM_FACTORY,COLUM_OPERATOR,COLUM_VOLT,COLUM_LINE,COLUM_DEFECT,COLUM_NOTES, COLUM_PHOTO};
	    	
	    }
	    /**获取所有射频信息
	     * @return
	     */
	    public List<Uhf> getAllUhf(){
	    	List<Uhf> uhfs= new ArrayList<Uhf>();
	    	SQLiteDatabase db = helper.getReadableDatabase();
	    	Cursor cursor = db.query(TABLE_NAME, getColumns(), null, null, null, null, COLUM_ID+" DESC");
			
	    	while(cursor.moveToNext()){
	    		
	    		Uhf uhf=fillUhf(cursor);
	    		uhfs.add(uhf);
	    		
	    	}
	    	cursor.close();
	    	db.close();
	    	return uhfs;
	    	
	    }
	    public List<Uhf> getAllHistoryUhf(){
	    	List<Uhf> uhfs= new ArrayList<Uhf>();
	    	SQLiteDatabase db = helper.getReadableDatabase();
	    	Cursor cursor = db.query(TABLE_NAME2, getHistoryColumns(), null, null, null, null, COLUM_TIME);
			//Cursor cursor = db.rawQuery("select "+COLUM_ID+","+COLUM_NAME+","+COLUM_TIME+","+COLUM_FACTORY+","+COLUM_OPERATOR+","+COLUM_VOLT+","+COLUM_LINE+","+COLUM_DEFECT+","+COLUM_NOTES+"from history_tb order by "+COLUM_TIME+" DESC",null);
	    	while(cursor.moveToNext()){
	    		
	    		Uhf uhf=fillHistoryUhf(cursor);
	    		uhfs.add(uhf);
	    		
	    	}
	    	cursor.close();
	    	db.close();
	    	return uhfs;
	    	
	    }

		private Uhf fillUhf(Cursor cursor) {
			Uhf uhf=new Uhf();
			uhf.setUhfId(cursor.getString(cursor.getColumnIndex(COLUM_ID)));
			uhf.setUhfName(cursor.getString(cursor.getColumnIndex(COLUM_NAME)));
			uhf.setFactory(cursor.getString(cursor.getColumnIndex(COLUM_FACTORY)));
			uhf.setTime(cursor.getString(cursor.getColumnIndex(COLUM_TIME)));
			uhf.setOperator(cursor.getString(cursor.getColumnIndex(COLUM_OPERATOR)));
			uhf.setVoltGrade(cursor.getString(cursor.getColumnIndex(COLUM_VOLT)));
			uhf.setLineSpace(cursor.getString(cursor.getColumnIndex(COLUM_LINE)));
			return uhf;
		}
		private Uhf fillHistoryUhf(Cursor cursor) {
			Uhf uhf=new Uhf();
			uhf.setUhfId(cursor.getString(cursor.getColumnIndex(COLUM_ID)));
			uhf.setUhfName(cursor.getString(cursor.getColumnIndex(COLUM_NAME)));
			uhf.setFactory(cursor.getString(cursor.getColumnIndex(COLUM_FACTORY)));
			uhf.setTime(cursor.getString(cursor.getColumnIndex(COLUM_TIME)));
			uhf.setOperator(cursor.getString(cursor.getColumnIndex(COLUM_OPERATOR)));
			uhf.setVoltGrade(cursor.getString(cursor.getColumnIndex(COLUM_VOLT)));
			uhf.setLineSpace(cursor.getString(cursor.getColumnIndex(COLUM_LINE)));
			uhf.setDefect(cursor.getString(cursor.getColumnIndex(COLUM_DEFECT)));
			uhf.setNotes(cursor.getString(cursor.getColumnIndex(COLUM_NOTES)));
			uhf.setPhotos(cursor.getString(cursor.getColumnIndex(COLUM_PHOTO)));
			return uhf;
		}
		public void clear(){
			
			SQLiteDatabase db=helper.getWritableDatabase();
			db.delete(TABLE_NAME, null, null);
			db.close();
			
		}
		
		/**
		 * 根据Id获取对应的Uhf射频对象
		 * @param id
		 * @return
		 */
		public Uhf getUhfById(String id){
			
			SQLiteDatabase db=helper.getReadableDatabase();
			String where = "_id=?";
			String[] args=new String[]{id};
			Cursor cursor = db.query(TABLE_NAME, getColumns(), where, args, null, null, null);
			
			Uhf uhf=null;
			if(cursor.moveToFirst()){
				
				uhf=fillUhf(cursor);
				
			}
			cursor.close();//关闭游标
			db.close();
			return uhf;
			
		}
}
