package nz.james.crosswordhelperr;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.goebl.david.Webb;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BugReportFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BugReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BugReportFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private String priority = "LOW";
    private String type = "Bug";

    private Context context;

    private String apiKey = "d35c9d609f223f10342ada84752e789e";
    private String apiToken = "81c7f58f5a45d9ff9480e51f2c0abbea7b2b683ce3760b43a6accb085b5a9ce2";

    private ProgressDialog progressDialog;

    private Gson gson;

    public BugReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BugReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BugReportFragment newInstance(String param1, String param2) {
        BugReportFragment fragment = new BugReportFragment();
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bug_report, container, false);

        this.context = getActivity();
        this.gson = new Gson();

        final EditText editTextTitle = (EditText) view.findViewById(R.id.editTextSubject);
        final EditText editTextDescription = (EditText) view.findViewById(R.id.editTextDescription);
        RadioGroup radioGroupPriority = (RadioGroup) view.findViewById(R.id.radioGroupPriority);
        RadioGroup radioGroupType = (RadioGroup) view.findViewById(R.id.radioGroupType);

        radioGroupPriority.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButtonLow:
                        setPriority("LOW");
                        break;
                    case R.id.radioButtonMedium:
                        setPriority("MEDIUM");
                        break;
                    case R.id.radioButtonHigh:
                        setPriority("HIGH");
                        break;

                }
            }
        });

        radioGroupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.radioButtonBug:
                        setType("Bug");
                        break;
                    case R.id.radioButtonFeature:
                        setType("Feature");
                        break;
                }
            }
        });


        Button btnSubmit = (Button) view.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add card to automated Trello board
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Submitting...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();

                Ion.with(context)
                        .load("https://api.trello.com/1/boards/nWL0de1l/lists?key=" + apiKey + "&token=" + apiToken)
                        .as(new TypeToken<List[]>(){})
                        .setCallback(new FutureCallback<List[]>() {
                            @Override
                            public void onCompleted(Exception e, List[] lists) {
                                String listID = "";

                                for(List list : lists){
                                    if(list.getName().equals("To Do")){
                                        listID = list.getId();
                                        break;
                                    }
                                }

                                if(listID != null && !listID.equals("")){
                                    Card card = new Card();

                                    card.setName("[" + type + "] " + "[" + priority + "] " + editTextTitle.getText().toString());
                                    card.setDesc(editTextDescription.getText().toString());
                                    card.setIdList(listID);
                                    card.setPos("top");
                                    card.setDue(null);

                                    JsonObject jsonObject = new JsonObject();
                                    jsonObject.addProperty("name", card.getName());
                                    jsonObject.addProperty("desc", card.getDesc());
                                    jsonObject.addProperty("idList", card.getIdList());
                                    jsonObject.addProperty("pos", card.getPos());
                                    jsonObject.addProperty("due", card.getDue());

                                    Ion.with(context)
                                            .load("https://api.trello.com/1/cards?key=" + apiKey + "&token=" + apiToken)
                                            .setJsonObjectBody(jsonObject)
                                            .asString()
                                            .setCallback(new FutureCallback<String>() {
                                                @Override
                                                public void onCompleted(Exception e, String result) {
                                                    if(progressDialog != null && progressDialog.isShowing()){
                                                        progressDialog.dismiss();
                                                        progressDialog = null;
                                                    }

                                                    if(result.contains("error")){
                                                        Toast.makeText(getActivity(), "Failed to submit report/request", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        Toast.makeText(getActivity(), "Report/Request Submitted", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }


                            }
                        });
            }
        });




        return view;
    }

    private class TrelloAPITask extends AsyncTask<Void, Void, Void>{

        String url;
        Card card;
        ProgressDialog progressDialog;

        public TrelloAPITask(String url, Card card){
            this.url = url;
            this.card = card;
        }

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Submitting...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Webb webb = Webb.create();
            webb.post(url)
                    .param("name", card.getName())
                    .param("desc", card.getDesc())
                    .param("pos", card.getPos())
                    .param("due", card.getDue())
                    .param("idList", card.getIdList())
                    .ensureSuccess()
                    .asVoid();


            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if(progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    private void setPriority(String priority){
        this.priority = priority;
    }

    private void setType(String type){
        this.type = type;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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
        void onFragmentInteraction(Uri uri);
    }
}
