/*
* Copyright (c) 2011 Makoto Ishida
* Please see the file MIT-LICENSE.txt for copying permission.
*/

package com.example.mireversi;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Pref  extends PreferenceActivity{
	
	private static final String KEY_SHOW_HINTS = "show_hints";
	private static final String KEY_STATE = "status";
	private static final String KEY_PLAYER1 = "player1";
	private static final String KEY_PLAYER1_NAME = "player1_name";
	private static final String KEY_PLAYER2 = "player2";
	private static final String KEY_PLAYER2_NAME = "player2_name";
	
	private SharedPreferences mSharedPreferences = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.pref);
        mSharedPreferences =  getPreferenceScreen().getSharedPreferences();

        setResult(RESULT_OK, null);        
    }
    
    public static boolean getShowHints(Context con){
    	boolean def_value = Boolean.valueOf(con.getString(R.string.pref_show_hints_default));
    	boolean value = PreferenceManager.getDefaultSharedPreferences(con)
    						.getBoolean(KEY_SHOW_HINTS, def_value);
    	return value;
    }
    
    public static String getState(Context con){
    	String value = PreferenceManager.getDefaultSharedPreferences(con)
    						.getString(KEY_STATE, "");
    	return value;
    }
    
    public static void setState(Context con, String value){
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
    	Editor editor = sp.edit();
    	editor.putString(KEY_STATE, value);
    	editor.commit();
    }

    public static String getPlayer1(Context con){
    	String value = PreferenceManager.getDefaultSharedPreferences(con)
    						.getString(KEY_PLAYER1, con.getString(R.string.pref_player1_default));
    	return value;
    }
    
    public static void setPlayer1(Context con, String value){
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
    	Editor editor = sp.edit();
    	editor.putString(KEY_PLAYER1, value);
    	editor.commit();
    }

    public static String getPlayer1Name(Context con){
    	String value = PreferenceManager.getDefaultSharedPreferences(con)
    						.getString(KEY_PLAYER1_NAME, con.getString(R.string.pref_player1_name_default));
    	return value;
    }
    
    public static void setPlayer1Name(Context con, String value){
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
    	Editor editor = sp.edit();
    	editor.putString(KEY_PLAYER1_NAME, value);
    	editor.commit();
    }

    public static String getPlayer2(Context con){
    	String value = PreferenceManager.getDefaultSharedPreferences(con)
    						.getString(KEY_PLAYER2, con.getString(R.string.pref_player2_default));
    	return value;
    }
    
    public static void setPlayer2(Context con, String value){
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
    	Editor editor = sp.edit();
    	editor.putString(KEY_PLAYER2, value);
    	editor.commit();
    }

    public static String getPlayer2Name(Context con){
    	String value = PreferenceManager.getDefaultSharedPreferences(con)
    						.getString(KEY_PLAYER2_NAME, con.getString(R.string.pref_player2_name_default));
    	return value;
    }
    
    public static void setPlayer2Name(Context con, String value){
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
    	Editor editor = sp.edit();
    	editor.putString(KEY_PLAYER2_NAME, value);
    	editor.commit();
    }

	private void dispSummary(){
		String s;
		
		if (getShowHints(getBaseContext())){
			findPreference(KEY_SHOW_HINTS).setSummary(R.string.pref_show_hints_on);
		} else {
			findPreference(KEY_SHOW_HINTS).setSummary(R.string.pref_show_hints_off);
		}
		
		ListPreference listpref =(ListPreference)(findPreference(KEY_PLAYER1));
		listpref.setSummary((String)listpref.getEntry());
		
		s = getPlayer1Name(getBaseContext());
		findPreference(KEY_PLAYER1_NAME).setSummary(s);

		listpref =(ListPreference)(findPreference(KEY_PLAYER2));
		listpref.setSummary((String)listpref.getEntry());

		s = getPlayer2Name(getBaseContext());
		findPreference(KEY_PLAYER2_NAME).setSummary(s);
	}
    
    @Override  
    protected void onResume() {  
        super.onResume();  
        mSharedPreferences =  getPreferenceScreen().getSharedPreferences();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);  
        dispSummary();
    }  
       
    @Override  
    protected void onPause() {  
        super.onPause();  
        mSharedPreferences =  getPreferenceScreen().getSharedPreferences();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);  
    }  
    
    private SharedPreferences.OnSharedPreferenceChangeListener listener =   
        new SharedPreferences.OnSharedPreferenceChangeListener() {  
           
		public void onSharedPreferenceChanged(SharedPreferences sp, String key) {  
			dispSummary();
		}
    };
   
}
