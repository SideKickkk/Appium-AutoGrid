package com.au.grid;

public class Parameters {

	public static String ROOT_FOLDER = System.getProperty("user.dir");

	public enum Commands {
		SPECIFY_DEVICE("adb -s"), DEVICE_PROPERTY("shell getprop"), GET_DEVICES("adb devices"),
		 GET_ANDROID_VERSION("ro.build.version.release");

		private String val;

		private Commands(String val) {
			this.val = val;
		}

		public String command() {
			return val;
		}

		@Override
		public String toString() {
			return val;
		}
	}

	public enum Constant {
		LOCAL_IP("127.0.0.1"),SERIAL("Serial"),PARALLEL("Parallel");

		private String val;

		private Constant(String val) {
			this.val = val;
		}

		public String value() {
			return val;
		}

		@Override
		public String toString() {
			return val;
		}
	}

}
