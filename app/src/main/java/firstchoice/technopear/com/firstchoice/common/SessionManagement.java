package firstchoice.technopear.com.firstchoice.common;

/**
 * Created by sanjay on 28/12/15.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import firstchoice.technopear.com.firstchoice.LoginActivity;


public class SessionManagement {
    static SharedPreferences pref;

    Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;


    private static String PREF_NAME = "FirstChoice_Login";


    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String ALAIS_ID = "Mail";
    private static final String ACC_ID = "dnd_acc_info_id";
    private static final String TYPE = "TYPE";


    @SuppressLint("CommitPrefEdits")
    public SessionManagement(Context context) {
        this._context = context;
//        this.PREF_NAME = PREF_NAME;
        pref = _context.getSharedPreferences(this.PREF_NAME, PRIVATE_MODE);

        editor = pref.edit();
    }


    /**
     * Create login session
     */
    public void createLoginSession(String alias_id, int type) { //,String username

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(ALAIS_ID, alias_id);
        editor.putInt(TYPE, type);
        editor.commit();
    }


    public void logoutUser() {

        editor.clear();
        editor.commit();
    }

    public String getAccId() {
        return pref.getString(ACC_ID, null);
    }

    public int getType() {
        return pref.getInt(TYPE, 0);
    }

    public String getAliasId() {
        return pref.getString(ALAIS_ID, null);
    }


    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

}

