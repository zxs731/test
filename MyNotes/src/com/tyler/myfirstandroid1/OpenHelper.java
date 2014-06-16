package com.tyler.myfirstandroid1;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class OpenHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "BugTraceNote";	//��ݿ��ļ����
	public static final String TABLE_BUG = "Bug";		//����
	public static final String BUG_ID="_id";						//ID
	public static final String TFS_ID="tfsid";
	public static final String BUG_TITLE="bugtitle";					//bug brife
	
	public static final String TABLE_EVENT = "Event";		//����
	public static final String EVENT_ID="_id";						//ID
	public static final String EVENT_DES="event";					//event brife
	public static final String REF_BUG_ID="bugid";	 //bug id

	public OpenHelper(Context context, String name, CursorFactory factory, int version)
	{//���ø��๹����
		super(context, name, factory, version);		
	}
	@Override
	public void onCreate(SQLiteDatabase db)
	{		//��дonCreate����
		db.execSQL
		(
				"create table if not exists "+TABLE_BUG+"("	//����execSQL����������
				+ BUG_ID + " integer primary key,"
				+ BUG_TITLE + " varchar,"
				+ TFS_ID + " varchar);"
		);
		
		db.execSQL
		(
				"create table if not exists "+TABLE_EVENT+"("	//����execSQL����������
				+ EVENT_ID + " integer primary key,"
				+ EVENT_DES + " varchar,"
				+ REF_BUG_ID + " integer);" //? how to define Foregner Key
		);
		//add some sample data
		ContentValues cv=new ContentValues();
		cv.put(BUG_TITLE, "Cannot load home page.");
		cv.put(TFS_ID, "102345");
		long count=db.insert(TABLE_BUG, BUG_ID, cv);
		System.out.println("1----------after insert return value: "+count);
		ContentValues cv2=new ContentValues();
		cv2.put(EVENT_DES, "send email to customer! waiting.");
		cv2.put(REF_BUG_ID,count+"");
		db.insert(TABLE_EVENT, EVENT_ID, cv2);
		ContentValues cv3=new ContentValues();
		cv3.put(EVENT_DES, "Got feedback, go ahead.");
		cv3.put(REF_BUG_ID,count+"");
		db.insert(TABLE_EVENT, EVENT_ID, cv3);
		
		ContentValues cv4=new ContentValues();
		cv4.put(BUG_TITLE, "Error exists when close page.");
		cv4.put(TFS_ID, "310265");
		long count2=db.insert(TABLE_BUG, BUG_ID, cv4);
		System.out.println("2----------after insert return value: "+count2);
		//end sample data
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{	//��дonUpgrade����
		
	}

}
