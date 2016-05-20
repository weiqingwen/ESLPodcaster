package com.qingwenwei.eslpodcaster.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qingwenwei.eslpodcaster.R;
import com.qingwenwei.eslpodcaster.constant.Constants;
import com.qingwenwei.eslpodcaster.event.OnLoadWordDefinitionEvent;
import com.qingwenwei.eslpodcaster.util.MerriamWebsterDictLookUpAsyncTask;
import com.qingwenwei.eslpodcaster.util.WordHtmlUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DictionaryActivity extends AppCompatActivity {
    public static String TAG = "DictionaryActivity";

    private TextView dictionaryTextView;
    private TextView dictionaryIndicatorTextView;
    private ProgressBar dictionaryProgressBar;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        Intent intent = getIntent();
        String word = (String) intent.getExtras().get(Constants.MESSAGE_START_DICTIONARY_ACTIVITY);

        Toolbar toolbar = (Toolbar) findViewById(R.id.dictionaryToolBar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if(ab != null){
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
            ab.setTitle("Dictionary");
        }

        dictionaryProgressBar = (ProgressBar) findViewById(R.id.dictionaryProgressBar);
        dictionaryTextView = (TextView) findViewById(R.id.dictionaryTextView);
        dictionaryIndicatorTextView = (TextView) findViewById(R.id.dictionaryIndicatorTextView);

        lookUpWord(word);
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

    private void lookUpWord(String word){
        dictionaryIndicatorTextView.setText("Looking up \"" + word + "\"");

        new MerriamWebsterDictLookUpAsyncTask(
                Constants.MERRIAM_WEBSTER_DICTIONARY_URL,
                Constants.MERRIAM_WEBSTER_DICTIONARY_LEARNER_KEY
        ).parse(word);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadWordDefinition(OnLoadWordDefinitionEvent event){
        if(event.message.equalsIgnoreCase(OnLoadWordDefinitionEvent.MSG_SUCCESS)) {
            Log.i(TAG, event.word.toString());
            String html = WordHtmlUtil.constructHtml(event.word);
            dictionaryTextView.setText(Html.fromHtml(html));
            dictionaryProgressBar.setVisibility(View.INVISIBLE);
            dictionaryIndicatorTextView.setVisibility(View.INVISIBLE);
        }else{
            dictionaryIndicatorTextView.setText("Failed to look up the word");
            dictionaryProgressBar.setVisibility(View.INVISIBLE);
        }
    }



}
