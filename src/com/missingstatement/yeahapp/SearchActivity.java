package com.missingstatement.yeahapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.missingstatement.yeahapp.networking.SearchHandler;
import com.missingstatement.yeahapp.networking.SearchTask;
import com.missingstatement.yeahapp.utils.Keys;
import com.missingstatement.yeahapp.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchActivity extends Activity {
    private EditText mSearchQueryEdit;
    private ImageButton mBtnSearch;

    private ExpandableListView mResultList;
    private SearchResultExpendableAdapter mSearchResultAdapter;
    private ArrayList<HashMap<String, ArrayList<String>>> mSearchResult;
    private ProgressBar mResultListFooterProgress;

    private ArrayList<String> mUrlsInProgress;

    private JaSearcher mJaSearcher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        mUrlsInProgress = new ArrayList<String>();
        mJaSearcher = new JaSearcher();
        mSearchResult = new ArrayList<HashMap<String, ArrayList<String>>>();
        mSearchResultAdapter = new SearchResultExpendableAdapter(mSearchResult);

        initViews();
    }

    private void initViews() {
        mSearchQueryEdit = (EditText) findViewById(R.id.et_searchQuery);

        mBtnSearch = (ImageButton) findViewById(R.id.btn_search);
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                search();
            }
        });

        mResultList = (ExpandableListView) findViewById(R.id.list_search_results);
        //TODO Add empty view to listview?

        View footerView = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.result_list_footer_progress, null);
        mResultListFooterProgress = (ProgressBar) footerView.findViewById(R.id.list_next_progress);
        mResultList.addFooterView(footerView);
        mResultList.setAdapter(mSearchResultAdapter);
        mResultList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onScroll(AbsListView lw, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
                switch (lw.getId()) {
                    case R.id.list_search_results:
                        final int lastItem = firstVisibleItem + visibleItemCount;

                        if (lastItem != totalItemCount) {
                            return;
                        }

                        if (mJaSearcher == null) {
                            return;
                        }

                        if (mJaSearcher.hasNext()) {

                            String nextUrl = mJaSearcher.getNextUrl();
                            if (!mUrlsInProgress.contains(nextUrl)) {
                                //TODO only put nextUrl in mUrlsInProgress if we successfully get next results
                                mUrlsInProgress.add(nextUrl);
                                boolean isNextTask = true;
                                SearchTask nextSearchTask = new SearchTask(lw.getContext(), mJaSearcher,
                                        new NextSearchResultHandler(), isNextTask);
                                nextSearchTask.execute();
                            }
                        } else {
                            mResultListFooterProgress.setVisibility(View.GONE);
                        }
                }
            }
        });
    }

    private void search() {
        if (!Utils.isNetworkOn(this)) {
            Utils.showToast(this, getString(R.string.label_network_error));
            return;
        }

        String queryText = mSearchQueryEdit.getText().toString();
        if (TextUtils.isEmpty(queryText)) {
            Utils.showToast(this, getString(R.string.label_empty_query));
            return;
        }

        //Clear urls in progress as we are starting a new query
        mUrlsInProgress.clear();

        boolean isNextTask = false;
        SearchTask searchTask = new SearchTask(this, mJaSearcher, new SearchResultHandler(), isNextTask);
        searchTask.initProgressDialog(null, getString(R.string.label_progress_message));

        searchTask.execute(queryText);
    }

    private class SearchResultHandler implements SearchHandler {
        @Override
        public void handleSearchResponse(ArrayList<HashMap<String, ArrayList<String>>> results) {
            mSearchResult.clear();
            mSearchResult.addAll(results);
            mSearchResultAdapter.notifyDataSetChanged();

            mResultListFooterProgress.setVisibility(View.VISIBLE);
        }
    }

    private class NextSearchResultHandler implements SearchHandler {
        @Override
        public void handleSearchResponse(ArrayList<HashMap<String, ArrayList<String>>> results) {
            mSearchResult.addAll(results);
            mSearchResultAdapter.notifyDataSetChanged();
        }
    }


    private class SearchResultExpendableAdapter extends BaseExpandableListAdapter {
        private final int NUM_RESULT_OPTIONS = 3;
        private final int FIRST_ENTRY = 0;

        private final int POSITION_ACTION_CALL = 0;
        private final int POSITION_ACTION_SEND_SMS = 1;
        private final int POSITION_ACTION_SAVE_CONTACT = 2;

        private final int DIALOG_CALL = 0;
        private final int DIALOG_SMS = 1;
        private final int DIALOG_CONTACT = 2;


        private ArrayList<HashMap<String, ArrayList<String>>> searchResults;

        public SearchResultExpendableAdapter(ArrayList<HashMap<String, ArrayList<String>>> searchResults) {
            this.searchResults = searchResults;
        }

        @Override
        public int getGroupCount() {
            return searchResults.size();
        }

        @Override
        public int getChildrenCount(int position) {
            return NUM_RESULT_OPTIONS;
        }

        @Override
        public Object getGroup(int position) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Object getChild(int i, int i1) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public long getGroupId(int i) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public long getChildId(int i, int i1) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean hasStableIds() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View groupView = convertView;

            if (groupView == null) {
                groupView = View.inflate(SearchActivity.this, R.layout.search_result_item_group, null);

                GroupViewHolder groupViewHolder = new GroupViewHolder();
                groupViewHolder.nameView = (TextView) groupView.findViewById(R.id.txt_list_name);
                groupViewHolder.addressView = (TextView) groupView.findViewById(R.id.txt_list_address);
                groupViewHolder.phoneNumberView = (TextView) groupView.findViewById(R.id.txt_list_phoneNumber);
                groupViewHolder.indicatorView = (ImageView) groupView.findViewById(R.id.list_indicator);

                groupView.setTag(groupViewHolder);
            }

            HashMap<String, ArrayList<String>> result = searchResults.get(groupPosition);

            if (result == null) {
                return groupView;
            }

            ArrayList<String> phoneNumbers = result.get(Keys.KEY_PHONE_NUMBERS);
            ArrayList<String> names = result.get(Keys.KEY_NAMES);
            ArrayList<String> addresses = result.get(Keys.KEY_ADDRESSES);

            GroupViewHolder groupViewHolder = (GroupViewHolder) groupView.getTag();

            groupViewHolder.nameView.setText(names.get(FIRST_ENTRY));
            groupViewHolder.phoneNumberView.setText(phoneNumbers.get(FIRST_ENTRY));

            if (TextUtils.isEmpty(addresses.get(FIRST_ENTRY))) {
                groupViewHolder.addressView.setVisibility(View.GONE);
            } else {
                groupViewHolder.addressView.setVisibility(View.VISIBLE);
                groupViewHolder.addressView.setText(addresses.get(FIRST_ENTRY));
            }

            if (isExpanded) {
                groupView.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_bg_search_detail));
                groupViewHolder.indicatorView.setImageResource(R.drawable.list_search_indicator_expanded);
            } else {
                groupView.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                groupViewHolder.indicatorView.setImageResource(R.drawable.list_search_indicator);
            }

            return groupView;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View childView = convertView;

            if (childView == null) {
                childView = View.inflate(SearchActivity.this, R.layout.search_result_item_child, null);

                ChildViewHolder childViewHolder = new ChildViewHolder();
                childViewHolder.iconView = (ImageView) childView.findViewById(R.id.list_icon);
                childViewHolder.actionView = (Button) childView.findViewById(R.id.txt_list_action);

                childView.setTag(childViewHolder);
            }

            ChildViewHolder childViewHolder = (ChildViewHolder) childView.getTag();

            HashMap<String, ArrayList<String>> result = searchResults.get(groupPosition);

            if (result == null) {
                return childView;
            }

            final ArrayList<String> phoneNumbers = result.get(Keys.KEY_PHONE_NUMBERS);
            final ArrayList<String> names = result.get(Keys.KEY_NAMES);
            final ArrayList<String> addresses = result.get(Keys.KEY_ADDRESSES);

            final String[] phoneNumbersArray = phoneNumbers.toArray(new String[phoneNumbers.size()]);

            //TODO directly call or send sms if person has only 1 number

            //Handle on click depending on action pressed
            childView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    switch (childPosition) {
                        case POSITION_ACTION_CALL:

                            if (phoneNumbers.size() == 1) {
                                callNumber(phoneNumbers.get(FIRST_ENTRY));
                            } else {
                                builder.setTitle(getString(R.string.label_select_number_to_call));
                                builder.setItems(phoneNumbersArray, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int position) {
                                        callNumber(phoneNumbers.get(position));
                                    }
                                });
                                builder.create().show();
                            }
                            break;
                        case POSITION_ACTION_SEND_SMS:

                            if (phoneNumbers.size() == 1) {
                                sendSms(phoneNumbers.get(FIRST_ENTRY));
                            } else {
                                builder.setTitle(getString(R.string.label_select_number_to_sms));
                                builder.setItems(phoneNumbersArray, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int position) {
                                        sendSms(phoneNumbers.get(position));
                                    }
                                });
                                builder.create().show();
                            }
                            break;
                        case POSITION_ACTION_SAVE_CONTACT:
                            Utils.showToast(view.getContext(), "Ekki virkt ennþá");
                    }

                }
            });

            //Setting the view to the correct one, depending on position
            switch (childPosition) {
                case POSITION_ACTION_CALL:
                    childViewHolder.iconView.setImageResource(R.drawable.list_icon_phone);
                    childViewHolder.actionView.setText(getString(R.string.action_call));
                    break;
                case POSITION_ACTION_SEND_SMS:
                    childViewHolder.iconView.setImageResource(R.drawable.list_icon_sms);
                    childViewHolder.actionView.setText(getString(R.string.action_sms));
                    break;
                case POSITION_ACTION_SAVE_CONTACT:
                    childViewHolder.iconView.setImageResource(R.drawable.list_icon_contact);
                    childViewHolder.actionView.setText(getString(R.string.action_contact));
                    break;
            }

            return childView;  //To change body of implemented methods use File | Settings | File Templates.
        }

        private void callNumber(String phoneNumber) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        }

        private void sendSms(String phoneNumber) {
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setData(Uri.parse("sms:" + phoneNumber));
            startActivity(smsIntent);
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;  //To change body of implemented methods use File | Settings | File Templates.
        }

        private class GroupViewHolder {
            TextView nameView;
            TextView phoneNumberView;
            TextView addressView;
            ImageView indicatorView;
        }

        private class ChildViewHolder {
            ImageView iconView;
            Button actionView;
        }
    }
}