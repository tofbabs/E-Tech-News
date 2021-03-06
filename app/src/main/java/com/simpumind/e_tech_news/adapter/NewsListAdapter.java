package com.simpumind.e_tech_news.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.simpumind.e_tech_news.R;
import com.simpumind.e_tech_news.activities.NewsDetailActivity;
import com.simpumind.e_tech_news.activities.VendorNewsListActivity;
import com.simpumind.e_tech_news.models.News;
import com.simpumind.e_tech_news.models.NewsPaper;
import com.simpumind.e_tech_news.utils.Const;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by simpumind on 3/28/17.
 */

public class NewsListAdapter extends FirebaseRecyclerAdapter<News, RecyclerView.ViewHolder> {

    private Context context;
    private AppCompatActivity activity;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private String vendorName;
    private String vendorIcon;
    private String vendorId;

    private StorageReference mStorage;



    private Query mRef;
    private Class<News> mModelClass;
    private int mLayout;
    private LayoutInflater mInflater;
    private ChildEventListener mListener;

    List<News> newsPaperList;

    private List<News> posts = new ArrayList<>();
    private List<News> postsCopy= new ArrayList<>();
    private List<String> mKeys;

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
    public  NewsListAdapter(Class<News> modelClass, int modelLayout, Class<RecyclerView.ViewHolder> viewHolderClass,
                            Query ref, Context context, AppCompatActivity activity,
                            String vendorName, String vendorIcon, String vendorId) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
        this.activity = activity;
        this.vendorName = vendorName;
        this.vendorIcon = vendorIcon;
        this.vendorId = vendorId;


        this.mRef = ref;
        this.mModelClass = modelClass;
        this.mLayout = modelLayout;
        mInflater = activity.getLayoutInflater();
        posts = new ArrayList<>();
        mKeys = new ArrayList<>();
        // Look for all child events. We will then map them to our own internal ArrayList, which backs ListView
        mListener = this.mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                News model = dataSnapshot.getValue(NewsListAdapter.this.mModelClass);
                String key = dataSnapshot.getKey();

