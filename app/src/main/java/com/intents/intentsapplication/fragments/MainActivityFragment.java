package com.intents.intentsapplication.fragments;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.intents.intentsapplication.R;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


public class MainActivityFragment extends Fragment {
    @BindView(R.id.output_speech)
    TextView outputText;
    @BindView(R.id.push_to_talk)
    Button speechButton;
    @BindView(R.id.push_to_command)
    Button commandButton;
    @BindView(R.id.google_search_text)
    EditText googleSearchText;
    @BindView(R.id.push_to_contacts)
    Button contactsButton;
    @BindView(R.id.push_to_dialer)
    Button dialerButton;
    @BindView(R.id.url_text)
    EditText urlText;
    @BindView(R.id.push_to_url)
    Button urlButton;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String buttonVariable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_activity, container, false);

        ButterKnife.bind(this, rootView);

        googleSearchText.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_ENTER:
                            searchGoogle();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        return rootView;
    }

    @OnClick(R.id.push_to_talk)
    public void speak(){
        buttonVariable = "speak";
        prepareSpeechIntent();
    }

    @OnClick(R.id.push_to_command)
    public void command(){
        buttonVariable = "command";
        prepareSpeechIntent();
    }

    @OnClick(R.id.push_to_url)
    public void pushUrlButton(){
        browseUrl();
    }

    public void browseUrl(){
        try {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlText.getText().toString()));
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "No application can handle this request. Please install a webbrowser",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch(ParseException e) {
            Toast.makeText(getActivity(), "You need to give in an existing Url.",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @OnClick(R.id.push_to_dialer)
    public void pushDialer(){
        goToDialer();
    }

    public void goToDialer(){
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "No application can handle this request.",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @OnClick(R.id.push_to_contacts)
    public void pushContactsButton(){
        goToContacts();
    }

    public void goToContacts(){
        try {
            Intent i = new Intent();
            i.setComponent(new ComponentName("com.android.contacts", "com.android.contacts.DialtactsContactsEntryActivity"));
            i.setAction("android.intent.action.MAIN");
            i.addCategory("android.intent.category.LAUNCHER");
            i.addCategory("android.intent.category.DEFAULT");
            startActivity(i);
        } catch (ActivityNotFoundException e) {
        Toast.makeText(getActivity(), "No application can handle this request.",  Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }
    }

    public void searchGoogle(){

        try {
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            String searchWord = googleSearchText.getText().toString();
            intent.putExtra(SearchManager.QUERY, searchWord);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "No application can handle this request.",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    private void prepareSpeechIntent(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "nl-NL");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "text to show");
        PackageManager pm = getActivity().getPackageManager();
        ComponentName cn = intent.resolveActivity(pm);
        if (cn == null) {
            Uri marketUri =
                    Uri.parse("market://search?q=pname:com.intentsapplication.intents");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(marketUri);
            if (marketIntent.resolveActivity(pm) != null){
                startActivity(marketIntent);
            }
            else{
                Log.d(TAG, "Market client not available.");
            }
        }
        else {
            startActivity(intent);
        }
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
                        if(buttonVariable.equals("speak")) {
                            Random rn = new Random();
                            int i = rn.nextInt(4) + 1;
                            if (i == 1) {
                                outputText.setText("I can't speak dutch very well but I think you said: \"" + zin + "\".");
                            } else if (i == 2) {
                                outputText.setText("Did you just say: \"" + zin + "\" in dutch?");
                            } else if (i == 3) {
                                outputText.setText("\" " + zin.toUpperCase() + "!! \" ... Wow, you don't have to yell at me!?");
                            } else if (i == 4) {
                                outputText.setText("\" " + zin.toLowerCase().replaceAll("r", "l") + " \" ... I didn't know you were chinese.");
                            }
                        } else if(buttonVariable.equals("command")){
                            if(zin.equalsIgnoreCase("go to contacts")){
                                goToContacts();
                            } else if(zin.equalsIgnoreCase("go to dialer")){
                                goToDialer();
                            } else if(zin.equalsIgnoreCase("go to url")){
                                browseUrl();
                            } else if(zin.equalsIgnoreCase("search on google")){
                                searchGoogle();
                            } else {
                                outputText.setText("I didn't understand you, press the button again to retry it.");
                            }
                        }

                    }
                    break;
                }
            }
        }
    }
}
