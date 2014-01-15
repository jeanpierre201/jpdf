package neleso.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase_Helper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "Neleso";

	private static final int DATABASE_VERSION = 1;

	public DataBase_Helper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase database) {
		DataBase_Handler.onCreate(database);
	}

	// Method is called during an upgrade of the database,
	// e.g. if you increase the database version
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		DataBase_Handler.onUpgrade(database, oldVersion, newVersion);
	}
}
