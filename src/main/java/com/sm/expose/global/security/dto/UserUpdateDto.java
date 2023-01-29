package com.sm.expose.global.security.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ApiModel
public class UserUpdateDto {

    @ApiModelProperty(value = "1")
    private Integer whole;

    @ApiModelProperty(value = "2")
    private Integer sit;

    @ApiModelProperty(value = "0")
    private Integer half;

    @ApiModelProperty(value = "4")
    private Integer selfie;

    @ApiModelProperty(value = "1")
    private Integer two;

    @ApiModelProperty(value = "3")
    private Integer many;
}
