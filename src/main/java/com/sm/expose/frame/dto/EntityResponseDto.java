package com.sm.expose.frame.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class EntityResponseDto {

    private int status;
    private String message;
    private Object data;

    @Getter
    @AllArgsConstructor
    public static class getFrameAllResponseDto{
        private int status;
        private String message;
        private List<FrameDetailDto> data;
        private Pageable pageable;
        private Integer totalPages;
        private long totalElement;
    }

    @Getter
    @AllArgsConstructor
    public static class getFrameResponseDto{
        private int status;
        private String message;
        private FrameDetailDto data;
    }

    @Getter
    @AllArgsConstructor
    public static class messageResponse{
        private int status;
        private String message;
    }

}

