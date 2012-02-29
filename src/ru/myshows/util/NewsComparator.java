package ru.myshows.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: gb
 * Date: 11.08.2011
 * Time: 23:47:24
 * To change this template use File | Settings | File Templates.
 */
public class NewsComparator implements Comparator {

    DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public int compare(Object o1, Object o2) {
        String s1 = (String) o1;
        String s2 = (String) o2;

        try {
            Date d1 = df.parse(s1);
            Date d2 = df.parse(s2);
            if (d1.before(d2)) return 1;
            else if (d2.before(d1)) return -1;
            else return 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;

    }
}
