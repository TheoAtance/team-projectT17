package util;

import entity.QuerySpec;
import entity.Restaurant;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;


public class TextMatch {


    public static boolean passesHardFilters(QuerySpec q, Restaurant r) {
        String haystack = fullText(r);

        if (q.must != null) {
            for (String kw : q.must) {
                if (!haystack.contains(norm(kw))) {
                    return false;
                }
            }
        }

        if (q.avoid != null) {
            for (String bad : q.avoid) {
                if (haystack.contains(norm(bad))) {
                    return false;
                }
            }
        }

        // openNow
        if (q.openNow != null && q.openNow && !r.isOpenNow()) {
            return false;
        }

        return true;
    }

    public static double softMatchScore(List<String> kws, Restaurant r) {
        if (kws == null || kws.isEmpty()) {
            return 0.0;
        }
        String haystack = fullText(r);
        int hit = 0;
        for (String kw : kws) {
            if (haystack.contains(norm(kw))) {
                hit++;
            }
        }
        return Math.min(1.0, hit / (double) kws.size());
    }

    private static String fullText(Restaurant r) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(r.getName());
        if (r.getTags() != null) {
            for (String t : r.getTags()) {
                joiner.add(t);
            }
        }
        return norm(joiner.toString());
    }

    private static String norm(String s) {
        if (s == null) return "";
        return s.toLowerCase(Locale.ROOT).trim();
    }
}
