package controller;

import entity.QuerySpec;
import entity.Restaurant;
import service.LLMClient;
import service.RestaurantCatalog;
import use_case.ParseQuerySpecInteractor;
import use_case.RankRecommendationsInteractor;
import use_case.SearchInCatalogInteractor;

import java.util.List;

public class ChatController {

    private final ParseQuerySpecInteractor parse;
    private final SearchInCatalogInteractor search;
    private final RankRecommendationsInteractor rank;

    public ChatController(LLMClient llmClient, RestaurantCatalog catalog) {
        this.parse = new ParseQuerySpecInteractor(llmClient);
        this.search = new SearchInCatalogInteractor(catalog);
        this.rank = new RankRecommendationsInteractor();
    }


    public List<Restaurant> handleMessage(String userMessage) throws Exception {
        // 1. GPT 解析用户需求
        QuerySpec spec = parse.handle(userMessage);

        // 2. 在本地餐厅列表里做硬过滤
        List<Restaurant> filtered = search.handle(spec);

        // 3. 对结果排序
        List<Restaurant> ranked = rank.handle(spec, filtered);

        // 4. 返回前 N 个（例如 5 个）
        int n = Math.min(5, ranked.size());
        return ranked.subList(0, n);
    }
}
