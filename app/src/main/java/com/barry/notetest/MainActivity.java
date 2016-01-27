package com.barry.notetest;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private MyAdapter adapter;
    private MyDataBase notesDB;
    private SQLiteDatabase dbReader;
    private Intent intent;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        FloatingActionButton toAddActivity = (FloatingActionButton) findViewById(R.id.fab);
        toAddActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, AddActivity.class);
                intent.putExtra("type", "isText");
                startActivity(intent);
            }
        });
    }

    public void initView() {
        listView = (ListView) findViewById(R.id.noteList);
        notesDB = new MyDataBase(this);
        dbReader = notesDB.getReadableDatabase();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                Intent toDetail = new Intent(MainActivity.this, DetailActivity.class);
                toDetail.putExtra(MyDataBase.ID, cursor.getInt(cursor.getColumnIndex(MyDataBase.ID)));
                toDetail.putExtra(MyDataBase.TITLE, cursor.getString(cursor.getColumnIndex(MyDataBase.TITLE)));
                toDetail.putExtra(MyDataBase.TIME, cursor.getString(cursor.getColumnIndex(MyDataBase.TIME)));
                toDetail.putExtra(MyDataBase.CONTENT, cursor.getString(cursor.getColumnIndex(MyDataBase.CONTENT)));
                toDetail.putExtra(MyDataBase.PATH, cursor.getString(cursor.getColumnIndex(MyDataBase.PATH)));
                toDetail.putExtra(MyDataBase.VIDEO, cursor.getString(cursor.getColumnIndex(MyDataBase.VIDEO)));
                startActivity(toDetail);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void selectDB() {
        cursor = dbReader.query(MyDataBase.TABLE_NAME, null, null, null, null, null, null);
        adapter = new MyAdapter(this, cursor);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        selectDB();
        super.onResume();
    }
}
