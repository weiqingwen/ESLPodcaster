package com.qingwenwei.eslpodcaster.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.qingwenwei.eslpodcaster.R;
import com.qingwenwei.eslpodcaster.constant.Constants;
import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;
import com.qingwenwei.eslpodcaster.fragment.DownloadedFragment;
import com.qingwenwei.eslpodcaster.fragment.FavoritesFragment;
import com.qingwenwei.eslpodcaster.fragment.PodcastListFragment;
import com.qingwenwei.eslpodcaster.util.AudioPlayer;
import com.qingwenwei.eslpodcaster.util.EslPodScriptParser;
import com.qingwenwei.eslpodcaster.util.ExtractorRendererBuilder;
import com.qingwenwei.eslpodcaster.util.RendererBuilder;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private int[] tabIcons = {
            R.drawable.ic_cast_white_36dp,
            R.drawable.ic_file_download_white_36dp,
            R.drawable.ic_favorite_white_36dp
    };

    //Sliding up panel sliding_up_panel_player_layout
    private SlidingUpPanelLayout slidingUpPanelLayout;

    //collapsed panel views
    private View collapsedPanel;
    private View slidingUpPanelPlayerView;

    private ImageButton collapsedPanelPlayButton;
    private ImageButton collapsedPanelMenuButton;

    //sliding up player
    private AudioPlayer player;
    private TextView collapsedPanelTitleTextView;
    private TextView slidingUpPanelScriptTextView;
    private TextView slidingUpPanelCurrPosTextView;
    private TextView slidingUpPanelMaxPosTextView;
    private SeekBar slidingUpPanelSeekBar;
    private ImageButton slidingUpPanelPlayButton;
    private ImageButton slidingUpPanelReplayButton;
    private ImageButton slidingUpPanelForwardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if(ab != null){
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setHomeButtonEnabled(false);
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if(viewPager != null){
            setupViewPager(viewPager);
        }

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        if(tabLayout != null){
            tabLayout.setupWithViewPager(viewPager);
        }

        if(tabLayout != null){
            setupTabIcons();
        }

        //Sliding Panel
        slidingUpPanelLayout = (SlidingUpPanelLayout)findViewById(R.id.activity_main);
        slidingUpPanelLayout.addPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
//                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
                if(newState == PanelState.EXPANDED){
                    collapsedPanelPlayButton.setVisibility(View.INVISIBLE);
                    collapsedPanelMenuButton.setVisibility(View.VISIBLE);
                }else if(newState == PanelState.COLLAPSED){
                    collapsedPanelPlayButton.setVisibility(View.VISIBLE);
                    collapsedPanelMenuButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        slidingUpPanelLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "OnClickListener() " + view.getContext().getPackageName());
//                slidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
            }
        });

        //collapsed panel
        collapsedPanel = findViewById(R.id.collapsedPanel);
        collapsedPanel.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"collapsedPanel onClick()");
                    toggleSlidingUpPanel();
                }
            }
        );
        collapsedPanelTitleTextView = (TextView)findViewById(R.id.collapsedPanelTitleTextView);
        collapsedPanelPlayButton = (ImageButton)findViewById(R.id.collapsedPanelPlayButton);
        collapsedPanelMenuButton = (ImageButton)findViewById(R.id.collapsedPanelMenuButton);

        //find sliding up player views
        slidingUpPanelPlayerView = findViewById(R.id.slidingUpPanelPlayerLayout);
        slidingUpPanelScriptTextView = (TextView)findViewById(R.id.slidingUpPanelScriptTextView);
        slidingUpPanelCurrPosTextView = (TextView)findViewById(R.id.slidingUpPanelCurrPosTextView);
        slidingUpPanelMaxPosTextView = (TextView)findViewById(R.id.slidingUpPanelMaxPosTextView);
        slidingUpPanelPlayButton = (ImageButton)findViewById(R.id.slidingUpPanelPlayButton);
        slidingUpPanelSeekBar = (SeekBar)findViewById(R.id.slidingUpPanelSeekBar);
        slidingUpPanelReplayButton = (ImageButton)findViewById(R.id.slidingUpPanelReplayButton);
        slidingUpPanelForwardButton = (ImageButton)findViewById(R.id.slidingUpPanelForwardButton);

        setupSlidingUpPanelPlayerListeners();
        setupInterceptOnTouchListeners();
    }

    //collapse and expand the sliding up panel
    private void toggleSlidingUpPanel(){
        if (slidingUpPanelLayout.getPanelState() == PanelState.COLLAPSED){
            slidingUpPanelLayout.setPanelState(PanelState.EXPANDED);
        }else{
            slidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
        }
    }

    //intercept TextView scrolling
    private void setupInterceptOnTouchListeners(){
        slidingUpPanelScriptTextView.setMovementMethod(new ScrollingMovementMethod());
        slidingUpPanelScriptTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        slidingUpPanelPlayerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
    }

    //setup listeners to widgets from Sliding Up Panel player
    private void setupSlidingUpPanelPlayerListeners(){
        //register listeners
        slidingUpPanelSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if(player.isPlaying()) {
                        slidingUpPanelCurrPosTextView.setText(toMinuteFormat(progress));
                        player.seekTo(progress);
                    }else{
                        slidingUpPanelCurrPosTextView.setText(toMinuteFormat(progress));
                        player.seekTo(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //do nothing
            }
        });

        slidingUpPanelPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"slidingUpPanelPlayButton.setOnClickListener()");
                if (!player.isPlaying()) {
                    player.setPlayWhenReady(true);
                    slidingUpPanelSeekBar.setMax((int) player.getDuration());
                    slidingUpPanelMaxPosTextView.setText(toMinuteFormat(player.getDuration()));
                    slidingUpPanelSeekBar.postDelayed(onEverySecond, 1000);
                } else {
                    player.setPlayWhenReady(false);
                }
            }
        });

        slidingUpPanelReplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currPos = player.getCurrentPosition();
                if(currPos - 10000 < 0){
                    player.seekTo(0);
                }else{
                    player.seekTo(currPos - 10000);
                }

                if(!player.isPlaying()){
                    updateSlidingUpPanelSeekBar();
                }
            }
        });

        slidingUpPanelForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currPos = player.getCurrentPosition();
                long duration = player.getDuration();
                if(currPos + 10000 >= duration) {
                    player.seekTo(duration);
                }else{
                    player.seekTo(currPos + 10000);
                }

                if(!player.isPlaying()){
                    updateSlidingUpPanelSeekBar();
                }
            }
        });
    }

    //setup tab icons and their color
    private void setupTabIcons(){
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);

        tabLayout.getTabAt(0).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(Color.parseColor("#dddddd"), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(Color.parseColor("#dddddd"), PorterDuff.Mode.SRC_IN);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#dddddd"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    //setup fragments
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PodcastListFragment(), "Podcast");
        adapter.addFragment(new DownloadedFragment(), "Download");
        adapter.addFragment(new FavoritesFragment(), "Favorite");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
