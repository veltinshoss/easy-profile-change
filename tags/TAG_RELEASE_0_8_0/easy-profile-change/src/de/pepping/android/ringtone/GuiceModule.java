package de.pepping.android.ringtone;

import roboguice.config.AbstractAndroidModule;

public class GuiceModule extends AbstractAndroidModule {

	 @Override
	    protected void configure() {
	        /*
	         * Here is the place to write the configuration specific to your application, i.e. your own custom bindings.
	         */
//	        bind(DBManager.class).toInstance(new DBManager(null));
	 }

}
