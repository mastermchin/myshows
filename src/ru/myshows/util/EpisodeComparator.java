package ru.myshows.util;

import ru.myshows.domain.Episode;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 13.07.2011
 * Time: 18:58:47
 * To change this template use File | Settings | File Templates.
 */
public class EpisodeComparator implements Comparator<Episode> {

    private String sortBy = "season";

    public EpisodeComparator(String sortBy) {
        this.sortBy = sortBy;
    }

    public EpisodeComparator() {
    }

    @Override
    public int compare(Episode e1, Episode e2) {


        if (sortBy.equals("season")) {
            if (e1.getSeasonNumber() > e2.getSeasonNumber())
                return 1;
            else if (e1.getSeasonNumber() < e2.getSeasonNumber())
                return -1;
            else return 0;

        } else if (sortBy.equals("asc")) {
            if (e1.getEpisodeNumber() > e2.getEpisodeNumber())
                return 1;
            else if (e1.getEpisodeNumber() < e2.getEpisodeNumber())
                return -1;
            else return 0;
        } else if (sortBy.equals("desc")) {
            if (e1.getEpisodeNumber() > e2.getEpisodeNumber())
                return -1;
            else if (e1.getEpisodeNumber() < e2.getEpisodeNumber())
                return 1;
            else return 0;

        } else if (sortBy.equals("shortName")) {
            e1.setShortName("s" + String.format("%1$02d", e1.getSeasonNumber()) + "e" + String.format("%1$02d", e1.getEpisodeNumber()));
            e2.setShortName("s" + String.format("%1$02d", e2.getSeasonNumber()) + "e" + String.format("%1$02d", e2.getEpisodeNumber()));
            return e1.getShortName().compareToIgnoreCase(e2.getShortName());
        } else if (sortBy.equals("date")) {
            if (e1.getAirDate().after(e2.getAirDate()))
                return 1;
            if (e1.getAirDate().before(e2.getAirDate()))
                return -1;
            else return 0;
        }
        return 0;

    }
}
