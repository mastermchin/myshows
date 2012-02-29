package ru.myshows.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import ru.myshows.activity.MainActivity;
import ru.myshows.api.MyShowsApi;
import ru.myshows.client.MyShowsClient;
import ru.myshows.domain.IShow;
import ru.myshows.domain.UserShow;
import ru.myshows.prefs.Prefs;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 08.07.2011
 * Time: 18:44:56
 * To change this template use File | Settings | File Templates.
 */
public class MyShowsUtil {

    public static final String APP_DIR_NAME = "MyShows";

    public static final File SD_DIR = Environment.getExternalStorageDirectory();
    public static final File APP_DIR = new File(SD_DIR + "/MyShows");
    public static final File LOG_DIR = new File(APP_DIR_NAME + "/log");


    public static List<IShow> getByWatchStatus(List<IShow> shows, MyShowsApi.STATUS status) {
        List<IShow> list = new ArrayList<IShow>();
        for (IShow show : shows) {
            if (show.getWatchStatus().equals(status)) {
                list.add(show);
            }
        }
        return list;

    }

     public static List<IShow> getUserShowsByWatchStatus(List<UserShow> shows, MyShowsApi.STATUS status) {
        List<IShow> list = new ArrayList<IShow>();
        for (UserShow show : shows) {
            if (show.getWatchStatus().equals(status)) {
                list.add(show);
            }
        }
        return list;

    }


    public static Bitmap getCompressedBitmap(byte[] imageBytes) {
        System.out.println("Original size = " + imageBytes.length);
        if (imageBytes == null || imageBytes.length < 1) return null;
        try {
            InputStream stream = new ByteArrayInputStream(imageBytes);
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeStream(stream, null, o2);
            stream.close();
            System.out.println("Size after resize = " + bitmap.getRowBytes());
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap bytesToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    public static Bitmap getAvatar(String url) {
        if (url == null || url.trim().length() < 1) return null;
        MyShowsClient client = MyShowsClient.getInstance();
        InputStream stream = client.getImage(url);
        if (stream == null) return null;
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        try {
            stream.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getImageBytes(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().length() < 1) return null;
        MyShowsClient client = MyShowsClient.getInstance();
        byte[] originalImageData = streamToBytes(client.getImage(imageUrl));
        Bitmap bitmap = getCompressedBitmap(originalImageData);
        return bitmapToBytes(bitmap);
    }

    private static byte[] streamToBytes(InputStream stream) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[4096];
            while ((nRead = stream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static boolean isServerAvailable() {
        return true;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null);

    }

    public static void copyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ;) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String mark = "-_.!~*'()\"/:?=";

    public static String encodeURI(String argString) {
        StringBuilder uri = new StringBuilder(); // Encoded URL
        // thanks Marco!

        char[] chars = argString.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') ||
                    (c >= 'A' && c <= 'Z') || mark.indexOf(c) != -1) {
                uri.append(c);
            } else {
                uri.append("%");
                uri.append(Integer.toHexString((int) c));
            }
        }
        System.out.println("URL = " + uri.toString());
        return uri.toString();
    }


    public static void applyTheme(Activity activity) {
        int theme = Prefs.getIntPrefs(activity, Prefs.KEY_CURRENT_THEME, Prefs.VALUE_DARK_THEME);
        activity.setTheme(theme);
    }

    public static void changeTheme(Activity activity) {
        int currentTheme = Prefs.getIntPrefs(activity, Prefs.KEY_CURRENT_THEME, Prefs.VALUE_DARK_THEME);

        if (currentTheme == Prefs.VALUE_DARK_THEME) {
            Prefs.setIntPrefs(activity, Prefs.KEY_CURRENT_THEME, Prefs.VALUE_DARK_THEME);
        }
        activity.finish();
        activity.startActivity(new Intent(activity, MainActivity.class));
    }


}
