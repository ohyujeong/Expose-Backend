package com.sm.expose.frame.controller;

import com.sm.expose.frame.domain.Frame;
import com.sm.expose.frame.dto.EntityResponseDto;
import com.sm.expose.frame.dto.FrameCreateDto;
import com.sm.expose.frame.dto.FrameDetailDto;
import com.sm.expose.frame.dto.FrameUploadDto;
import com.sm.expose.frame.service.AwsS3Service;
import com.sm.expose.frame.service.CategoryService;
import com.sm.expose.frame.service.FrameService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/frame")
@RequiredArgsConstructor
public class FrameController {

    private final AwsS3Service s3Service;
    private final FrameService frameService;
    private final CategoryService categoryService;

    @ApiOperation(value = "프레임 카테고리별 조회", notes = "프레임 카테고리별 조회 엔드 포인트")
    @ApiImplicitParam(name="category", value = "category1,category2")
    @GetMapping()
    public EntityResponseDto.getFrameAllResponseDto getStudies(
            @RequestParam(name="category", required = false) String category) {

        List<FrameDetailDto> responseData = frameService.getFramesByCategory(category);
        return new EntityResponseDto.getFrameAllResponseDto(200, "프레임 조회 성공", responseData);
    }

    @ApiOperation(value = "프레임 업로드", notes = "프레임 업로드 엔드포인트")
    @PostMapping(consumes = {"multipart/form-data"})
    public EntityResponseDto.getFrameResponseDto uploadFrame(@ModelAttribute FrameCreateDto frameDto) throws IOException {

        FrameUploadDto frameUploadDto = s3Service.upload(frameDto.getMultipartFile());
        String fileName = frameDto.getMultipartFile().getOriginalFilename();

        Frame frame = new Frame(fileName, frameUploadDto.getFramePath(), frameUploadDto.getS3FrameName());

        //DTO에서 리스트 정보 값 가져와서 차례대로 넣어주기
        for (int i = 0; i < frameDto.getCategories().size(); i++) {
            String category = frameDto.getCategories().get(i);
            List<String> categories = List.of(category);
            frameService.saveFrame(frame);
            categoryService.saveCategory(categories, frame);
        }

        FrameDetailDto frameDetailDto = frameService.getOneFrame(frame);
        return new EntityResponseDto.getFrameResponseDto(201, "프레임 등록", frameDetailDto);
    }
}

