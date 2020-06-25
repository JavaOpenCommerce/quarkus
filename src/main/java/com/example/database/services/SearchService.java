package com.example.database.services;

import com.example.business.models.ItemModel;
import com.example.business.models.PageModel;
import com.example.database.entity.Item;
import com.example.database.repositories.SearchRepository;
import com.example.utils.LanguageResolver;
import com.example.utils.converters.ItemConverter;
import org.hibernate.search.engine.search.query.SearchQuery;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class SearchService {

    private final SearchRepository searchRepository;
    private final LanguageResolver languageResolver;

    public SearchService(SearchRepository searchRepository,
            LanguageResolver languageResolver) {
        this.searchRepository = searchRepository;
        this.languageResolver = languageResolver;
    }

    @Transactional
    public PageModel<ItemModel> searchForAProduct(String pattern, int pageSize, int pageIndex) {
        SearchQuery<Item> itemSearchQuery = searchRepository.searchForAProduct(pattern);

        int pageCount = getPageCount(pageSize, itemSearchQuery.fetchTotalHitCount());

        List<ItemModel> items = itemSearchQuery.fetchHits(pageSize*pageIndex, pageSize).stream()
                .map(i -> ItemConverter
                        .convertToModel(i, languageResolver.getLanguage(), languageResolver.getDefault()))
                .collect(Collectors.toList());

        return PageModel.<ItemModel>builder().items(items)
                .pageSize(pageSize)
                .totalElementsCount((int) itemSearchQuery.fetchTotalHitCount())
                .pageNumber(pageIndex)
                .pageCount(pageCount)
                .build();
    }

    public int getPageCount(int pageSize, long totalCount) {
        return (int) Math.ceil((double)totalCount/pageSize);
    }
}
