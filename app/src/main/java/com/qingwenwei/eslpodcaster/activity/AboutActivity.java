package com.qingwenwei.eslpodcaster.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import com.qingwenwei.eslpodcaster.R;

public class AboutActivity extends AppCompatActivity {
    public static final String TAG = "AboutActivity";

    private TextView authorTextView;
    private TextView libraryTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.aboutActivityToolBar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if(ab != null){
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
            ab.setTitle("About");
        }

        authorTextView = (TextView) findViewById(R.id.aboutActivityAuthorTextView);
        libraryTextView = (TextView) findViewById(R.id.aboutActivityLibraryTextView);
        setUpInfo();
    }

    private void setUpInfo(){
        String versionName = "";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String authorInfo = "ESLPodcaster\n" +
                "Version:" +
                versionName + "\n" +
                "Created by Qingwen Wei\n" +
                "Copyright ® 2016\n" +
                "Licensed under the MIT License";
        authorTextView.setText(authorInfo);

        String usedLibrariesInfoHtml = "<h3>Used Libraries</h3>" +
                "<b>ExoPlayer</b><br><br>" +
                "<b>EventBus</b><br><br>" +
                "<b>Jsoup</b><br><br>" +
                "<b>Stetho</b><br><br>" +
                "<b>Material Design Icons</b><br><br>" +
                "<b>AndroidSlidingUpPanel</b><br><br>" +
                "<b>Merriam-Webster Dictionary API </b><br><br>";
        libraryTextView.setText(Html.fromHtml(usedLibrariesInfoHtml));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
