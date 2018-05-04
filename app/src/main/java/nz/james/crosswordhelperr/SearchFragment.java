package nz.james.crosswordhelperr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends android.support.v4.app.Fragment implements DatamuseAPIResultsListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ArrayList<String> synonyms = new ArrayList<>();

    private SeekBar seekBarLength;

    private TextView textViewLength;

    private EditText editTextWord;
    private EditText editTextPrefix;
    private EditText editTextSuffix;
    private EditText editTextContains;


    private ProgressDialog progressDialog;

    private Context context;
    private DatamuseAPIResultsListener datamuseAPIResultsListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        seekBarLength = (SeekBar) view.findViewById(R.id.seekBarLength);

        textViewLength = (TextView) view.findViewById(R.id.textViewMinLength);

        editTextWord = (EditText) view.findViewById(R.id.editTextWord);
        editTextPrefix = (EditText) view.findViewById(R.id.editTextPrefix);
        editTextSuffix = (EditText) view.findViewById(R.id.editTextSuffix);
        editTextContains = (EditText) view.findViewById(R.id.editTextContains);

        Button btnSearch = (Button) view.findViewById(R.id.btnSearch);

        this.context = getActivity();
        this.datamuseAPIResultsListener = this;

        seekBarLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewLength.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synonyms.clear();
                String wordToSearch = editTextWord.getText().toString();
                int wordLength = seekBarLength.getProgress();
                String prefix = editTextPrefix.getText().toString();
                String suffix = editTextSuffix.getText().toString();
                String contains = editTextContains.getText().toString();
                GetSynonymsTask getSynonymsTask = new GetSynonymsTask(wordToSearch, wordLength, prefix, suffix, contains);
                getSynonymsTask.execute();
            }

        });


        return view;
    }

    @Override
    public void onSynonymResults(ArrayList<String> synonyms) {
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog = null;
        }

        if(synonyms.size() > 0){
            mListener.onFragmentInteraction(synonyms);
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "No Results Found", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private class GetSynonymsTask extends AsyncTask<Void, Void, Void> {

        private String wordToSearch;
        private int wordLength;
        private String prefix;
        private String suffix;
        private String contains;

        public GetSynonymsTask(String wordToSearch, int wordLength, String prefix, String suffix, String contains){
            this.wordToSearch = wordToSearch;
            this.wordLength = wordLength;
            this.prefix = prefix;
            this.suffix = suffix;
            this.contains = contains;
        }

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Searching...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String searchUrl;
            //try {
                if(wordToSearch.contains(" ")){
                    wordToSearch = wordToSearch.toLowerCase().replaceAll(" ", "%20");
                }

                searchUrl = "https://api.datamuse.com/words?rel_syn=" + wordToSearch;

                Ion.with(context)
                        .load(searchUrl)
                        .as(new TypeToken<ArrayList<Synonym>>() {
                        })
                        .setCallback(new FutureCallback<ArrayList<Synonym>>() {
                            @Override
                            public void onCompleted(Exception e, ArrayList<Synonym> synonymList) {
                                ArrayList<String> temp = new ArrayList<>();

                                for(Synonym synonym : synonymList){
                                    if(synonym.getWord().contains(contains) && synonym.getWord().startsWith(prefix) && synonym.getWord().endsWith(suffix)){
                                        if(wordLength != 0){
                                            if(synonym.getWord().length() == wordLength){
                                                temp.add(synonym.getWord());
                                            }
                                        } else {
                                            temp.add(synonym.getWord());
                                        }
                                    }
                                }

                                synonyms = temp;

                                datamuseAPIResultsListener.onSynonymResults(synonyms);
                            }
                        });

//                URL url = new URL("http://www.thesaurus.com/browse/" + wordToSearch);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 5.1.1; Vodafone Smart ultra 6"
//                        + " Build/LMY47V) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.91"
//                        + " Mobile Safari/537.36");
//
//                if(connection.getResponseCode() == 404){
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getContext(), "I could not find that word", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } else {
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                    String line;
//                    ArrayList<String> entries = new ArrayList<>();
//
//                    while((line = bufferedReader.readLine()) != null){
//                        System.out.println(line);
//                        if(line.contains("class=\"result synstart\"")){
//                            String[] list = line.split("<b>Synonyms:</b>");
//
//
//                            for(int i = 0; i < list.length; i++){
//                                if(i > 0){
//                                    String entry = list[i];
//                                    String newEntry = entry.trim().substring(0, entry.indexOf("</div>") - 1);
//                                    entries.add(newEntry);
//                                }
//                            }
//                        }
//                    }
//
//                    for(String entry : entries){
//                        String[] synonymList = entry.split(", ");
//
//                        for(String synonym : synonymList){
//                            synonyms.add(synonym);
//                        }
//                    }
//
//                    // Remove any potential duplicate entries from ArrayList
//                    Set<String> hashSet = new HashSet<>();
//                    hashSet.addAll(synonyms);
//                    synonyms.clear();
//                    synonyms.addAll(hashSet);
//                    hashSet = null;
//
//
//

//
//
                //}
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){


        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(ArrayList<String> results);
    }
}
