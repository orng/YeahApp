package com.missingstatement.yeahapp.networking;

import android.app.ProgressDialog;
import android.content.Context;
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
public class SearchTask extends AsyncTask<String, Void, ArrayList<HashMap<String, ArrayList<String>>>>
{
    private SearchHandler mSearchHandler;
    private JaSearcher mJaSearcher;

    private Context mContext;
    private boolean mIsNext;
    private ProgressDialog mProgressDialog;

    /**
     * Creates a new Search task for searching on mja.is. A {@link ProgressDialog} is only shown
     * if {@link #initProgressDialog(String, String)} has been called
     * @param context the context in which the task is created in
     * @param handler the handler to handle the results
     * @param isNext set to true only and only if we are querying more results
     */
    public SearchTask(Context context, SearchHandler handler, boolean isNext)
    {
        this(context, new JaSearcher(), handler, isNext);
    }

    public SearchTask(Context context, JaSearcher jaSearcher, SearchHandler handler, boolean isNext)
    {
        mContext = context;
        mSearchHandler = handler;
        mJaSearcher = jaSearcher;
        mIsNext = isNext;
    }

    /**
     * Initalized a {@link ProgressDialog}, to display to the user when searching
     * @param title title of the dialog
     * @param message message of the dialog
     */
    public void initProgressDialog(String title, String message)
    {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
    }

    @Override
    protected void onPreExecute()
    {
        if( mProgressDialog !=null && !mProgressDialog.isShowing() )
        {
            mProgressDialog.show();
        }
    }

    @Override
    protected ArrayList<HashMap<String, ArrayList<String>>> doInBackground(String... queries)
    {
        if(mIsNext)
        {
            return mJaSearcher.getNext();
        }
        else
        {
            return mJaSearcher.search(queries[0]);
        }
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, ArrayList<String>>> result)
    {
        mSearchHandler.handleSearchResponse(result);

        dismissDialog();
    }
    @Override
    public void onCancelled()
    {
        dismissDialog();
    }

    private void dismissDialog()
    {
        if(mProgressDialog!=null && mProgressDialog.isShowing())
        {
            mProgressDialog.dismiss();
        }
    }
}
