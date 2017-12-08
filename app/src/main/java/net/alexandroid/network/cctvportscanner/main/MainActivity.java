package net.alexandroid.network.cctvportscanner.main;

import android.app.Activity;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity implements
        MainMvp.RequiredViewOps,
        View.OnClickListener,
        View.OnLongClickListener {

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

    private SearchView.SearchAutoComplete mSearchAutoComplete;
    private BtnsRecyclerAdapter mBtnsRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        attachPresenter();
        mPresenter.onGetButtons(this);

        setViews();
        setBtnsRecyclerView();
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

    private void setBtnsRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mBtnsRecyclerAdapter = new BtnsRecyclerAdapter(this);
        recyclerView.setAdapter(mBtnsRecyclerAdapter);
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

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View viewWithCurrentFocus = getCurrentFocus();
        if (inputMethodManager != null && viewWithCurrentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(viewWithCurrentFocus.getWindowToken(), 0);
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
            case R.id.btn:
                onRecyclerBtnClick(v);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                showAddOrEditDialog((Btn) v.getTag(), false);
                return true;
        }
        return false;
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
        checkPorts(list);
    }

    private void onRecyclerBtnClick(View v) {
        MyLog.d("Click on: " + ((Btn) v.getTag()).getTitle());
        String portList = ((Btn) v.getTag()).getPorts();
        ArrayList<Integer> list = Utils.convertStringToIntegerList(portList);
        checkPorts(list);
    }

    private void checkPorts(ArrayList<Integer> pList) {
        if (mHost != null) {
            if (pList != null) {
                mTvResult.setText("");
                mPresenter.onCheckBtn(mHost, pList);
                mInputPort.clearFocus();
                hideSoftKeyboard();
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.msg_no_host_selected, Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_button:
                showAddOrEditDialog(null, true);
                return true;
        }
        return super.onOptionsItemSelected(item);
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

        mSuggestionsAdapter = new SuggestionsAdapter(getApplicationContext(), new ArrayList<String>());
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
    private void removeSuggestionFromDb(String pHost) {
        mPresenter.onRemoveSuggestionFromDb(new Host(pHost));
    }

    // Btns control
    private void updateBtnsIfNeed(List<Btn> pBtns) {
        mBtnsRecyclerAdapter.swapItems(pBtns);
    }

    // Dialogs
    public void showAddOrEditDialog(final Btn pBtn, final boolean addingBtn) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_btns_fragment, null);
        alertDialog.setTitle(addingBtn ? getString(R.string.add_btn) : getString(R.string.edit_btn));
        alertDialog.setView(view);
        alertDialog.setPositiveButton(addingBtn ? getString(R.string.add) : getString(R.string.save), null);
        if (!addingBtn) {
            alertDialog.setNegativeButton(R.string.delete, null);
        }
        final EditText etTitle = view.findViewById(R.id.input_title);
        final EditText etPort = view.findViewById(R.id.input_port);
        if (!addingBtn) {
            etTitle.setText(pBtn.getTitle());
            etPort.setText(pBtn.getPorts());
        }

        final AlertDialog dialog = alertDialog.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public void onClick(View v) {
                        MyLog.d("onClick");
                        String newTitle = etTitle.getText().toString().trim();
                        String newPorts = etPort.getText().toString().trim();
                        ArrayList<Integer> list = Utils.convertStringToIntegerList(newPorts);
                        String checkedPorts = Utils.convertIntegerListToString(list);
                        etPort.setText(checkedPorts);

                        MyLog.d("title: " + newTitle);
                        MyLog.d("checkedPorts: " + checkedPorts);

                        if (newTitle.length() > 0 && checkedPorts.length() > 0) {
                            if (addingBtn) {
                                MyLog.d("Btn add");
                                mPresenter.onAddBtn(getApplicationContext(), newTitle, checkedPorts);
                                dialog.dismiss();
                            } else {
                                MyLog.d("Btn edit");
                                pBtn.setTitle(newTitle);
                                pBtn.setPorts(checkedPorts);
                                mPresenter.onEditBtn(getApplicationContext(), pBtn);
                                dialog.dismiss();
                            }
                        } else {
                            MyLog.d("Wrong parameters");
                            Snackbar.make(etTitle, R.string.wrong_parameters, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View pView) {
                        mPresenter.onRemoveBtn(getApplicationContext(), pBtn);
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
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
    public void showScanInProgressMsg() {
        Toast.makeText(getApplicationContext(), R.string.scan_in_progress_msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public SuggestionsAdapter getSuggestionsAdapter() {
        return mSuggestionsAdapter;
    }

    @Override
    public void removeSuggestionFromAdapter(List<String> newList, List<String> oldList) {
        for (String host : oldList) {
            if (!newList.contains(host)) {
                mSuggestionsAdapter.remove(host);
                // Workaround to update the list
                mSearchAutoComplete.setText(mSearchAutoComplete.getText());
                break;
            }
        }
    }

    @Override
    public void addSuggestionToAdapter(List<String> newList, List<String> oldList) {
        for (String host : newList) {
            if (!oldList.contains(host)) {
                mSuggestionsAdapter.add(host);
                break;
            }
        }
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
