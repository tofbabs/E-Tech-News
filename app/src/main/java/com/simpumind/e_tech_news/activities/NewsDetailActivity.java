package com.simpumind.e_tech_news.activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simpumind.e_tech_news.R;
import com.simpumind.e_tech_news.models.News;

import java.util.ArrayList;
import java.util.List;

import at.blogc.android.views.ExpandableTextView;

public class NewsDetailActivity extends AppCompatActivity {

    private static final String TAG = NewsDetailActivity.class.getSimpleName();
    public static final String SINGLE_NEWS = "Single_news";
    public static final String VENDOR_NAME = "vendor_name";
    public static final String VENDOR_ICON = "vendor_icon";
    public static final String VENDOR_ID = "vendor_id";

    private DatabaseReference mDatabaseRef;
    private DatabaseReference childRef;
    private DatabaseReference mDataRef;
    ExpandableTextView expandableTextView;
    ImageView newsImage;
    TextView titleNews;
    TextView vendorName;
    ImageView vendorIcon;
    Button subscribe;
    ViewGroup transitionsContainer;

    private boolean isUserSubscribed;


    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");


        Intent intent = getIntent();
        final String news_id  = intent.getStringExtra(SINGLE_NEWS);

        Log.d("fdmfmdmdfdc", news_id);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("news");
        mDatabaseRef.child(news_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                News n = dataSnapshot.getValue(News.class);
                Log.d("dmfmdmdmf", n.content);
                updateViews(n);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        newsImage = (ImageView) findViewById(R.id.toolbarImage);

        vendorIcon = (ImageView) findViewById(R.id.vendorIcon);

        titleNews = (TextView) findViewById(R.id.titleNews);

        vendorName = (TextView) findViewById(R.id.vendorName);

        subscribe = (Button) findViewById(R.id.subscribe);
        transitionsContainer = (ViewGroup) findViewById(R.id.transitions_container);
        expandableTextView = (ExpandableTextView) this.findViewById(R.id.description);
        final Button buttonToggle = (Button) this.findViewById(R.id.button_toggle);


        final String vendor_id  = intent.getStringExtra(VENDOR_ID);

        mDataRef = FirebaseDatabase.getInstance().getReference();
        childRef = mDataRef.child("users_subscription");
        childRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String subscribedId = dataSnapshot.getValue().toString();
                if(subscribedId.equals(vendor_id)){

                    isUserSubscribed = true;
                    expandableTextView.expand();
                    buttonToggle.setVisibility(View.INVISIBLE);
                    TransitionManager.beginDelayedTransition(transitionsContainer, new AutoTransition());
                    subscribe.setBackgroundTintList(getResources().getColorStateList(R.color.button_back_color));
                    subscribe.setBackground(getResources().getDrawable(R.drawable.round_corner));
                    subscribe.setText("Unsubscribe");

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("read_by_user").push();
                    mDatabase.setValue(news_id);
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        expandableTextView.setAnimationDuration(1000L);


        // toggle the ExpandableTextView
        buttonToggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                expandableTextView.toggle();
                buttonToggle.setText(expandableTextView.isExpanded() ? "Collapse" : "Expand");
            }
        });

// but, you can also do the checks yourself
        buttonToggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if (!isUserSubscribed)
                {
                    //expandableTextView.collapse();
                   // buttonToggle.setText("UnSubscribe");

                    Toast.makeText(NewsDetailActivity.this, "You are not subscribed to this vendor", Toast.LENGTH_LONG).show();
                }
                else
                {
                    expandableTextView.expand();
//                    buttonToggle.setText("Subscribe");
//                    mDatabase = FirebaseDatabase.getInstance().getReference().child("read_by_user").push();
//                    mDatabase.setValue(news_id);
                    buttonToggle.setVisibility(View.INVISIBLE);
                }
            }
        });

// listen for expand / collapse events
        expandableTextView.setOnExpandListener(new ExpandableTextView.OnExpandListener()
        {
            @Override
            public void onExpand(final ExpandableTextView view)
            {
                Log.d(TAG, "ExpandableTextView expanded");
            }

            @Override
            public void onCollapse(final ExpandableTextView view)
            {
                Log.d(TAG, "ExpandableTextView collapsed");
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        Log.d(TAG,"--onSupportNavigateUp()--");
        getSupportFragmentManager().popBackStack();
        super.onBackPressed();
        return true;
    }

    private void updateViews(News news){

        Intent intent = getIntent();

        if(intent.getStringExtra(VENDOR_NAME) != null){
            String vendName = intent.getStringExtra(VENDOR_NAME);
            String vendIcon = intent.getStringExtra(VENDOR_ICON);

            vendorName.setText(vendName);

//            String encodedDataString = vendIcon;
//            encodedDataString = encodedDataString.replace("data:image/jpeg;base64,","");
//            String[] dataString = encodedDataString.split("=");
//
//            byte[] imageAsBytes = Base64.decode(dataString[0].getBytes(), Base64.NO_PADDING);
//            vendorIcon.setImageBitmap(BitmapFactory.decodeByteArray(
//                    imageAsBytes, 0, imageAsBytes.length));
        }

        expandableTextView.setText(Html.fromHtml(news.content));

        titleNews.setText(news.caption);

        String encodedDataString = news.getThumbnail();
        encodedDataString = encodedDataString.replace("data:image/jpeg;base64,","");
        String[] dataString = encodedDataString.split("=");

        byte[] imageAsBytes = Base64.decode(dataString[0].getBytes(), Base64.NO_PADDING);
        newsImage.setImageBitmap(BitmapFactory.decodeByteArray(
                imageAsBytes, 0, imageAsBytes.length));

    }
}
