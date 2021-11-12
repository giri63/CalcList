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
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ListItemAdapter.AdpaterCallback {
    private static final String TAG = "MainActivity";

    ListItemAdapter adapter = null;
    private TextView mAmountField;
    ArrayList<String> itemList = new ArrayList<>();
    RecyclerView listItemRecyclerView = null;
    Integer amountTotal = 0;
    TextView tvTotal;
    TextView tvAdd;

    LinearLayoutManager linearLayoutManager;

    TextView mCurrentEditItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

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
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Item List")
                        .setMessage("Clear Items?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                itemList.clear();
                                adapter.clear();
                                adapter.notifyDataSetChanged();
                                amountTotal = 0;
                                mAmountField.setText("0");
                                tvTotal.setText(String.format("%d", amountTotal));
                                tvAdd.setEnabled(true);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                Log.d(TAG, "onOptionsItemSelected: action_name");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        mAmountField = findViewById(R.id.idEnterAmount);
        tvTotal = findViewById(R.id.idListTotal);
        tvAdd = findViewById(R.id.idKeypadKeyAdd);
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
            if(mCurrentEditItem != null) {
                mCurrentEditItem.setText(mAmountField.getText());
            }
            return;
        }

        switch (v.getId()) {
            case R.id.idKeypadKeyAdd: {
                String amount = mAmountField.getText().toString();
                Log.d(TAG, "onClick idKeypadKeyAdd:" + amount);

                if(!amount.isEmpty() && !amount.equals("0")) {
                    amountTotal += Integer.parseInt(amount);
                    tvTotal.setText(getFormattedNumber(amountTotal));

                    itemList.add(amount);
                    listItemRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    //adapter.notifyDataSetChanged();

                    mAmountField.setText("0");
                }
                break;
            }


            case R.id.idKeypadKeyClear: {
                mAmountField.setText("0");
                if(mCurrentEditItem != null) {
                    mCurrentEditItem.setText(mAmountField.getText());
                }
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

                if(mCurrentEditItem != null) {
                    mCurrentEditItem.setText(mAmountField.getText());
                }
                break;
            }
        }
    }

    String getFormattedNumber(int number) {
        //String.format("%d", amountTotal)
        DecimalFormat formatter = new DecimalFormat("##,##,###");
        String formatString = formatter.format(number);
        return formatString;
    }

    @Override
    public void onValueUpdated(int position, String oldValue, String newValue) {
        Log.d(TAG, "onValueUpdated: position:" + position
                + "oldValue" + oldValue
                + "newValue" + newValue);

        amountTotal = amountTotal - Integer.parseInt(oldValue) + Integer.parseInt(newValue);
        tvTotal.setText(String.format("%d", amountTotal));
        itemList.set(position, newValue);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setEntrySelected(int position, TextView editText, Boolean status) {
        Log.d(TAG, "setEntrySelected: position:" + position);
        if(status) {
            mCurrentEditItem = editText;
            mAmountField.setText(mCurrentEditItem.getText().toString());
            tvAdd.setEnabled(false);
            linearLayoutManager.scrollToPositionWithOffset(position, 0);

        } else {
            mCurrentEditItem = null;
            mAmountField.setText("0");
            tvAdd.setEnabled(true);
            linearLayoutManager.scrollToPositionWithOffset(position, 0);
        }
    }

    public void setEntryDeleted(int position, String oldValue) {
        Log.d(TAG, "setEntryDeleted: " + position);

        amountTotal = amountTotal - Integer.parseInt(oldValue);
        tvTotal.setText(String.format("%d", amountTotal));

        mCurrentEditItem = null;
        mAmountField.setText("0");
        tvAdd.setEnabled(true);

        itemList.remove(position);
        adapter.notifyDataSetChanged();
        linearLayoutManager.scrollToPositionWithOffset(position, 0);
    }
}