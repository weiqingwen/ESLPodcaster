package com.qingwenwei.eslpodcaster.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.qingwenwei.eslpodcaster.R;
import com.qingwenwei.eslpodcaster.constant.Constants;
import com.qingwenwei.eslpodcaster.event.OnLoadWordDefinitionEvent;
import com.qingwenwei.eslpodcaster.util.MerriamWebsterDictLookUpAsyncTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class DictionaryActivity extends AppCompatActivity {
    public static String TAG = "DictionaryActivity";

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.dictionaryToolBar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if(ab != null){
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        }

        Intent intent = getIntent();
        String word = (String) intent.getExtras().get(Constants.MESSAGE_START_DICTIONARY_ACTIVITY);
        new MerriamWebsterDictLookUpAsyncTask(
                Constants.MERRIAM_WEBSTER_DICTIONARY_URL,
                Constants.MERRIAM_WEBSTER_DICTIONARY_LEARNER_KEY
        ).parse(word);
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

    @Subscribe
    public void loadWord(OnLoadWordDefinitionEvent event){
        Log.i(TAG, event.word.toString());
    }

}
