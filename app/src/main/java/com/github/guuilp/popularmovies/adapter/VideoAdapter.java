package com.github.guuilp.popularmovies.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.guuilp.popularmovies.R;
import com.github.guuilp.popularmovies.model.Videos;

import java.util.List;

/**
 * Created by Guilherme on 25/04/2017.
 */

public class VideoAdapter extends BaseAdapter{
    private final List<Videos.Result> videos;
    private final Activity activity;

    public VideoAdapter(List<Videos.Result> videos, Activity activity){
        this.videos = videos;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public Object getItem(int position) {
        return videos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = activity.getLayoutInflater().inflate(R.layout.list_trailer_videos, parent, false);
        Videos.Result item = videos.get(position);

        ImageView imagem = (ImageView) view.findViewById(R.id.id_play_button);
        TextView nome = (TextView) view.findViewById(R.id.tvTrailer);

        imagem.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
        nome.setText("Trailer " + (position + 1));

        return view;
    }
}