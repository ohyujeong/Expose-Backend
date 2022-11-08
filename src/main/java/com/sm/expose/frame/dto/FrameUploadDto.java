package com.sm.expose.frame.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FrameUploadDto {

    private String framePath;
    private String s3FrameName;
}