//        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
//            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
//            return mFragmentTitleList.get(position);
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout != null &&
                (slidingUpPanelLayout.getPanelState() == PanelState.EXPANDED || slidingUpPanelLayout.getPanelState() == PanelState.ANCHORED)) {
            slidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    public SlidingUpPanelLayout getSlidingUpPanelLayout() {
        return slidingUpPanelLayout;
    }

    private class DownloadEpisodeScriptAsyncTask extends AsyncTask<String, Integer, PodcastEpisode> {
        private static final String TAG = "DownloadEpisodeScriptAsyncTask";
        private PodcastEpisode episode;

        public DownloadEpisodeScriptAsyncTask(PodcastEpisode episode){
            this.episode = episode;
        }

        @Override
        protected PodcastEpisode doInBackground(String... urls) {
            new EslPodScriptParser().getEpisodeScript(episode);
            return this.episode;
        }

        @Override
        protected void onPostExecute(final PodcastEpisode scriptedEpisode) {
//            Log.i(TAG,"onPostExecute()" + episode.getContent());

            //show episode scripts on the screen
            slidingUpPanelScriptTextView.setText(Html.fromHtml(episode.getContent()));
        }
    }

    //player helper method
    public void loadPlayingPodcast(PodcastEpisode episode){
        Log.i(TAG, "loadPlayingPodcast()" + episode.getTitle());

        //toggle sliding-up panel
        toggleSlidingUpPanel();

        //set play button background image to play image
        setSlidingUpPanelPlayButtonPlaying();

        //change the sliding-up panel episode title
        collapsedPanelTitleTextView.setText(episode.getTitle());

        //prepare audio play and update the script in the sliding-up panel
        preparePlayer(episode);
        new DownloadEpisodeScriptAsyncTask(episode).execute();
    }

    public void setSlidingUpPanelPlayButtonPlaying(){
        slidingUpPanelPlayButton.setImageResource(R.drawable.ic_play_arrow_white_36dp);
    }

    public void setSlidingUpPanelPlayButtonPause(){
        slidingUpPanelPlayButton.setImageResource(R.drawable.ic_pause_white_36dp);
    }

    //player helper method
    private void preparePlayer(PodcastEpisode episode){
        Log.i(TAG,"preparePlayer() " + episode.audioFileUrl);

        if(player != null){
            player.release();
            player = null;
        }

        RendererBuilder rendererBuilder = new ExtractorRendererBuilder(
                getBaseContext(),
                Constants.USER_AGENT,
                episode.audioFileUrl);
        player = new AudioPlayer(this, rendererBuilder);
        player.prepare();
    }

    //player helper method
    private String toMinuteFormat(long millis){
        return String.format(
                Locale.getDefault(),
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    //player helper method
    private Runnable onEverySecond = new Runnable() {
        @Override
        public void run() {
            if(player.isPlaying()){
                slidingUpPanelCurrPosTextView.setText(toMinuteFormat(player.getCurrentPosition()));
                slidingUpPanelSeekBar.setProgress((int)player.getCurrentPosition());
                slidingUpPanelSeekBar.postDelayed(onEverySecond, 1000);
            }
        }
    };

    //player helper method
    private void updateSlidingUpPanelSeekBar(){
        int currPos = (int) player.getCurrentPosition();
        slidingUpPanelCurrPosTextView.setText(toMinuteFormat(currPos));
        slidingUpPanelSeekBar.setProgress(currPos);
    }
}

