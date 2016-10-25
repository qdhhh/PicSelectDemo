package com.android.qdhhh.picselectdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private Button main_single_bt_id;
    private Button main_multi_bt_id;
    private RecyclerView main_rv_id;
    private MainAdapter mainAdapter;

    private MainOnClickListener mainOnClickListener;

    private List<String> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {
            /**
             * 单选时的返回
             */
            if (requestCode == 0) {
                if (resultCode == 0) {
                    list = data.getStringArrayListExtra("PICS");
                    mainAdapter.notifyDataSetChanged();
                } else {
                }
            }
            /**
             * 多选时的返回
             * resultCode = 0表示在上个界面确认返回
             * resultCode = 1表示在上个界面取消， 不再回调图片路径
             *
             */
            else {
                if (resultCode == 0) {
                    list = data.getStringArrayListExtra("PICS");
                    mainAdapter.notifyDataSetChanged();
                } else {
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        list = new ArrayList<>();
        main_rv_id = (RecyclerView) findViewById(R.id.main_rv_id);

        mainOnClickListener = new MainOnClickListener();

        main_single_bt_id = (Button) findViewById(R.id.main_single_bt_id);
        main_multi_bt_id = (Button) findViewById(R.id.main_multi_bt_id);

        main_single_bt_id.setOnClickListener(mainOnClickListener);
        main_multi_bt_id.setOnClickListener(mainOnClickListener);

        setRecyclerView();

    }

    /**
     * RecyclerView的相关操作
     */
    private void setRecyclerView() {

        mainAdapter = new MainAdapter();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);

        main_rv_id.setLayoutManager(gridLayoutManager);

        main_rv_id.setAdapter(mainAdapter);
    }


    /**
     * 控件的点击监听事件
     */
    private final class MainOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            Intent intent = null;

            switch (v.getId()) {

                /**
                 * 选择单图的跳转
                 */
                case R.id.main_single_bt_id: {

                    intent = new Intent(MainActivity.this, FileSelectActivity.class);

                    intent.putExtra("MODE", 0);

                    startActivityForResult(intent, 0);

                    break;
                }
                /**
                 * 选择多图的跳转
                 */
                case R.id.main_multi_bt_id: {

                    intent = new Intent(MainActivity.this, FileSelectActivity.class);

                    intent.putExtra("MODE", 1);

                    startActivityForResult(intent, 1);

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
    private final class MainAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            MainViewHolder mainViewHolder = new MainViewHolder(View.inflate(MainActivity.this
                    , R.layout.single_select_item, null));

            return mainViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            MainViewHolder mainViewHolder = (MainViewHolder) holder;

            Glide.with(MainActivity.this).load(list.get(position))
                    .into(mainViewHolder.single_iv_id);

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }


    /**
     * RecyclerView适配器的ViewHolder
     */
    private final class MainViewHolder extends RecyclerView.ViewHolder {

        public ImageView single_iv_id;

        public MainViewHolder(View itemView) {
            super(itemView);

            single_iv_id = (ImageView) itemView.findViewById(R.id.single_iv_id);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
    }

}
