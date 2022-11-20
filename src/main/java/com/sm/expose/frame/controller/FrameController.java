package com.sm.expose.frame.controller;

import com.sm.expose.frame.domain.Frame;
import com.sm.expose.frame.dto.EntityResponseDto;
import com.sm.expose.frame.dto.FrameCreateDto;
import com.sm.expose.frame.dto.FrameDetailDto;
import com.sm.expose.frame.dto.FrameUploadDto;
import com.sm.expose.frame.service.AwsS3Service;
import com.sm.expose.frame.service.CategoryService;
import com.sm.expose.frame.service.FrameService;
import com.sm.expose.global.security.domain.User;
import com.sm.expose.global.security.service.UserDetailsServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

@Api(tags={"프레임 API"})
@RestController
@RequestMapping("/frame")
@RequiredArgsConstructor
public class FrameController {

    private final AwsS3Service s3Service;
    private final FrameService frameService;
    private final CategoryService categoryService;
    private final UserDetailsServiceImpl userDetailsService;

    @ApiOperation(value = "프레임 카테고리별 조회", notes = "프레임 카테고리별 조회 엔드 포인트")
    @ApiImplicitParam(name="category", value = "half,whole")
    @GetMapping()
    public EntityResponseDto.getFrameAllResponseDto getFrameByCategory(
            @RequestParam(name="category", required = false) String category) {

        List<FrameDetailDto> responseData = frameService.getFramesByCategory(category);
        return new EntityResponseDto.getFrameAllResponseDto(200, "프레임 조회 성공", responseData);
    }

    @ApiOperation(value = "프레임 상세 조회", notes = "프레임 하나를 선택 해서 가져 온다.")
    @GetMapping("/{frameId}")
    public EntityResponseDto.getFrameResponseDto getFrameOne(@RequestParam(name="frameId") long frameId, @ApiIgnore Principal principal) {


        FrameDetailDto responseData = frameService.getOneFrame(frameId);

//        //비회원 유저일 때
//        if(principal == null){
//            responseData = frameService.getOneFrame(frameId);
//        }
//        //회원 유저가 프레임 조회 했을 때 (!=사용) FrameUser에 사용자가 선택한 프레임 저장 (사용여부는 아직 false)
//        else{
//            responseData = frameService.getOneFrameUser(frameId, principal);
//        }
        return new EntityResponseDto.getFrameResponseDto(200, "프레임 조회 성공", responseData);
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

        FrameDetailDto frameDetailDto = frameService.getOneFrame(frame.getFrameId());
        return new EntityResponseDto.getFrameResponseDto(201, "프레임 등록", frameDetailDto);
    }

    @ApiOperation(value = "추천 프레임 조회",
            notes = "총 카테고리 4개(whole, half, selfie, sit) 중 사용자가 가장 많이 선택한 " +
                    "카테고리 2개를 기준으로 2:1의 비율로 3개의 포즈를 추천해줌")
    @GetMapping("/recommend")
    public EntityResponseDto.getFrameAllResponseDto getRecommendFrame(@ApiIgnore Principal principal, @ApiIgnore Pageable pageable) {

        //유저한테서 선호 카테고리 정보 가져오기
        User user = userDetailsService.findUser(principal);
        HashMap<String, Integer> categories = new HashMap<String, Integer>();
        categories.put("Whole", user.getWhole());
        categories.put("Half", user.getHalf());
        categories.put("Selfie", user.getSelfie());
        categories.put("Sit", user.getSit());

        //제일 선호하는 순으로 정렬
        Map<String, Integer> sortCategories = frameService.sortByValue(categories);

        //정렬한 카테고리 기준으로 프레임 찾아주기
        List<FrameDetailDto> responseData = frameService.getRecommendFrame(sortCategories);

        return new EntityResponseDto.getFrameAllResponseDto(200, "추천 프레임 조회 성공", responseData);
    }

    @ApiOperation(value = "프레임 사용 여부 업데이트",
            notes = "사용자가 조회했던 프레임을 사용하면 사용여부를 true로 바꿔준다.(추천 갱신에 사용)")
    @PatchMapping("/use")
    public EntityResponseDto.messageResponse updateUserFrameStatus(@ApiIgnore Principal principal, @RequestParam(name="frameId") long frameId) {

        frameService.updateFrameUser(frameId, principal);

        return new EntityResponseDto.messageResponse(200, "프레임 사용여부 업데이트 성공");
    }
}

