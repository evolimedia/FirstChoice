package firstchoice.technopear.com.firstchoice;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import firstchoice.technopear.com.firstchoice.common.FieldName;
import firstchoice.technopear.com.firstchoice.common.RequestMethodTag;
import firstchoice.technopear.com.firstchoice.common.ResponseTagName;
import firstchoice.technopear.com.firstchoice.common.Search_ListAdapter;
import firstchoice.technopear.com.firstchoice.common.SessionManagement;
import firstchoice.technopear.com.firstchoice.common.Urls;

public class Add_Data extends AppCompatActivity {
    private AutoCompleteTextView edt_companyName, edt_Amount, edt_box;
    private Button btn_done;
    private RelativeLayout Rl_add;
    private View mProgressView;
    private ListView LV_Search;

    private static final String TAG = Add_Data.class.getSimpleName();
    private SessionManagement sessionManagement;
    private Search_ListAdapter searchListAdapter;
    private ArrayList<String> companyNameArrayList;
    private LinearLayout LLSearch;
    private Set hashSet;
    private boolean allowAnim = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__data);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.btn_color));
        }
        sessionManagement = new SessionManagement(Add_Data.this);

        LLSearch = (LinearLayout) findViewById(R.id.LLSearch);
        edt_companyName = (AutoCompleteTextView) findViewById(R.id.edt_companyName);
        edt_Amount = (AutoCompleteTextView) findViewById(R.id.edt_AmountName);
        edt_box = (AutoCompleteTextView) findViewById(R.id.edt_box);
        Rl_add = (RelativeLayout) findViewById(R.id.RL_addData);

        btn_done = (Button) findViewById(R.id.btn_done);
        mProgressView = findViewById(R.id.login_progress);
        LV_Search = (ListView) findViewById(R.id.LV_Search);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        companyNameArrayList = new ArrayList<String>();


