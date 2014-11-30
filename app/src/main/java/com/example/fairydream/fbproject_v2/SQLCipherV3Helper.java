package com.example.fairydream.fbproject_v2;
/**
 * Created by Wendy on 11/29/14.
 */

import android.content.Context;
import android.content.SharedPreferences;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;
public class SQLCipherV3Helper implements SQLiteDatabaseHook {
    private static final String PREFS=
            "net.sqlcipher.database.SQLCipherV3Helper";

    private final Context context;

    public static void resetMigrationFlag(Context ctxt, String dbPath) {
        SharedPreferences prefs=
                ctxt.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(dbPath, false).commit();
    }

    public SQLCipherV3Helper(Context context) {
        this.context = context;
    }

    @Override
    public void preKey(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void postKey(SQLiteDatabase database) {
        SharedPreferences prefs=
                context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        boolean isMigrated=prefs.getBoolean(database.getPath(), false);

        if (!isMigrated) {
            database.rawExecSQL("PRAGMA cipher_migrate;");
            prefs.edit().putBoolean(database.getPath(), true).commit();
        }
    }
}
