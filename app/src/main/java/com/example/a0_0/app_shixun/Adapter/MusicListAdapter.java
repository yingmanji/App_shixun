package com.example.a0_0.app_shixun.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.a0_0.app_shixun.Base.App;
import com.example.a0_0.app_shixun.R;
import com.example.a0_0.app_shixun.music.MusicUtils;

/**
 * Created by 樱满集0_0 on 2017/6/20.
 */

public class MusicListAdapter extends BaseAdapter {
    private int playingPosition;

    public int getPlayingPosition() {
        return playingPosition;
    }

    public void setPlayingPosition(int playingPosition) {
        this.playingPosition = playingPosition;
    }

    @Override
    public int getCount() {
        return MusicUtils.musicList.size();
    }

    @Override
    public Object getItem(int position) {
        return MusicUtils.musicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView==null)
        {
            convertView= convertView.inflate(App.context, R.layout.bendi_music_item,null);
            holder=new ViewHolder();
            holder.title=(TextView)convertView.findViewById(R.id.tv_music_title);
            holder.artist=(TextView)convertView.findViewById(R.id.tv_music_artist);
            convertView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder) convertView.getTag();
        }
        if(playingPosition==position){
            holder.mark.setVisibility(View.VISIBLE);
        }
        else{
            holder.mark.setVisibility(View.INVISIBLE);
        }
        holder.title.setText(MusicUtils.musicList.get(position).getTitle());
        holder.artist.setText(MusicUtils.musicList.get(position).getArtist());
        return convertView;
    }
    static class ViewHolder {
        TextView title;
        TextView artist;
        View mark;
    }

}