//                        mHandler = new Handler();
        searchListAdapter = new Search_ListAdapter(Add_Data.this, companyNameArrayList);
        LV_Search.setAdapter(searchListAdapter);
        edt_companyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edt_companyName.getText().length() > 1) {


                    companyNameArrayList = new ArrayList<String>();
                    searchListAdapter = new Search_ListAdapter(Add_Data.this, companyNameArrayList);
                    LV_Search.setAdapter(searchListAdapter);

                    new getSimiliarName().execute();

                } else {
                    LLSearch.clearAnimation();
                    LLSearch.setVisibility(View.GONE);
                    allowAnim = true;

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        LV_Search.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                edt_companyName.setText(companyNameArrayList.get(position));

                LLSearch.setVisibility(View.GONE);
                LLSearch.clearAnimation();
                edt_companyName.setSelection(edt_companyName.getText().length());
//                allowAnim = true;
            }
        });
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetConnected()){
                    String companyName = edt_companyName.getText().toString().trim();
                String AmountName = edt_Amount.getText().toString().trim();
                String box = edt_box.getText().toString().trim();

                edt_companyName.setError(null);
                edt_Amount.setError(null);
                edt_box.setError(null);

                boolean cancel = false;
                View focusView = null;

                if (TextUtils.isEmpty(companyName)) {
                    edt_companyName.setError(getString(R.string.error_field_required));
                    focusView = edt_companyName;
                    cancel = true;
                } else if (TextUtils.isEmpty(AmountName)) {
                    edt_Amount.setError(getString(R.string.error_field_required));
                    focusView = edt_Amount;
                    cancel = true;
                } else if (TextUtils.isEmpty(box)) {
                    edt_box.setError(getString(R.string.error_field_required));
                    focusView = edt_box;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    // Show a progress spinner, and kick off a background task to
                    // perform the user login attempt.

                    showProgress(true);
                    if (MainActivity.isEdit_Add_Data) {
                        new startUpdatingData().execute();
                    } else {
                        new startAddingData().execute();
                    }
                    MainActivity.isEdit_Add_Data = false;
                }
            }else{
                    Toast.makeText(Add_Data.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                }
            }
        });
        if (MainActivity.isEdit_Add_Data) {
            edt_companyName.setText(MainActivity.productBean.getCompanyName());
            edt_Amount.setText(MainActivity.productBean.getAmount());
            edt_box.setText(MainActivity.productBean.getBox() + "");
            edt_companyName.setSelection(edt_companyName.getText().length());

        }

    }

    private class getSimiliarName extends AsyncTask<String, Void, ArrayList<String>> {

        SoapPrimitive soapPrimitive;
        String cityname;

        @Override
        protected ArrayList<String> doInBackground(String... params) {

            soapPrimitive = getCompanyName(cityname.trim());//cityname
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {

//            mIsLoading = false;
            if (soapPrimitive != null) {


                hashSet = new HashSet();
                setNameData(soapPrimitive);

                companyNameArrayList.addAll(hashSet);
                searchListAdapter.notifyDataSetChanged();
                if (allowAnim) {
                    if (companyNameArrayList.size() > 0) {
                        LLSearch.setVisibility(View.VISIBLE);
                        Animation listVisible = AnimationUtils.loadAnimation(Add_Data.this, R.anim.zoom_in);
                        LLSearch.startAnimation(listVisible);
                        allowAnim = false;
                    }
                }
                if (companyNameArrayList.size() == 0) {

                    LLSearch.clearAnimation();
                    LLSearch.setVisibility(View.GONE);

                }
                Log.i("", "LV_Search.getAdapter().getCount()========= " + companyNameArrayList.size());
            }
        }

        @Override
        protected void onPreExecute() {
            cityname = edt_companyName.getText().toString().trim();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private Set<String> setNameData(SoapPrimitive soapPrimitive) {
        hashSet = new HashSet();
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(soapPrimitive.toString());

            int recordsCount = jsonObj.getInt(ResponseTagName.record_count);
            if (recordsCount > 0) {

                try {
                    String str = jsonObj.getString("data");
                    Log.i("===>", "=setNameData====>" + str);
                    JSONArray jsonArray = new JSONArray(str);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject c = jsonArray.getJSONObject(i);

                            hashSet.add(c.getString(FieldName.DND_SALES_INFO_COMPANY_NAME).trim());


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hashSet;
    }

    private SoapPrimitive getCompanyName(String cityName) {

        SoapObject Request = new SoapObject(Urls.NAMESPACE, RequestMethodTag.Get_Data);

        SoapObject soapLogs = new SoapObject(Urls.NAMESPACE, "");

        soapLogs.addProperty(FieldName.TABLE, FieldName.TABLE_DND_SALES_INFO);
        soapLogs.addProperty(FieldName.QUERY, FieldName.DND_SALES_INFO_COMPANY_NAME + " like " + "'%" + cityName + "%' GROUP BY " + FieldName.DND_SALES_INFO_COMPANY_NAME);//
        soapLogs.addProperty(FieldName.LIMIT, "" + 0 + "," + 100);//

        Log.i("====>","=====>"+soapLogs);
        Request.addSoapObject(soapLogs);

        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.dotNet = true;
        soapEnvelope.setOutputSoapObject(Request);

        HttpTransportSE transport = new HttpTransportSE(Urls.Main_URL, Urls.TimeOut);

        try {
            transport.call(Urls.SOAP_ACTION_GET_DATA, soapEnvelope, MainActivity.headerPropertyArrayList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        SoapPrimitive soapPrimitive = null;
        try {
            soapPrimitive = (SoapPrimitive) soapEnvelope.getResponse();
            Log.i("===>", "=getCompanyName*====>" + (SoapPrimitive) soapEnvelope.getResponse());
            try {
                transport.getServiceConnection().disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        }
        return soapPrimitive;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            Rl_add.setVisibility(show ? View.GONE : View.VISIBLE);
            Rl_add.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    Rl_add.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            Rl_add.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class startAddingData extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... strings) {
//            addData();
            return addData();
        }

        @Override
        protected void onPostExecute(final Boolean s) {
            super.onPostExecute(s);
            showProgress(false);
            if (s) {
                Toast.makeText(Add_Data.this, "Data Added Successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(Add_Data.this, "Problem at server,Please try again", Toast.LENGTH_LONG).show();
            }

            finish();
        }
    }

    private boolean addData() {

        try {
            Date date = new Date();

            DateFormat writeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = "";
            if (date != null)
                formattedDate = writeFormat.format(date);


            SoapObject Request = new SoapObject(Urls.NAMESPACE, RequestMethodTag.Create_Account);

            SoapObject soapLogs = new SoapObject(Urls.NAMESPACE, "");

            soapLogs.addProperty(FieldName.TABLE, FieldName.TABLE_DND_SALES_INFO);
            soapLogs.addProperty(FieldName.FIELD_NAME, FieldName.DND_ACC_INFO_ALIAS_ID + "," + FieldName.DND_ACC_INFO_TYPE + "," + FieldName.DND_SALES_INFO_COMPANY_NAME + "," + FieldName.DND_SALES_AMOUNT + "," + FieldName.DND_SALES_BOX + "," + FieldName.DND_SALES_DATE + "," + FieldName.DND_SALES_DELETED);
//            soapLogs.addProperty(FieldName.FIELD_VALUE, "'" + "12345" + "','" +1 + "','" + edt_companyName.getText().toString().trim() + "','" + edt_Amount.getText().toString().trim() + "','" + edt_box.getText().toString().trim() + "','" + "0'");
            soapLogs.addProperty(FieldName.FIELD_VALUE, "'" + sessionManagement.getAliasId().toString() + "','" + sessionManagement.getType() + "','" + edt_companyName.getText().toString().trim() + "','" + edt_Amount.getText().toString().trim() + "','" + edt_box.getText().toString().trim() + "','" + formattedDate + "','" + "0'");

            Request.addSoapObject(soapLogs);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);
            Log.i(TAG, Urls.SOAP_ACTION_CREATE_ACCOUNT + " =====My Add Data Request ========>  " + Request);

            HttpTransportSE transport = new HttpTransportSE(Urls.Main_URL, Urls.TimeOut);

            transport.call(Urls.SOAP_ACTION_CREATE_ACCOUNT, soapEnvelope, MainActivity.headerPropertyArrayList);
            Urls.resultString = (SoapPrimitive) soapEnvelope.getResponse();
            Log.i(TAG, "response ========>  " + Urls.resultString.toString());
            transport.getServiceConnection().disconnect();


        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
        return valueVadilation(ResponseTagName.result);
    }

    public class startUpdatingData extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... strings) {

            return updateData();
        }

        @Override
        protected void onPostExecute(final Boolean s) {
            super.onPostExecute(s);
            showProgress(false);
            if (s) {
                Toast.makeText(Add_Data.this, "Done", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(Add_Data.this, "Problem at server,Please try again", Toast.LENGTH_LONG).show();
            }

            finish();
        }
    }

    private boolean updateData() {

        try {

            SoapObject Request = new SoapObject(Urls.NAMESPACE, RequestMethodTag.Update_Data);

            SoapObject soapLogs = new SoapObject(Urls.NAMESPACE, "");

            soapLogs.addProperty(FieldName.TABLE, FieldName.TABLE_DND_SALES_INFO);
            soapLogs.addProperty(FieldName.FIELD_VALUE, FieldName.DND_SALES_INFO_COMPANY_NAME + "='" + edt_companyName.getText().toString().trim() + "', " + FieldName.DND_SALES_AMOUNT + "='" + edt_Amount.getText().toString().trim() + "'," + FieldName.DND_SALES_BOX + "='" + edt_box.getText().toString().trim() + "'");
            soapLogs.addProperty(FieldName.QUERY, FieldName.DND_SALES_INFO_ID + "='" + MainActivity.productBean.getId() + "'");

            Request.addSoapObject(soapLogs);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);
            Log.i(TAG, Urls.SOAP_ACTION_UPDATE_DATA + " =====My Add Data Request ========>  " + Request);

            HttpTransportSE transport = new HttpTransportSE(Urls.Main_URL, Urls.TimeOut);

            transport.call(Urls.SOAP_ACTION_UPDATE_DATA, soapEnvelope, MainActivity.headerPropertyArrayList);
            Urls.resultString = (SoapPrimitive) soapEnvelope.getResponse();
            Log.i(TAG, "response ========>  " + Urls.resultString.toString());
            transport.getServiceConnection().disconnect();


        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
        return valueVadilation(ResponseTagName.result);
    }

    private boolean valueVadilation(String responseTag) {
        boolean b = false;
        if (Urls.resultString != null) {
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(Urls.resultString.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Getting JSON Array node
            try {
                int s = jsonObj.getInt(responseTag);

                if (s == -1) {
                    b = false;
                } else if (s == 1) {
                    b = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return b;
    }

    private boolean isNetConnected() {
        boolean isConnected = false;

        ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();

        if (nf != null && nf.isConnected()) {

            isConnected = true;

        } else {
            isConnected = false;


        }
        return isConnected;
    }
}
