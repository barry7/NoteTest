package com.barry.notetest;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Barry on 2016/1/15 0015.
 */
public class MyAdapter extends BaseAdapter {

    private Context context;
    private Cursor cursor;
    private RelativeLayout layout;

    public MyAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return cursor.getPosition();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        layout = (RelativeLayout) inflater.inflate(R.layout.cell, null);
        TextView titleTv = (TextView) layout.findViewById(R.id.list_title);
        TextView contentTv = (TextView) layout.findViewById(R.id.list_content);
        TextView timeTv = (TextView) layout.findViewById(R.id.list_time);
        ImageView imgIv = (ImageView) layout.findViewById(R.id.list_img);
        ImageView videoIv = (ImageView) layout.findViewById(R.id.list_video);
        cursor.moveToPosition(position);
        String title = cursor.getString(cursor.getColumnIndex("title"));
        String content = cursor.getString(cursor.getColumnIndex("content"));
        String time = cursor.getString(cursor.getColumnIndex("time"));
        String imageUrl = cursor.getString(cursor.getColumnIndex("path"));
        String videoUrl = cursor.getString(cursor.getColumnIndex("video"));
        titleTv.setText(title);
        contentTv.setText(content);
        timeTv.setText(time);
        imgIv.setImageBitmap(getImageThumbnail(imageUrl, 200, 200));
        videoIv.setImageBitmap(getVideoThumbnail(videoUrl, 200, 200, MediaStore.Images.Thumbnails.MICRO_KIND));

        return layout;
    }

    public Bitmap getImageThumbnail(String uri, int width, int height) {

        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(uri, options);
        options.inJustDecodeBounds = false;

        int beWidth = options.outWidth / width;
        int beHeight = options.outHeight / height;
        int be = 1;
        if (beWidth < be) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }

        options.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(uri);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        return bitmap;
    }

    public Bitmap getVideoThumbnail(String uri, int width, int height, int kind) {
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(uri, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
}
