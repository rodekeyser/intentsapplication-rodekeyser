package com.intents.intentsapplication.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.intents.intentsapplication.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


public class MainActivityFragment extends Fragment {
    @BindView(R.id.output_speech)
    TextView outputText;
    @BindView(R.id.push_to_talk)
    Button speechButton;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_activity, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @OnClick(R.id.push_to_talk)
    public void speak(){
        prepareSpeechIntent();
    }

    private void prepareSpeechIntent(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "nl-NL");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "text to show");

        try
        {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a)
        {
            Toast.makeText(getActivity(), getString(R.string.not_supported), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
        {

        } else
        {
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode)
            {
                case REQ_CODE_SPEECH_INPUT:
                {
                    if (resultCode == RESULT_OK && null != data)
                    {
                        ArrayList<String> resultSTT = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                            String zin = resultSTT.get(0);
                            outputText.setText(zin);

                    }
                    break;
                }
            }
        }
    }
}
