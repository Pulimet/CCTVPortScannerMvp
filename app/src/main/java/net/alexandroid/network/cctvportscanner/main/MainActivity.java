package net.alexandroid.network.cctvportscanner.main;

import android.app.SearchManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import net.alexandroid.network.cctvportscanner.R;
import net.alexandroid.network.cctvportscanner.db.AppDatabase;
import net.alexandroid.network.cctvportscanner.db.Btn;
import net.alexandroid.network.cctvportscanner.db.Host;
import net.alexandroid.network.cctvportscanner.main.adapter.SuggestionsAdapter;
import net.alexandroid.network.cctvportscanner.scan.PortScanFinishEvent;
import net.alexandroid.network.cctvportscanner.scan.PortScanRunnable;
import net.alexandroid.network.cctvportscanner.utils.Utils;
import net.alexandroid.utils.mylog.MyLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

// TODO Check about CIDR notation.
public class MainActivity extends AppCompatActivity implements
        MainMvp.RequiredViewOps,
        View.OnClickListener {

    private MainMvp.PresenterOps mPresenter;
    private SearchView mSearchView;
    private TextView mTvPingStatus;
    private ProgressBar mProgressBarPing;
    private ImageView mBtnRePing;
    private String mHost;
    private AdView mAdView;
    private TextInputLayout mInputLayoutPort;
    private EditText mInputPort;
    private TextView mTvResult;
    private ProgressBar mProgressBarScan;
    private String mTempHost;
    private SuggestionsAdapter mSuggestionsAdapter;
    private List<String> mTempSuggestionsList = new ArrayList<>();
    private SearchView.SearchAutoComplete mSearchAutoComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        attachPresenter();
        mPresenter.onGetButtons(this);

        setViews();
        setBanner();


    }

    private void attachPresenter() {
        mPresenter = (MainMvp.PresenterOps) getLastCustomNonConfigurationInstance();
        if (mPresenter == null) {
            mPresenter = new MainPresenter();
        }
        mPresenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        AppDatabase.destroyInstance();
        super.onDestroy();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mPresenter;
    }

    private void setViews() {
        mTvPingStatus = findViewById(R.id.tvPingStatus);
        mProgressBarPing = findViewById(R.id.pingProgressBar);
        mProgressBarScan = findViewById(R.id.scanProgressBar);
        mBtnRePing = findViewById(R.id.btnRePing);
        mAdView = findViewById(R.id.adView);
        mInputLayoutPort = findViewById(R.id.input_layout_port);
        mInputPort = findViewById(R.id.input_port);
        mTvResult = findViewById(R.id.tvResult);

        mBtnRePing.setOnClickListener(this);
        findViewById(R.id.btnCheck).setOnClickListener(this);

        mInputPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence pCharSequence, int pI, int pI1, int pI2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int pI, int pI1, int pI2) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mInputLayoutPort.setErrorEnabled(false);
                }
            }
        });
    }

    private void setBanner() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("0694A3AE02A5874D3DB2202B251F156A").build();
        mAdView.loadAd(adRequest);
    }

    private ArrayList<Integer> isPortValid() {
        String inputText = mInputPort.getText().toString().trim();

        ArrayList<Integer> list = Utils.convertStringToIntegerList(inputText.trim());

        if (list.size() == 0) {
            mInputLayoutPort.setError(getString(R.string.enter_custom_ports));
            mInputPort.setText("");
            requestFocus(mInputPort);
            return null;
        }

        String checkedPorts = Utils.convertIntegerListToString(list);
        mInputPort.setText(checkedPorts);

        mInputLayoutPort.setErrorEnabled(false);
        return list;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRePing:
                onBtnRePingClick();
                break;
            case R.id.btnCheck:
                onBtnCheckClick();
                break;
            case R.id.tvHost:
                onHostSelected(v);
                break;
            case R.id.btnClear:
                onHostRemoveClick(v);
                break;
        }
    }

    private void onHostRemoveClick(View v) {
        String host = (String) v.getTag();
        MyLog.d("Click on btnClear, host: " + host);
        removeSuggestionFromDb(host);
    }

    private void onHostSelected(View v) {
        String host = (String) v.getTag();
        MyLog.d("Click on tvHost, host: " + host);
        mSearchView.onActionViewCollapsed();
        onHostSubmit(new Host(host));
    }

    private void onBtnRePingClick() {
        mPresenter.onPingBtn(mHost);
    }

    private void onBtnCheckClick() {
        ArrayList<Integer> list = isPortValid();
        if (list != null && mHost != null) {
            mTvResult.setText("");
            mPresenter.onCheckBtn(mHost, list);
        }
    }


    // Set SearchView
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        setSearchView(menu);
        mPresenter.onGetSuggestions(this);
        return true;
    }

    private void setSearchView(Menu menu) {
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setQueryHint(getString(R.string.hint_search_view));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchView.onActionViewCollapsed();
                onHostSubmit(new Host(query));
                return true;
                // return true if the query has been handled by the listener, false to let the SearchView perform the default action.
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                MyLog.d("newText: " + newText);
                mTempHost = newText;
                return false;
            }
        });
        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    MyLog.d("mTempHost: " + mTempHost);
                    if (mTempHost != null && mTempHost.length() > 3) {
                        onHostSubmit(new Host(mTempHost));
                    }
                    mSearchView.onActionViewCollapsed();
                }
            }
        });

        setSearchAutoComplete();
    }

    private void setSearchAutoComplete() {
        mSearchAutoComplete = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);

        if (searchManager != null) {
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        mSuggestionsAdapter = new SuggestionsAdapter(getApplicationContext(), mTempSuggestionsList);
        mSearchAutoComplete.setAdapter(mSuggestionsAdapter);
        mSuggestionsAdapter.setOnClickListener(this);
    }

    private void onHostSubmit(Host pHost) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(String.format(getString(R.string.host), pHost.getHost()));
        }
        mPresenter.onHostSubmit(pHost);
        mHost = pHost.getHost();
    }

    // Suggestions control
    private void setSuggestionsAdapter(List<Host> pSuggestionsList) {
        List<String> suggesetionsList = convertoToListOfStrings(pSuggestionsList);

        if (mSuggestionsAdapter.getCount() == 0) {
            mSuggestionsAdapter.addAll(suggesetionsList);
        } else {
            if (pSuggestionsList.size() > mTempSuggestionsList.size()) {
                addSuggestionToAdapter(suggesetionsList, mTempSuggestionsList);
            } else {
                removeSuggestionFromAdapter(suggesetionsList, mTempSuggestionsList);
            }
        }

        mTempSuggestionsList = suggesetionsList;
    }

    @NonNull
    private ArrayList<String> convertoToListOfStrings(List<Host> pSuggestionsList) {
        ArrayList<String> newList = new ArrayList<>();
        for (Host host : pSuggestionsList) {
            newList.add(host.getHost());
        }
        return newList;
    }

    private void removeSuggestionFromAdapter(List<String> newList, List<String> oldList) {
        for (String host : oldList) {
            if (!newList.contains(host)) {
                mSuggestionsAdapter.remove(host);
                // Workaround to update the list
                mSearchAutoComplete.setText(mSearchAutoComplete.getText());
                break;
            }
        }
    }

    private void addSuggestionToAdapter(List<String> newList, List<String> oldList) {
        for (String host : newList) {
            if (!oldList.contains(host)) {
                mSuggestionsAdapter.add(host);
                break;
            }
        }
    }

    private void removeSuggestionFromDb(String pHost) {
        mPresenter.onRemoveSuggestionFromDb(new Host(pHost));
    }

    // Btns control
    private void updateBtnsIfNeed(List<Btn> pBtns) {
        // TODO Show buttons
    }



    /**
     * MainMvp.RequiredViewOps
     */
    @Override
    public void updateProgressBarPingVisibility(final boolean visible) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBarPing.setVisibility(visible ? View.VISIBLE : View.GONE);
                if (visible) {
                    mBtnRePing.setVisibility(View.GONE);
                    mTvPingStatus.setText("");
                }
            }
        });
    }

    @Override
    public void updateProgressBarScanVisibility(boolean visible) {
        mProgressBarScan.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updatePingResult(String host, final boolean pingResult) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBtnRePing.setVisibility(View.VISIBLE);
                mTvPingStatus.setText(pingResult ? getString(R.string.success) : getString(R.string.fail));
                mTvPingStatus.setTextColor(pingResult ? Color.GREEN : Color.RED);
            }
        });
    }

    @Override
    public void updateScanResult(final PortScanFinishEvent event) {
        MyLog.d("");
        setResults(event.scanResults);
    }

    private void setResults(ConcurrentHashMap<Integer, Integer> results) {
        StringBuilder result = new StringBuilder();
        int firstRangeNum = 0;

        List<Integer> ports = new ArrayList<>(results.keySet());
        Collections.sort(ports);

        for (int i = 0; i < ports.size(); i++) {
            int port = ports.get(i);
            int nextPort = 0;
            boolean isNextPortOpen = false;
            if (i + 1 < ports.size()) {
                nextPort = ports.get(i + 1);
                isNextPortOpen = results.get(nextPort) == PortScanRunnable.OPEN;
            }
            // get the object by the key.
            int state = results.get(port); // state of port

            if (state == PortScanRunnable.OPEN) {
                if (isNextPortOpen && nextPort - port == 1) {
                    if (firstRangeNum == 0) {
                        firstRangeNum = port;
                    }
                } else {
                    if (firstRangeNum == 0) {
                        Utils.appendGreenText(result, port);
                    } else {
                        Utils.appendGreenText(result, "" + firstRangeNum + "-" + port);
                        firstRangeNum = 0;
                    }
                }
            } else {
                if (!isNextPortOpen && nextPort - port == 1) {
                    if (firstRangeNum == 0) {
                        firstRangeNum = port;
                    }
                } else {
                    if (firstRangeNum == 0) {
                        Utils.appendRedText(result, port);
                    } else {
                        Utils.appendRedText(result, "" + firstRangeNum + "-" + port);
                        firstRangeNum = 0;
                    }
                }

            }

        }

        mTvResult.setText(Html.fromHtml(result.toString()));
    }

    @Override
    public void onSuggestionsUpdated(List<Host> suggestionsList) {
        MyLog.d("");

        setSuggestionsAdapter(suggestionsList);
    }

    @Override
    public void onBtnsUpdated(List<Btn> pBtns) {
        MyLog.d("");
        for (Btn btn : pBtns) {
            MyLog.d("Host: " + btn.getTitle());
        }
        updateBtnsIfNeed(pBtns);
    }
}