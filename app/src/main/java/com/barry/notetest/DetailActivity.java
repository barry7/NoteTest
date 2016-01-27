package com.barry.notetest;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

public class DetailActivity extends AppCompatActivity {

    private int dbId;
    private TextView title, time, content;
    private ImageView imageView;
    private VideoView videoView;
    private MyDataBase notesDB;
    private SQLiteDatabase dbWriter;
    private String titleString, timeString, contentString, imagePath, videoUri;
    private Intent toEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        toEdit = new Intent(DetailActivity.this, AddActivity.class);
        title = (TextView) findViewById(R.id.detail_title);
        time = (TextView) findViewById(R.id.detail_time);
        content = (TextView) findViewById(R.id.detail_content);
        content.setMovementMethod(new ScrollingMovementMethod());
        imageView = (ImageView) findViewById(R.id.detail_image);
        videoView = (VideoView) findViewById(R.id.detail_video);
        notesDB = new MyDataBase(this);
        dbWriter = notesDB.getWritableDatabase();

        getContent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initAlertDialog();
            }
        });
    }

    public void getContent() {

        if (getIntent().getStringExtra(MyDataBase.PATH).equals("null")) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            imagePath = getIntent().getStringExtra(MyDataBase.PATH);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
        }
        if (getIntent().getStringExtra(MyDataBase.VIDEO).equals("null")) {
            videoView.setVisibility(View.GONE);
        } else {
            videoView.setVisibility(View.VISIBLE);
            videoUri = getIntent().getStringExtra(MyDataBase.VIDEO);
            videoView.setVideoURI(Uri.parse(videoUri));
            videoView.start();
        }

        dbId = getIntent().getIntExtra(MyDataBase.ID, -1);
        titleString = getIntent().getStringExtra(MyDataBase.TITLE);
        title.setText(titleString);
        timeString = getIntent().getStringExtra(MyDataBase.TIME);
        time.setText(timeString);
        contentString = getIntent().getStringExtra(MyDataBase.CONTENT);
        content.setText(contentString);

    }

    public void initAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        builder.setTitle("请选择");
        builder.setMessage("您是否要删除此条内容？");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteData();
                finish();
            }

        });
        builder.setNegativeButton("否", null);
        builder.show();
    }

    public void deleteData() {
        dbWriter.delete(MyDataBase.TABLE_NAME, "_id=" + getIntent().getIntExtra(MyDataBase.ID, 0), null);
    }

    public void toEditActivity(int id) {
        id = dbId;
        Cursor cursor = dbWriter.query(MyDataBase.TABLE_NAME, null, MyDataBase.ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            toEdit.putExtra(MyDataBase.ID, cursor.getString(cursor.getColumnIndex(MyDataBase.ID)));
            toEdit.putExtra(MyDataBase.TITLE, cursor.getString(cursor.getColumnIndex(MyDataBase.TITLE)));
            toEdit.putExtra(MyDataBase.TIME, cursor.getString(cursor.getColumnIndex(MyDataBase.TIME)));
            toEdit.putExtra(MyDataBase.CONTENT, cursor.getString(cursor.getColumnIndex(MyDataBase.CONTENT)));
            toEdit.putExtra(MyDataBase.PATH, cursor.getString(cursor.getColumnIndex(MyDataBase.PATH)));
            toEdit.putExtra(MyDataBase.VIDEO, cursor.getString(cursor.getColumnIndex(MyDataBase.VIDEO)));
            startActivity(toEdit);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.detail_settings) {
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }
        if (id == R.id.detail_edit) {
            toEditActivity(dbId);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
