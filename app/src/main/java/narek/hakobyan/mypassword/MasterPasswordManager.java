package narek.hakobyan.mypassword;

import android.content.Context;
import android.content.SharedPreferences;

public class MasterPasswordManager {
    private static final String PREF_FILE = "secure_auth_prefs";
    private static final String KEY_PASSWORD_HASH = "master_password_hash";

    private final SharedPreferences prefs;
    private final PasswordHashManager hashManager;

    public MasterPasswordManager(Context context) {
        prefs = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        hashManager = new PasswordHashManager();
    }

    public boolean hasMasterPassword() {
        return prefs.contains(KEY_PASSWORD_HASH);
    }

    public void saveMasterPassword(String plainPassword) {
        String hash = hashManager.hashPassword(plainPassword);
        prefs.edit().putString(KEY_PASSWORD_HASH, hash).apply();
    }

    public boolean verifyMasterPassword(String plainPassword) {
        String storedHash = prefs.getString(KEY_PASSWORD_HASH, null);
        return hashManager.verifyPassword(plainPassword, storedHash);
    }
}
