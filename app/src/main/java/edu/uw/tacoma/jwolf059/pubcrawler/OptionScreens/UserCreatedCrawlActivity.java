package edu.uw.tacoma.jwolf059.pubcrawler.OptionScreens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import edu.uw.tacoma.jwolf059.pubcrawler.R;
import edu.uw.tacoma.jwolf059.pubcrawler.login.LoginActivity;
import edu.uw.tacoma.jwolf059.pubcrawler.map.PubCrawlMapActivity;
import edu.uw.tacoma.jwolf059.pubcrawler.model.Crawl;
import edu.uw.tacoma.jwolf059.pubcrawler.model.Pub;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class UserCreatedCrawlActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    /**
     * URl used to gather pub locaitons. The locaiton must be added to the end of the string
     */
    public static final String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    /**
     * Second half of the URL added after the location.
     */
    public static final String URL_2 = "&keyword=brewery&name=bar&type=pub&radius=10000&key=AIzaSyCEn4Fhg1PNkBk30X-tffOtNzTiPZCh58k";
    // List of Pubs
    private ArrayList<Pub> mPubList;

    private Spinner mPubListSpinnerOne;
    private Spinner mPubListSpinnerTwo;
    private Spinner mPubListSpinnerThree;
    private Spinner mPubListSpinnerFour;
    private Spinner mPubListSpinnerFive;
    private Spinner mPubListSpinnerSix;


    private Spinner mStopNumber;

    private Pub mStartPub;
    private Pub mSecondStop;
    private Pub mThirdStop;
    private Pub mFourthStop;
    private Pub mFifthStop;
    private Pub mSixthStop;

    private int mNumberOfStops;

    private Crawl mCrawl;

    private String mCrawlName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_created_crawl);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        PubSearchTask task = new PubSearchTask();
        mPubList = new ArrayList<>();
        Button button = (Button) findViewById(R.id.create_crawl);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameField = (EditText) findViewById(R.id.crawl_name);
                mCrawlName = nameField.toString();
                mCrawl = new Crawl(mCrawlName);

                //Add the user selected pubs
                if (mStartPub != null) {
                    mCrawl.addPub(mStartPub);
                }
                if (mSecondStop != null) {
                    mCrawl.addPub(mSecondStop);
                }
                if (mThirdStop != null) {
                    mCrawl.addPub(mThirdStop);
                }
                if (mFourthStop != null) {
                    mCrawl.addPub(mFourthStop);
                }
                if (mFifthStop != null) {
                    mCrawl.addPub(mFifthStop);
                }
                if (mSixthStop != null) {
                    mCrawl.addPub(mSixthStop);
                }


                Intent crawlMap = new Intent(getApplicationContext(), PubCrawlMapActivity.class);
                crawlMap.putExtra("object", mCrawl);
                startActivity(crawlMap);
            }
        });
        if (!searchCompleted()) {
            task.execute(new String[]{buildPubSearchURL()});
        } else {
            createUI();
        }


    }

    public boolean searchCompleted() {
        //Check to see if a search has already been done (Save data usage)
        return false;
    }

    /**
     * Create and fill the UI elements using data from Google Places.
     */
    public void createUI() {

        ArrayList<String> pubs = new ArrayList<>();
        pubs.add("Select a Pub");

        for (Pub p : mPubList) {
            pubs.add(p.getmName());
        }
        // Build the pub list spinners
        mPubListSpinnerOne = (Spinner) findViewById(R.id.pub_selector1);
        mPubListSpinnerOne.setOnItemSelectedListener(this);
        ArrayAdapter<String> pubDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pubs);
        pubDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPubListSpinnerOne.setAdapter(pubDataAdapter);


        mPubListSpinnerTwo = (Spinner) findViewById(R.id.pub_selector2);
        mPubListSpinnerTwo.setOnItemSelectedListener(this);
        ArrayAdapter<String> pubDataAdapterTwo = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pubs);
        pubDataAdapterTwo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPubListSpinnerTwo.setAdapter(pubDataAdapterTwo);

        mPubListSpinnerThree = (Spinner) findViewById(R.id.pub_selector3);
        mPubListSpinnerThree.setOnItemSelectedListener(this);
        ArrayAdapter<String> pubDataAdapterThree = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pubs);
        pubDataAdapterThree.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPubListSpinnerThree.setAdapter(pubDataAdapterThree);

        mPubListSpinnerFour = (Spinner) findViewById(R.id.pub_selector4);
        mPubListSpinnerFour.setOnItemSelectedListener(this);
        ArrayAdapter<String> pubDataAdapterFour = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pubs);
        pubDataAdapterFour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPubListSpinnerFour.setAdapter(pubDataAdapterFour);

        mPubListSpinnerFive = (Spinner) findViewById(R.id.pub_selector5);
        mPubListSpinnerFive.setOnItemSelectedListener(this);
        ArrayAdapter<String> pubDataAdapterFive = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pubs);
        pubDataAdapterFive.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPubListSpinnerFive.setAdapter(pubDataAdapterFive);

        mPubListSpinnerSix = (Spinner) findViewById(R.id.pub_selector6);
        mPubListSpinnerSix.setOnItemSelectedListener(this);
        ArrayAdapter<String> pubDataAdapterSix = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pubs);
        pubDataAdapterSix.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPubListSpinnerSix.setAdapter(pubDataAdapterSix);
    }

    /**
     * Gets the position of each selected item and places the corresponding pub into the class variable.
     * If the selection is 0 (default value) then set the pub variable to null.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            if (parent.getId() == (findViewById(R.id.pub_selector1).getId())) {
                if (position != 0) {
                    mStartPub = mPubList.get(position - 1);
                } else {
                    mStartPub = null;
                }

            } else if (parent.getId() == (findViewById(R.id.pub_selector2).getId())) {
                    if (position != 0) {
                        mSecondStop = mPubList.get(position - 1);
                    } else {
                        mSecondStop = null;
                    }

            } else if (parent.getId() == (findViewById(R.id.pub_selector3).getId())) {
                if (position != 0) {
                    mThirdStop = mPubList.get(position - 1);
                } else {
                    mThirdStop = null;
                }

            } else if (parent.getId() == (findViewById(R.id.pub_selector4).getId())) {
                if (position != 0) {
                    mFourthStop = mPubList.get(position - 1);
                } else {
                    mFourthStop = null;
                }

            } else if (parent.getId() == (findViewById(R.id.pub_selector5).getId())) {
                if (position != 0) {
                    mFifthStop = mPubList.get(position - 1);
                } else {
                    mFifthStop = null;
                }

            } else if (parent.getId() == (findViewById(R.id.pub_selector6).getId())) {
                if (position != 0) {
                    mSixthStop = mPubList.get(position - 1);
                } else {
                    mSixthStop = null;
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Creates the Pub Seach URL to be sent to the Google Place server that will return a JSON object
     * of all pubs within a 10 kilometer radius.
     *
     * @return String that contains the URL to include current locaiton.
     */
    public String buildPubSearchURL() {
        // Ken, Need to be able to get the Longitute and Latitude from a member variable.

        StringBuilder sb = new StringBuilder();
        sb.append(URL);
        //This will be the actaul device locaiton
        sb.append("47.253361,-122.439191");
        sb.append(URL_2);

        return sb.toString();
    }


    /**
     * Creates the PubSearchTask that executes the Pub Search.
     */
    private class PubSearchTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    java.net.URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to Login, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }


        /**
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         * If not, it displays the exception.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {

            Log.i("json result ", result);
//
//            try {
//                JSONObject jsonObject = new JSONObject(result);
//                JSONArray jArray = jsonObject.getJSONArray("results");
//                int len = jArray.length();

                mPubList = Pub.parsePubJSON(result);
                createUI();


//            } catch (JSONException e) {
//                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
//                        e.getMessage(), Toast.LENGTH_LONG).show();
//                Log.e("Wrong Data", e.getMessage());
//            }

        }
    }

    /**
     * If the Menu Item is selected Log the user out.
     * @param item the menu item selected
     * @return boolean if action was ttaken.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            SharedPreferences sharedPreferences =
                    getDefaultSharedPreferences(getApplicationContext());
            sharedPreferences.edit().putBoolean(getString(R.string.LOGGEDIN), false)
                    .commit();
            LoginManager.getInstance().logOut();

            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
            return true;
        } else {
            return false;
        }
    }

    /**{@inheritDoc}
     *
     * @param menu the menu to be created
     * @return boolean if menu was created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
}
