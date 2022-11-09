package com.sm.expose.frame.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ApiModel
public class FrameCreateDto {

//    @ApiModelProperty(value = "프레임 이름")
//    private String frameName;

    @ApiModelProperty(value="프레임 카테고리", required = true, dataType = "List")
    private List<String> categories = new ArrayList<>();

    @ApiModelProperty(value="프레임 파일 ", required = false)
    private MultipartFile multipartFile;


//    //DTO를 Entity로 변환해주기 위한 메소드
//    public Frame toEntity(){
//        return Frame.builder()
//                .frameName(frameName)
//                .build();
//    }

    @Getter
    @AllArgsConstructor
    public static class downloadFileResponse{
        private byte[] bytes;
        private String frameName;
    }
}
