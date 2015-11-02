package com.gmail.mrmioxin.ainfo;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by palchuk on 28.09.2015.
 * Agroinform progect
 */
public class AiCursorAdapter extends CursorAdapter {
    private static final String MY_LOG = "My_log.CursorAdapter";
    private Context ctx;
    private LayoutInflater lInflater;
    private ArrayList<Article> objects;
    private Picasso mPicasso;


    public AiCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        lInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPicasso = Picasso.with(context);
        mPicasso.setIndicatorsEnabled(true);

    }

    @Override
    public View newView(Context ctx, Cursor cur, ViewGroup parent) {
        View root = lInflater.inflate(R.layout.item, parent, false);
        ViewHolder holder = new ViewHolder();
        Log.d(MY_LOG, "newView: ");
        TextView tvDate = (TextView) root.findViewById(R.id.tvDate);
        TextView tvTitle = (TextView) root.findViewById(R.id.tvTitle);
        TextView tvDescr = (TextView) root.findViewById(R.id.tvDescr);
        //TextView tvClassName = (TextView)root.findViewById(R.id.tvTitle);
        ImageView imageView = (ImageView) root.findViewById(R.id.imageView);
        //holder.tvClassName = tvClassName;
        holder.tvDate   = tvDate    ;
        holder.tvTitle  = tvTitle   ;
        holder.tvDescr  = tvDescr   ;
        holder.imageView= imageView ;
        root.setTag(holder);
        return root;

    }

     @Override
    public void bindView(View view, Context ctx, Cursor cur) {
        long id = cur.getLong(cur.getColumnIndex(ContractClass.Articles._ID));
        String date = cur.getString(cur.getColumnIndex(ContractClass.Articles.COLNAME_DATE));
        String title = cur.getString(cur.getColumnIndex(ContractClass.Articles.COLNAME_TITLE));
        String descr = cur.getString(cur.getColumnIndex(ContractClass.Articles.COLNAME_DESCR));
        String img = cur.getString(cur.getColumnIndex(ContractClass.Articles.COLNAME_IMG));

        ViewHolder holder = (ViewHolder) view.getTag();
        if(holder != null) {
            holder.ID = id;
            holder.tvDate.setText(date);
            holder.tvTitle.setText(title);
            holder.tvDescr.setText(descr);
            mPicasso.load(img)
                    .error(R.mipmap.ic_launcher)
                    .placeholder(R.mipmap.ic_launcher)
//                    .centerInside()
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            //Log.d(MY_LOG,"Loading is success " + img_url);
                        }

                        @Override
                        public void onError() {
                            //Log.d(MY_LOG,"Loading is failed " + img_url);
                        }
                    });

            //holder.imageView.
            //holder.tvClassName.setText(classNumber+"\""+classLetter+"\"");
            //holder.classID = id;
        }

    }
    public static class ViewHolder {
        public TextView  tvDate;
        public TextView  tvTitle;
        public TextView  tvDescr;
        public ImageView imageView;
        //public TextView tvClassName;
        public long ID;
    }

}
