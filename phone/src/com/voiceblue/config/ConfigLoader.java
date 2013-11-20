package com.voiceblue.config;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;

import com.csipsimple.api.SipProfile;
import com.csipsimple.db.DBProvider;
import com.csipsimple.utils.Log;

public class ConfigLoader {

	private static final String TAG = "ConfigLoader";	
	private static VoiceBlueAccount mLoadedAccount;
	
	public static VoiceBlueAccount loadFromResult(ConfigDownloaderResult result) {
		
		try {
			JSONObject config = result.getObjectResult();			
			
			JSONArray jsonAcccounts = config.getJSONArray("accounts");
			VoiceBlueAccount account = null;
			
			// TODO fix this
			for (int i = 0; i < 1; i++) {
				account = new VoiceBlueAccount();
				
				account.setUsername(jsonAcccounts.getJSONObject(i).getString("username"));
				account.setPassword(jsonAcccounts.getJSONObject(i).getString("data"));
				account.setDisplayName(jsonAcccounts.getJSONObject(i).getString("display_name"));
				account.setRegURI(jsonAcccounts.getJSONObject(i).getString("reg_uri"));
				account.setAccID(jsonAcccounts.getJSONObject(i).getString("acc_id"));
				account.setProxy(jsonAcccounts.getJSONObject(i).getString("proxy"));
				account.setRealm(jsonAcccounts.getJSONObject(i).getString("realm"));
				account.setRegUseProxy(jsonAcccounts.getJSONObject(i).getString("reg_use_proxy"));
			}

			mLoadedAccount = account;
			return mLoadedAccount;
		}
		catch(Exception e) {
			e.printStackTrace();			
			return null;
		}		
	}

	public static VoiceBlueAccount getLoadedAccount() {			
		return mLoadedAccount;
	}
	
	public static VoiceBlueAccount loadFromDatabase(Context ctx) {	
		Cursor c = ctx.getContentResolver().query(SipProfile.ACCOUNT_URI, DBProvider.ACCOUNT_FULL_PROJECTION, 
				SipProfile.FIELD_ACTIVE + "=?", new String[] {"1"}, null);
		
		if (c != null) {
			try {				
				if(c.getCount() > 0) {
					// load only the first
					// TODO load every account and register all of them
    				
					c.moveToFirst();
    				SipProfile account = new SipProfile(c);
    				mLoadedAccount = new VoiceBlueAccount();
    				
    				mLoadedAccount.setUsername(account.getSipUserName());
    				mLoadedAccount.setPassword(account.getPassword());
    				mLoadedAccount.setDisplayName(account.getDisplayName());
    				mLoadedAccount.setRegURI(account.reg_uri);
    				mLoadedAccount.setAccID(account.acc_id);
    				mLoadedAccount.setProxy(account.getProxyAddress());
    				mLoadedAccount.setRealm(account.realm);
    				mLoadedAccount.setRegUseProxy(Integer.toString(account.reg_use_proxy));
    				
    				return mLoadedAccount;
				}
			} catch (Exception e) {
				Log.e(TAG, "Error on looping over sip profiles", e);
			} finally {
				c.close();
			}					
		}
		
		return null;
	}

//	public static void setLoadedAccount(VoiceBlueAccount mLoadedAccount) {
//		ConfigLoader.mLoadedAccount = mLoadedAccount;
//	}
	
}