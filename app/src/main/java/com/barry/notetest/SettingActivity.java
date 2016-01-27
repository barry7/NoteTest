package com.barry.notetest;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    private MyDataBase notesDB;
    private SQLiteDatabase dbWriter;
    private ListView settingList;
    private final String[] settings = new String[]{"登陆", "删除所有数据", "关于"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        notesDB = new MyDataBase(this);
        dbWriter = notesDB.getWritableDatabase();
        settingList = (ListView) findViewById(R.id.settingList);
        settingList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, settings));
        settingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    //Log in
                    case 0:
                        Toast.makeText(SettingActivity.this, "正在完善中", Toast.LENGTH_SHORT).show();
                        break;
                    //Show data
                    case 1:
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                        builder.setTitle("警告")
                                .setMessage("您确定要删除所有数据吗？这个过程将不可逆")
                                .setNegativeButton("取消", null)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteAll();
                                        finish();
                                        startActivity(new Intent(SettingActivity.this, MainActivity.class));
                                    }
                                });
                        builder.show();
                        break;
                    //Delete ALL data
                    case 2:
                        Snackbar.make(view, "更多功能正在完善中", Snackbar.LENGTH_LONG)
                                .setAction("知道了", null).show();
                        break;
                }
            }
        });
    }

    private void deleteAll() {
        dbWriter.delete(MyDataBase.TABLE_NAME, null, null);
    }
}
