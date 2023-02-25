package com.example.syq.nfcpro00.tools.helper.dbhelper;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.syq.nfcpro00.tools.exception.SQLException;

import org.assertj.core.util.Strings;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


/**
 * Project Name MyApplication1
 * Packege Name com.example.syq.nfcpro00.tools.helper.dbhelper
 * Class Name MySQLiteOpenHelper
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/4/14 14:22
 */
@Slf4j
public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        mContext = context;
    }
    @Setter private String SQL ;
    private Context mContext;

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        if (!Strings.isNullOrEmpty(SQL)){
            sqLiteDatabase.execSQL(SQL);
            log.info("Create {} Succeed",SQL);
        }
        else {
            log.error("MySQLiteOpenHelper->onCreate->SQL is Null",new SQLException());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
