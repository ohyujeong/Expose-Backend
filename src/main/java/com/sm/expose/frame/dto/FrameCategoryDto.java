package com.sm.expose.frame.dto;

import com.sm.expose.frame.domain.Category;
import com.sm.expose.frame.domain.Frame;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class FrameCategoryDto {

    private Frame frame;
    private Category category;

}
