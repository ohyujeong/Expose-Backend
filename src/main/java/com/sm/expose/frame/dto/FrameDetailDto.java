package com.sm.expose.frame.dto;

import com.sm.expose.frame.domain.Frame;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class FrameDetailDto {

    @ApiModelProperty(value= "프레임 id")
    private Long frameId;

    @ApiModelProperty(value = "프레임 이름")
    private String frameName;

    @ApiModelProperty(value = "프레임 이미지 경로")
    private String framePath;

    @ApiModelProperty(value = "S3에 저장된 프레임 이름")
    private String s3FrameName;

    @ApiModelProperty(value = "프레임 카테고리", example = "[wholeStand, longHalf]")
    private List<String> categories;


    public static FrameDetailDto from(Frame frame){
        return FrameDetailDto.builder()
                .frameId(frame.getFrameId())
                .frameName(frame.getFrameName())
                .framePath(frame.getFramePath())
                .s3FrameName(frame.getS3FrameName())
                .build();
    }
}
