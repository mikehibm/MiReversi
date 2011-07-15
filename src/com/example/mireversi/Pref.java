package com.example.mireversi;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Pref  extends PreferenceActivity{
	
	private static final String KEY_SHOW_HINTS = "show_hints";
	private static final String KEY_STATE = "status";
	
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

	private void dispSummary(){
		String s;
		
		if (getShowHints(getBaseContext())){
			s = getString(R.string.pref_show_hints_on);
		} else {
			s = getString(R.string.pref_show_hints_off);
		}
		
		findPreference(KEY_SHOW_HINTS).setSummary(s);
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
