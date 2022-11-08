package com.sm.expose.frame.dto;

import com.sm.expose.frame.domain.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryDto {

    private long categoryId;
    private String categoryName;

    public CategoryDto(final Category category){
        this.categoryId = category.getCategoryId();
        this.categoryName = category.getCategoryName();
    }

    public Category toEntity() {
        return Category.builder()
                .categoryName(categoryName)
                .build();
    }
}

