package net.alexandroid.network.cctvportscanner.main;

import android.content.Context;
import android.support.annotation.NonNull;

import net.alexandroid.network.cctvportscanner.db.Btn;
import net.alexandroid.network.cctvportscanner.db.Host;
import net.alexandroid.network.cctvportscanner.main.adapter.SuggestionsAdapter;
import net.alexandroid.network.cctvportscanner.scan.PortScanFinishEvent;
import net.alexandroid.network.cctvportscanner.utils.NetworkUtils;
import net.alexandroid.network.cctvportscanner.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainPresenter implements MainMvp.RequiredPresenterOps, MainMvp.PresenterOps {

    // Layer View reference
    private WeakReference<MainMvp.RequiredViewOps> mView;

    // Layer Model reference
    private MainMvp.ModelOps mModel;

    private List<String> mTempSuggestionsList = new ArrayList<>();
    private boolean mIsScanInProgress;


    public MainPresenter() {
        this.mModel = new MainModel(this);
    }

    /**
     * MainMvp.PresenterOps
     */

    @Override
    public void attachView(MainMvp.RequiredViewOps view) {
        this.mView = new WeakReference<>(view);
    }

    @Override
    public void onDestroy() {
        mModel.onDestroy();
    }

    @Override
    public void onHostSubmit(Host host) {
        if (mView.get() != null) {
            mView.get().updateProgressBarPingVisibility(true);
        }
        mModel.addHostToDb(host);
        mModel.ping(host.getHost());
        networkCheck();
    }

    private void networkCheck() {
        if (!NetworkUtils.isConnected() && mView.get()!= null) {
            mView.get().showNoInternetConnectionMsg();
        }
    }

    @Override
    public void onRemoveSuggestionFromDb(Host pHost) {
        mModel.removeHostFromDb(pHost);
    }

    @Override
    public void onPingBtn(String host) {
        if (mView.get() != null) {
            mView.get().updateProgressBarPingVisibility(true);
        }
        mModel.ping(host);
        networkCheck();
    }


    @Override
    public void onCheckBtn(String host, ArrayList<Integer> list) {
        if (mIsScanInProgress) {
            if (mView.get() != null) {
                mView.get().showScanInProgressMsg();
            }
        } else {
            mIsScanInProgress = true;
            if (mView.get() != null) {
                mView.get().updateProgressBarScanVisibility(true);
            }
            mModel.checkPorts(host, list, 0);
            networkCheck();
        }
    }

    @Override
    public void onGetSuggestions(MainActivity pMainActivity) {
        mModel.getSuggestions(pMainActivity);
    }

    @Override
    public void onGetButtons(MainActivity pMainActivity) {
        mModel.getButtons(pMainActivity);
    }

    @Override
    public void onRemoveBtn(Context pApplicationContext, Btn pBtn) {
        mModel.removeBtn(pApplicationContext, pBtn);
    }

    @Override
    public void onEditBtn(Context pApplicationContext, Btn pBtn) {
        mModel.editBtn(pApplicationContext, pBtn);
    }

    @Override
    public void onAddBtn(Context pApplicationContext, String title, String ports) {
        mModel.addBtn(pApplicationContext, new Btn(title, ports));
    }

    @Override
    public void onSaveLastUsedHostAndPorts(String pHost, String pPorts) {
        mModel.onSaveLastUsedHostAndPorts(pHost, pPorts);
    }

    @Override
    public String onGetLasUsedHost() {
        return mModel.onGetLasUsedHost();
    }

    @Override
    public String onGetLasUsedPorts() {
        return mModel.onGetLasUsedPorts();
    }

    /**
     * MainMvp.RequiredPresenterOps
     */


    @Override
    public void onPingResult(String host, boolean pingResult) {
        if (mView.get() != null) {
            mView.get().updateProgressBarPingVisibility(false);
            mView.get().updatePingResult(host, pingResult);
        }
    }

    @Override
    public void onCheckResult(PortScanFinishEvent event) {
        if (mView.get() != null) {
            mView.get().updateScanResult(event);
            if (event.isListScanFinished) {
                mIsScanInProgress = false;
                mView.get().updateProgressBarScanVisibility(false);
            }
        }
    }

    @Override
    public void onSuggestionsUpdated(List<Host> suggestionsList) {
        if (mView.get() != null) {
            List<String> suggesetionsList = convertoToListOfStrings(suggestionsList);
            SuggestionsAdapter suggestionsAdapter = mView.get().getSuggestionsAdapter();
            if (suggestionsAdapter.getCount() == 0) {
                suggestionsAdapter.addAll(suggesetionsList);
            } else {
                if (suggestionsList.size() > mTempSuggestionsList.size()) {
                    mView.get().addSuggestionToAdapter(suggesetionsList, mTempSuggestionsList);
                } else {
                    mView.get().removeSuggestionFromAdapter(suggesetionsList, mTempSuggestionsList);
                }
            }
            mTempSuggestionsList = suggesetionsList;
        }
    }

    @NonNull
    private ArrayList<String> convertoToListOfStrings(List<Host> pSuggestionsList) {
        ArrayList<String> newList = new ArrayList<>();
        for (Host host : pSuggestionsList) {
            newList.add(host.getHost());
        }
        return newList;
    }

    @Override
    public void onBtnsUpdated(List<Btn> pBtns) {
        if (mView.get() != null) {
            mView.get().onBtnsUpdated(pBtns);
        }
    }

    @Override
    public void onBtnRemoved() {

    }

    @Override
    public void onBtnEdited() {

    }

    @Override
    public void onBtnAdded() {

    }
}
