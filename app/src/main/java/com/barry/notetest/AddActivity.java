package com.barry.notetest;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddActivity extends AppCompatActivity {

    private EditText editTextTitle, editText;
    private ImageView imageView;
    private VideoView videoView;
    private Button saveBtn, cancelBtn;
    private MyDataBase notesDB;
    private SQLiteDatabase dbWriter;
    private Intent imgIntent, videoIntent;
    private File imgFile, videoFile;
    private String titleString, timeString, contentString, imagePath, videoUri, getId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        imgIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        editTextTitle = (EditText) findViewById(R.id.cell_title);
        editText = (EditText) findViewById(R.id.ettext);
        imageView = (ImageView) findViewById(R.id.cell_img);
        videoView = (VideoView) findViewById(R.id.cell_video);
        saveBtn = (Button) findViewById(R.id.save);
        cancelBtn = (Button) findViewById(R.id.cancel);

        if (getIntent().getStringExtra(MyDataBase.TITLE) != null) {
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

            getId = getIntent().getStringExtra(MyDataBase.ID);
            titleString = getIntent().getStringExtra(MyDataBase.TITLE);
            editTextTitle.setText(titleString);
            contentString = getIntent().getStringExtra(MyDataBase.CONTENT);
            editText.setText(contentString);
            editText.requestFocus();
            editText.setSelection(contentString.length());
        }

        notesDB = new MyDataBase(this);
        dbWriter = notesDB.getWritableDatabase();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initAlertDialog();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editTextTitle.getText())) {
                    Toast.makeText(AddActivity.this, "请输入标题", Toast.LENGTH_SHORT).show();
                } else {
                    if (editTextTitle.getLineCount() > 2) {
                        Toast.makeText(AddActivity.this, "标题太长！", Toast.LENGTH_SHORT).show();
                    } else {
                        addDB();
                        Toast.makeText(AddActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        if (getIntent().getStringExtra(MyDataBase.TITLE) != null) {
                            dbWriter.delete(MyDataBase.TABLE_NAME, MyDataBase.ID + "=?", new String[]{String.valueOf(getId)});
                        }
                        finish();
                        startActivity(new Intent(AddActivity.this, MainActivity.class));
                    }
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void addDB() {
        ContentValues cv = new ContentValues();
        cv.put(MyDataBase.TITLE, editTextTitle.getText().toString());
        cv.put(MyDataBase.CONTENT, editText.getText().toString());
        cv.put(MyDataBase.TIME, getTime());
        cv.put(MyDataBase.PATH, imgFile + "");
        cv.put(MyDataBase.VIDEO, videoFile + "");
        dbWriter.insert(MyDataBase.TABLE_NAME, null, cv);
    }

    public String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date();
        String str = format.format(curDate);
        return str;
    }

    public String getFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date();
        String name = format.format(curDate);
        return name;
    }

    public void initAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
        builder.setTitle("请选择");
        builder.setMessage("请问您想要添加图片还是视频？");
        builder.setPositiveButton("图片", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (imageView.getVisibility() == View.VISIBLE){
                    Toast.makeText(AddActivity.this, "此版本中仅能添加一张图片", Toast.LENGTH_SHORT).show();
                }else {
                    imgFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + getFileName() + ".jpg");
                    imgIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgFile));
                    startActivityForResult(imgIntent, 1);
                }
            }
        });
        builder.setNegativeButton("视频", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (videoView.getVisibility() == View.VISIBLE){
                    Toast.makeText(AddActivity.this, "此版本中仅能添加一段视频", Toast.LENGTH_SHORT).show();
                }else {
                    videoFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + getFileName() + ".mp4");
                    videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
                    startActivityForResult(videoIntent, 2);
                }
            }
        });
        builder.setNeutralButton("取消", null);
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (imgFile.exists()) {
                imageView.setVisibility(View.VISIBLE);
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
            }

        }
        if (requestCode == 2) {
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(Uri.fromFile(videoFile));
            videoView.start();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
