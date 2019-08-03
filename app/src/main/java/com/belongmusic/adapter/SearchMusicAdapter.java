package com.belongmusic.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.belongmusic.R;
import com.belongmusic.entity.Music;

import java.util.List;

/**
 * Created by 枫暮晓 on 2018/8/3.
 */

public class SearchMusicAdapter extends BaseAdapter {

    private List<Music> musics;
    private Context mContext;

    public SearchMusicAdapter(List<Music> musics, Context mContext) {
        this.musics = musics;
        this.mContext = mContext;
    }
    @Override
    public int getCount() {
        return musics.size();
    }

    @Override
    public Object getItem(int position) {
        return musics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lv_search, parent, false);
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvSinger = convertView.findViewById(R.id.tvSinger);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //给控件赋值

        Music music = (Music) getItem(position);
        String title = music.getTitle();
        String singer = music.getAuthor();
        holder.tvTitle.setText(Html.fromHtml(title));
        holder.tvSinger.setText(Html.fromHtml(singer));

        return convertView;
    }
    class ViewHolder{
        private TextView tvTitle;
        private TextView tvSinger;
    }

}
