package net.alexandroid.network.cctvportscanner.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.alexandroid.network.cctvportscanner.R;
import net.alexandroid.network.cctvportscanner.db.Btn;

import java.util.ArrayList;
import java.util.List;


public class BtnsRecyclerAdapter extends RecyclerView.Adapter<BtnsRecyclerAdapter.ViewHolder> {

    private final View.OnClickListener mClickListener;

    private List<Btn> mBtnList = new ArrayList<>();

    public BtnsRecyclerAdapter(MainActivity pMainActivity) {
        mClickListener = pMainActivity;
    }

    public void swapItems(List<Btn> pBtns) {
        mBtnList = pBtns;
        notifyDataSetChanged();
    }

    @SuppressWarnings("WeakerAccess")
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final Button mButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mButton = (Button) itemView;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_btn, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mButton.setText(mBtnList.get(position).getTitle());
        holder.mButton.setTag(mBtnList.get(position));
        holder.mButton.setOnClickListener(mClickListener);
    }

    @Override
    public int getItemCount() {
        return mBtnList.size();
    }
}
