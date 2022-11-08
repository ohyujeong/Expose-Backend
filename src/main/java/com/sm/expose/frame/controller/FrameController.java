package com.sm.expose.frame.controller;

import com.sm.expose.frame.domain.Frame;
import com.sm.expose.frame.dto.EntityResponseDto;
import com.sm.expose.frame.dto.FrameCreateDto;
import com.sm.expose.frame.dto.FrameDetailDto;
import com.sm.expose.frame.dto.FrameUploadDto;
import com.sm.expose.frame.service.AwsS3Service;
import com.sm.expose.frame.service.CategoryService;
import com.sm.expose.frame.service.FrameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/frame")
@RequiredArgsConstructor
public class FrameController {

    private final AwsS3Service s3Service;
    private final FrameService frameService;
    private final CategoryService categoryService;

//    @ApiOperation(value = "프레임 조회(필터링)", notes = "프레임 필터링 조회 엔드 포인트, ")
//    @ApiImplicitParam(name="category", value = "category1,category2")
//    @GetMapping()
//    public EntityResponseDto.getFrameAllResponseDto getStudies(
//            @RequestParam(name="category", required = false) String category,
//            @PageableDefault(size=5, sort="createAt", direction = Sort.Direction.DESC) Pageable pageable) {
//
//        Page<FrameDetailDto> responseData = frameService.getFrames(category, pageable);
//        return new EntityResponseDto.getFrameAllResponseDto(200, "스터디 조회 성공", responseData.getContent(), responseData.getPageable(), responseData.getTotalPages(), responseData.getTotalElements());
//    }

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

