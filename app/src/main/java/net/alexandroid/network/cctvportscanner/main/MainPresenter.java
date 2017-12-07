package net.alexandroid.network.cctvportscanner.main;

import net.alexandroid.network.cctvportscanner.db.Btn;
import net.alexandroid.network.cctvportscanner.db.Host;
import net.alexandroid.network.cctvportscanner.scan.PortScanFinishEvent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainPresenter implements MainMvp.RequiredPresenterOps, MainMvp.PresenterOps {

    // Layer View reference
    private WeakReference<MainMvp.RequiredViewOps> mView;

    // Layer Model reference
    private MainMvp.ModelOps mModel;


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
        // TODO Network check
        if (mView.get() != null) {
            mView.get().updateProgressBarPingVisibility(true);
        }
        mModel.ping(host.getHost());
        mModel.addHostToDb(host);
    }

    @Override
    public void onRemoveSuggestionFromDb(Host pHost) {
        mModel.removeHostFromDb(pHost);
    }

    @Override
    public void onPingBtn(String host) {
        // TODO Network check
        if (mView.get() != null) {
            mView.get().updateProgressBarPingVisibility(true);
        }
        mModel.ping(host);
    }


    @Override
    public void onCheckBtn(String host, ArrayList<Integer> list) {
        // TODO Network check
        if (mView.get() != null) {
            mView.get().updateProgressBarScanVisibility(true);
        }
        mModel.checkPorts(host, list, 0);
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
    public void onRemoveBtn(int btnPosition) {

    }

    @Override
    public void onEditBtn(int btnPosition, String title, String ports) {

    }

    @Override
    public void onAddBtn(String title, String ports) {

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
                mView.get().updateProgressBarScanVisibility(false);
            }
        }
    }

    @Override
    public void onSuggestionsUpdated(List<Host> suggestionsList) {
        if (mView.get() != null) {
            mView.get().onSuggestionsUpdated(suggestionsList);
        }
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
