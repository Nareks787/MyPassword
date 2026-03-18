package narek.hakobyan.mypassword;

import android.content.Context;
import android.content.SharedPreferences;

public class MasterPasswordManager {
    private static final String PREF_FILE = "secure_auth_prefs";
    private static final String KEY_PASSWORD = "master_password";

    private final SharedPreferences prefs;

    public MasterPasswordManager(Context context) {
        prefs = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
    }

    public boolean hasMasterPassword() {
        String storedPassword = prefs.getString(KEY_PASSWORD, null);
        return storedPassword != null && !storedPassword.isEmpty();
    }

    public void saveMasterPassword(String plainPassword) {
        prefs.edit().putString(KEY_PASSWORD, plainPassword).apply();
    }

    public boolean verifyMasterPassword(String plainPassword) {
        String storedPassword = prefs.getString(KEY_PASSWORD, null);
        return plainPassword != null && plainPassword.equals(storedPassword);
    }
}
