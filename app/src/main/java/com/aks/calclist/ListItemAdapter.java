package com.aks.calclist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ViewHolder> {
    private static final String TAG = "ListItemAdapter";
    
    private List<String> mData;
    private LayoutInflater mInflater;
    private AdpaterCallback mAdapterCb;
    Context mContext;
    Boolean hasEditFocus = false;

    // data is passed into the constructor
    ListItemAdapter(Context context, List<String> data, AdpaterCallback callback) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        mAdapterCb = callback;
        mContext = context;
    }

    void clear() {
        hasEditFocus = false;
    }


    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_view_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = mData.get(position);
        holder.etItem.setText(item);
        //holder.etItem.setShowSoftInputOnFocus(false);
        //holder.etItem.setInputType(0);
        holder.tvSN.setText(String.format("%d", position + 1));
        holder.btDelete.setVisibility(View.GONE);
        holder.position = position;
        //Log.d(TAG, "onBindViewHolder: item:" + item + " position:" + position);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + mData.size());
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView etItem;
        int defaultTextColor;
        TextView tvSN;
        Button btDelete;
        int position;

        int getCurrentPosition() {
            return position;
        }

        void setAmountText(String amount) {
            etItem.setText(amount);
        }

        String getAmountText() {
            return etItem.getText().toString();
        }

        public void unselectEntry() {
            btDelete.setVisibility(View.GONE);
            hasEditFocus = false;
            etItem.setTextColor(defaultTextColor);
            mAdapterCb.setEntrySelected(this, false);
        }

        ViewHolder(View itemView) {
            super(itemView);
            etItem = itemView.findViewById(R.id.idItemAmount);
            tvSN = itemView.findViewById(R.id.idItemSN);
            btDelete = itemView.findViewById(R.id.idEditButtonDelete);

            //itemView.setOnClickListener(this);

            etItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(hasEditFocus == true) {
                        Log.d(TAG, "onClick: Already one edit enabled");
                        return;
                    }
                    hasEditFocus = true;
                    btDelete.setVisibility(View.VISIBLE);
                    defaultTextColor = etItem.getCurrentTextColor();
                    etItem.setTextColor(Color.parseColor("#ff0000")); //red
                    mAdapterCb.setEntrySelected(ViewHolder.this, true);
                }
            });

            btDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(mContext)
                            .setTitle("Delete")
                            .setMessage("Delete Entry?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    etItem.setTextColor(defaultTextColor);
                                    mAdapterCb.setCurrentEntryDeleted();
                                    btDelete.setVisibility(View.GONE);
                                    hasEditFocus = false;
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "ViewHolder onClick: ");
            //if (mAdapterCb != null) mAdapterCb.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        Log.d(TAG, "getItem: " + id);
        return mData.get(id);
    }

    // parent activity will implement this method to respond to click events
    public interface AdpaterCallback {
        public void setEntrySelected(ViewHolder entry, Boolean status);
        public void setCurrentEntryDeleted();

        //void onItemClick(View view, int position);
    }
}
