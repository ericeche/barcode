package com.example.uhfxintong.db;

import java.io.File;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jxl.CellView;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;


/**
 * @author zhaojing
 *
 */
public class UhfService {

	private UhfStorage storage;
	
	private static File root=Environment.getExternalStorageDirectory();
	//打开文件
	private static	File exceltest=new File(root.getAbsolutePath()+File.separator+"test.xls");
  	
//	private File xml=new File(root.getAbsolutePath()+File.separator+"test.xml");
	
	static SimpleDateFormat df=new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
	public UhfService(Context context) {

		storage = new UhfStorage(context);
	}

	/**
	 * 保存射频信息
	 * @param uhf
	 * @return
	 */
	public long saveUhf(Uhf uhf) {

		ContentValues cv = fillContentValues(uhf);
		long count = storage.insert(cv);
		return count;

	}
	public long saveUhfHistory(Uhf uhf) {

		ContentValues cv = fillContentValues2(uhf);
		long count = storage.insertHistoryTb(cv);
		return count;

	}
	public int update(Uhf uhf){
		ContentValues cv = fillContentValues(uhf);
		return storage.update(cv,uhf.getUhfId());
	}
	
	private ContentValues fillContentValues(Uhf uhf) {
		
		ContentValues cv =new ContentValues();
		cv.put(storage.COLUM_ID, uhf.getUhfId());
		cv.put(storage.COLUM_NAME, uhf.getUhfName());
		cv.put(storage.COLUM_TIME, uhf.getTime());
		cv.put(storage.COLUM_FACTORY, uhf.getFactory());
		cv.put(storage.COLUM_OPERATOR, uhf.getOperator());
		cv.put(storage.COLUM_VOLT, uhf.getVoltGrade());
		cv.put(storage.COLUM_LINE, uhf.getLineSpace());
		return cv;
	}
	private ContentValues fillContentValues2(Uhf uhf) {
		
		ContentValues cv =new ContentValues();
		cv.put(storage.COLUM_ID, uhf.getUhfId());
		cv.put(storage.COLUM_NAME, uhf.getUhfName());
		cv.put(storage.COLUM_TIME, uhf.getTime());
		cv.put(storage.COLUM_FACTORY, uhf.getFactory());
		cv.put(storage.COLUM_OPERATOR, uhf.getOperator());
		cv.put(storage.COLUM_VOLT, uhf.getVoltGrade());
		cv.put(storage.COLUM_LINE, uhf.getLineSpace());
		cv.put(storage.COLUM_DEFECT, uhf.getDefect());
		cv.put(storage.COLUM_NOTES, uhf.getNotes());
		cv.put(storage.COLUM_PHOTO, uhf.getPhotos());
		return cv;
	}
	
	/**
	 * 获取所有UHF的信息
	 * @return
	 */
	public List<Uhf> getAllUhf(){
		return storage.getAllUhf();
		
	}
	public List<Uhf> getAllHistoryUhf(){
		return storage.getAllHistoryUhf();
		
	}
	/**
	 * 根据ID获取用户信息
	 * @param id
	 * @return
	 */
	public Uhf getUhfById(String id){
		return storage.getUhfById(id);
		
	}

	/**
	 * 删除所有数据
	 */
	public void deleteAll(){
		
		storage.clear();
	}
	
	
	public void createExcel(List<Uhf> uhfs) throws RowsExceededException, WriteException, IOException, BiffException {
		//获取SD卡的设备状态
		String state=Environment.getExternalStorageState();
		System.out.println(root.getAbsolutePath());
	
		//拥有可读可写权限
		if(state.equals(Environment.MEDIA_MOUNTED)){
		
			System.out.println("SD卡拥有可读可写权限.............");
			//如果不存在，创建该文件
			if(!exceltest.exists()){
				  
				try {
					exceltest.createNewFile();
					System.out.println("创建了新的文件.............");
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("发生异常");
				}
			}	
		}
		System.out.println("createExcel>>>>>>>>>>>>>>>>>>>>>>>>>>.");	
		WritableWorkbook book = null;
		try {
			book = Workbook.createWorkbook(exceltest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		WritableSheet sheet=book.createSheet("test", 0);
		Label label1=new Label(1, 1, "UII号");
		sheet.addCell(label1);
		Label label4=new Label(2, 1, "设备名称");
		sheet.addCell(label4);
		Label label2=new Label(3, 1, "操作者");
		sheet.addCell(label2);
		Label label3=new Label(4, 1, "工作时间");
		sheet.addCell(label3);
		Label label6=new Label(5, 1, "电压等级");
		sheet.addCell(label6);
		Label label7=new Label(6, 1, "所属间隔");
		sheet.addCell(label7);
		
		System.out.println("设备名称  设备名称>>>>>>>>.");	
		//写入数据并关闭文件
		writeExcel(book,uhfs);
//		book.write();
//		book.close();
	
	}
	
	
	public void writeExcel(WritableWorkbook book, List<Uhf> uhfs) throws BiffException, IOException, RowsExceededException, WriteException{
		
		
		CellView cellView = new CellView();
		cellView.setAutosize(true);
		System.out.println("writeExcel>>>>>>>>>>>>>>>>>>>>>>>>>>.");
		WritableSheet sheet=  book.getSheet("test");
		sheet.setColumnView(1, cellView);
		sheet.setColumnView(4, cellView);
		int row=2;
		for(Uhf uhf:uhfs){
			
		
		sheet.addCell(new Label(1, row, uhf.getUhfId()));
		sheet.addCell(new Label(2, row, uhf.getUhfName()));
		sheet.addCell(new Label(3, row, uhf.getOperator()));
		sheet.addCell(new Label(4, row, uhf.getTime()));
		sheet.addCell(new Label(5, row, uhf.getVoltGrade()));
		sheet.addCell(new Label(6, row, uhf.getLineSpace()));
		row++;
		}
		System.out.println("writeExcel.");	
		book.write();
		System.out.println("writeExcel>>>>>>>>>>end ******s.");
		
		book.close();

	}
}
