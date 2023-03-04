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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags={"프레임 API"})
@RestController
@RequestMapping("/frame")
@RequiredArgsConstructor
public class FrameController {

    private final AwsS3Service s3Service;
    private final FrameService frameService;
    private final CategoryService categoryService;
    private final UserDetailsServiceImpl userDetailsService;

    @ApiOperation(value = "이미지 데이터 결과 받기 테스트 용")
    @PostMapping(value = "/test", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String test(@RequestPart(value = "multipartFile", required = false) List<MultipartFile> multipartFile) throws IOException {
        System.out.println(multipartFile);
        if(multipartFile != null){
            return "파일 개수 : " + multipartFile.size();
        }
        else{
            return "file is null";
        }
    }

    @ApiOperation(value = "프레임 카테고리별 조회(회원)", notes = "프레임 카테고리별 조회 엔드 포인트")
    @ApiImplicitParam(name="category", value = "half,whole")
    @GetMapping()
    public EntityResponseDto.getFrameAllResponseDto getFrameByCategory(@ApiIgnore Principal principal, @RequestParam(name="category", required = false) String category) {
        if(principal != null){
            User user = userDetailsService.findUser(principal);
            List<FrameDetailDto> responseData = frameService.getFramesByCategory(user.getUserId(), category);
            return new EntityResponseDto.getFrameAllResponseDto(200, "프레임 조회 성공", responseData);
        }
        List<FrameDetailDto> responseData = frameService.getFramesByCategory(0L, category);
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
            notes = "총 카테고리 6개(whole, half, selfie, sit, two, many) 중 사용자가 가장 많이 선택한 " +
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
        categories.put("Two", user.getTwo());
        categories.put("Many", user.getMany());

        //제일 선호하는 순으로 정렬
        Map<String, Integer> sortCategories = frameService.sortByValue(categories);

        //정렬한 카테고리 기준으로 프레임 찾아주기
        List<FrameDetailDto> responseData = frameService.getContentBasedFrame(user, sortCategories);
//        FrameDetailDto test = frameService.getCollaborationFilter(user);

        return new EntityResponseDto.getFrameAllResponseDto(200, "추천 프레임 조회 성공", responseData);
    }

    @ApiOperation(value = "프레임 사용 횟수, 유저 취향 카테고리 업데이트",
            notes = "사용자가 프레임을 사용하면 사용횟수와 유저 취향 카테고리를 업데이트 한다.(추천 갱신에 사용)")
    @PatchMapping("/use")
    public EntityResponseDto.messageResponse updateUserFrameStatus(@ApiIgnore Principal principal, @RequestParam(name="frameId") long frameId) {

        User user = userDetailsService.findUser(principal);
        frameService.updateFrameUser(frameId, user);

        return new EntityResponseDto.messageResponse(200, "프레임 사용 횟수, 유저취향 업데이트 성공");
    }

    @ApiOperation(value = "프레임 좋아요")
    @PatchMapping("/like")
    public EntityResponseDto.messageResponse updateFrameUserLike(@ApiIgnore Principal principal, @RequestParam(name="frameId") long frameId) {

        User user = userDetailsService.findUser(principal);
        frameService.updateFrameUserLike(frameId, user);

        return new EntityResponseDto.messageResponse(200, "프레임 좋아요 성공");
    }

    @ApiOperation(value = "좋아요한 프레임 조회")
    @GetMapping("/like")
    public EntityResponseDto.getFrameAllResponseDto getFrameUserLike(@ApiIgnore Principal principal) {

        User user = userDetailsService.findUser(principal);
        List<FrameDetailDto> responseData = frameService.getFrameUserLike(user);

        return new EntityResponseDto.getFrameAllResponseDto(200, "좋아요한 프레임 조회 성공", responseData);
    }

    @ApiOperation(value = "프레임 좋아요 취소")
    @PatchMapping("/like/cancel")
    public EntityResponseDto.messageResponse cancelFrameUserLike(@ApiIgnore Principal principal, @RequestParam(name="frameId") long frameId) {

        User user = userDetailsService.findUser(principal);
        frameService.cancelFrameUserLike(frameId, user);

        return new EntityResponseDto.messageResponse(200, "프레임 좋아요 취소 성공");
    }
}