                // Insert into the correct location, based on previousChildName
                if (previousChildName == null) {
                    posts.add(0, model);
                    postsCopy.add(0, model);
                    mKeys.add(0, key);
                } else {
                    int previousIndex = mKeys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == posts.size()) {
                        posts.add(model);
                        postsCopy.add(model);
                        mKeys.add(key);
                    } else {
                        posts.add(nextIndex, model);
                        postsCopy.add(nextIndex, model);
                        mKeys.add(nextIndex, key);
                    }
                }

                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // One of the posts changed. Replace it in our list and name mapping
                String key = dataSnapshot.getKey();
                News newModel = dataSnapshot.getValue(NewsListAdapter.this.mModelClass);
                int index = mKeys.indexOf(key);

                posts.set(index, newModel);
                postsCopy.set(index, newModel);

                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                // A model was removed from the list. Remove it from our list and the name mapping
                String key = dataSnapshot.getKey();
                int index = mKeys.indexOf(key);

                mKeys.remove(index);
                posts.remove(index);
                postsCopy.remove(index);

                notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

                // A model changed position in the list. Update our list accordingly
                String key = dataSnapshot.getKey();
                News newModel = dataSnapshot.getValue(NewsListAdapter.this.mModelClass);
                int index = mKeys.indexOf(key);
                posts.remove(index);
                postsCopy.remove(index);
                mKeys.remove(index);
                if (previousChildName == null) {
                    posts.add(0, newModel);
                    postsCopy.add(0,newModel);
                    mKeys.add(0, key);
                } else {
                    int previousIndex = mKeys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == posts.size()) {
                        posts.add(newModel);
                        postsCopy.add(newModel);
                        mKeys.add(key);
                    } else {
                        posts.add(nextIndex, newModel);
                        postsCopy.add(nextIndex, newModel);
                        mKeys.add(nextIndex, key);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur");
            }

        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //return super.onCreateViewHolder(parent, viewType);


        if (viewType == TYPE_HEADER) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_header, parent, false);
            return new HeaderViewHolder(layoutView);
        } else if(viewType == TYPE_ITEM){
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(mModelLayout, parent, false);
            return new NewsListHolder(layoutView);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
           // return TYPE_HEADER;
        }

        return TYPE_ITEM;

    }

    private void loadImage(final ImageView imageView, String imagePath, final Context context){
        mStorage = FirebaseStorage.getInstance().getReference().child(imagePath);

        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(mStorage)
                .fitCenter()
                .placeholder(R.drawable.denews)
                .into(imageView);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        final News model = getItem(position);

        if(model.isStatus()) {
            ((NewsListHolder) viewHolder).itemView.setVisibility(View.VISIBLE);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String lang = preferences.getString("lang", "fr");

            if (lang.equals("fr")){
                if(!model.getCaption().getFrench().equals("")) {
                    ((NewsListHolder) viewHolder).newsTitle.setText(model.getCaption().getFrench());
                }else{
                    ((NewsListHolder) viewHolder).newsTitle.setText(model.getCaption().getEnglish());
                }
            }else{
                if (!model.getCaption().getEnglish().equals("")) {
                    ((NewsListHolder) viewHolder).newsTitle.setText(model.getCaption().getEnglish());
                }else {
                    ((NewsListHolder) viewHolder).newsTitle.setText(model.getCaption().getFrench());
                }
            }
            ((NewsListHolder) viewHolder).vendorName.setText(vendorName);

            commentCount((NewsListHolder) viewHolder, position);
            Locale LocaleBylanguageTag = Locale.forLanguageTag(lang);
            TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(LocaleBylanguageTag).build();

            String text = TimeAgo.using(model.getCreated_on(), messages);

            ((NewsListHolder) viewHolder).timeDate.setText(text);

            loadImage(((NewsListHolder) viewHolder).newsImage, model.getThumbnail(), context);

            ((NewsListHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, NewsDetailActivity.class);
                    View sharedView = ((NewsListHolder) viewHolder).newsImage;
                    String transitionName = activity.getString(R.string.blue_name);
                    ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(activity, sharedView, transitionName);
                    intent.putExtra(NewsDetailActivity.SINGLE_NEWS, model.getMain_news_id());
                    intent.putExtra(NewsDetailActivity.VENDOR_NAME, vendorName);
                    intent.putExtra(NewsDetailActivity.VENDOR_ICON, vendorIcon);
                    intent.putExtra(NewsDetailActivity.VENDOR_ID, vendorId);
                    ActivityCompat.startActivity(activity, intent, transitionActivityOptions.toBundle());
                }
            });
        }else{
            ((NewsListHolder) viewHolder).itemView.setVisibility(View.GONE);
        }

    }


    public void setList(List<News> list) {
        newsPaperList = list;
    }


    public void cleanup() {
        // We're being destroyed, let go of our mListener and forget about all of the posts
        mRef.removeEventListener(mListener);
        posts.clear();
        postsCopy.clear();
        mKeys.clear();
    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public News getItem(int i) {
        return posts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void filter(String text) {
        posts.clear();
        if(text.isEmpty()){
            posts.addAll(postsCopy);
        } else{
            text = text.toLowerCase();
            for(News post : postsCopy){
                if(post.getCaption().getEnglish().toLowerCase().contains(text)){
                    posts.add(post);
                }
            }
        }
        notifyDataSetChanged();
    }


    @Override
    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, final News model, final int position) {


    }

    private void commentCount(final NewsListHolder holder, int position){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("comments").child(getRef(position).getKey());

//You can use the single or the value.. depending if you want to keep track
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0){
                    holder.commentCount.setText(String.valueOf(dataSnapshot.getChildrenCount() + ""));
                    Log.e(dataSnapshot.getKey(),dataSnapshot.getChildrenCount() + "");
                }else{
                    holder.commentCount.setText(String.valueOf(0));
                    Log.e(dataSnapshot.getKey(),dataSnapshot.getChildrenCount() + "");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
