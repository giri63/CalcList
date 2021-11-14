package com.aks.calclist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ListItemAdapter.AdpaterCallback {
    private static final String TAG = "MainActivity";

    ListItemAdapter adapter = null;
    private TextView mAmountField;
    ArrayList<String> itemList = new ArrayList<>();
    RecyclerView listItemRecyclerView = null;
    int mAmountTotal = 0;
    String mOldAmount;
    TextView tvTotal;
    TextView tvAdd;
    TextView tvOK;
    TextView tvCancel;
    TextView tvSpace3;

    TextView tvListDate;

    DBListItem dbListItem = null;
    Boolean dbListRead = false;

    LinearLayoutManager linearLayoutManager;

    ListItemAdapter.ViewHolder mCurrentEntry = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setDate();

        dbListItem = new DBListItem(this);

        // set up the RecyclerView
        listItemRecyclerView = findViewById(R.id.idItemList);

        listItemRecyclerView.setHasFixedSize(true);
        listItemRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.HORIZONTAL));
        listItemRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        listItemRecyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ListItemAdapter(this, itemList, this);
        //adapter.setClickListener(this);
        listItemRecyclerView.setAdapter(adapter);
        loadListFromDB();
    }

    void loadListFromDB() {
        Log.d(TAG, "loadListFromDB: ");
        ListItemEntry item = dbListItem.getItem("1");
        if(item == null || item.getListCount() == 0) {
            Log.d(TAG, "loadListFromDB: no entry in DB");
            return;
        }
        dbListRead = true;

        itemList.clear();
        adapter.clear();
        itemList.addAll(item.getListItems());
        adapter.notifyDataSetChanged();

        mAmountTotal = item.getListAmount();
        tvTotal.setText(String.format("%d", mAmountTotal));

        setDate();
        mAmountField.setText("0");
        tvAdd.setEnabled(true);
    }

    void clearListFromDB() {
        Log.d(TAG, "clearListFromDB dbListRead:" + dbListRead);
        if(dbListRead) {
            dbListItem.deleteListEntry("1");
            dbListRead = false;
        }
    }

    void saveListToDB() {
        Log.d(TAG, "saveListToDB dbListRead:" + dbListRead
                + "itemList.size:" + itemList.size());

        if(itemList.size() == 0) {
            return;
        }

        ListItemEntry item = new ListItemEntry();

        item.setListID("1");
        item.setListName("List1");

        item.setListCount(itemList.size());
        item.setListAmount(mAmountTotal);
        item.setListTime(tvListDate.getText().toString());
        item.setListItems(itemList);

        dbListItem.addOrUpdateItem(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.layout.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.idConverter:
                Intent intent = new Intent(this, ConverterActivity.class);
                startActivity(intent);
                Log.d(TAG, "onOptionsItemSelected: action_name");
                break;

            case R.id.idListClear:
                if(mAmountTotal == 0) {
                    break;
                }
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Item List")
                        .setMessage("Delete Item List?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                setListClear();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                Log.d(TAG, "onOptionsItemSelected: action_name");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setListClear() {
        itemList.clear();
        adapter.clear();
        adapter.notifyDataSetChanged();
        mAmountTotal = 0;
        setDate();
        mAmountField.setText("0");
        tvTotal.setText(String.format("%d", mAmountTotal));
        tvAdd.setEnabled(true);
        clearListFromDB();
    }

    private void initViews() {
        mAmountField = findViewById(R.id.idEnterAmount);
        tvTotal = findViewById(R.id.idListTotal);
        tvAdd = findViewById(R.id.idKeypadKeyAdd);
        tvOK = findViewById(R.id.idKeypadKeyOK);
        tvCancel = findViewById(R.id.idKeypadKeyCancel);
        tvSpace3 = findViewById(R.id.idKeypadKeySpace3);

        tvListDate = findViewById(R.id.idListDate);
    }

    void setDate() {
        String currentDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.getDefault()).format(new Date());
        tvListDate.setText(currentDate);
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveListToDB();
    }

    @Override
    public void onClick(View v) {
        String keyValue = ((TextView)v).getText().toString();
        Log.d(TAG, "onClick: " + keyValue);

        // handle number button click
        if (v.getTag() != null && "number_button".equals(v.getTag())) {
            if(mAmountField.getText().toString().equals("0")) {
                mAmountField.setText(keyValue);
            } else {
                mAmountField.append(keyValue);
            }
            return;
        }

        switch (v.getId()) {
            case R.id.idKeypadKeyAdd: {

                String amount = mAmountField.getText().toString();
                Log.d(TAG, "onClick idKeypadKeyAdd:" + amount);

                if (!amount.isEmpty() && !amount.equals("0")) {
                    if (itemList.size() == 0) {
                        setDate();
                    }

                    mAmountTotal += Integer.parseInt(amount);
                    tvTotal.setText(getFormattedNumber(mAmountTotal));

                    itemList.add(amount);
                    listItemRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    //adapter.notifyDataSetChanged();

                    mAmountField.setText("0");
                }
                break;
            }

            case R.id.idKeypadKeyOK: {
                String newAmount = mAmountField.getText().toString();

                if(newAmount.equals(mOldAmount)) {
                    return;
                }

                mCurrentEntry.setAmountText(newAmount);

                mAmountTotal = mAmountTotal - Integer.parseInt(mOldAmount)
                        + Integer.parseInt(newAmount);

                tvTotal.setText(String.format("%d", mAmountTotal));
                itemList.set(mCurrentEntry.getCurrentPosition(), newAmount);
                mCurrentEntry.unselectEntry();
                //adapter.notifyDataSetChanged();
                break;
            }

            case R.id.idKeypadKeyCancel: {
                mCurrentEntry.unselectEntry();
                break;
            }


            case R.id.idKeypadKeyClear: {
                mAmountField.setText("0");
                break;
            }

            case R.id.idKeypadKeyBack: {
                // delete one character
                String text = mAmountField.getText().toString();

                if(text.isEmpty()) {
                    return;
                }

                String sub = text.substring(0, text.length() - 1);
                if(sub.isEmpty()) {
                    sub = "0";
                }
                mAmountField.setText(sub);
                break;
            }
        }
    }

    String getFormattedNumber(int number) {
        //String.format("%d", mAmountTotal)
        DecimalFormat formatter = new DecimalFormat("##,##,###");
        String formatString = formatter.format(number);
        return formatString;
    }

    private void editEntryOK() {

    }

    @Override
    public void setEntrySelected(ListItemAdapter.ViewHolder entry, Boolean status) {
        Log.d(TAG, "setEntrySelected: status:" + status);
        if(status) {
            mCurrentEntry = entry;
            mOldAmount = entry.getAmountText();
            mAmountField.setText(mOldAmount);

            tvAdd.setVisibility(View.GONE);
            tvOK.setVisibility(View.VISIBLE);
            tvCancel.setVisibility(View.VISIBLE);
            tvSpace3.setVisibility(View.GONE);

            linearLayoutManager.scrollToPositionWithOffset(mCurrentEntry.getCurrentPosition(), 0);
        } else {
            mAmountField.setText("0");

            tvAdd.setVisibility(View.VISIBLE);
            tvOK.setVisibility(View.GONE);
            tvCancel.setVisibility(View.GONE);
            tvSpace3.setVisibility(View.VISIBLE);



            linearLayoutManager.scrollToPositionWithOffset(mCurrentEntry.getCurrentPosition(), 0);
            mCurrentEntry = null;
        }
    }

    @Override
    public void setCurrentEntryDeleted() {
        Log.d(TAG, "setEntryDeleted: mCurrentEntry:" + mCurrentEntry);

        if(mCurrentEntry == null) {
            return;
        }

        int position = mCurrentEntry.getCurrentPosition();

        mAmountTotal = mAmountTotal - Integer.parseInt(mOldAmount);

        tvTotal.setText(String.format("%d", mAmountTotal));

        mAmountField.setText("0");

        tvAdd.setVisibility(View.VISIBLE);
        tvOK.setVisibility(View.GONE);
        tvCancel.setVisibility(View.GONE);
        tvSpace3.setVisibility(View.VISIBLE);

        itemList.remove(position);
        adapter.notifyDataSetChanged();

        if((position >= 0) && position == itemList.size()) {
            position--;
        }
        mCurrentEntry = null;
        linearLayoutManager.scrollToPositionWithOffset(position, 0);
    }
}