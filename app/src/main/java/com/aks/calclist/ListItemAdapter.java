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
        holder.btOK.setVisibility(View.GONE);
        holder.btCancel.setVisibility(View.GONE);
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
        Button btOK;
        Button btCancel;
        Button btDelete;
        String oldAmount, newAmount;
        int position;

        ViewHolder(View itemView) {
            super(itemView);
            etItem = itemView.findViewById(R.id.idItemAmount);
            tvSN = itemView.findViewById(R.id.idItemSN);
            btOK = itemView.findViewById(R.id.idEditButtonOK);
            btCancel = itemView.findViewById(R.id.idEditButtonCancel);
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

                    oldAmount = ((TextView)v).getText().toString();
                    btOK.setVisibility(View.VISIBLE);
                    btCancel.setVisibility(View.VISIBLE);
                    btDelete.setVisibility(View.VISIBLE);

                    defaultTextColor = etItem.getCurrentTextColor();
                    etItem.setTextColor(Color.parseColor("#ff0000")); //red
                    mAdapterCb.setEntrySelected(position, etItem, true);
                }
            });

            btOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newAmount = etItem.getText().toString();
                    if(!newAmount.equals(oldAmount)) {
                        mAdapterCb.onValueUpdated(position, oldAmount, newAmount);
                    }

                    btOK.setVisibility(View.GONE);
                    btCancel.setVisibility(View.GONE);
                    btDelete.setVisibility(View.GONE);
                    oldAmount = null;
                    hasEditFocus = false;
                    mAdapterCb.setEntrySelected(position, etItem, false);
                    etItem.setTextColor(defaultTextColor);
                }
            });

            btCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: btCancel");
                    btOK.setVisibility(View.GONE);
                    btCancel.setVisibility(View.GONE);
                    btDelete.setVisibility(View.GONE);
                    if(oldAmount != null) {
                        etItem.setText(oldAmount);
                    }
                    oldAmount = null;
                    hasEditFocus = false;
                    mAdapterCb.setEntrySelected(position, etItem, false);
                    etItem.setTextColor(defaultTextColor);
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
                                    mAdapterCb.setEntryDeleted(position, oldAmount);
                                    btOK.setVisibility(View.GONE);
                                    btCancel.setVisibility(View.GONE);
                                    btDelete.setVisibility(View.GONE);
                                    hasEditFocus = false;
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });

            etItem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    Log.d(TAG, "onFocusChange: hasFocus:"
                            + hasFocus + "text:" + ((TextView)v).getText());

                    if(hasFocus) {
                        oldAmount = ((TextView)v).getText().toString();
                        btOK.setVisibility(View.VISIBLE);
                        btCancel.setVisibility(View.VISIBLE);
                        btDelete.setVisibility(View.VISIBLE);
                        mAdapterCb.setEntrySelected(position, etItem, true);

                    } else {
                        if(oldAmount != null) {
                            etItem.setText(oldAmount);
                        }
                        btOK.setVisibility(View.GONE);
                        btCancel.setVisibility(View.GONE);
                        btDelete.setVisibility(View.GONE);
                        mAdapterCb.setEntrySelected(position, etItem, false);
                    }



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
        public void onValueUpdated(int position, String oldValue, String newValue);
        public void setEntrySelected(int position, TextView editText, Boolean status);
        public void setEntryDeleted(int position, String oldValue);

        //void onItemClick(View view, int position);
    }
}
