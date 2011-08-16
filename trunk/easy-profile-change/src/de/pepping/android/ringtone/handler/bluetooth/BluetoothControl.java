package de.pepping.android.ringtone.handler.bluetooth;

public interface BluetoothControl {
	
	public abstract void setEnabled(boolean enabled);

	public abstract int getBluetoothState();

}