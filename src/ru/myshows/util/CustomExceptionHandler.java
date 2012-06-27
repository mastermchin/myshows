package ru.myshows.util;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 08.09.2011
 * Time: 2:25:51
 * To change this template use File | Settings | File Templates.
 */
public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultUEH;

    private String localPath;

    private String url;

    /*
     * if any of the parameters is null, the respective functionality
     * will not be used
     */

    public CustomExceptionHandler(String localPath, String url) {
        this.localPath = localPath;
        this.url = url;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable e) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);

        String stacktrace = result.toString() + "\n" + e.getCause().toString();
        printWriter.close();
        System.out.println("stacktrace = " + stacktrace);
        String filename = "log.txt";

        if (localPath != null) {
            writeToFile(stacktrace, filename);
        }
        if (url != null) {
            sendToServer(stacktrace, filename);
        }

        defaultUEH.uncaughtException(t, e);
    }

    private void writeToFile(String stacktrace, String filename) {
        try {
//            BufferedWriter bos = new BufferedWriter(new FileWriter(new File(localPath + "/" + filename)));
//            bos.write(stacktrace);
//            bos.flush();
//            bos.close();
            FileOutputStream trace = new FileOutputStream(new File(localPath, filename));
            trace.write(stacktrace.getBytes());
            trace.flush();
            trace.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToServer(String stacktrace, String filename) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("filename", filename));
        nvps.add(new BasicNameValuePair("stacktrace", stacktrace));
        try {
            httpPost.setEntity(
                    new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
