package com.qingwenwei.eslpodcaster.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.qingwenwei.eslpodcaster.R;
import com.qingwenwei.eslpodcaster.constant.Constants;
import com.qingwenwei.eslpodcaster.db.SQLiteDatabaseManager;
import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;
import com.qingwenwei.eslpodcaster.fragment.DownloadFragment;
import com.qingwenwei.eslpodcaster.fragment.FavoriteFragment;
import com.qingwenwei.eslpodcaster.fragment.PodcastFragment;
import com.qingwenwei.eslpodcaster.listener.OnEpisodeStatusChangeHandler;
import com.qingwenwei.eslpodcaster.listener.OnLoadPlayingEpisodeHandler;
import com.qingwenwei.eslpodcaster.util.AudioPlayer;
import com.qingwenwei.eslpodcaster.util.EpisodeDownloadManager;
import com.qingwenwei.eslpodcaster.util.ExtractorRendererBuilder;
import com.qingwenwei.eslpodcaster.util.PodcastEpisodeScriptParser;
import com.qingwenwei.eslpodcaster.util.RendererBuilder;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.github.skyhacker2.sqliteonweb.SQLiteOnWeb;

public class MainActivity extends AppCompatActivity
        implements OnEpisodeStatusChangeHandler, OnLoadPlayingEpisodeHandler{

    private static final String TAG = "MainActivity";

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private int[] tabIcons = {
            R.drawable.ic_rss_feed_white_36dp,
            R.drawable.ic_file_download_white_36dp,
            R.drawable.ic_favorite_white_36dp
    };

    //Sliding up panel panel_layout_sliding_up_player
    private SlidingUpPanelLayout slidingUpPanelLayout;

    //collapsed panel views
    private View collapsedPanel;
    private View slidingUpPanelPlayerView;

    private ImageButton collapsedPanelPlayButton;
    private ImageButton collapsedPanelMenuButton;
    private ImageView collapsedPanelPodcastIcon;

    private boolean scriptLoaded = false;

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

    //current playing episode
    private PodcastEpisode playingEpisode;

    //Popup option menu
    private PopupMenu optionPopupMenu;

    private Fragment podcastFragment;
    private Fragment downloadFragment;
    private Fragment favoriteFragment;

    //database
//    private SQLiteDatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize fragments
        podcastFragment = new PodcastFragment();
        downloadFragment = new DownloadFragment();
        favoriteFragment = new FavoriteFragment();

        ((FavoriteFragment)favoriteFragment).setOnEpisodeStatusChangeHandler(this);
        ((FavoriteFragment)favoriteFragment).setOnLoadPlayingEpisodeHandler(this);

        ((DownloadFragment)downloadFragment).setOnEpisodeStatusChangeHandler(this);
        ((DownloadFragment)downloadFragment).setOnLoadPlayingEpisodeHandler(this);

//        //initialize database utility
//        db = new SQLiteDatabaseManager(getApplicationContext());

        //orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //setup tabs
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
            setupTabIcons();
        }

        //setup sliding up panel
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
                    if(scriptLoaded) {
                        collapsedPanelMenuButton.setVisibility(View.VISIBLE);
                    }
                    collapsedPanelPodcastIcon.clearColorFilter();
                    collapsedPanelPodcastIcon.setImageResource(R.drawable.ic_keyboard_arrow_down_black_36dp);
                }else if(newState == PanelState.COLLAPSED){
                    collapsedPanelPlayButton.setVisibility(View.VISIBLE);
                    collapsedPanelMenuButton.setVisibility(View.INVISIBLE);
                    collapsedPanelPodcastIcon.clearColorFilter();
                    collapsedPanelPodcastIcon.setImageResource(R.drawable.ic_keyboard_arrow_up_black_36dp);
                }
            }
        });

        slidingUpPanelLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "OnClickListener() " + view.getContext().getPackageName());
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

        //collapsed panel views
        collapsedPanelTitleTextView = (TextView)findViewById(R.id.collapsedPanelTitleTextView);
        collapsedPanelPlayButton = (ImageButton)findViewById(R.id.collapsedPanelPlayButton);
        collapsedPanelMenuButton = (ImageButton)findViewById(R.id.collapsedPanelMenuButton);
        collapsedPanelPodcastIcon = (ImageView)findViewById(R.id.collapsedPanelPodcastIcon);

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


        //SQLite browser
        SQLiteOnWeb.init(this).start();
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
                if(player != null){
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
                Log.i(TAG,"slidingUpPanelPlayButton.OnClickListener()");
                if(player != null){
                    if (!player.isPlaying()) {
                        player.setPlayWhenReady(true);
                        slidingUpPanelSeekBar.postDelayed(onEverySecond, 1000);
                        updateSlidingUpPanelSeekBar();
                    } else {
                        player.setPlayWhenReady(false);
                    }
                }else{
                    Toast.makeText(MainActivity.this,"Please select an episode to play", Toast.LENGTH_SHORT).show();
                }
            }
        });

        slidingUpPanelReplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player != null){
                    long currPos = player.getCurrentPosition();
                    if(currPos - 10000 < 0){
                        player.seekTo(0);
                    }else{
                        player.seekTo(currPos - 10000);
                    }

                    if(!player.isPlaying()){
                        updateSlidingUpPanelSeekBar();
                    }
                }else{
                    Toast.makeText(MainActivity.this,"Please select an episode to play", Toast.LENGTH_SHORT).show();
                }
            }
        });

        slidingUpPanelForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player != null){
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
                }else{
                    Toast.makeText(MainActivity.this,"Please select an episode to play", Toast.LENGTH_SHORT).show();
                }
            }
        });

        collapsedPanelPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"collapsedPanelPlayButton.OnClickListener()");
                if(player != null){
                    if (!player.isPlaying()) {
                        player.setPlayWhenReady(true);
                        slidingUpPanelSeekBar.postDelayed(onEverySecond, 1000);
                        updateSlidingUpPanelSeekBar();
                    } else {
                        player.setPlayWhenReady(false);
                    }
                }else{
                    Toast.makeText(MainActivity.this,"Please select an episode to play", Toast.LENGTH_SHORT).show();
                }
            }
        });

        collapsedPanelMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"collapsedPanelMenuButton.setOnClickListener()");

                if(optionPopupMenu == null){
                    setupOptionPopupMenu(v);
                }
                optionPopupMenu.show();
            }
        });
    }

    //setup tab icons and their color
    private void setupTabIcons(){
        final int colorSelected = ContextCompat.getColor(getApplicationContext(), R.color.colorTabIconSelected);
        final int colorUnselected = ContextCompat.getColor(getApplicationContext(), R.color.colorTabIconUnSelected);

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);

        tabLayout.getTabAt(0).getIcon().setColorFilter(colorSelected, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(colorUnselected, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(colorUnselected, PorterDuff.Mode.SRC_IN);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(colorSelected, PorterDuff.Mode.SRC_IN);
                int tabPosition = tab.getPosition();
                viewPager.setCurrentItem(tabPosition);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(colorUnselected, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, ""+v);
            }
        });
    }

    //setup ViewPager with fragments and listeners
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(podcastFragment, "Podcast");
        adapter.addFragment(downloadFragment, "Download");
        adapter.addFragment(favoriteFragment, "Favorite");

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG,"onPageSelected()" + position);
                switch(position){
                    case 1:{
                        //refresh the download list
                        ((DownloadFragment)downloadFragment).refresh();
                        break;
                    }
                    case 2:{
                        //refresh the favorite list
                        ((FavoriteFragment)favoriteFragment).refresh();
                        break;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupOptionPopupMenu(View v){
        optionPopupMenu = new PopupMenu(getApplicationContext(),v);
        optionPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String title = item.getTitle().toString().toLowerCase();
                Log.i(TAG,"onMenuItemClick() " + title);

                switch (title){
                    case "download":{
                        if(playingEpisode != null) {
                            setEpisodeDownloaded(playingEpisode,true);
                        }
                        break;
                    }

                    case "add to favorites": {
                        if(playingEpisode != null) {
                            setEpisodeFavoured(playingEpisode,true);
                        }
                        break;
                    }
                }

                return true;
            }
        });

        MenuInflater inflater = optionPopupMenu.getMenuInflater();
        inflater.inflate(R.menu.option_popup_menu, optionPopupMenu.getMenu());
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

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
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout != null
                && (slidingUpPanelLayout.getPanelState() == PanelState.EXPANDED
                        || slidingUpPanelLayout.getPanelState() == PanelState.ANCHORED)) {
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
            new PodcastEpisodeScriptParser().getEpisodeScript(episode);
            return this.episode;
        }

        @Override
        protected void onPostExecute(final PodcastEpisode scriptedEpisode) {
//            Log.i(TAG,"onPostExecute()" + episode.getContent());

            //show episode scripts on the screen
            slidingUpPanelScriptTextView.setText(Html.fromHtml(episode.getContent()));
            collapsedPanelPlayButton.setVisibility(View.INVISIBLE);
            collapsedPanelMenuButton.setVisibility(View.VISIBLE);
            scriptLoaded = true;
        }
    }

    //player helper method
    @Override
    public void loadPlayingEpisode(PodcastEpisode episode){
        Log.i(TAG, "loadPlayingEpisode()" + episode.getTitle());

        //reset script loaded state
        scriptLoaded = false;

        //clear original scripts
        slidingUpPanelScriptTextView.setText("");
        slidingUpPanelScriptTextView.scrollTo(0,0);

        //download and update the script TextView
        new DownloadEpisodeScriptAsyncTask(episode).execute();

        //toggle sliding-up panel
        toggleSlidingUpPanel();

        //set play button background image to play image
        setSlidingUpPanelPlayButtonPlaying();

        //change the sliding-up panel episode title
        collapsedPanelTitleTextView.setText(episode.getTitle());

        //prepare audio play and update the script in the sliding-up panel
        preparePlayer(episode);

        //set currText and maxText 00:00
        slidingUpPanelCurrPosTextView.setText("00:00");
        slidingUpPanelMaxPosTextView.setText("00:00");

        //setup the current playing episode
        this.playingEpisode = episode;
    }

    public void setSlidingUpPanelPlayButtonPlaying(){
        slidingUpPanelPlayButton.setImageResource(R.drawable.ic_play_arrow_white_36dp);
        collapsedPanelPlayButton.setImageResource(R.drawable.ic_play_circle_outline_black_36dp);
    }

    public void setSlidingUpPanelPlayButtonPause(){
        slidingUpPanelPlayButton.setImageResource(R.drawable.ic_pause_white_36dp);
        collapsedPanelPlayButton.setImageResource(R.drawable.ic_pause_circle_outline_black_36dp);
    }

    //player helper method
    private void preparePlayer(PodcastEpisode episode){
        Log.i(TAG,"preparePlayer() URL: " + episode.getAudioFileUrl() + " LOCAL: " + episode.getLocalAudioFile());

        if(player != null){
            player.release();
            player = null;
        }

        String targetPath = null;

        if(episode.getLocalAudioFile() != "" && episode.getLocalAudioFile() != null){
            targetPath = episode.getLocalAudioFile();
        }else{
            targetPath = episode.getAudioFileUrl();
        }
        Log.i(TAG,"preparePlayer() targetPath: " + targetPath);

        RendererBuilder rendererBuilder = new ExtractorRendererBuilder(
                getBaseContext(),
                Constants.USER_AGENT,
                targetPath);
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
        int max = (int) player.getDuration();
        slidingUpPanelSeekBar.setMax(max);

        int duration = (int) player.getDuration();
        slidingUpPanelMaxPosTextView.setText(toMinuteFormat(duration));

        int currPos = (int) player.getCurrentPosition();
        slidingUpPanelCurrPosTextView.setText(toMinuteFormat(currPos));

        slidingUpPanelSeekBar.setProgress(currPos);
    }

    /*
    PodcastEpisodeOnStatusChangeListener override methods
     */
    @Override
    public void setEpisodeFavoured(PodcastEpisode episode, boolean favor) {
        String title = episode.getTitle();
        SQLiteDatabaseManager db = new SQLiteDatabaseManager(getApplicationContext());
        Context context = getApplicationContext();
        if(favor){
            db.addFavoriteEpisode(episode);
            Toast.makeText(context, "Favoured " + title, Toast.LENGTH_LONG).show();
        }else{
            db.deleteFavoriteEpisode(episode);
            Toast.makeText(context, "Disfavoured " + title, Toast.LENGTH_LONG).show();
        }
        db.close();

        ((FavoriteFragment)favoriteFragment).refresh();
    }

    @Override
    public void setEpisodeDownloaded(PodcastEpisode episode, boolean downloaded) {
        Context context = getApplicationContext();
        if(downloaded){
            Toast.makeText(context,"Downloading in the background......", Toast.LENGTH_LONG).show();
            new EpisodeDownloadManager(context).downloadEpisode(episode);
        }else{
            new EpisodeDownloadManager(context).deleteEpisode(episode);
            Toast.makeText(context,"Deleted " + episode.getTitle(), Toast.LENGTH_LONG).show();
        }

        ((DownloadFragment)downloadFragment).refresh();
    }
}

