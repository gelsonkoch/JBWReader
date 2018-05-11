package br.edu.mca.jbwreader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import android.os.Environment;
import android.util.Log;

public class FileUtil {
	public static boolean writeToSD(String data, String filename) {
		boolean write_successful = false;
		File root = null;
		try {
			// check for SDcard
			root = Environment.getExternalStorageDirectory();

			// check sdcard permission
			if (root.canWrite()) {
				File fileDir = new File(root.getAbsolutePath());
				File file = new File(fileDir, filename);
				FileOutputStream fos = new FileOutputStream(file, true);
				PrintWriter pw = new PrintWriter(fos, true);
				pw.println(data);
				fos.flush();
				pw.close();
				fos.close();
				write_successful = true;
			}
		} catch (IOException e) {
			Log.e("ERROR:---",
					"Could not write file to SDCard" + e.getMessage());
			write_successful = false;
		}
		return write_successful;
	}
}