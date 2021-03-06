package com.tyler.myfirstandroid1;


import static com.tyler.myfirstandroid1.OpenHelper.*;

import com.tyler.myfirstandroid1.OpenHelper;
import com.tyler.myfirstandroid1.R;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BugDetailActivity extends Activity {
	OpenHelper ohelper;			//锟斤拷锟斤拷一锟斤拷MyOpenHelper锟斤拷锟斤拷
	final int M_EXIT = Menu.FIRST;			//锟斤拷锟斤拷说锟斤拷锟侥憋拷锟�
	final int M_Clear = Menu.FIRST+1;		//锟斤拷锟斤拷说锟斤拷锟侥憋拷锟�
	final int MENU_DELETE = Menu.FIRST+2;		//锟斤拷锟斤拷说锟斤拷锟侥憋拷锟�
	final int MENU_SAVE = Menu.FIRST+3;			//锟斤拷锟斤拷说锟斤拷锟侥憋拷锟�    
	int id = -1;					//锟斤拷录锟斤拷前锟斤拷示锟斤拷bugid 
	int [] textIds =
	{
		R.id.BugTitle,
		R.id.tfsId,
		R.id.BugDescription,
	};
	EditText [] textArray;			//锟斤拷沤锟斤拷锟斤拷械锟紼ditText锟截硷拷锟斤拷锟斤拷锟斤拷
	Button ibSave;				//锟斤拷锟芥按钮
	int status = -1;				//0锟斤拷示锟介看锟斤拷息锟斤拷1锟斤拷示锟斤拷锟絙ug锟斤拷2锟斤拷示锟睫革拷bug
	View.OnClickListener myListener = new View.OnClickListener()
	{
		//@Override
		public void onClick(View v)
		{
			int ptId=-1;
			String [] strArray = new String[textArray.length];
			for(int i=0;i<strArray.length;i++)
			{
				strArray[i] = textArray[i].getText().toString().trim();	//锟斤拷锟斤拷没锟斤拷锟斤拷锟斤拷锟斤拷息锟斤拷锟斤拷
			}
			if(strArray[0].equals("") || strArray[1].equals(""))
			{
				Toast.makeText(BugDetailActivity.this, "锟皆诧拷锟斤拷锟斤拷锟斤拷写锟斤拷锟斤拷!", Toast.LENGTH_LONG).show();
				ptId = 0;
			}
			if(ptId==-1)
			{
				switch(status)
				{		//锟叫断碉拷前锟斤拷状态
					case 0:				//锟斤拷询bug锟斤拷细锟斤拷息时锟斤拷锟铰憋拷锟斤拷
						updateBug(strArray);		//锟斤拷锟斤拷bug锟斤拷息
					break;
					case 1:				//锟铰斤拷bug时锟斤拷锟铰憋拷锟芥按钮
						addNewBug(strArray);		//锟斤拷锟斤拷bug锟斤拷息
					break;
				}
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bug_detail);
		textArray = new EditText[textIds.length];
		for(int i=0;i<textIds.length;i++)
		{
			textArray[i] = (EditText)findViewById(textIds[i]); 
		}
		ibSave = (Button)findViewById(R.id.ibSave);	
		ibSave.setOnClickListener(myListener);
		ohelper = new OpenHelper(this, OpenHelper.DB_NAME, null, 1);
		Intent intent = getIntent();
		status = intent.getExtras().getInt("cmd");
		switch(status)
		{
			case 0:			//锟介看bug锟斤拷锟斤拷细锟斤拷息
				ibSave.setText("Update");
				id = intent.getExtras().getInt("id");		//锟斤拷锟揭拷锟绞撅拷锟絙ug锟斤拷id
				SQLiteDatabase db = ohelper.getWritableDatabase();
				Cursor c = db.query(OpenHelper.TABLE_BUG, new String[]{BUG_TITLE,TFS_ID}, BUG_ID+"=?", new String[]{id+""}, null, null, null);
				if(c.getCount() == 0)
				{
					Toast.makeText(this, "No update", Toast.LENGTH_LONG).show();
				}
				else
				{
					c.moveToFirst();
					textArray[0].setText(c.getString(0));
					textArray[1].setText(c.getString(1));
				}
				c.close();
				db.close();
			break;
			case 1:					//锟铰斤拷bug锟斤拷息
				ibSave.setText("Add");
				for(EditText et:textArray)
				{
					et.getEditableText().clear();
				}
			break;
		}
	}
	
	public void addNewBug(String [] strArray)
	{
		SQLiteDatabase db = ohelper.getWritableDatabase();		//锟斤拷锟斤拷锟捷匡拷锟斤拷锟�
		ContentValues cvalue = new ContentValues();
		cvalue.put(BUG_TITLE, strArray[0]);
		cvalue.put(TFS_ID, strArray[1]);
		long count = db.insert(TABLE_BUG, BUG_ID, cvalue);			//锟斤拷锟斤拷锟斤拷锟�	
		db.close();
		System.out.println("---------add new bug return: "+count);
		if(count == -1)
		{
			Toast.makeText(this,getResources().getString(R.string.AddNoteFailed) , Toast.LENGTH_LONG).show();
		}
		else
		{
			Toast.makeText(this, getResources().getString(R.string.AddNoteSuccess), Toast.LENGTH_LONG).show();
		}
	}
	
	public void updateBug(String [] strArray)
	{
		SQLiteDatabase db = ohelper.getWritableDatabase();		//锟斤拷锟斤拷锟捷匡拷锟斤拷锟�
		ContentValues cvalue = new ContentValues();
		cvalue.put(BUG_TITLE, strArray[0]);
		cvalue.put(TFS_ID, strArray[1]);
		int count = db.update(TABLE_BUG, cvalue, BUG_ID+"=?", new String[]{id+""});	//锟斤拷锟斤拷锟斤拷菘锟�	
		db.close();
		if(count == 1)
		{
			Toast.makeText(this, getResources().getString(R.string.UpdateNoteSuccess), Toast.LENGTH_LONG).show();
		}
		else
		{
			Toast.makeText(this,getResources().getString(R.string.UpdateNoteFailed), Toast.LENGTH_LONG).show();
		}
	}
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	    	menu.add(0,M_Clear, 0, "Clear ALL");//锟斤拷印锟紼xit锟斤拷锟剿碉拷选锟斤拷
	    	menu.add(0,M_EXIT, 0, "Return");//锟斤拷印锟紼xit锟斤拷锟剿碉拷选锟斤拷
	        return true;
	    }
	    @Override
		public boolean onOptionsItemSelected(MenuItem item)
		{
			switch(item.getItemId()){		//锟叫断帮拷锟铰的菜碉拷选锟斤拷
			case M_EXIT:			//锟斤拷锟斤拷锟斤拷影锟脚�	
				this.finish();
				break;
			case M_Clear:			//锟斤拷锟斤拷锟斤拷影锟脚�
			for(int i=0;i<textArray.length;i++)
				{
					 textArray[i].setText("");	//锟斤拷锟斤拷没锟斤拷锟斤拷锟斤拷锟斤拷息锟斤拷锟斤拷
				}
				break;
			}
			return super.onOptionsItemSelected(item);
		}
}
