package com.gmail.mrmioxin.ainfo;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by palchuk on 05.08.2015.
 * Agroinform progect
 */
public class AiListAdapter extends BaseAdapter {
    private static final String MY_LOG = "My_log.Adapter";
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Article> objects;
    Picasso mPicasso;

    AiListAdapter(Context context, ArrayList<Article> items) {
        ctx = context;
        objects = items;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPicasso = Picasso.with(context);
        mPicasso.setIndicatorsEnabled(true);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Article getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item, parent, false);
        }
        final Article a = getItem(position);//.Img.toString();
        final String img_url = a.Img.toString();
        Log.d(MY_LOG, "getView: " + img_url);
        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        TextView tvDescr = (TextView) view.findViewById(R.id.tvDescr);
        tvDate.setText(a.Date);
        tvDescr.setText(a.Descr);
        tvTitle.setText(a.Title);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        mPicasso.load(img_url)
                    .error(R.mipmap.ic_launcher)
                    .placeholder(R.mipmap.ic_launcher)
//                    .centerInside()
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            //Log.d(MY_LOG,"Loading is success " + img_url);
                        }

                        @Override
                        public void onError() {
                            //Log.d(MY_LOG,"Loading is failed " + img_url);
                        }
                    });
        return view;
    }

    public String getTitleArticle(int position) {
        return objects.get(position).Title;
    }
}
