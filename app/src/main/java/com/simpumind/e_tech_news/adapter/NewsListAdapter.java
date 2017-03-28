package com.simpumind.e_tech_news.adapter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.simpumind.e_tech_news.R;
import com.simpumind.e_tech_news.models.News;
import com.squareup.picasso.Picasso;

/**
 * Created by simpumind on 3/28/17.
 */

public class NewsListAdapter extends FirebaseRecyclerAdapter<News, RecyclerView.ViewHolder> {

    private  boolean isColorsInverted = false;
    private Context context;
    private AppCompatActivity activity;

    private DatabaseReference mDatabaseRef;
    private Query oneQuery;
    private DatabaseReference childRef;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    /**
     * @param modelClass      Firebase will marshall the data at a location into
     *                        an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list.
     *                        You will be responsible for populating an instance of the corresponding
     *                        view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location,
     *                        using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public NewsListAdapter(Class<News> modelClass, int modelLayout, Class<RecyclerView.ViewHolder> viewHolderClass, Query ref, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //return super.onCreateViewHolder(parent, viewType);

        if (viewType == TYPE_HEADER) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_header, parent, false);
            return new HeaderViewHolder(layoutView);
        } else{
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(mModelLayout, parent, false);
            return new NewsListHolder(layoutView);
        }
    }

    @Override
    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, News model, int position) {


        if(viewHolder instanceof HeaderViewHolder){
            ((HeaderViewHolder) viewHolder).title.setText(model.getCaption());
            //((HeaderViewHolder) viewHolder).dateTime.setText(model.getime());
            Picasso.with(context).load(model.getThumbnail()).into(((HeaderViewHolder) viewHolder).imageView2);
        }else {

            ((NewsListHolder) viewHolder).newsTitle.setText(model.getCaption());
            Picasso.with(context).load(model.getThumbnail()).into(((NewsListHolder) viewHolder).newsImage);
        }

    }


    public class HeaderViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView2;
        public TextView title;
        public TextView dateTime;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
            dateTime = (TextView) itemView.findViewById(R.id.dateTime);

            imageView2 = (ImageView) itemView.findViewById(R.id.imageView2);
        }
    }
}