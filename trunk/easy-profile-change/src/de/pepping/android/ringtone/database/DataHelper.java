package de.pepping.android.ringtone.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import de.pepping.android.ringtone.fwk.ProfileSettings;

public class DataHelper {

	 private static final String DATABASE_NAME = "de.pepping.android.ringtone";
	   private static final int DATABASE_VERSION = 1;
	   private static final String TABLE_NAME = "profile";

	   private Context context;
	   private SQLiteDatabase db;

	   private SQLiteStatement insertStmt;
	   private SQLiteStatement updateStmt;
	   private static final String INSERT = "insert into " + TABLE_NAME + "(id, profile_id, profile_name, brightness ) values (?,?,?,?)";
	   private static final String UPDATE_PROFILE_BY_ID = "update " + TABLE_NAME + " set profile_name=?, brightness=? where profile_id=?";

	   public DataHelper(Context context) {
	      this.context = context;
	      OpenHelper openHelper = new OpenHelper(this.context);
	      this.db = openHelper.getWritableDatabase();
	      this.insertStmt = this.db.compileStatement(INSERT);
	      this.updateStmt = this.db.compileStatement(UPDATE_PROFILE_BY_ID);
	      
	   }

	   public long insert(int profileId, String profileName, int brightness) {
	      this.insertStmt.bindLong(1, Long.valueOf(profileId));
	      this.insertStmt.bindLong(2, Long.valueOf(profileId));
	      this.insertStmt.bindString(3, profileName);
	      this.insertStmt.bindLong(4, brightness);
	      return this.insertStmt.executeInsert();
	   }
	   
	   public void update(int profileId, String profileName, int brightness) {
//		   this.updateStmt.bindString(1, profileName);
//		   this.updateStmt.bindLong(2, brightness);
//		   this.updateStmt.bindLong(3, Long.valueOf(profileId));
//		   this.updateStmt.execute();
		   
//		   db.execSQL(UPDATE_PROFILE_BY_ID, new Object[]{profileName, brightness,Long.valueOf(profileId)});
		   
		ContentValues values = new ContentValues();
		values.put("brightness", brightness);
		db.update(TABLE_NAME, values, "id=" + profileId, null);
		ProfileSettings findProfileSettingById = findProfileSettingById(profileId);
		Log.d("!!", "!!");
	   }

	   public void deleteAll() {
	      this.db.delete(TABLE_NAME, null, null);
	   }

	   public List<ProfileSettings> selectAll() {
	      List<ProfileSettings> list = new ArrayList<ProfileSettings>();
	      Cursor cursor = this.db.query(TABLE_NAME, new String[] { "profile_id", "profile_name" }, 
	        null, null, null, null, "profile_id desc");
	      if (cursor.moveToFirst()) {
	         do {
	        	 ProfileSettings ps = new ProfileSettings();
	        	 ps.profileId =  Long.valueOf(cursor.getLong(0)).intValue();
	        	 ps.profileName = cursor.getString(1);
	            list.add(ps); 
	         } while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      return list;
	   }
	   
	   public ProfileSettings findProfileSettingById(int profile_id) {
		   ProfileSettings ps = new ProfileSettings();
		   Cursor cursor = this.db.query(TABLE_NAME, new String[] { "id", "profile_id", "profile_name" , "brightness" }, 
				   "profile_id=" +profile_id , null, null, null, "profile_id desc");
		   if (cursor.moveToFirst()) {
			   do {
				   ps.id =  Long.valueOf(cursor.getLong(0)).intValue();
				   ps.profileId =  Long.valueOf(cursor.getLong(1)).intValue();
				   ps.profileName = cursor.getString(2);
				   ps.brightness = (int) cursor.getLong(3);
			   } while (cursor.moveToNext());
		   }
		   if (cursor != null && !cursor.isClosed()) {
			   cursor.close();
		   }
		   return ps;
	   }
	      
	  private static class OpenHelper extends SQLiteOpenHelper {
	
	      OpenHelper(Context context) {
	         super(context, DATABASE_NAME, null, DATABASE_VERSION);
	      }
	
	      @Override
	      public void onCreate(SQLiteDatabase db) {
	         db.execSQL("CREATE TABLE " + TABLE_NAME + "(id INTEGER PRIMARY KEY, profile_id INTEGER, profile_name TEXT, brightness INTEGER)");
	      }
	
	      @Override
	      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	         Log.w("Example", "Upgrading database, this will drop tables and recreate.");
	         db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	         onCreate(db);
	      }
	   }
}