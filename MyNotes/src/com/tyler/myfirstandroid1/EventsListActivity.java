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

public class EventsListActivity extends Activity {
	OpenHelper ohelper;		//锟斤拷锟斤拷MyOpenHelper锟斤拷锟斤拷
	String [] eventDes;	
	//String [] tfsId;
	int [] eId;			//锟斤拷锟斤拷锟斤拷锟节达拷锟絜vent id锟斤拷锟斤拷锟斤拷
	final int M_ADD = Menu.FIRST;			//锟斤拷锟斤拷说锟窖★拷械锟絀D
	final int M_DELETE = Menu.FIRST+1;		//锟斤拷锟斤拷说锟斤拷锟侥憋拷锟�
	final int DIALOG_DELETE = 0;		//确锟斤拷删锟斤拷曰锟斤拷锟斤拷ID 
	ListView lview;						//锟斤拷锟斤拷ListView锟斤拷锟斤拷
	String bugId;
	TextView tfsIdTv;
	TextView bugTitleTv;
	BaseAdapter adapter = new BaseAdapter(){
		//@Override
		public int getCount()
		{
			if(eventDes != null)
			{		//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷椴晃拷锟�	
				return eventDes.length;
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
			LinearLayout ll = new LinearLayout(EventsListActivity.this);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			ll.setPadding(5, 5, 5, 5);
			/*TextView tvTfsId = new TextView(EventsListActivity.this);
			tvTfsId.setText(tfsId[position]);
			tvTfsId.setTextSize(20);
			tvTfsId.setTextColor(Color.BLACK);
			tvTfsId.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			tvTfsId.setGravity(Gravity.LEFT);
			tvTfsId.setWidth(90);*/
			TextView tv = new TextView(EventsListActivity.this);
			tv.setText(eventDes[position]);
			tv.setTextSize(20);
			tv.setTextColor(Color.BLACK);
			tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			tv.setGravity(Gravity.LEFT);
			
			ll.addView(tv);
			return ll;
		}
	};
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);
        
        Intent intent = getIntent();
		bugId = intent.getExtras().getInt("id")+"";
		
		
        ohelper = new OpenHelper(this, DB_NAME, null, 1);
        getBugInfo( ohelper);
        
        lview = (ListView)findViewById(R.id.lvEvent);
        lview.setAdapter(adapter);
        lview.setOnItemClickListener
        (
        	new OnItemClickListener()
        	{
				//@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
				{
					
					Intent intent= new Intent(EventsListActivity.this,EventDetailActivity.class);
					intent.putExtra("cmd", 0);		//0锟斤拷锟斤拷询Event锟斤拷1锟斤拷锟斤拷锟斤拷Event
					intent.putExtra("id", eId[position]);
					startActivity(intent);
					/*
					Toast.makeText(view.getContext(), "select item id: "+eId[position], Toast.LENGTH_SHORT).show();
				*/
				}
        	}
        );
    }
	@Override
	protected void onResume() {
		getEventInfo(ohelper);
		adapter.notifyDataSetChanged();
		super.onResume();
	}
	private void getBugInfo(OpenHelper helper){
		SQLiteDatabase db = helper.getWritableDatabase(); 
    	Cursor c = db.query(TABLE_BUG, new String[]{BUG_TITLE,TFS_ID},BUG_ID+" = '" + bugId
    		     + "'", null, null, null, BUG_ID);
    	int idIndex = c.getColumnIndex(BUG_ID);
    	int nameIndex = c.getColumnIndex(BUG_TITLE);
    	int tfsIndex=c.getColumnIndex(TFS_ID);
    	c.moveToFirst();
    	if(!(c.isAfterLast()))
    	{
    		
    		tfsIdTv = (TextView)findViewById(R.id.tfsIdTv);
    		tfsIdTv.setText(c.getString(tfsIndex));
    		bugTitleTv = (TextView)findViewById(R.id.bugTitle);
    		bugTitleTv.setText(c.getString(nameIndex));
    	}
	}
    public void getEventInfo(OpenHelper helper)
    {
    	SQLiteDatabase db = helper.getWritableDatabase(); 
    	Cursor c = db.query(TABLE_EVENT, new String[]{EVENT_ID,EVENT_DES},REF_BUG_ID+" = '" + bugId
    		     + "'", null, null, null, EVENT_ID+" desc");
    	int idIndex = c.getColumnIndex(EVENT_ID);
    	int nameIndex = c.getColumnIndex(EVENT_DES);		//锟斤拷锟�Event description 锟叫碉拷锟叫猴拷
    	eventDes = new String[c.getCount()];			//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷String锟斤拷锟斤拷锟斤拷锟�  
    	eId = new int[c.getCount()];	
    	int i=0;			//锟斤拷锟斤拷一锟斤拷锟斤拷锟斤拷锟�
    	for(c.moveToFirst();!(c.isAfterLast());c.moveToNext())
    	{
    		eventDes[i] = c.getString(nameIndex);
    		eId[i] = c.getInt(idIndex);
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
		return super.onCreateOptionsMenu(menu);
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){		//锟叫断帮拷锟铰的菜碉拷选锟斤拷
		case M_ADD:			//锟斤拷锟斤拷锟斤拷影锟脚�
			Intent intent= new Intent(EventsListActivity.this,EventDetailActivity.class);
			intent.putExtra("cmd", 1);
			intent.putExtra("bugId", bugId);
			startActivity(intent);
			/*Toast.makeText(EventsListActivity.this.getBaseContext(), "click add", Toast.LENGTH_SHORT).show();
			*/
			break;
		case M_DELETE:				//锟斤拷锟斤拷锟斤拷删锟斤拷选锟斤拷
			showDialog(DIALOG_DELETE);	//锟斤拷示确锟斤拷删锟斤拷曰锟斤拷锟�	
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
			b.setTitle("锟斤拷示");							//锟斤拷锟矫对伙拷锟斤拷锟斤拷锟�
		b.setMessage("delete");		//锟斤拷锟矫对伙拷锟斤拷锟斤拷锟斤拷
			b.setPositiveButton
			(
				R.string.ok,
				new OnClickListener()
				{				//锟斤拷锟斤拷确锟斤拷删锟斤拷钮
					//@Override
					public void onClick(DialogInterface dialog, int which)
					{
						int position = EventsListActivity.this.lview.getSelectedItemPosition();
						if(position!=-1)
						{
							deleteContact(eId[position]);
							getEventInfo(ohelper);
							adapter.notifyDataSetChanged();
						}
						else
						{
							Toast.makeText(EventsListActivity.this, "锟斤拷选锟斤拷一锟斤拷", Toast.LENGTH_LONG).show();
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
		}
		return dialog;
	}
	
	public void deleteContact(int id)
	{
		SQLiteDatabase db = ohelper.getWritableDatabase();		//锟斤拷锟斤拷锟捷匡拷锟斤拷锟�
		db.delete(TABLE_EVENT, EVENT_ID+"=?", new String[]{id+""});
		db.close();
	}

    
}
