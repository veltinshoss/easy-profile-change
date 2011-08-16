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
	   private static final String INSERT = "insert into " + TABLE_NAME + "(id, profile_id, profile_name, brightness_mode, brightness, ringer_mode," +
	   		" ringer_stream_ring , ringer_stream_notification , ringer_stream_music , ringer_stream_alarm , ringer_stream_voice_call , ringer_stream_system," +
	   		" bluetooth, wifi, screen_timeout, airplane_mode, auto_rotation ) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	   private static final String UPDATE_PROFILE_BY_ID = "update " + TABLE_NAME + " set profile_name=?, brightness_mode=?, brightness=?, ringer_stream_ring=?, ringer_stream_notification=?, ringer_stream_music=?, ringer_stream_alarm=?, ringer_stream_voice_call=?, ringer_stream_system=?," +
	   		" bluetooth=?, wifi=?, airplane_mode=?, auto_rotation=? " +
	   		"where profile_id=?";

	   public DataHelper(Context context) {
	      this.context = context;
	      OpenHelper openHelper = new OpenHelper(this.context);
	      this.db = openHelper.getWritableDatabase();
	      this.insertStmt = this.db.compileStatement(INSERT);
	      this.updateStmt = this.db.compileStatement(UPDATE_PROFILE_BY_ID);
	      
	   }

	   public long insert(int profileId, String profileName, int brightness, int ringer_mode) {
	      this.insertStmt.bindLong(1, Long.valueOf(profileId));
	      this.insertStmt.bindLong(2, Long.valueOf(profileId));
	      this.insertStmt.bindString(3, profileName);
	      this.insertStmt.bindLong(4, brightness);
	      this.insertStmt.bindLong(5, ringer_mode);
	      return this.insertStmt.executeInsert();
	   }
	   
	   public void update(int profileId, String profileName, int brightness, int ringer) {
//		   this.updateStmt.bindString(1, profileName);
//		   this.updateStmt.bindLong(2, brightness);
//		   this.updateStmt.bindLong(3, Long.valueOf(profileId));
//		   this.updateStmt.execute();
		   
//		   db.execSQL(UPDATE_PROFILE_BY_ID, new Object[]{profileName, brightness,Long.valueOf(profileId)});
		   
		ContentValues values = new ContentValues();
		values.put("brightness", brightness);
		values.put("ringer_mode", ringer);
		db.update(TABLE_NAME, values, "id=" + profileId, null);
//		ProfileSettings findProfileSettingById = findProfileSettingById(profileId);
	   }
	   
	   public void update(ProfileSettings profileSetting) {
		   ContentValues values = new ContentValues();
		   values.put("brightness_mode", profileSetting.brightness_mode);
		   values.put("brightness", profileSetting.brightness);
		   values.put("ringer_mode", profileSetting.ringer_mode);
		   
		   values.put("ringer_stream_alarm", profileSetting.ringer_stream_alarm);
		   values.put("ringer_stream_ring", profileSetting.ringer_stream_ring);
		   values.put("ringer_stream_notification", profileSetting.ringer_stream_notification);
		   values.put("ringer_stream_music", profileSetting.ringer_stream_music);
		   values.put("ringer_stream_voice_call", profileSetting.ringer_stream_voice_call);
		   values.put("ringer_stream_system", profileSetting.ringer_stream_system);
		   
		   values.put("bluetooth", profileSetting.bluetooth);
		   values.put("wifi", profileSetting.wifi);
		   values.put("screen_timeout", profileSetting.screen_timeout);
		   values.put("airplane_mode", profileSetting.airplane_mode);
		   values.put("auto_rotation", profileSetting.auto_rotation);
		   
		   db.update(TABLE_NAME, values, "id=" + profileSetting.profileId, null);
	   }
	   
	   public void updateProfileName(int profileId, String profileName) {
//		   this.updateStmt.bindString(1, profileName);
//		   this.updateStmt.bindLong(2, brightness);
//		   this.updateStmt.bindLong(3, Long.valueOf(profileId));
//		   this.updateStmt.execute();
		   
//		   db.execSQL(UPDATE_PROFILE_BY_ID, new Object[]{profileName, brightness,Long.valueOf(profileId)});
		   
		   ContentValues values = new ContentValues();
		   values.put("profile_name", profileName);
		   db.update(TABLE_NAME, values, "id=" + profileId, null);
//		   ProfileSettings findProfileSettingById = findProfileSettingById(profileId);
	   }

	   public void deleteAll() {
	      this.db.delete(TABLE_NAME, null, null);
	   }

	   public List<ProfileSettings> selectAll() {
	      List<ProfileSettings> list = new ArrayList<ProfileSettings>();
	      Cursor cursor = this.db.query(TABLE_NAME, new String[] { "id", "profile_id", "profile_name" }, 
	        null, null, null, null, "profile_id desc");
	      if (cursor.moveToFirst()) {
	         do {
	        	 ProfileSettings ps = new ProfileSettings();
	        	 ps.id =  Long.valueOf(cursor.getLong(0)).intValue();
	        	 ps.profileId =  Long.valueOf(cursor.getLong(1)).intValue();
	        	 ps.profileName = cursor.getString(2);
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
		   Cursor cursor = this.db.query(TABLE_NAME, new String[] { "id", "profile_id", "profile_name" , "brightness", "ringer_mode", 
				   "ringer_stream_ring", "ringer_stream_notification", "ringer_stream_music", "ringer_stream_alarm", "ringer_stream_voice_call", "ringer_stream_system",
				   "bluetooth", "wifi", "screen_timeout", "airplane_mode", "brightness_mode", "auto_rotation"}, 
				   "profile_id=" +profile_id , null, null, null, "profile_id desc");
		   if (cursor.moveToFirst()) {
			   do {
				   ps.id =  Long.valueOf(cursor.getLong(0)).intValue();
				   ps.profileId =  Long.valueOf(cursor.getLong(1)).intValue();
				   ps.profileName = cursor.getString(2);
				   ps.brightness = (int) cursor.getLong(3);
				   ps.ringer_mode = (int) cursor.getLong(4);
				   ps.ringer_stream_ring=(int) cursor.getLong(5);
				   ps.ringer_stream_notification =(int) cursor.getLong(6);
				   ps.ringer_stream_music=(int) cursor.getLong(7);
				   ps.ringer_stream_alarm =(int) cursor.getLong(8);
				   ps.ringer_stream_voice_call =(int) cursor.getLong(9);
				   ps.ringer_stream_system =(int) cursor.getLong(10);
				   ps.bluetooth = (int) cursor.getInt(11);
				   ps.wifi = (int) cursor.getInt(12);
				   ps.screen_timeout= (int) cursor.getInt(13);
				   ps.airplane_mode= (int) cursor.getInt(14);
				   ps.brightness_mode= (int) cursor.getInt(15);
				   ps.auto_rotation= (int) cursor.getInt(16);
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
	         db.execSQL("CREATE TABLE " + TABLE_NAME + "(id INTEGER PRIMARY KEY, profile_id INTEGER, profile_name TEXT, brightness_mode INTEGER, brightness INTEGER, ringer_mode INTEGER, " +
	         		"ringer_stream_ring INTEGER, ringer_stream_notification INTEGER, ringer_stream_music INTEGER, ringer_stream_alarm INTEGER, ringer_stream_voice_call INTEGER, ringer_stream_system INTEGER," +
	         		" bluetooth INTEGER, wifi INTEGER, screen_timeout INTEGER, airplane_mode INTEGER, auto_rotation INTEGER )");
	         db.execSQL(INSERT, new Object[]{ 1,1,"Profil 1", 0, 250, 3,
	        		 6,6,9,4,1,7,
	        		 0,0,600000,0,1 });
	         db.execSQL(INSERT, new Object[]{ 2,2,"Profil 2", 0, 250, 2,
	        		 6,6,9,4,1,7,
	        		 0,0,600000,0,1 });
	         db.execSQL(INSERT, new Object[]{ 3,3,"Profil 3", 0, 250, 1,
	        		 6,6,9,4,1,7,
	        		 0,0,600000,0,1 });
	         db.execSQL(INSERT, new Object[]{ 4,4,"Profil 4", 0, 250, 0,
	        		 6,6,9,4,1,7,
	        		 0,0,600000,0,1 });
	      }
	
	      @Override
	      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	         Log.w("Example", "Upgrading database, this will drop tables and recreate.");
//	         db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN airplane_mode INTEGER");
	         db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	         onCreate(db);
	      }
	   }
}