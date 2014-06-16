package com.tyler.myfirstandroid1;


import static com.tyler.myfirstandroid1.OpenHelper.*;


import com.tyler.myfirstandroid1.OpenHelper.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	OpenHelper ohelper;		//锟斤拷锟斤拷MyOpenHelper锟斤拷锟斤拷
	String [] bName;	
	String [] tfsId;
	int [] cId;			//锟斤拷锟斤拷锟斤拷锟节达拷锟絙ug id锟斤拷锟斤拷锟斤拷
	final int M_ADD = Menu.FIRST;			//锟斤拷锟斤拷说锟窖★拷械锟絀D
	final int M_DELETE = Menu.FIRST+1;		//锟斤拷锟斤拷说锟斤拷锟侥憋拷锟�	
	final int M_ABOUT = Menu.FIRST+2;	
	final int DIALOG_DELETE = 0;		//确锟斤拷删锟斤拷曰锟斤拷锟斤拷ID 
	final int DIALOG_ABOUT=1;
	ListView lview;	//锟斤拷锟斤拷ListView锟斤拷锟斤拷
	int selectedid=0;
	int selectid=-1;
	
	
	BaseAdapter adapter = new BaseAdapter(){
		//@Override
		public int getCount()
		{
			if(bName != null)
			{		//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷椴晃拷锟�	
				return bName.length;
			}
			else
			{
				return 0;					//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟轿拷锟斤拷蚍祷锟�
			}
		}
		//@Override
		public Object getItem(int arg0) {
			return null;
		}
		//@Override
		public long getItemId(int arg0) {
			return 0;
		}
		//@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			LinearLayout ll = new LinearLayout(MainActivity.this);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			ll.setPadding(5, 5, 5, 5);
			ll.setTag(cId[position]);
			if(position%2==0){
				ll.setBackgroundColor(getResources().getColor( R.color.line1));
			}else{
				ll.setBackgroundColor(getResources().getColor(R.color.line2));
			}
			if(selectedid==cId[position])
			{
				ll.setBackgroundColor(getResources().getColor(R.color.lineSelected));
			}
		    
			
			TextView tvTfsId = new TextView(MainActivity.this);
			tvTfsId.setText(tfsId[position]);
			tvTfsId.setTextSize(20);
			tvTfsId.setTextColor(Color.BLACK);
			tvTfsId.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			tvTfsId.setGravity(Gravity.LEFT);
			tvTfsId.setWidth(110);
			
			TextView tv = new TextView(MainActivity.this);
			tv.setText(bName[position]);
			tv.setTextSize(20);
			tv.setTextColor(Color.BLACK);
			tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			tv.setGravity(Gravity.LEFT);
			//tv.setWidth(200);
			Button btnBugDetail=new Button(MainActivity.this);
			btnBugDetail.setText("Events");
			btnBugDetail.setTag(cId[position]);
			//btnBugDetail.setWidth(80);
			btnBugDetail.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					selectedid= Integer.parseInt( v.getTag().toString());	
					Intent intent= new Intent(MainActivity.this,EventsListActivity.class);
					intent.putExtra("cmd", 0);
					intent.putExtra("id",  Integer.parseInt( v.getTag().toString()));
					startActivity(intent);
				}
			});
			Button btnEditBug=new Button(MainActivity.this);
			btnEditBug.setText("Edit");
			btnEditBug.setTag(cId[position]);
			//btnEditBug.setWidth(60);
			btnEditBug.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					selectedid= Integer.parseInt( v.getTag().toString());
					Intent intent= new Intent(MainActivity.this,BugDetailActivity.class);
					intent.putExtra("cmd", 0);
					intent.putExtra("id",  Integer.parseInt( v.getTag().toString()));
					startActivity(intent);
				}
			});
			//btnEditBug.setGravity(Gravity.LEFT);
			ll.addView(btnBugDetail);
			ll.addView(tvTfsId);
			ll.addView(btnEditBug);
			ll.addView(tv);
			ll.setFocusable(true);
			ll.setClickable(true);
			ll.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
			
	            	v.setBackgroundColor(getResources().getColor(R.color.lineSelected));
	            	String id=v.getTag().toString();
	            	selectedid= Integer.parseInt( id);
					Toast.makeText(v.getContext(), "select item id: "+id, Toast.LENGTH_SHORT).show();
	            	Intent intent= new Intent(MainActivity.this,BugDetailActivity.class);
					intent.putExtra("cmd", 0);
					intent.putExtra("id",  Integer.parseInt( id));
					startActivity(intent);
	            }
	        });
			ll.setOnLongClickListener(new View.OnLongClickListener() {
	           
				public boolean onLongClick(View v) {
					v.setBackgroundColor(getResources().getColor(R.color.lineSelected));
	            	//v.setSelected(true);
	            	String id=v.getTag().toString();
					selectedid= Integer.parseInt( id);
	            	Toast.makeText(v.getContext(), "select item id: "+id, Toast.LENGTH_SHORT).show();
	            	Intent intent= new Intent(MainActivity.this,EventsListActivity.class);
					intent.putExtra("cmd", 0);
					intent.putExtra("id",  Integer.parseInt( id));
					startActivity(intent);
					return true;
				}
	        });
			
			return ll;
		}
	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ohelper = new OpenHelper(this, DB_NAME, null, 1);
        lview = (ListView)findViewById(R.id.lv);
        lview.setAdapter(adapter);
        lview.setOnItemClickListener
        (
        	new OnItemClickListener()
        	{
				//@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
				{
					/*
					Intent intent= new Intent(MainActivity.this,EActivity.class);
					intent.putExtra("cmd", 0);		//0锟斤拷锟斤拷询锟较碉拷耍锟�锟斤拷锟斤拷锟斤拷锟较碉拷锟�		
								intent.putExtra("id", cId[position]);
					startActivity(intent);
					*/
					Toast.makeText(view.getContext(), "select item id: "+cId[position], Toast.LENGTH_SHORT).show();
				}
        	}
        );
    }

    
    @Override
	protected void onResume() {
		getBasicInfo(ohelper);
		adapter.notifyDataSetChanged();
		super.onResume();
	}
    public void getBasicInfo(OpenHelper helper)
    {
    	SQLiteDatabase db = helper.getWritableDatabase(); 
    	Cursor c = db.query(TABLE_BUG, new String[]{BUG_ID,BUG_TITLE,TFS_ID}, null, null, null, null, BUG_ID);
    	int idIndex = c.getColumnIndex(BUG_ID);
    	int nameIndex = c.getColumnIndex(BUG_TITLE);		//锟斤拷锟紹UG_TITLE锟叫碉拷锟叫猴拷
    	int tfsIndex = c.getColumnIndex(TFS_ID);
    	bName = new String[c.getCount()];			//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷String锟斤拷锟斤拷锟斤拷锟�  
    	cId = new int[c.getCount()];	
    	tfsId=new String[c.getCount()];
    	int i=0;			//锟斤拷锟斤拷一锟斤拷锟斤拷锟斤拷锟�  
    	for(c.moveToFirst();!(c.isAfterLast());c.moveToNext())
    	{
    		bName[i] = c.getString(nameIndex);
    		cId[i] = c.getInt(idIndex);
    		tfsId[i]=c.getString(tfsIndex);
    		i++;
    	}
    	c.close();				//锟截憋拷Cursor锟斤拷锟斤拷
    	db.close();				//锟截憋拷SQLiteDatabase锟斤拷锟斤拷
    }
    @Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0, M_ADD, 0, R.string.menu_add);//锟斤拷印锟斤拷锟接★拷锟剿碉拷选锟斤拷
		menu.add(0, M_DELETE, 0, R.string.menu_delete);//锟斤拷印锟缴撅拷锟剿碉拷选锟斤拷
		menu.add(0, M_ABOUT, 0, R.string.menu_about);//锟斤拷印锟缴撅拷锟剿碉拷选锟斤拷
		return super.onCreateOptionsMenu(menu);
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){		//锟叫断帮拷锟铰的菜碉拷选锟斤拷
		case M_ADD:			//锟斤拷锟斤拷锟斤拷影锟脚�			
			Intent intent= new Intent(MainActivity.this,BugDetailActivity.class);
			intent.putExtra("cmd", 1);
			startActivity(intent);
			break;
		case M_DELETE:				//锟斤拷锟斤拷锟斤拷删锟斤拷选锟斤拷
			showDialog(DIALOG_DELETE);	//锟斤拷示确锟斤拷删锟斤拷曰锟斤拷锟�	
			break;
		case M_ABOUT:				//锟斤拷锟斤拷锟斤拷删锟斤拷选锟斤拷
			showDialog(DIALOG_ABOUT);	//锟斤拷示确锟斤拷删锟斤拷曰锟斤拷锟�			
			break;
		}
		return super.onOptionsItemSelected(item);
	}
    @Override
	protected Dialog onCreateDialog(int id)
	{
		Dialog dialog = null;
		switch(id){			//锟皆对伙拷锟斤拷ID锟斤拷锟斤拷锟叫讹拷
		case DIALOG_DELETE:		//锟斤拷锟斤拷删锟斤拷确锟较对伙拷锟斤拷
			Builder b = new AlertDialog.Builder(this);	
			b.setTitle("del_comfirm");							//锟斤拷锟矫对伙拷锟斤拷锟斤拷锟�
		b.setMessage("need_confirm");		//锟斤拷锟矫对伙拷锟斤拷锟斤拷锟斤拷
			b.setPositiveButton
			(
				R.string.ok,
				new OnClickListener()
				{				//锟斤拷锟斤拷确锟斤拷删锟斤拷钮
					//@Override
					public void onClick(DialogInterface dialog, int which)
					{
						//int position = MainActivity.this.lview.getSelectedItemPosition();
						if(selectedid!=0)
						{
							deleteContact(selectedid);
							getBasicInfo(ohelper);
							adapter.notifyDataSetChanged();
						}
						else
						{
							Toast.makeText(MainActivity.this, "please_sel_first", Toast.LENGTH_LONG).show();
						}
					}
				}
			);
			b.setNegativeButton
			(
				R.string.cancel,
				new OnClickListener()
				{
					//@Override
					public void onClick(DialogInterface dialog, int which){ }
				}
			);
			dialog = b.create();
			break;
		case DIALOG_ABOUT:	
			Builder b1 = new AlertDialog.Builder(this);	
			b1.setTitle("About");	
			b1.setMessage("Build Version: 0.01 by Tyler");	
			b1.setNegativeButton
			(
				R.string.cancel,
				new OnClickListener()
				{
					//@Override
					public void onClick(DialogInterface dialog, int which){ }
				}
			);
			dialog = b1.create();
			break;
		}
		return dialog;
	}
	
	public void deleteContact(int id)
	{
		SQLiteDatabase db = ohelper.getWritableDatabase();		//锟斤拷锟斤拷锟捷匡拷锟斤拷锟�	
		db.delete(TABLE_BUG, BUG_ID+"=?", new String[]{id+""});
		db.delete(TABLE_EVENT, REF_BUG_ID+"=?", new String[]{id+""});
		db.close();
	}
}
