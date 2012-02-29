package ru.myshows.util;

import ru.myshows.domain.IShow;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 18.08.2011
 * Time: 15:19:03
 * To change this template use File | Settings | File Templates.
 */
public class ShowsComparator implements Comparator {

    private String sortBy = "rating";

    public ShowsComparator(String sortBy) {
        this.sortBy = sortBy;
    }

    public ShowsComparator() {
    }

    @Override
    public int compare(Object o1, Object o2) {
        IShow show1 = (IShow) o1;
        IShow show2 = (IShow) o2;

         if (sortBy.equals("rating")) {

             if(show1.getRating() < show2.getRating()) return 1;
             else if(show1.getRating() > show2.getRating()) return 1;
             else return 0;
         }  else if (sortBy.equals("title")){
              return show1.getTitle().compareToIgnoreCase(show2.getTitle());
         }
        return 0;
    }
}
