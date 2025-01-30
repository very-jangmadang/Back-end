package com.example.demo.domain.dto.Search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponseDTO {

    private List<String> recentSearch;
    private List<String> popularSearch;


}
