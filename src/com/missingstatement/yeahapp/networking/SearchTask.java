package com.missingstatement.yeahapp.networking;

import android.os.AsyncTask;
import com.missingstatement.yeahapp.JaSearcher;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 9/23/12
 * Time: 8:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchTask extends AsyncTask<String, Void, ArrayList<HashMap<String, ArrayList<String>>>> {

    private SearchHandler mSearchHandler;
    JaSearcher mJaSearcher;

    public SearchTask(SearchHandler handler)
    {
        mSearchHandler = handler;
        mJaSearcher = new JaSearcher();
    }

    @Override
    protected ArrayList<HashMap<String, ArrayList<String>>> doInBackground(String... phoneNumbers) {
        String phoneNumber = phoneNumbers[0];

        return mJaSearcher.search(phoneNumber);
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, ArrayList<String>>> result) {
        mSearchHandler.handleSearchResponse(result);
    }
}
