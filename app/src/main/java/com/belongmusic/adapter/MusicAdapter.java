package com.belongmusic.adapter;

import android.content.Context;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.belongmusic.R;
import com.belongmusic.entity.Music;
import com.belongmusic.util.ImageLoader;



import java.util.List;



/**
 * Created by 枫暮晓 on 2018/7/31.
 * 音乐列表适配器
 */

public class MusicAdapter extends BaseAdapter {

    private Context context;
    private ListView listView;
    private List<Music> musics;
    private ImageLoader imageLoader;
    private LayoutInflater inflater;

    public MusicAdapter(Context context, List<Music> musics, ListView listView) {
        this.context = context;
        this.listView = listView;
        this.musics = musics;
        this.inflater = LayoutInflater.from(context);
        this.imageLoader = new ImageLoader(context, listView);
    }

    @Override
    public int getCount () {
        return musics.size();
    }

    @Override
    public Object getItem ( int position){
        return musics.get(position);
    }

    @Override
    public long getItemId ( int position){
        return position;
    }


    @Override
    public View getView ( int position, View convertView, ViewGroup parent){
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_lv_music, parent,false);
            holder.ivAlbum = convertView.findViewById(R.id.ivAlbum);
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvSinger = convertView.findViewById(R.id.tvSinger);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }
        //找到某一首歌，再从每首歌中找到各自的信息。
        Music m = (Music) getItem(position);

        holder.tvTitle.setText(m.getTitle());
        holder.tvSinger.setText(m.getAuthor());
        
		//给item设置图片，通过图片网络路径下载图片，然后设置图片
        String path = m.getPic_small();
        imageLoader.display(holder.ivAlbum, path);
        
		return convertView;
    }




    public class ViewHolder {
        private ImageView ivAlbum;
        private TextView tvTitle;
        private TextView tvSinger;
    }

    public ImageLoader get() {
        return this.imageLoader;
    }
}





