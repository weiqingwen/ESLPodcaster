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

    //Sliding up panel panel_layout_sliding_up_player
    private SlidingUpPanelLayout slidingUpPanelLayout;

    //collapsed panel views
    private View collapsedPanel;
    private View slidingUpPanelPlayerView;

    private ImageButton collapsedPanelPlayButton;
    private ImageButton collapsedPanelMenuButton;
    private ImageView collapsedPanelPodcastIcon;

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
//                        slidingUpPanelSeekBar.setMax((int) player.getDuration());
//                        slidingUpPanelMaxPosTextView.setText(toMinuteFormat(player.getDuration()));
                        updateSlidingUpPanelSeekBar();
                        slidingUpPanelSeekBar.postDelayed(onEverySecond, 1000);
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
//                        slidingUpPanelSeekBar.setMax((int) player.getDuration());
//                        slidingUpPanelMaxPosTextView.setText(toMinuteFormat(player.getDuration()));
                        updateSlidingUpPanelSeekBar();
                        slidingUpPanelSeekBar.postDelayed(onEverySecond, 1000);
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
                showOptionPopupMenu(v);
            }
        });
    }

    //setup tab icons and their color
    private void setupTabIcons(){
        final String colorSelected = "#edf4fa";
        final String colorUnselected = "#b7cbda";

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);

        tabLayout.getTabAt(0).getIcon().setColorFilter(Color.parseColor(colorSelected), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(Color.parseColor(colorUnselected), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(Color.parseColor(colorUnselected), PorterDuff.Mode.SRC_IN);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor(colorSelected), PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor(colorUnselected), PorterDuff.Mode.SRC_IN);
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
        int max = (int) player.getDuration();
        slidingUpPanelSeekBar.setMax(max);

        int duration = (int) player.getDuration();
        slidingUpPanelMaxPosTextView.setText(toMinuteFormat(duration));

        int currPos = (int) player.getCurrentPosition();
        slidingUpPanelCurrPosTextView.setText(toMinuteFormat(currPos));

        slidingUpPanelSeekBar.setProgress(currPos);
    }

    private void showOptionPopupMenu(View v){
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(),v);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.i(TAG,"You have clicked on: " + item.getTitle());
                return false;
            }
        });
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.option_popup_menu,popupMenu.getMenu());
        popupMenu.show();
    }

}

