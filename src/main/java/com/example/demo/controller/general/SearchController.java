package com.example.demo.controller.general;

import com.example.demo.base.ApiResponse;
import com.example.demo.base.status.SuccessStatus;
import com.example.demo.domain.dto.Search.SearchResponseDTO;
import com.example.demo.service.general.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permit/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "최근검색어와 인기검색어 조회")
    @GetMapping("")
    public ApiResponse<SearchResponseDTO.RecentPopularSearchDTO> getPopularSearch(){
        SearchResponseDTO.RecentPopularSearchDTO result = searchService.getRecentPopularSearch(null);
        return ApiResponse.of(SuccessStatus._OK, result);

    }

    @Operation(summary = "검색")
    @GetMapping("/raffles")
    public ApiResponse<SearchResponseDTO.SearchRaffleListDTO> searchRaffles(@RequestParam("keyword") String keyword,
                                                                            @RequestParam(defaultValue = "1") int page,
                                                                            @RequestParam(defaultValue = "16") int size){
        SearchResponseDTO.SearchRaffleListDTO result = searchService.searchRaffles(keyword, null, page, size);
        return ApiResponse.of(SuccessStatus._OK, result);
    }

}
