package nz.james.crosswordhelperr;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

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
            case "Bug/Feature":
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
