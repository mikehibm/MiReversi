package com.example.mireversi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Pref  extends PreferenceActivity{
	
	private static final String KEY_SHOW_HINTS = "show_hints";
	
	private SharedPreferences mSharedPreferences = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.pref);
        mSharedPreferences =  getPreferenceScreen().getSharedPreferences();

        setResult(RESULT_OK, null);        
    }
    
    public static boolean getShowHints(Context con){  
    	boolean value = PreferenceManager.getDefaultSharedPreferences(con).getBoolean(KEY_SHOW_HINTS, true);
    	return value;
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
