package nz.james.crosswordhelper;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.DocumentsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements ResultListFragment.OnFragmentInteractionListener, SearchFragment.OnFragmentInteractionListener, HelpFeedbackFragment.OnFragmentInteractionListener, BugReportFragment.OnFragmentInteractionListener {


    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment searchFragment = new SearchFragment();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, searchFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.itemHelpFeedback:
                HelpFeedbackFragment helpFeedbackFragment = new HelpFeedbackFragment();
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, helpFeedbackFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFragmentInteraction(String option) {
        switch (option){
            case "Report Bug":
                BugReportFragment bugReportFragment = new BugReportFragment();
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, bugReportFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
        }

    }


    @Override
    public void onFragmentInteraction(ArrayList<String> results) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("Results", results);

        Fragment resultListFragment = new ResultListFragment();
        resultListFragment.setArguments(bundle);
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, resultListFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
