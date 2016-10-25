package com.android.qdhhh.picselectdemo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.android.qdhhh.picselectdemo.bean.FileBean;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SingleSelectActivity extends AppCompatActivity {

    private int selectMode;

    private RecyclerView single_rv_id;
    private Button select_butten_id;

    private List<FileBean> list;

    private SingleAdapter singleAdapter;

    private String path;

    private ArrayList<String> picList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_select);
        selectMode = getIntent().getIntExtra("MODE", 2);
        path = getIntent().getStringExtra("PATH");
        picList = getIntent().getStringArrayListExtra("PICS");

        Log.i("haha", path);

        path = new File(path).getParentFile().getAbsolutePath().toString();

        initView();

        getData();

    }


    /**
     * RecyclerView的相关操作
     */
    private void setRecyclerView() {

        singleAdapter = new SingleAdapter();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);

        single_rv_id.setLayoutManager(gridLayoutManager);

        single_rv_id.setAdapter(singleAdapter);
    }

    /**
     * 从手机存贮获取当前文件夹里的所有图片的路径
     */
    private void getData() {
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                {
                    ArrayList<FileBean> list = new ArrayList<>();
                    String[] columns = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};

                    String whereclause = MediaStore.Images.ImageColumns.DATA + " like'" + path + "/%'";

                    Cursor corsor = null;

                    try {
                        corsor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, whereclause, null,
                                null);
                        if (corsor != null && corsor.getCount() > 0 && corsor.moveToFirst()) {
                            do {
                                String path = corsor.getString(corsor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                                int id = corsor.getInt(corsor.getColumnIndex(MediaStore.Images.ImageColumns._ID));

                                FileBean fileBean = new FileBean();
                                fileBean.path = path;
                                fileBean._id = id;

                                list.add(0, fileBean);
                            } while (corsor.moveToNext());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        subscriber.onNext(list);
                        subscriber.onCompleted();
                        if (corsor != null)
                            corsor.close();
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
     * 初始化控件
     */
    private void initView() {
        single_rv_id = (RecyclerView) findViewById(R.id.single_rv_id);
        select_butten_id = (Button) findViewById(R.id.select_butten_id);
        select_butten_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra("PICS", picList);
                setResult(1, intent);
                finish();
            }
        });
    }


    /**
     * RecyclerView 的适配器
     */
    private final class SingleAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            SingleViewHolder singleViewHolder = new SingleViewHolder(View.inflate(SingleSelectActivity.this
                    , R.layout.single_select_item, null));

            return singleViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            SingleViewHolder singleViewHolder = (SingleViewHolder) holder;

            Glide.with(SingleSelectActivity.this).load(list.get(position).path)
                    .into(singleViewHolder.single_iv_id);

            if (picList.contains(list.get(position).path)) {
                singleViewHolder.item_sign_iv_id.setVisibility(View.VISIBLE);
                Log.i("hahaha","---");
            }else {
                Log.i("hahaha","+++");
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    /**
     * RecyclerView 适配器的ViewHolder
     */
    private final class SingleViewHolder extends RecyclerView.ViewHolder {

        public ImageView single_iv_id;
        public ImageView item_sign_iv_id;

        public SingleViewHolder(View itemView) {
            super(itemView);

            single_iv_id = (ImageView) itemView.findViewById(R.id.single_iv_id);
            item_sign_iv_id = (ImageView) itemView.findViewById(R.id.item_sign_iv_id);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectMode == 0) {
                        picList.add(list.get(getAdapterPosition()).path);
                        Intent intent = new Intent();
                        intent.putStringArrayListExtra("PICS", picList);
                        setResult(0, intent);
                        finish();
                    } else if (selectMode == 1) {

                        if (item_sign_iv_id.getVisibility() == View.GONE) {
                            item_sign_iv_id.setVisibility(View.VISIBLE);
                            if (!picList.contains(list.get(getAdapterPosition()).path)) {
                                picList.add(list.get(getAdapterPosition()).path);
                            }
                        } else {
                            item_sign_iv_id.setVisibility(View.GONE);
                            if (picList.contains(list.get(getAdapterPosition()).path)) {
                                picList.remove(list.get(getAdapterPosition()).path);
                            }
                        }

                    }
                }
            });

        }
    }


}
