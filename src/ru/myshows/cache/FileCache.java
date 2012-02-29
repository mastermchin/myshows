package ru.myshows.cache;

import android.content.Context;
import ru.myshows.util.MyShowsUtil;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 06.09.2011
 * Time: 13:40:13
 * To change this template use File | Settings | File Templates.
 */
public class FileCache {

    private File cacheDir;

    public FileCache(Context context) {
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(MyShowsUtil.SD_DIR, MyShowsUtil.APP_DIR_NAME);
        else
            cacheDir = context.getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getFile(String url) {
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename = String.valueOf(url.hashCode());
        File f = new File(cacheDir, filename);
        return f;

    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        for (File f : files)
            f.delete();
    }
}
