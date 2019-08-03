package com.belongmusic.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.belongmusic.R;
import com.belongmusic.adapter.SearchMusicAdapter;
import com.belongmusic.app.MusicApplication;
import com.belongmusic.entity.Music;
import com.belongmusic.entity.SongInfo;
import com.belongmusic.entity.SongUrl;
import com.belongmusic.fragment.HotMusicListFragment;
import com.belongmusic.fragment.NewMusicListFragment;
import com.belongmusic.model.MusicModel;
import com.belongmusic.service.DownService;
import com.belongmusic.service.PlayMusicService;
import com.belongmusic.util.BitmapUtils;
import com.belongmusic.util.GlobalConsts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.content.DialogInterface.*;

public class MainActivity extends FragmentActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private ImageButton ibPMMenue;
    private RelativeLayout rlPlayMusic;
    private ImageView ivPMBackground;
    private ImageView ivPMAlbum;
    private TextView tvPMTitle;
    private TextView tvPMSinger;
    private TextView tvPMLrc;
    private ImageButton btnCircle;
    private TextView tvPMCurrentTime;
    private TextView tvPMTotalTime;
    private SeekBar seekBar;
    private ImageView ivPMPrevious;
    private ImageView ivPMStartOrPause;
    private ImageView ivPMNext;
    private ImageButton ibPMBar;
    private RadioGroup radioGroup;
    private RadioButton radioNew;
    private RadioButton radioHot;
    private ViewPager viewPager;
    private RelativeLayout rlSearchMusic;
    private Button btnCancel,btnSearch;
    private EditText etSearch;
    private ListView lvSearchMusic;
    private ImageButton btnToSearch;
    private List<Fragment> fragments;
    private MainpagerAdapter pagerAdapter;
    private  ServiceConnection conn;
    private ImageView ivCMAlbum;
    private TextView tvCMTitle;
    private TextView tvCMSinger;
    private MusicStateReciever receiver;
    private long pressTime = 0;
    private PlayMusicService.MusicBinder binder;
    private MusicModel model;
    private boolean isOk = true;
    private  boolean isOk1 =false;
    private boolean isOk2 = true;
    private boolean isOk3 = false;
    private  boolean isOk4 = true;
    private SearchMusicAdapter searchAdapter;
    private  List<Music> searchMusicList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setPagerAdapter();
        setListeners();
        //绑定service
        bindMusicService();
        //（注册各种组件）注册广播接收器
        registComponents();
    }
    private void init() {
        radioGroup = findViewById(R.id.radioGrup);
        radioNew = findViewById(R.id.radioNew);
        radioHot = findViewById(R.id.radioHot);
        viewPager = findViewById(R.id.viewPager);
        ivCMAlbum = findViewById(R.id.ivCMAlbum);
        tvCMTitle = findViewById(R.id.tvCMTitle);
        tvCMSinger = findViewById(R.id.tvCMSinger);
        rlPlayMusic = findViewById(R.id.rlPlayMusic);
        tvPMTitle = findViewById(R.id.tvPMTitle);
        tvPMSinger = findViewById(R.id.tvPMSinger);
        ivPMBackground = findViewById(R.id.ivPMBackground);
        ivPMAlbum = findViewById(R.id.ivPMAlbum);
        seekBar = findViewById(R.id.seekBar);
        tvPMCurrentTime = findViewById(R.id.tvPMCurrentTime);
        tvPMTotalTime = findViewById(R.id.tvPMTotalTime);
        ivPMPrevious = findViewById(R.id.ivPMPrevious);
        ivPMNext = findViewById(R.id.ivPMNext);
        ivPMStartOrPause = findViewById(R.id.ivPMStartOrPause);
        tvPMLrc = findViewById(R.id.tvPMLrc);
        ibPMBar = findViewById(R.id.ibPMBar);
        btnToSearch = findViewById(R.id.btnToSearch);
        btnCancel = findViewById(R.id.btnCancel);
        btnSearch = findViewById(R.id.btnSearch);
        etSearch = findViewById(R.id.etSearch);
        lvSearchMusic = findViewById(R.id.lvSearchMusic);
        rlSearchMusic = findViewById(R.id.rlSearchMusic);
        ibPMMenue = findViewById(R.id.ibPMMenue);
        btnCircle = findViewById(R.id.btnCircle);

        model = new MusicModel();

        ivCMAlbum.setOnClickListener(this);
        ivPMStartOrPause.setOnClickListener(this);
        ibPMBar.setOnClickListener(this);
        ivPMPrevious.setOnClickListener(this);
        ivPMNext.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnToSearch.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        ibPMMenue.setOnClickListener(this);
        ivPMAlbum.setOnClickListener(this);
        btnCircle.setOnClickListener(this);

        lvSearchMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //当前搜索列表List与position存入applicatiion
               //播放歌曲的时候再存也不迟
                MusicApplication app= MusicApplication.getApp();
                 app.setMusics(searchMusicList);
                app.setPosition(position);
                //获取当前需要播放的列表
                final Music m = app.getCurrentMusic();//url,info都要在这里存入music，否则拿不到图片和歌词,客户端都是从
                //通过songId获取音乐信息
                String path = m.getSong_id();
                model.loadSongInfoBySongId(path, new MusicModel.SongInfoCallBack() {
                    @Override
                    public void onSongInfoLoaded(SongUrl url, SongInfo info) {
                        m.setUrl(url);
                        m.setInfo(info);
                        String searchUrl = url.getFile_link();
                        if (!searchUrl.equals("")){
                            binder.playMusic(searchUrl);
                        }else{
                            Toast.makeText(MainActivity.this, "没有找到音乐播放地址", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }

    /**
     * seek的监听
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {//进度改变的时候
        if (fromUser){//进度是由用户引起的，
            //定位到相应的位置继续播放
            binder.seekTO(progress);
        }
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {//开始触摸的时候
    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {//停止
    }

    /**
     * 监听
     * @param v
     */
    @Override
    public void onClick(View v) {
        MusicApplication app = MusicApplication.getApp();
        Animation anim = null;
        switch (v.getId()){
            case R.id.ivCMAlbum://显示出rlPlayMusic(窜出来)
                if (isOk1) {
                    rlPlayMusic.setVisibility(View.VISIBLE);
                     anim = new ScaleAnimation(0, 1, 0, 1, 0, rlPlayMusic.getHeight());
                    anim.setDuration(600);
                    rlPlayMusic.startAnimation(anim);
                }
                if (isOk3) {
                    rlSearchMusic.setVisibility(View.INVISIBLE);
                    anim = new TranslateAnimation(0, 0, 0, -rlSearchMusic.getHeight());
                    anim.setDuration(400);
                    rlSearchMusic.startAnimation(anim);
                    isOk3 = false;
                }
                break;
            case R.id.ivPMStartOrPause://控制音乐播放
                if (isOk1) {
                    if (isOk) {
                        ivPMStartOrPause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                        isOk = false;
                    } else {
                        ivPMStartOrPause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                        isOk = true;
                    }
                }
                    binder.startOrPause();
                    break;
            case R.id.ibPMBar:
                if (isOk1){
                    if (isOk){
                        ibPMBar.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                        isOk = false;
                    }else{
                        ibPMBar.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                        isOk = true;
                    }
                }
                    binder.startOrPause();
                    break;
            case R.id.ivPMPrevious://上一曲

                    app.preMusic();
                    final Music m1 = app.getCurrentMusic();
                    String songId = m1.getSong_id();
                    model.loadSongInfoBySongId(songId, new MusicModel.SongInfoCallBack() {
                        @Override
                        public void onSongInfoLoaded(SongUrl url, SongInfo Info) {
                            m1.setInfo(Info);
                            m1.setUrl(url);
                            String path = url.getFile_link();
                            binder.playMusic(path);
                        }
                    });

                break;
            case R.id.ivPMNext://下一曲
                app.nextMusic();
                final Music m2 = app.getCurrentMusic();
                String songId2 = m2.getSong_id();
                model.loadSongInfoBySongId(songId2, new MusicModel.SongInfoCallBack() {
                    @Override
                    public void onSongInfoLoaded(SongUrl url, SongInfo Info) {
                        m2.setInfo(Info);
                        m2.setUrl(url);
                        String path2 = url.getFile_link();
                        binder.playMusic(path2);
                    }
                });
                break;
            case R.id.btnSearch:
                searchMusic();
                break;
            case R.id.btnCancel:
                isOk3 = false;
                isOk4 = true;
                rlSearchMusic.setVisibility(View.INVISIBLE);
                 anim = new TranslateAnimation(0, 0,0,-rlSearchMusic.getHeight());
                anim.setDuration(400);
                rlSearchMusic.startAnimation(anim);
                break;
            case R.id.btnToSearch:
                isOk3 = true;
                isOk4 = false;
                rlSearchMusic.setVisibility(View.VISIBLE);
                 anim = new TranslateAnimation(0, 0,-rlSearchMusic.getHeight(),0);
                anim.setDuration(400);
                rlSearchMusic.startAnimation(anim);
                break;
            case R.id.ibPMMenue:
                if (isOk1) {
                    if (isOk4) {
                        rlSearchMusic.setVisibility(View.VISIBLE);
                        anim = new TranslateAnimation(0, 0, -rlSearchMusic.getHeight(), 0);
                        anim.setDuration(400);
                        rlSearchMusic.startAnimation(anim);
                        isOk4 = false;
                    }else {
                        rlSearchMusic.setVisibility(View.INVISIBLE);
                        anim = new TranslateAnimation(0, 0,0,-rlSearchMusic.getHeight());
                        anim.setDuration(400);
                        rlSearchMusic.startAnimation(anim);
                        isOk4 = true;
                        isOk3 = false;
                    }
                }
                break;
                //给播放界面中的专辑图片添加监听
                //点击弹出提供下载版本的AlertDialog
            case R.id.ivPMAlbum:
                //获取音乐有几个版本
                 app = MusicApplication.getApp();
                final Music m = app.getCurrentMusic();
                final SongUrl loadPath = m.getUrl();
                String[] items = new String[]{Math.floor( 100.0*loadPath.getFile_size()/1024/1024)/100+"M"};
                 /*String[] items = new  String[loadPath.size]{};
                 for (int i=0;i<items.length;i++){
                  SongUrl url = urls.get(i);
                   String items[i] =Math.floor( 100.0*url.getFile_size/1024/1024;)/100+"M"//向下取整
                }*/
                //弹出AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("选择下载版本")
                       .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      //Log.i("info", "选择的版本"+which);
                        String title = m.getTitle();
                        int bitrate = loadPath.getFile_bitrate();
                        String path = loadPath.getFile_link();
                        int filesize = loadPath.getFile_size();
                        //启动service
                        Intent intent = new Intent(MainActivity.this, DownService.class);
                        intent.putExtra("title",title);
                        intent.putExtra("bitrate",bitrate);
                        intent.putExtra("path",path);
                        intent.putExtra("filesize",filesize);
                        startService(intent);

                    }
                });
                builder.create().show();

                //Toast.makeText(this, "告诉我，你被点了吗？", Toast.LENGTH_SHORT).show();
                break;
            default:
                   Toast.makeText(this,"对不起，奴家也不知道怎么了？", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void searchMusic() {
        String keyword = etSearch.getText().toString();
        if("".equals(keyword.trim())){
            Toast.makeText(this, "请输入歌曲名称", Toast.LENGTH_SHORT).show();
            return;
        }
        //执行搜索业务
        model.searchMusicList(keyword, new MusicModel.MusicListCallback() {
            @Override
            public void onMusicListLoaded(List<Music> musics) {
                //把musics存入全局，以后要用
                 searchMusicList = musics;
                //更新listview
                searchAdapter = new SearchMusicAdapter(musics ,MainActivity.this);
                lvSearchMusic.setAdapter(searchAdapter);
            }
        });
    }

    /**
     * 设置监听器
     */
    @SuppressLint("ClickableViewAccessibility")
    @SuppressWarnings("deprecation")
    private void setListeners() {
        //RadioGrup操作Viewpager
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioNew:
                        viewPager.setCurrentItem(0);//设置当前选中项
                        break;
                    case R.id.radioHot:
                        viewPager.setCurrentItem(1);
                        break;
                }
            }
        });

        //捕获触摸事件，不让它漏到下层去
        rlPlayMusic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        rlSearchMusic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        //ViewPager操作RadioGroup
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        radioNew.setChecked(true);
                        break;
                    case 1:
                        radioHot.setChecked(true);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     *  //绑定PlayMusicService组件
     */
    private void bindMusicService() {
        Intent intent = new Intent(this, PlayMusicService.class);
        conn = new ServiceConnection() {
            //当与service的连接正常时执行
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                 binder = (PlayMusicService.MusicBinder) service;
                //把binder对象传给俩个fragment
                NewMusicListFragment f1 = (NewMusicListFragment) fragments.get(0);
                f1.setBinder(binder);
                HotMusicListFragment f2 = (HotMusicListFragment) fragments.get(1);
                f2.setBinder(binder);
            }
            //当与service的连接异常断开时执行
            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        this.bindService(intent, conn, Service.BIND_AUTO_CREATE);//自动绑定的意思
    }



    /**
     * 注册我们写好的广播接收器，并接收发来的广播
     */
    private void registComponents() {
        receiver = new MusicStateReciever();
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalConsts.ACTION_MUSIC_STARTED);
        filter.addAction(GlobalConsts.ACTION_UPDATA_MUSIC_STARTED);
        filter.addAction(GlobalConsts.ACTION_CIRCLE_MUSIC_STARTED);
        this.registerReceiver(receiver,filter);
    }



    /**
     * 广播接收器，音乐状态相关的广播。
     */
     class MusicStateReciever extends BroadcastReceiver {

         @Override
         public void onReceive(Context context, Intent intent) {
             //这俩行代码要放在方法里面
             MusicApplication app = MusicApplication.getApp();
             final Music music = app.getCurrentMusic();
             String songId = music.getUrl().getFile_link();
             model.loadSongInfoBySongId(songId, new MusicModel.SongInfoCallBack() {
                 @Override
                 public void onSongInfoLoaded(SongUrl url, SongInfo Info) {
                     music.setUrl(url);
                     music.setInfo(Info);
                     String path = url.getFile_link();
                     binder.playMusic(path);
                 }
             });

             String action = intent.getAction();
             if (action.equals(GlobalConsts.ACTION_UPDATA_MUSIC_STARTED)){
                //更新音乐进度
                int total = intent.getIntExtra("total",0);
                int current = intent.getIntExtra("current",0);
                //更新控件内容
                SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");//将毫秒值转换成分秒
                tvPMCurrentTime.setText(sdf.format(new Date(current)));
                tvPMTotalTime.setText(sdf.format(new Date(total)));
                //seekbar设置
                seekBar.setMax(total);
                seekBar.setProgress(current);
                //更新歌词
                HashMap<String,String> lrc = music.getLrc();//歌词
                //Log.i("你是什么情况？","");
                if(lrc != null){//歌词已经加载完成了
                    String ct =  sdf.format(new Date(current));
                    String content = lrc.get(ct);
                   // Log.i("你是什么情况？",""+content);
                    if (content != null){//有歌词与当前时间匹配
                        tvPMLrc.setText(Html.fromHtml(content));
                    }
                }
            } else if (action.equals(GlobalConsts.ACTION_MUSIC_STARTED)) {
               //音乐播放了，按钮可以用了
                if (isOk2) {
                    isOk1 = true;
                    isOk2 = false;
                    ibPMBar.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                    ivPMStartOrPause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                }


                //音乐开始播放了
                //获取当前正在播放的音乐对象
                String title = music.getTitle();
                String singer = music.getAuthor();
                //给控件赋值
                tvCMSinger.setText(Html.fromHtml(singer));
                tvCMTitle.setText(Html.fromHtml(title));
                //设置rlPlayMusic
                tvPMTitle.setText(Html.fromHtml(title));
                tvPMSinger.setText(Html.fromHtml(singer));

                //设置图片
                String path = music.getPic_small();
                BitmapUtils.loadBitmap(path, 0, new BitmapUtils.BitmapCallback() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap) {
                        if (bitmap !=null ) {
                            ivCMAlbum.setImageBitmap(bitmap);
                            /**
                             * 1.起始位置
                             * 2.终止位置
                             * 3.x一半
                             * 4.y一半  ivCMAlbum的中点
                             */
                            RotateAnimation anim = new RotateAnimation(0,360,ivCMAlbum.getWidth()/2, ivCMAlbum.getHeight()/2);
                            anim.setDuration(10000);//周期
                            anim.setRepeatCount(RotateAnimation.INFINITE);//重复次数
                            anim.setInterpolator(new LinearInterpolator());//设置匀速运动
                            ivCMAlbum.startAnimation(anim);
                        }else{
                              //ivCMAlbum.setImageResource(R.drawable.ic_autorenew_black_24dp);
                        }
                    }
                });
                //设置播放界面中的专辑图片
                String albumpath = music.getInfo().getPic_huge();
                if (albumpath.equals("")){
                    albumpath = music.getInfo().getPic_small();
                }
                BitmapUtils.loadBitmap(albumpath, 0,new BitmapUtils.BitmapCallback() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap) {
                                if (bitmap != null) {
                                    ivPMAlbum.setImageBitmap(bitmap);
                                }
                            }
                        });

                 //播放界面中的背景图片
                String bgPath = music.getInfo().getPic_big();
                if (bgPath.equals("")){
                    bgPath = music.getInfo().getPic_small();
                }
                if (bgPath.equals("")){
                    bgPath = music.getInfo().getPic_huge();
                }

                BitmapUtils.loadBitmap(bgPath, 6,new BitmapUtils.BitmapCallback() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap) {
                        if (bitmap != null){
                            //对下载下来的图片进行模糊化处理
                            BitmapUtils.loadBluredBitmap(bitmap, 10, new BitmapUtils.BitmapCallback() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap) {
                                    if (bitmap != null) {
                            ivPMBackground.setImageBitmap(bitmap);
                                    }
                                }
                            });
                        }
                    }
                });

                //加载当前音乐的歌词。
                String lrcPath = music.getInfo().getLrclink();
                //String lrcPath = music.getLrclink();
                model.loadLrc(lrcPath, new MusicModel.LrcCallback() {
                    @Override
                    public void onMusicLrcLoaded(HashMap<String, String> lrc) {
                        //给当前music对象设置歌词
                        music.setLrc(lrc);
                    }
                });
            }
        }
    }

    /**
     * 设置适配器
     */
    private void setPagerAdapter() {
        //构建Fragment集合 作为viewPager的数据源
        fragments = new ArrayList<>();
        //添加第一页
        fragments.add(new NewMusicListFragment());
        //添加第二页
        fragments.add(new HotMusicListFragment());
        pagerAdapter = new  MainpagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
    }

    /**
     * viewPager的适配器
     */
    class MainpagerAdapter extends FragmentPagerAdapter{

        public MainpagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    @Override
    protected void onDestroy() {
        //与Service解除绑定
        this.unbindService(conn);
        //取消广播器的注册
        this.unregisterReceiver(receiver);
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        if (rlPlayMusic.getVisibility() == View.VISIBLE){
            //隐藏rlPlayMusic
            ScaleAnimation anim = new ScaleAnimation(1,0,1,0,rlPlayMusic.getWidth()/2,rlPlayMusic.getHeight()/2);
            anim.setDuration(600);
            rlPlayMusic.startAnimation(anim);
            rlPlayMusic.setVisibility(View.INVISIBLE);
        }else{
            if(System.currentTimeMillis() - pressTime >2000){
                Toast.makeText(this, "哥们，再按一下我就滚了", Toast.LENGTH_SHORT).show();
                pressTime = System.currentTimeMillis();
            }else {
                super.onBackPressed();
            }
        }
    }
}
