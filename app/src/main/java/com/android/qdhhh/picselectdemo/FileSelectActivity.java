package com.android.qdhhh.picselectdemo;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.qdhhh.picselectdemo.bean.FileBean;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FileSelectActivity extends AppCompatActivity {

    private RecyclerView file_rv_id;
    private Button select_bt_sure_id;
    private Button select_bt_cancel_id;

    private int selectMode;

    private SelectOnCLickListener selectOnCLickListener;

    private FileAdapter fileAdapter;

    private List<FileBean> list;

    private ArrayList<String> strList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select);

        selectMode = getIntent().getIntExtra("MODE", 2);

        if (selectMode == 2) {
            Toast.makeText(this, "发生错误", Toast.LENGTH_SHORT).show();
            finish();
        }
        initView();

        getFileData();
    }


    /**
     * 使用Rxjava从手机存储中读取包含了图片信息的文件夹
     * 最终获得的是FileBean的集合
     */
    private void getFileData() {

        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                {
                    ArrayList<FileBean> imageFolders = new ArrayList<>();
                    ContentResolver contentResolver = getContentResolver();
                /*查询id、  缩略图、原图、文件夹ID、 文件夹名、 文件夹分类的图片总数*/
                    String[] columns = {MediaStore.Images.Media._ID, MediaStore.Images.Thumbnails.DATA, MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_ID,
                            MediaStore.Images.Media.BUCKET_DISPLAY_NAME, "COUNT(1) AS count"};
                    String selection = "0==0) GROUP BY (" + MediaStore.Images.Media.BUCKET_ID;
                    String sortOrder = MediaStore.Images.Media.DATE_MODIFIED;
                    Cursor cursor = null;
                    try {
                        cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, selection, null, sortOrder);
                        if (cursor != null && cursor.moveToFirst()) {

                            int columnPath = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                            int columnId = cursor.getColumnIndex(MediaStore.Images.Media._ID);

                            int columnFileName = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                            int columnCount = cursor.getColumnIndex("count");

                            do {
                                FileBean fileBean = new FileBean();
                                fileBean.path = cursor.getString(columnPath);
                                fileBean._id = cursor.getInt(columnId);
                                fileBean.pisNum = cursor.getInt(columnCount);

                                String bucketName = cursor.getString(columnFileName);
                                fileBean.fileName = bucketName;

                                if (!Environment.getExternalStorageDirectory().getPath().contains(bucketName)) {
                                    imageFolders.add(0, fileBean);
                                }
                            } while (cursor.moveToNext());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        subscriber.onNext(imageFolders);
                        subscriber.onCompleted();
                        if (cursor != null)
                            cursor.close();
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {

                    @Override
                    public void onNext(Object o) {
                        Log.i("haha", o.toString());
                        list = (ArrayList<FileBean>) o;
                        Log.i("haha", list.size() + "");
                        setRecyclerView();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onCompleted() {

                    }
                });
    }


    /**
     * 设置RecyclerView
     */
    private void setRecyclerView() {

        file_rv_id.setItemAnimator(new DefaultItemAnimator());

        file_rv_id.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        file_rv_id.setLayoutManager(layoutManager);

        fileAdapter = new FileAdapter();

        file_rv_id.setAdapter(fileAdapter);
    }


    /**
     * 初始化控件
     */
    private void initView() {

        selectOnCLickListener = new SelectOnCLickListener();

        strList = new ArrayList<String>();

        file_rv_id = (RecyclerView) findViewById(R.id.file_rv_id);
        select_bt_sure_id = (Button) findViewById(R.id.select_bt_sure_id);
        select_bt_cancel_id = (Button) findViewById(R.id.select_bt_cancel_id);

        select_bt_sure_id.setOnClickListener(selectOnCLickListener);
        select_bt_cancel_id.setOnClickListener(selectOnCLickListener);
    }


    /**
     * 控件的点击事件监听器
     */
    private final class SelectOnCLickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.select_bt_sure_id: {

                    if (strList.size() == 0) {
                        Toast.makeText(FileSelectActivity.this, "还未选择任何照片", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent();
                        intent.putStringArrayListExtra("PICS", strList);
                        setResult(0, intent);
                        finish();
                    }
                    break;
                }
                case R.id.select_bt_cancel_id: {
                    finish();
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }


    /**
     * RecyclerView的适配器
     */
    private final class FileAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            FileViewHolder vh = new FileViewHolder(View.inflate(FileSelectActivity.this,
                    R.layout.file_item, null));

            Log.i("haha", "onCreateViewHolder");

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Log.i("haha", "onBindViewHolder");
            final FileViewHolder viewHolder = (FileViewHolder) holder;
            FileBean fileBean = list.get(position);
            viewHolder.fileNameTv.setText(fileBean.fileName);
            viewHolder.fileNumsTv.setText(String.format("(%1$d)", fileBean.pisNum));
            Glide.with(FileSelectActivity.this).load(fileBean.path).into(viewHolder.imageIv);


        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }


    /**
     * RecyclerView适配器的ViewHolder
     */
    private final class FileViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageIv;
        public TextView fileNameTv;
        public TextView fileNumsTv;
        public CardView mCardView;

        public FileViewHolder(View itemView) {
            super(itemView);
            fileNameTv = (TextView) itemView.findViewById(R.id.tv_file_name);
            fileNumsTv = (TextView) itemView.findViewById(R.id.tv_pic_nums);
            imageIv = (ImageView) itemView.findViewById(R.id.iv_icon);
            mCardView = (CardView) itemView.findViewById(R.id.card_view);
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(FileSelectActivity.this, getAdapterPosition() + "-----"
                            , Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(FileSelectActivity.this, SingleSelectActivity.class);

                    intent.putExtra("PATH", list.get(getAdapterPosition()).path);
                    intent.putExtra("MODE", selectMode);
                    intent.putStringArrayListExtra("PICS",strList);

                    startActivityForResult(intent, selectMode);
                }
            });
        }
    }


    /**
     *
     * 处理从选择图片的界面传回的数据
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == 0) {
            if (data != null) {
                strList = data.getStringArrayListExtra("PICS");
                Intent intent = new Intent();
                intent.putStringArrayListExtra("PICS", strList);
                setResult(0, intent);
                finish();
            } else {
                return;
            }

        } else if (resultCode == 1) {
            if (data != null) {
                strList = data.getStringArrayListExtra("PICS");
            } else {
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
