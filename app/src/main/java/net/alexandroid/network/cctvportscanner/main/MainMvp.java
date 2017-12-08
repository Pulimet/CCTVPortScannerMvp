package net.alexandroid.network.cctvportscanner.main;

import android.content.Context;

import net.alexandroid.network.cctvportscanner.db.Btn;
import net.alexandroid.network.cctvportscanner.db.Host;
import net.alexandroid.network.cctvportscanner.main.adapter.SuggestionsAdapter;
import net.alexandroid.network.cctvportscanner.scan.PortScanFinishEvent;

import java.util.ArrayList;
import java.util.List;

public interface MainMvp {
    /**
     * View mandatory methods. Available to Presenter
     * Presenter -> View
     */
    interface RequiredViewOps {

        void updatePingResult(String host, boolean pingResult);

        void updateScanResult(PortScanFinishEvent event);

        void updateProgressBarPingVisibility(boolean visible);

        void updateProgressBarScanVisibility(boolean visible);

        void onBtnsUpdated(List<Btn> pBtns);

        SuggestionsAdapter getSuggestionsAdapter();

        void addSuggestionToAdapter(List<String> pSuggesetionsList, List<String> pTempSuggestionsList);

        void removeSuggestionFromAdapter(List<String> pSuggesetionsList, List<String> pTempSuggestionsList);

        void showScanInProgressMsg();

    }

    /**
     * Operations offered from Presenter to View
     * View -> Presenter
     */
    interface PresenterOps {
        void onDestroy();

        void attachView(RequiredViewOps view);

        void onHostSubmit(Host host);

        void onPingBtn(String host);

        void onCheckBtn(String host, ArrayList<Integer> list);

        void onGetSuggestions(MainActivity pMainActivity);

        void onRemoveBtn(Context pApplicationContext, Btn pBtn);

        void onEditBtn(Context pApplicationContext, Btn pBtn);

        void onAddBtn(Context pApplicationContext, String title, String ports);

        void onRemoveSuggestionFromDb(Host pQuery);

        void onGetButtons(MainActivity pMainActivity);
    }

    /**
     * Operations offered from Presenter to Model
     * Model -> Presenter
     */
    interface RequiredPresenterOps {
        void onPingResult(String host, boolean pingResult);

        void onCheckResult(PortScanFinishEvent event);

        void onSuggestionsUpdated(List<Host> suggestionsList);

        void onBtnsUpdated(List<Btn> pBtns);

        void onBtnRemoved();

        void onBtnEdited();

        void onBtnAdded();
    }

    /**
     * Model operations offered to Presenter
     * Presenter -> Model
     */
    interface ModelOps {
        void onDestroy();

        void ping(String host);

        void checkPorts(String host, ArrayList<Integer> list, int scanId);

        void getSuggestions(MainActivity pMainActivity);

        void addHostToDb(Host pHost);

        void removeHostFromDb(Host pHost);

        void getButtons(MainActivity pMainActivity);

        void removeBtn(Context pApplicationContext, Btn pBtn);

        void editBtn(Context pApplicationContext, Btn pBtn);

        void addBtn(Context pApplicationContext, Btn pBtn);
    }

}
