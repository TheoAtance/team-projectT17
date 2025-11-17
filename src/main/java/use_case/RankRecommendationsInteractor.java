package use_case;

import entity.QuerySpec;
import entity.Restaurant;
import util.TextMatch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RankRecommendationsInteractor {

    public List<Restaurant> handle(QuerySpec q, List<Restaurant> candidates) {
        // 根据评分 / should 匹配 / 价格等综合打分
        Collections.sort(candidates, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant a, Restaurant b) {
                double sa = score(q, a);
                double sb = score(q, b);
                return Double.compare(sb, sa); // 按分数从高到低
            }
        });
        return candidates;
    }

    private double score(QuerySpec q, Restaurant r) {
        double s = 0.0;

        // 评分占 0.4
        if (r.getRating() != null) {
            s += (r.getRating() / 5.0) * 0.4;
        }

        // should 关键词匹配占 0.4
        s += TextMatch.softMatchScore(q.should, r) * 0.4;

        // 价格占 0.2（如果有预算）
        s += priceScore(q, r) * 0.2;

        return s;
    }

    private double priceScore(QuerySpec q, Restaurant r) {
        if (q.budgetMax == null || r.getPriceLevel() == null) {
            return 0.0;
        }
        // 非严格，只是示意：价格等级 0/1 当成便宜
        if (q.budgetMax <= 15 && r.getPriceLevel() <= 1) {
            return 1.0;
        }
        return 0.0;
    }
}
