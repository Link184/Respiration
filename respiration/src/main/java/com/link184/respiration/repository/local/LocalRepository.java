package com.link184.respiration.repository.local;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by eugeniu on 3/2/18.
 */

public class LocalRepository<T> {
    private final String DB_NAME = "respiration_db";

    /**
     * Init repository from asset file.
     * @param context android context
     * @param filePath asset file path
     */
    public LocalRepository(Context context, String filePath) {
        initDB(context, filePath);
    }

    private String initDB(Context context, String filePath) {
        try {
            InputStream inputStream = context.getAssets().open(filePath);
            File file = new File(context.getFilesDir(), DB_NAME);
            file.delete();
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
