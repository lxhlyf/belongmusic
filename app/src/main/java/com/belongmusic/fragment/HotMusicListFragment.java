package com.belongmusic.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.belongmusic.R;
import com.belongmusic.adapter.MusicAdapter;
import com.belongmusic.app.MusicApplication;
import com.belongmusic.entity.Music;
import com.belongmusic.entity.SongInfo;
import com.belongmusic.entity.SongUrl;
import com.belongmusic.model.MusicModel;
import com.belongmusic.service.PlayMusicService;
import com.belongmusic.util.ImageLoader;

import java.util.List;

/**
 * Created by 枫暮晓 on 2018/7/30.
 *
 * 描述热歌榜的界面 Fragment
 */

public class HotMusicListFragment extends Fragment {
    private PlayMusicService.MusicBinder binder;
    private ListView listView;
    private MusicAdapter adapter;
    private List<Music> musics;
    private MusicModel model;

    /**
     * 该生命周期方法由容器自动调用
     * 当viewPager需要获取fragment的view的对象时。
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_list,null);

        //初始化Fragment中的控件
        listView = view.findViewById(R.id.listView);
        //设置监听
        setListener();
        //发送请求，调用业务成代码，访问新歌榜的列表
        model = new MusicModel();
        model.getHotMusicList(0, 20, new MusicModel.MusicListCallback() {
            @Override
            public void onMusicListLoaded(List<Music> musics) {
                HotMusicListFragment.this.musics= musics;
                adapter = new MusicAdapter(getActivity(), musics,listView);
                listView.setAdapter(adapter);
            }
        });
        return view;
    }

    /**
     * 设置当前界面的监听
     */
    private void setListener() {
        //滚动listview时设置的监听
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private boolean isBottom = false;
            private boolean requeating = false;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {//滚动状态改变时执行
                switch (scrollState){
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://空闲状态
                        if (isBottom&&!requeating){
                            // Log.i("这次真的到底了","");
                            //加载下一页数据
                            requeating = true;
                            model.getNewMusicList(musics.size(), 20, new MusicModel.MusicListCallback() {
                                @Override
                                public void onMusicListLoaded(List<Music> musics) {//从服务器端，下载下来的数据的集合
                                    if (musics.isEmpty()){
                                        Toast.makeText(getActivity(), "T-T 没有了", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    //把服务端返回的下一页的数据，
                                    //都添加到当前正在使用的书记集合
                                    HotMusicListFragment.this.musics.addAll(musics);
                                    //更新adapter
                                    adapter.notifyDataSetChanged();
                                    requeating = false;
                                }
                            });
                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:

                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://触摸着滚动

                        break;
                }
            }
            //滚动时执行，执行频率非常高
            //int firstVisibleItem 第一个可见项的下标
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if(firstVisibleItem + visibleItemCount == totalItemCount) {
                    isBottom = true;
                }else {
                    isBottom = false;
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                //把播放列表与position存入播放列表
                MusicApplication app = MusicApplication.getApp();
                app.setMusics(musics);
                app.setPosition(position);
                //调用业务层，获取音乐的基本信息
                final Music m = musics.get(position);
                String songId = m.getSong_id();
                Log.i("songId你究竟怎么了=",""+songId);
                model.loadSongInfoBySongId(songId, new MusicModel.SongInfoCallBack() {
                    @Override
                    public void onSongInfoLoaded(SongUrl url, SongInfo info) {
                        //把获取到的urls集合与songinfo对象存入music中，以后要用的
                        m.setUrl(url);
                        m.setInfo(info);
                        //播放音乐
                        String pmUrl = url.getFile_link();
                        binder.playMusic(pmUrl);

                    }
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //销毁adapter中的子线程
        ImageLoader imageLoader = adapter.get();
        imageLoader.stopThread();
    }

    public void setBinder(PlayMusicService.MusicBinder binder) {
        this.binder = binder;
    }
}
