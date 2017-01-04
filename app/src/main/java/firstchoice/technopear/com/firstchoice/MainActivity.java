package firstchoice.technopear.com.firstchoice;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import firstchoice.technopear.com.firstchoice.common.FieldName;
import firstchoice.technopear.com.firstchoice.common.ProductBean;
import firstchoice.technopear.com.firstchoice.common.RequestMethodTag;
import firstchoice.technopear.com.firstchoice.common.ResponseTagName;
import firstchoice.technopear.com.firstchoice.common.SessionManagement;
import firstchoice.technopear.com.firstchoice.common.Urls;
import firstchoice.technopear.com.firstchoice.swiperefresh.SwipeMenu;
import firstchoice.technopear.com.firstchoice.swiperefresh.SwipeMenuCreator;
import firstchoice.technopear.com.firstchoice.swiperefresh.SwipeMenuItem;
import firstchoice.technopear.com.firstchoice.swiperefresh.SwipeMenuListView;

public class MainActivity extends AppCompatActivity {
    private AppAdapter mAdapter;
    private SwipeMenuListView mListView;
    private ArrayList<ProductBean> mAppList;
    private SessionManagement sessionManagement;
    public static ProductBean productBean;
    public static boolean isEdit_Add_Data;
    private String password, newPassword;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static ArrayList<HeaderProperty> headerPropertyArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManagement = new SessionManagement(MainActivity.this);
        mAppList = new ArrayList<ProductBean>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        headerPropertyArrayList = new ArrayList<HeaderProperty>();
        headerPropertyArrayList.add(new HeaderProperty("Connection", "close"));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Add_Data.class);
                startActivity(intent);
            }
        });

        mListView = (SwipeMenuListView) findViewById(R.id.listView);

        mAdapter = new AppAdapter();
        mListView.setAdapter(mAdapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item

                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        MainActivity.this);
                // set item background
//

                // set item width
                deleteItem.setWidth(dp2px(100));
                // set a icon
                deleteItem.setIcon(R.drawable.edit);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        mListView.setMenuCreator(creator);
