package com.aks.calclist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ConverterActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //DecimalFormat df = new DecimalFormat("0.000");

    TextWatcher tvKGW2 = null;
    TextWatcher tvKGV2 = null;
    TextWatcher tvKGW1 = null;
    TextWatcher tvKGV1 = null;

    TextWatcher tvMGW1 = null;
    TextWatcher tvMGV1 = null;
    TextWatcher tvMGW2 = null;
    TextWatcher tvMGV2 = null;

    TextWatcher twGSTAmount = null;
    TextWatcher twGSTTax = null;
    TextWatcher twGSTTotal = null;

    EditText etKGV1 = null;
    EditText etKGV2 = null;

    Button btKGClr = null;
    Button btMGClr = null;
    Button btGSTClr = null;

    EditText etKGW1 = null;
    EditText etKGW2 = null;

    EditText etMGV1 = null;
    EditText etMGV2 = null;

    EditText etMGW1 = null;
    EditText etMGW2 = null;

    EditText etGSTAmount = null;
    EditText etGSTTax = null;
    EditText etGSTTotal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        etKGV1 = findViewById(R.id.idKGV1);
        etKGV2 = findViewById(R.id.idKGV2);

        etKGW1 = findViewById(R.id.idKGW1);
        etKGW2 = findViewById(R.id.idKGW2);

        etMGV1 = findViewById(R.id.idMGV1);
        etMGV2 = findViewById(R.id.idMGV2);

        etMGW1 = findViewById(R.id.idMGW1);
        etMGW2 = findViewById(R.id.idMGW2);

        etGSTAmount = findViewById(R.id.idGSTAmount);
        etGSTTax = findViewById(R.id.idGSTTax);
        etGSTTotal = findViewById(R.id.idGSTTotal);

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initKGValues();
        initMGValues();
        initGSTValues();
    }

    void clearKGValues(){
        removeKGTextChangeListeners();
        etKGW1.setText("");
        etKGW2.setText("");
        etKGV1.setText("");
        etKGV2.setText("");
        addKGTextChangeListeners();
    }

    void clearMGValues() {
        removeMGTextChangeListeners();
        etMGW1.setText("");
        etMGW2.setText("");
        etMGV1.setText("");
        etMGV2.setText("");
        addMGTextChangeListeners();
    }

    void clearGSTValues() {
        removeGSTTextChangeListeners();
        etGSTAmount.setText("");
        etGSTTax.setText("");
        etGSTTotal.setText("");
        addGSTTextChangeListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.layout.menu_converter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.idConverterClear:
                clearKGValues();
                clearMGValues();
                clearGSTValues();

                View view = this.getCurrentFocus();
                if(view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void calculateKGV2() {
        Float w1;

        if(etKGW1.getText().toString().equals(".")) {
            return;
        }

        if(etKGW1.getText().toString().isEmpty()) {
            w1 = 0.0f;
        } else {
            w1 = Float.valueOf(etKGW1.getText().toString());
        }

        Float v1;

        if(etKGV1.getText().toString().isEmpty()) {
            v1 = 0.0f;
        } else {
            v1 = Float.valueOf(etKGV1.getText().toString());
        }

        if(w1 == 0 || v1 == 0) {
            return;
        }

        Float w2;
        if(etKGW2.getText().toString().equals(".")) {
            return;
        }

        if(etKGW2.getText().toString().equals("")) {
            w2 = 0f;
        }
        else {
            w2 = Float.valueOf(etKGW2.getText().toString());
        }

        Float v2;
        if(etKGV2.getText().toString().equals("")) {
            v2 = 0f;
        } else {
            v2 = Float.valueOf(etKGV2.getText().toString());
        }

        Float value = (v1 * w2 /w1);


        removeKGTextChangeListeners();
        etKGV2.setText(String.format("%.2f", value));
        addKGTextChangeListeners();
    }

    private void calculateKGW2() {
        Float w1;

        if(etKGW1.getText().toString().isEmpty()) {
            w1 = 0.0f;
        } else {
            w1 = Float.valueOf(etKGW1.getText().toString());
        }

        Float v1;

        if(etKGV1.getText().toString().isEmpty()) {
            v1 = 0.0f;
        } else {
            v1 = Float.valueOf(etKGV1.getText().toString());
        }

        if(w1 == 0 || v1 == 0) {
            return;
        }

        Float w2;

        if(etKGW2.getText().toString().equals("")) {
            w2 = 0f;
        } else {
            w2 = Float.valueOf(etKGW2.getText().toString());
        }

        Float v2;

        if(etKGV2.getText().toString().equals("")) {
            v2 = 0f;
        } else {
            v2 = Float.valueOf(etKGV2.getText().toString());
        }

        Float value = (v2 * w1 /v1);
        //Float dfValue = Float.valueOf(df.format(value));

        removeKGTextChangeListeners();
        etKGW2.setText(String.format("%.3f", value));
        addKGTextChangeListeners();
    }

    void removeKGTextChangeListeners() {
        etKGW1.removeTextChangedListener(tvKGW1);
        etKGW2.removeTextChangedListener(tvKGW2);
        etKGV1.removeTextChangedListener(tvKGV1);
        etKGV2.removeTextChangedListener(tvKGV2);
    }

    void addKGTextChangeListeners() {
        etKGW1.addTextChangedListener(tvKGW1);
        etKGW2.addTextChangedListener(tvKGW2);
        etKGV1.addTextChangedListener(tvKGV1);
        etKGV2.addTextChangedListener(tvKGV2);
    }

    void removeMGTextChangeListeners() {
        etMGW1.removeTextChangedListener(tvMGW1);
        etMGW2.removeTextChangedListener(tvMGW2);
        etMGV1.removeTextChangedListener(tvMGV1);
        etMGV2.removeTextChangedListener(tvMGV2);
    }

    void addMGTextChangeListeners() {
        etMGW1.addTextChangedListener(tvMGW1);
        etMGW2.addTextChangedListener(tvMGW2);
        etMGV1.addTextChangedListener(tvMGV1);
        etMGV2.addTextChangedListener(tvMGV2);
    }

    void removeGSTTextChangeListeners() {
        etGSTAmount.removeTextChangedListener(twGSTAmount);
        etGSTTax.removeTextChangedListener(twGSTTax);
        etGSTTotal.removeTextChangedListener(twGSTTotal);
    }

    void addGSTTextChangeListeners() {
        etGSTAmount.addTextChangedListener(twGSTAmount);
        etGSTTax.addTextChangedListener(twGSTTax);
        etGSTTotal.addTextChangedListener(twGSTTotal);
    }

    void initKGValues() {
        tvKGW1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged tvKGW1");
                calculateKGV2();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        tvKGV1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged tvKGV1");
                calculateKGV2();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };


        tvKGW2 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged tvKGW2");
                calculateKGV2();
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        };


        tvKGV2 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged tvKGV2");
                calculateKGW2();
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        addKGTextChangeListeners();
    }

    private void calculateMGV2() {
        int w1;

        if(etMGW1.getText().toString().isEmpty()) {
            w1 = 0;
        } else {
            w1 = Integer.valueOf(etMGW1.getText().toString());
        }

        Float v1;

        if(etMGV1.getText().toString().equals(".")) {
            return;
        }

        if(etMGV1.getText().toString().isEmpty()) {
            v1 = 0.0f;
        } else {
            v1 = Float.valueOf(etMGV1.getText().toString());
        }

        if(w1 == 0 || v1 == 0) {
            return;
        }
        int w2;
        if(etMGW2.getText().toString().equals(".")) {
            return;
        }

        if(etMGW2.getText().toString().equals("")) {
            w2 = 0;
        }
        else {
            w2 = Integer.valueOf(etMGW2.getText().toString());
        }

        Float v2;
        if(etMGV2.getText().toString().equals("")) {
            v2 = 0f;
        } else {
            v2 = Float.valueOf(etMGV2.getText().toString());
        }

        Float value = (v1 * w2 /w1);

        removeMGTextChangeListeners();
        etMGV2.setText(String.format("%.2f", value));
        addMGTextChangeListeners();

    }

    private void calculateMGW2() {
        int w1;

        if(etMGW1.getText().toString().isEmpty()) {
            w1 = 0;
        } else {
            w1 = Integer.valueOf(etMGW1.getText().toString());
        }

        Float v1;

        if(etMGV1.getText().toString().equals(".")) {
            return;
        }

        if(etMGV1.getText().toString().isEmpty()) {
            v1 = 0.0f;
        } else {
            v1 = Float.valueOf(etMGV1.getText().toString());
        }

        if(w1 == 0 || v1 == 0) {
            return;
        }

        Integer w2;

        if(etMGW2.getText().toString().equals("")) {
            w2 = 0;
        } else {
            w2 = Integer.valueOf(etMGW2.getText().toString());
        }

        Float v2;

        if(etMGV2.getText().toString().equals(".")) {
            return;
        }

        if(etMGV2.getText().toString().equals("")) {
            v2 = 0f;
        } else {
            v2 = Float.valueOf(etMGV2.getText().toString());
        }

        Float value = (v2 * w1 /v1);

        //Float dfValue = Float.valueOf(df.format(value));

        removeMGTextChangeListeners();
        etMGW2.setText(String.format("%d", Math.round(value)));
        addMGTextChangeListeners();
    }

    void initMGValues() {
        tvMGW1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged tvMGW1");
                calculateMGV2();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        tvMGV1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged tvMGV1");
                calculateMGV2();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };


        tvMGW2 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: tvMGW2");
                calculateMGV2();
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        };


        tvMGV2 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged tvMGV2");
                calculateMGW2();
            }


            @Override
            public void afterTextChanged(Editable s) {



            }
        };
        addMGTextChangeListeners();
    }

    void initGSTValues() {
        twGSTAmount = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged twGSTAmount");
                calculateGSTTotal();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        twGSTTax = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged twGSTTax");
                calculateGSTTotal();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };


        twGSTTotal = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: tvMGW2");
                calculateGSTAmount();

            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        addGSTTextChangeListeners();
    }

    void calculateGSTTotal() {
        String strAmount = etGSTAmount.getText().toString();
        String strTax = etGSTTax.getText().toString();

        if(strAmount.isEmpty() || strAmount.equals("0")) {
            return;
        }

        if(strTax.isEmpty() || strTax.equals("0")) {
            return;
        }

        Float amount = Float.parseFloat(strAmount);
        Float tax = Float.parseFloat(strTax);

        Float total = amount + amount * (tax/100);

        removeGSTTextChangeListeners();
        etGSTTotal.setText(String.format("%.2f", total));
        addGSTTextChangeListeners();

    }

    void calculateGSTAmount() {
        String strTax = etGSTTax.getText().toString();
        String strTotal = etGSTTotal.getText().toString();

        if(strTotal.isEmpty() || strTotal.equals("0")) {
            return;
        }

        if(strTax.isEmpty() || strTax.equals("0")) {
            return;
        }

        Float total = Float.parseFloat(strTotal);
        Float tax = Float.parseFloat(strTax);

        Float amount = (total * 100)/(100 + tax);

        removeGSTTextChangeListeners();
        etGSTAmount.setText(String.format("%.2f", amount));
        addGSTTextChangeListeners();

    }
}