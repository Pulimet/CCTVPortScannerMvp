package net.alexandroid.network.cctvportscanner.main;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import net.alexandroid.network.cctvportscanner.R;
import net.alexandroid.network.cctvportscanner.db.AppDatabase;
import net.alexandroid.network.cctvportscanner.db.Btn;
import net.alexandroid.network.cctvportscanner.db.Host;
import net.alexandroid.network.cctvportscanner.ping.PingRunnable;
import net.alexandroid.network.cctvportscanner.scan.PortScanFinishEvent;
import net.alexandroid.network.cctvportscanner.scan.ScanService;
import net.alexandroid.shpref.Contextor;
import net.alexandroid.shpref.ShPref;
import net.alexandroid.utils.mylog.MyLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainModel implements MainMvp.ModelOps {
    // Presenter reference
    private MainMvp.RequiredPresenterOps mPresenter;


    public MainModel(MainMvp.RequiredPresenterOps mPresenter) {
        this.mPresenter = mPresenter;
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    /**
     * MainMvp.ModelOps
     *
     * @param host
     */
    @Override
    public void ping(String host) {
        new Thread(new PingRunnable(host, new PingRunnable.CallBack() {
            @Override
            public void onResult(String host, boolean pingResult) {
                mPresenter.onPingResult(host, pingResult);
            }
        })).start();
    }

    @Override
    public void checkPorts(String host, ArrayList<Integer> list, int scanId) {
        Intent intent = new Intent(Contextor.getInstance().getContext(), ScanService.class);
        intent.putExtra(ScanService.EXTRA_HOST, host);
        intent.putExtra(ScanService.EXTRA_SCAN_ID, scanId);
        intent.putIntegerArrayListExtra(ScanService.EXTRA_PORTS, list);
        Contextor.getInstance().getContext().startService(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEachPortScanResult(PortScanFinishEvent event) {
        mPresenter.onCheckResult(event);
    }

    @Override
    public void getSuggestions(MainActivity pMainActivity) {
        AppDatabase.getInstance(pMainActivity.getApplicationContext())
                .hostDao().getAll().observe(pMainActivity, new Observer<List<Host>>() {
            @Override
            public void onChanged(@Nullable List<Host> pHosts) {
                mPresenter.onSuggestionsUpdated(pHosts);
            }
        });
    }


    @Override
    public void addHostToDb(final Host pHost) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Context context = Contextor.getInstance().getContext();
                if (!isHostExist(context, pHost)) {
                    AppDatabase.getInstance(context).hostDao().insert(pHost);
                }
            }
        }).start();

    }

    private boolean isHostExist(Context pContext, Host pHost) {
        return AppDatabase.getInstance(pContext).hostDao().getHost(pHost.getHost()) != null;
    }

    @Override
    public void removeHostFromDb(final Host pHost) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase.getInstance(Contextor.getInstance().getContext())
                        .hostDao().delete(pHost.getHost());
            }
        }).start();
    }

    @Override
    public void getButtons(final MainActivity pMainActivity) {
        AppDatabase.getInstance(pMainActivity.getApplicationContext())
                .btnDao().getAll().observe(pMainActivity, new Observer<List<Btn>>() {
            @Override
            public void onChanged(@Nullable List<Btn> pBtns) {
                mPresenter.onBtnsUpdated(pBtns);
                addDefaultBtnsIfFirstTimeLoaded(pMainActivity.getApplicationContext());
            }
        });
    }

    private void addDefaultBtnsIfFirstTimeLoaded(final Context pApplicationContext) {
        if (!ShPref.contains(R.string.key_is_default_data_added)) {
            ShPref.put(R.string.key_is_default_data_added, true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MyLog.d("Add default data");
                    AppDatabase.getInstance(pApplicationContext).btnDao().insertAll(
                            new Btn("80", "80"),
                            new Btn("90", "90"),
                            new Btn("8080", "8080"),
                            new Btn("Geovision DVR/NVR", "80,4550,5550,6550,5552,8866,5511"),
                            new Btn("Geovision CenterV2", "5547"),
                            new Btn("Geovision IP Device", "80,5552,10000"),
                            new Btn("Rifatron", "80,2000,50100"),
                            new Btn("Procam", "80,90"),
                            new Btn("Avigilon", "38880-38883,80,50081-50083"),
                            new Btn("Evermedia", "0,5555"),
                            new Btn("Win4Net", "80, 9010, 2000"),
                            new Btn("Sentinel", "80,8000,9000"),
                            new Btn("Provision", "80, 8000"),
                            new Btn("Dahua", "80, 37777, 37778"),
                            new Btn("Avtech", "80"));
                }
            }).start();
        }
    }

    @Override
    public void removeBtn() {

    }

    @Override
    public void editBtn() {

    }

    @Override
    public void addBtn() {

    }

}
