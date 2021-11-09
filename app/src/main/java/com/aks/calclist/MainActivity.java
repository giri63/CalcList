package com.aks.calclist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ListItemAdapter.AdpaterCallback {
    private static final String TAG = "MainActivity";

    ListItemAdapter adapter = null;
    private EditText mAmountField;
    ArrayList<String> itemList = new ArrayList<>();
    RecyclerView listItemRecyclerView = null;
    Integer amountTotal = 0;
    TextView tvTotal;
    TextView tvAdd;
    Button btClearList;

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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        listItemRecyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ListItemAdapter(this, itemList, this);
        //adapter.setClickListener(this);
        listItemRecyclerView.setAdapter(adapter);


    }

    private void initViews() {
        mAmountField = findViewById(R.id.idEnterAmount);
        tvTotal = findViewById(R.id.idListTotal);
        tvAdd = findViewById(R.id.idKeypadKeyAdd);


        btClearList = findViewById(R.id.idButtonClearList);
        btClearList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Item List")
                        .setMessage("Clear List?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                itemList.clear();
                                adapter.notifyDataSetChanged();
                                amountTotal = 0;
                                tvTotal.setText(String.format("%d", amountTotal));
                                tvAdd.setEnabled(true);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

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
                Editable editable = mAmountField.getText();
                int charCount = editable.length();
                if (charCount > 0) {
                    editable.delete(charCount - 1, charCount);
                }
                if(mAmountField.getText().toString().isEmpty()) {
                    mAmountField.setText("0");
                }
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
    public void onValueUpdated(View view, String oldValue, String newValue) {
        Log.d(TAG, "onValueUpdated: view:" + view
                + "oldValue" + oldValue
                + "newValue" + newValue);

        amountTotal = amountTotal - Integer.parseInt(oldValue) + Integer.parseInt(newValue);
        tvTotal.setText(String.format("%d", amountTotal));
    }

    @Override
    public void setKeyboardEnabled(int position, TextView editText, Boolean status) {
        Log.d(TAG, "setKeyboardEnabled: position:" + position);
        if(status) {
            mCurrentEditItem = editText;
            mAmountField.setText(mCurrentEditItem.getText().toString());
            tvAdd.setEnabled(false);
            listItemRecyclerView.scrollToPosition(position);
        } else {
            mCurrentEditItem = null;
            mAmountField.setText("0");
            tvAdd.setEnabled(true);
            listItemRecyclerView.scrollToPosition(position);
        }

        /*LinearLayout layout = findViewById(R.id.idLayoutAmount);
        layout.setEnabled(false);*/
    }
}