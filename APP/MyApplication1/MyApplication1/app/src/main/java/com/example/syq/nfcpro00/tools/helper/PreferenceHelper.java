package com.example.syq.nfcpro00.tools.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceDataStore;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;

import static android.content.Context.MODE_PRIVATE;


/**
 * Project Name MyApplication1
 * Packege Name com.example.syq.nfcpro00.tools.helper
 * Class Name PreferenceHelper
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/4/14 16:32
 */
public class PreferenceHelper  {
  public static SharedPreferences getSharedPreferences(Context context){
      return context.getSharedPreferences("users",MODE_PRIVATE);
  }

}
