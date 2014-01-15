package neleso.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataBase_Handler {
	
	// Database creation SQL statement
		private static final String DATABASE_CREATE = "create table pdf "
				+ "(_id integer primary key autoincrement, "
				+ "guid, " + "title," +  "subtitle," + "thumbnail_link," + "thumbnail BLOB not null," + "pdf_link," + "pdf_file," + "pub_date," + "product_identifier," + "uid);";

		public static void onCreate(SQLiteDatabase database) {
			database.execSQL(DATABASE_CREATE);
		}

		public static void onUpgrade(SQLiteDatabase database, int oldVersion,
				int newVersion) {
			Log.w(DataBase_Handler.class.getName(), "Upgrading database from version "
					+ oldVersion + " to " + newVersion
					+ ", which will destroy all old data");
			database.execSQL("DROP TABLE IF EXISTS todo");
			onCreate(database);
		}
	}