//        new getData().execute();

        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                productBean = mAppList.get(position);
                switch (index) {
                    case 0:
                        // open
                        isEdit_Add_Data = true;
                        Intent intent = new Intent(MainActivity.this, Add_Data.class);
                        startActivity(intent);
//                        Toast.makeText(MainActivity.this, "edit position: "+position+ ", "+item.getCompanyName(), Toast.LENGTH_SHORT).show();
                        break;

                }
                return false;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppList = new ArrayList<ProductBean>();
        if (isNetConnected()) {
            new getData().execute();
        } else {
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.change_password) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Select any one")

                    .setPositiveButton("Change Password", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            LayoutInflater factory = LayoutInflater.from(MainActivity.this);

                            //text_entry is an Layout XML file containing two text field to display in alert dialog
                            final View textEntryView = factory.inflate(R.layout.changepassword, null);

                            final EditText edt_oldPassword = (EditText) textEntryView.findViewById(R.id.edt_oldPassword);
                            final EditText edt_newPassword = (EditText) textEntryView.findViewById(R.id.edt_newPassword);
                            final EditText edt_retypePassword = (EditText) textEntryView.findViewById(R.id.edt_retypePassword);


                            final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                            alert.setCancelable(false);
                            alert.setView(textEntryView).setPositiveButton("Save",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton) {
                                            password = edt_oldPassword.getText().toString().trim();
                                            newPassword = edt_newPassword.getText().toString().trim();

                                            updatePasswdAndName(edt_newPassword.getText().toString(), edt_retypePassword.getText().toString());

                                        }
                                    }).setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton) {
     /*
     * User clicked cancel so do some stuff
     */
                                        }
                                    });
                            alert.show();
                        }
                    })
                    .setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            sessionManagement.logoutUser();
                            dialog.cancel();
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mAppList.size();
        }

        @Override
        public ProductBean getItem(int position) {
            return mAppList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.item_list_app, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            ProductBean item = getItem(position);

            holder.tv_name.setText(item.getCompanyName());
//            holder.txt_amount.setText("Rs " + item.getAmount());

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            holder.txt_amount.setText(formatter.format(Integer.parseInt(item.getAmount())) + "");
            holder.txt_box.setText("Box " + item.getBox());
            return convertView;
        }

        class ViewHolder {

            TextView tv_name, txt_amount, txt_box;

            public ViewHolder(View view) {

                tv_name = (TextView) view.findViewById(R.id.tv_name);
                txt_amount = (TextView) view.findViewById(R.id.txt_amount);
                txt_box = (TextView) view.findViewById(R.id.txt_box);
                view.setTag(this);
            }
        }

        @Override
        public int getViewTypeCount() {
            // menu type count
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            // current menu type
            return position % 3;
        }

    }

    private class getData extends AsyncTask<String, Void, ArrayList<ProductBean>> {

        private ProgressDialog Pdialog;
        ArrayList<ProductBean> params = new ArrayList<ProductBean>();

        @Override
        protected void onPreExecute() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        Pdialog = ProgressDialog.show(MainActivity.this, "", "Loading please wait...", true);
                    }
                }
            });

        }

        @Override
        protected ArrayList<ProductBean> doInBackground(String... strings) {
//            Log.i(TAG, "response ========>  " + params.get(i));
            try {
                params = getUserData(getData().toString());
                for (int i = 0; i < params.size(); i++) {
                    mAppList.add(params.get(i));

                }
            } catch (Exception e) {

            }

            return null;
        }


        @Override
        protected void onPostExecute(ArrayList<ProductBean> result) {


            if (Pdialog != null)
                Pdialog.cancel();

            mListView.setAdapter(mAdapter);

        }
    }


    private SoapPrimitive getData() {

        try {

            SoapObject Request = new SoapObject(Urls.NAMESPACE, RequestMethodTag.Get_Data);

            SoapObject soapLogs = new SoapObject(Urls.NAMESPACE, "");

            soapLogs.addProperty(FieldName.TABLE, FieldName.TABLE_DND_SALES_INFO);
//            soapLogs.addProperty(FieldName.QUERY, FieldName.DND_ACC_INFO_ID +"='"+sessionManagement.getAccId()+"'"); //123132
            soapLogs.addProperty(FieldName.QUERY, FieldName.DND_ACC_INFO_ALIAS_ID + "='" + sessionManagement.getAliasId() + "'ORDER BY " + FieldName.DND_SALES_INFO_ID + " DESC");
            soapLogs.addProperty(FieldName.LIMIT, "0,10");
//            soapLogs.addProperty(FieldName.FIELD_VALUE, "'" + sessionManagement.getAccId() + "','" + sessionManagement.getType() + "','" + edt_companyName.getText().toString().trim() + "','"+ edt_Amount.getText().toString().trim()+"','" + edt_box.getText().toString().trim()+"','"+"'0'");

            Request.addSoapObject(soapLogs);

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet = true;
            soapEnvelope.setOutputSoapObject(Request);
            Log.i(TAG, Urls.SOAP_ACTION_CREATE_ACCOUNT + " =====My Get Data Request ========>  " + Request);
//
            HttpTransportSE transport = new HttpTransportSE(Urls.Main_URL, Urls.TimeOut);

            transport.call(Urls.SOAP_ACTION_CREATE_ACCOUNT, soapEnvelope, headerPropertyArrayList);
            Urls.resultString = (SoapPrimitive) soapEnvelope.getResponse();
            Log.i(TAG, "response ========>  " + Urls.resultString.toString());
            transport.getServiceConnection().disconnect();


        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
        return Urls.resultString;
    }

    private ArrayList<ProductBean> getUserData(String responseTag) {
        ArrayList<ProductBean> productBeans = new ArrayList<ProductBean>();

//        boolean b = false;
        if (responseTag != null) {
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(responseTag.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                int s = jsonObj.getInt(ResponseTagName.record_count);
                if (s > 0) {
                    try {
                        String str = jsonObj.getString("data");
                        JSONArray jsonArray = new JSONArray(str);


                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject c = jsonArray.getJSONObject(i);
                                ProductBean productBean = new ProductBean();
                                productBean.setId(c.getLong(FieldName.DND_SALES_INFO_ID));
                                productBean.setAcc_id(c.getLong(FieldName.DND_ACC_INFO_ALIAS_ID));
                                productBean.setType(c.getInt(FieldName.TYPE));
                                productBean.setCompanyName(c.getString(FieldName.DND_SALES_INFO_COMPANY_NAME));
                                productBean.setAmount(c.getString(FieldName.DND_SALES_AMOUNT));
                                productBean.setBox(c.getInt(FieldName.DND_SALES_BOX));
                                productBeans.add(productBean);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i(TAG, "====productBeans ========>  " + productBeans.size());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return productBeans;
    }

    private void updatePasswdAndName(String newPassword, String repeatPassword) {
        if (checkNewPassword(newPassword, repeatPassword)) {
            ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nf = cn.getActiveNetworkInfo();

            if (nf != null && nf.isConnected() == true) {
                new passwdvalidation().execute();
            } else {
                Toast.makeText(MainActivity.this,
                        R.string.no_internet, Toast.LENGTH_LONG).show();
            }


        } else {
            Snackbar.make(mListView, "New password & Repeat password does not match, please try again", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        }

    }

    private boolean checkNewPassword(String newPassword, String repeatPassword) {
        boolean isPasswdSame = false;
        if (!newPassword.trim().isEmpty() && !repeatPassword.trim().isEmpty()) {
            if (newPassword.trim().contains("&")) {
                Toast.makeText(MainActivity.this, "Password must not contain '&' symbol", Toast.LENGTH_LONG).show();
            } else {
                if (newPassword.trim().equalsIgnoreCase(repeatPassword.trim())) {
                    isPasswdSame = true;
                } else {
                    isPasswdSame = false;
                }
            }

        }

        return isPasswdSame;
    }

    private class passwdvalidation extends AsyncTask<String, Void, String> {

        boolean isLoginTrue;


        ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                "Please wait...", true);

        @Override
        protected String doInBackground(String... params) {
            if (LoginValidation()) {
                isLoginTrue = true;
            } else {
                isLoginTrue = false;
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("", "ServerSettings.resultString.toString()========================>    " + Urls.resultString.toString());
            dialog.cancel();
            if (isLoginTrue) {
//                UpdatePasswdAndName();
                SoapObject Request = new SoapObject(Urls.NAMESPACE, RequestMethodTag.Update_Data);

                SoapObject soapLogs = new SoapObject(Urls.NAMESPACE, "");

                soapLogs.addProperty(FieldName.TABLE, FieldName.TABLE_DND_ACC_INFO);
                soapLogs.addProperty(FieldName.FIELD_VALUE, FieldName.DND_ACC_INFO_PASSWORD + "=" + "'" + newPassword + "'");
                soapLogs.addProperty(FieldName.QUERY, FieldName.DND_ACC_INFO_ALIAS_ID + "=" + "'" + sessionManagement.getAliasId() + "'");

                Request.addSoapObject(soapLogs);
                Log.i("========>", "UpdatePasswdAndName =======>" + soapLogs);
                SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                soapEnvelope.dotNet = true;
                soapEnvelope.setOutputSoapObject(Request);

                HttpTransportSE transport = new HttpTransportSE(Urls.Main_URL, Urls.TimeOut);
                try {
                    transport.call(Urls.SOAP_ACTION_UPDATE_DATA, soapEnvelope, MainActivity.headerPropertyArrayList);
                    Urls.resultString = (SoapPrimitive) soapEnvelope.getResponse();
                    transport.getServiceConnection().disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                if (Urls.resultString != null) {

                    if (LoginResultVadilation(ResponseTagName.result)) {

                        Toast.makeText(MainActivity.this, "Password updated successfully", Toast.LENGTH_LONG).show();


                    } else {
                        Toast.makeText(MainActivity.this, "Oops! something went wrong, please do changes after some time", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Snackbar.make(mListView, "Current password does not match, Please try again", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private boolean LoginValidation() {
        SoapObject Request = new SoapObject(Urls.NAMESPACE, RequestMethodTag.Get_Data);

        SoapObject soapLogs = new SoapObject(Urls.NAMESPACE, "");

        soapLogs.addProperty(FieldName.TABLE, FieldName.TABLE_DND_ACC_INFO);
        soapLogs.addProperty(FieldName.QUERY, FieldName.DND_ACC_INFO_ALIAS_ID + "=" + "'" + sessionManagement.getAliasId().toString().trim() + "'" + " and " + FieldName.DND_ACC_INFO_PASSWORD + "=" + "'" + password + "'");//name='xyz'
        soapLogs.addProperty(FieldName.LIMIT, "0,1");
        Log.i("========>", "LoginValidation =======>" + soapLogs);

        Request.addSoapObject(soapLogs);

        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.dotNet = true;
        soapEnvelope.setOutputSoapObject(Request);

//
        HttpTransportSE transport = new HttpTransportSE(Urls.Main_URL, Urls.TimeOut);

        try {
            transport.call(Urls.SOAP_ACTION_GET_DATA, soapEnvelope, headerPropertyArrayList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        try {
            Urls.resultString = (SoapPrimitive) soapEnvelope.getResponse();
            try {
                transport.getServiceConnection().disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        }
        return LoginResultVadilation(ResponseTagName.record_count);
    }

    private boolean LoginResultVadilation(String responseTag) {
        boolean b = false;
        Log.i("========>", "LoginValidation =======>" + Urls.resultString);
        if (Urls.resultString != null) {
            JSONObject jsonObj = null;
            try {

                jsonObj = new JSONObject(Urls.resultString.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Getting JSON Array node
            try {
//                int s = jsonObj.getInt(responseTag);

                if (jsonObj.getInt(responseTag) == -1) {
                    b = false;
                } else if (jsonObj.getInt(responseTag) == 1) {
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
