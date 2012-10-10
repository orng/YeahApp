package com.missingstatement.yeahapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.missingstatement.yeahapp.networking.SearchHandler;
import com.missingstatement.yeahapp.networking.SearchTask;
import com.missingstatement.yeahapp.utils.Keys;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchActivity extends Activity {

	private EditText mSearchQueryEdit;
	private Button mBtnSearch;

    private ListView mResultList;

    private SearchResultAdapter mSearchResultAdapter;
    private ArrayList<HashMap<String, ArrayList<String>>> mSearchResult;

    private JaSearcher mJaSearcher;

    private ArrayList<String> mUrlsInProgress;

    private ProgressBar mResultListFooterProgress;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        mUrlsInProgress = new ArrayList<String>();
        mJaSearcher = new JaSearcher();
        mSearchResult = new ArrayList<HashMap<String, ArrayList<String>>>();
        mSearchResultAdapter = new SearchResultAdapter(this, mSearchResult);

        initViews();
    }

    private void initViews()
    {
        mSearchQueryEdit = (EditText) findViewById(R.id.et_searchQuery);

        mBtnSearch = (Button) findViewById(R.id.btn_search);
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                search();
            }
        });

        mResultList = (ListView) findViewById(R.id.result_list);
        //TODO Add empty view to listview

        View footerView = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.result_list_footer_progress, null);
        mResultListFooterProgress = (ProgressBar) footerView.findViewById(R.id.list_next_progress);
        mResultList.addFooterView(footerView);
        mResultList.setAdapter(mSearchResultAdapter);
        mResultList.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i)
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onScroll(AbsListView lw, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount)
            {
                switch(lw.getId())
                {
                    case R.id.result_list:
                        final int lastItem = firstVisibleItem + visibleItemCount;
                        if(lastItem == totalItemCount)
                        {
                            Log.e("Search", "count: " + totalItemCount);
                            if(mJaSearcher == null)
                            {
                                return;
                            }

                            if(mJaSearcher.hasNext())
                            {
                                //TODO check if we are already executing getNextUrl and if not we execute, else we do nothing

                                String nextUrl = mJaSearcher.getNextUrl();
                                if( !mUrlsInProgress.contains(nextUrl) )
                                {
                                    mUrlsInProgress.add(nextUrl);
                                    SearchTask nextSearchTask = new SearchTask(lw.getContext(), mJaSearcher, new NextSearchResultHandler());
                                    nextSearchTask.execute(nextUrl);
                                }
                            }
                            else
                            {
                                mResultListFooterProgress.setVisibility(View.GONE);
                            }
                        }
                }
            }
        });
    }

    private void search()
    {
        String queryText = mSearchQueryEdit.getText().toString();
        if(TextUtils.isEmpty(queryText))
        {
            Toast.makeText(this, "Vinsamlegast sladu inn leitarstreng.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        mUrlsInProgress.clear();

        SearchTask searchTask = new SearchTask(this, mJaSearcher, new SearchResultHandler());
        searchTask.initProgressDialog("ALLIR SLAKIR", "ROLEGUR ER AD FINNA THETTA FYRIR THIG");
        String searchUrl = mJaSearcher.getQueryUrl(queryText);
        searchTask.execute(searchUrl);
    }

    private class SearchResultHandler implements SearchHandler
    {
        @Override
        public void handleSearchResponse(ArrayList<HashMap<String, ArrayList<String>>> results) {
            mSearchResult.clear();
            mSearchResult.addAll(results);
            mSearchResultAdapter.notifyDataSetChanged();

            mResultListFooterProgress.setVisibility(View.VISIBLE);
        }
    }

    private class NextSearchResultHandler implements SearchHandler
    {
        @Override
        public void handleSearchResponse(ArrayList<HashMap<String, ArrayList<String>>> results)
        {
            mSearchResult.addAll(results);
            mSearchResultAdapter.notifyDataSetChanged();
        }
    }

    private class SearchResultAdapter extends ArrayAdapter<HashMap<String, ArrayList<String>>>
    {
        private final int FIRST_ENTRY = 0;

        private Context mContext;
        private ArrayList<HashMap<String, ArrayList<String>>> mSearchResults;

        public SearchResultAdapter(Context context, ArrayList<HashMap<String, ArrayList<String>>> searchResults)
        {
            super(context, R.layout.search_result_item, searchResults);

            mContext = context;
            mSearchResults  = searchResults;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
           View rowView = convertView;

            if(rowView == null)
            {
                rowView = View.inflate(mContext, R.layout.search_result_item, null);

                ViewHolder viewHolder = new ViewHolder();
                viewHolder.nameView = (TextView) rowView.findViewById(R.id.txt_list_name);
                viewHolder.phoneNumberView = (TextView) rowView.findViewById(R.id.txt_list_phoneNumber);
                viewHolder.addressView = (TextView) rowView.findViewById(R.id.txt_list_address);

                rowView.setTag(viewHolder);
            }

            HashMap<String, ArrayList<String>> result = mSearchResults.get(position);

            if(result == null) {
                return rowView;
            }

            ArrayList<String> phoneNumbers = result.get(Keys.KEY_PHONE_NUMBERS);
            ArrayList<String> names = result.get(Keys.KEY_NAMES);
            ArrayList<String> addresses = result.get(Keys.KEY_ADDRESSES);

            ViewHolder viewHolder = (ViewHolder) rowView.getTag();

            viewHolder.nameView.setText(names.get(FIRST_ENTRY));
            viewHolder.phoneNumberView.setText(phoneNumbers.get(FIRST_ENTRY));

            if(TextUtils.isEmpty(addresses.get(FIRST_ENTRY)))
            {
                viewHolder.addressView.setVisibility(View.GONE);
            }
            else
            {
                viewHolder.addressView.setVisibility(View.VISIBLE);
                viewHolder.addressView.setText(addresses.get(FIRST_ENTRY));
            }

            return rowView;
        }

        private class ViewHolder
        {
            TextView nameView;
            TextView phoneNumberView;
            TextView addressView;
        }
    }
}
