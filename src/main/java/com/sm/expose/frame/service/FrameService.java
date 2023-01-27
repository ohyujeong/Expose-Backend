package com.sm.expose.frame.service;

import com.sm.expose.frame.domain.Category;
import com.sm.expose.frame.domain.Frame;
import com.sm.expose.frame.domain.FrameCategory;
import com.sm.expose.frame.domain.FrameUser;
import com.sm.expose.frame.dto.FrameDetailDto;
import com.sm.expose.frame.respository.CategoryRepository;
import com.sm.expose.frame.respository.FrameCategoryRepository;
import com.sm.expose.frame.respository.FrameRepository;
import com.sm.expose.frame.respository.FrameUserRepository;
import com.sm.expose.global.security.domain.User;
import com.sm.expose.global.security.dto.UserUpdateDto;
import com.sm.expose.global.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor //final에 있는 애로 생성자 만들어줌 (lombok의 기능)
public class FrameService {

    private final FrameRepository frameRepository;
    private final FrameCategoryRepository frameCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final FrameUserRepository frameUserRepository;

    public long saveFrame(Frame frame){
        Frame createFrame = frameRepository.save(frame);
        return createFrame.getFrameId();

    }

    public List<FrameDetailDto> getFramesByCategory(String categoryQuery){

        //들어온 쿼리 카테고리 리스트로 만듦
        List <String> categories = Arrays.asList(categoryQuery.split(","));

        List<Long> frameIdList = new ArrayList<>();
        List<FrameDetailDto> result = new ArrayList<>();

        for(int i=0; i<categories.size(); i++){

            //findByName으로 카테고리 객체 받아오기
            Category category = categoryRepository.findByCategoryName(categories.get(i));
            List<FrameCategory> frameCategories = frameCategoryRepository.findByCategoryId(category.getCategoryId());

            for(int j =0; j<frameCategories.size(); j++){
                frameIdList.add(frameCategories.get(j).getFrame().getFrameId());
            }
        }

        List<Long> removeDuplicateFrame = frameIdList.stream().distinct().collect(Collectors.toList());

        for(int i=0; i<removeDuplicateFrame.size(); i++){
            Optional<Frame> frame = frameRepository.findById(removeDuplicateFrame.get(i));
            FrameDetailDto frameDetailDto = FrameDetailDto.from(frame.get());
            frameDetailDto.setCategories(this.getCategory(frame.get()));
            result.add(frameDetailDto);
        }
        return result;
    }

    public HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm){
        // 1
        List<Map.Entry<String, Integer> > list =
                new LinkedList<Map.Entry<String, Integer>>(hm.entrySet());
        // 2
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        // 3
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        // 4
        return temp;
    }

    public List<FrameDetailDto> getRecommendFrame(Map<String, Integer> hm){

        List<String> categories = new ArrayList<>();
        List<FrameDetailDto> result = new ArrayList<>();

        //제일 많이 나온 카테고리 2개를 찾아서 리스트에 넣어주기
        for (Map.Entry<String, Integer> en : hm.entrySet()) { categories.add(en.getKey()); }
        String mostCategory = categories.get(0);
        String mostCategory2 = categories.get(1);

        // 3개의 랜덤 프레임 ID를 추출해서 이 리스트에 넣어줌
        // 제일 많이 나온 카테고리 + 그 다음 카테고리를 2:1 비율로
        List<Long> frameIdList = new ArrayList<>();

        //일단 존재하는 모든 프레임 다 가져옴
        List<Frame> frames = frameRepository.findAll();

        //frame 추출여부를 true/false로 체크해서 랜덤 추출할 때 중복 제거
        Boolean[] bool = new Boolean[frames.size()+1];
        Arrays.fill(bool,false);

        //모든 프레임들의 ID
        List<Long> allFrameIds = new ArrayList<>();
        for (Frame value : frames) {
            allFrameIds.add(value.getFrameId());
        }

        //mostCategory (이 카테고리에서 프레임 2개 추출) 에 속해 있는 프레임 전체를 가져오기
        Category category = categoryRepository.findByCategoryName(mostCategory);
        List<FrameCategory> frameCategories = frameCategoryRepository.findByCategoryId(category.getCategoryId());

        List<Long> mostFrameIds = new ArrayList<>();

        //mostCategory에 속한 프레임들의 ID를 mostFrameIds 리스트에 넣어주고 이 리스트에서 랜덤으로 프레임들을 뽑아줌
        for (FrameCategory frameCategory : frameCategories) {
            mostFrameIds.add(frameCategory.getFrame().getFrameId());
        }

        Random random = new Random();

        for(int i=0; i<2; i++){
            int randomIndex = random.nextInt(mostFrameIds.size());
            int frameIndex = mostFrameIds.get(randomIndex).intValue();

            //중복 체크를 위해 뽑은 프레임은 true 로 바꿔줌
            if(!bool[frameIndex]){
                Long frameId = (long) frameIndex;
                frameIdList.add(frameId);
                bool[frameIndex] = true;
            }
            //프레임이 true이고, 아직 2개를 다 안 뽑았으면 중복이 없는 한 개를 더 뽑을 때까지 for 반복
            else if(bool[frameIndex] && frameIdList.size() <2){
                i -=1;
            }
        }

        // mostCategory2 랜덤 찾기 - 프레임 1개 추출
        Category category2 = categoryRepository.findByCategoryName(mostCategory2);
        List<FrameCategory> frameCategories2 = frameCategoryRepository.findByCategoryId(category2.getCategoryId());
        List<Long> mostFrameIds2 = new ArrayList<>();

        //mostCategory2에 속한 프레임들의 ID를 넣어줌
        for (FrameCategory frameCategory : frameCategories2) {
            mostFrameIds2.add(frameCategory.getFrame().getFrameId());
        }

        //앞서 뽑은 mostCategory의 프레임과 겹치지 않게 중복 체크를 해줌
        for(int i=0; i<1; i++){
            int randomIndex = random.nextInt(mostFrameIds2.size());
            int frameIndex = mostFrameIds2.get(randomIndex).intValue();
            if(!bool[frameIndex]){
                Long frameId = (long) frameIndex;
                frameIdList.add(frameId);
                bool[frameIndex] = true;
            }
            else if(bool[frameIndex] && frameIdList.size() <3){
                i -=1;
            }
        }

        for(int i=0; i<1; i++){
            int randomIndex = random.nextInt(frames.size());
            int frameIndex = allFrameIds.get(randomIndex).intValue();
            if(!bool[frameIndex]){
                Long frameId = (long) frameIndex;
                frameIdList.add(frameId);
                bool[frameIndex] = true;
            }
            else if(bool[frameIndex] && frameIdList.size() <4){
                i -=1;
            }
        }

        //최종적으로 프레임 3개 + 사용자 취향과 관계없는 랜덤 프레임1개 를 결과로 보내주기 위한 for 문
        //앞에서 랜덤으로 뽑은 프레임 id 3개를 repo에서 찾아서 dto로 변환하여 result 리스트에 추가해줌
        for(int i=0; i<frameIdList.size(); i++){
            Optional<Frame> frame = frameRepository.findById(frameIdList.get(i));
            FrameDetailDto frameDetailDto = FrameDetailDto.from(frame.get());
            frameDetailDto.setCategories(this.getCategory(frame.get()));
            result.add(frameDetailDto);
        }

        return result;
    }

    public FrameDetailDto getOneFrame(long frameId) {
        Frame frame = frameRepository.getById(frameId);
        FrameDetailDto frameDetailDto = FrameDetailDto.from(frame);
        frameDetailDto.setCategories(this.getCategory(frame));
        return frameDetailDto;
    }

    public void updateFrameUser(long frameId, User user) {

        Long userId = user.getUserId();

        Frame frame = frameRepository.getById(frameId);

        FrameUser existFrameUser = frameUserRepository.findByFrameUser(frameId, userId);

        //한 번도 사용하지 않았던 프레임 일 때 새로 저장
        if(existFrameUser == null){
            FrameUser frameUser = new FrameUser();
            frameUser.setFrame(frame);
            frameUser.setUser(user);

            frameUserRepository.save(frameUser);

            //저장 후 사용 횟수 업데이트
            FrameUser updateFrameUser = frameUserRepository.findByFrameUser(frameId, userId);

            Integer useCount = updateFrameUser.getUseCount()+1;
            updateFrameUser.setUseCount(useCount);
        }
        //사용 했던 프레임 일 때
        else{
            Integer useCount = existFrameUser.getUseCount()+1;
            existFrameUser.setUseCount(useCount);
        }
        //사용자 취향 업데이트
        //프레임 카테고리 가져오기
        List<String> categories = this.getCategory(frame);
        UserUpdateDto userUpdateDto = new UserUpdateDto();

        for(String category : categories){
            switch(category) {
                case "half":
                    Integer half = user.getHalf() + 1;
                    userUpdateDto.setHalf(half);
                    break;
                case "whole" :
                    Integer whole = user.getWhole()+1;
                    userUpdateDto.setWhole(whole);
                    break;
                case "sit":
                    Integer sit = user.getSit()+1;
                    userUpdateDto.setSit(sit);
                    break;
                case "selfie":
                    Integer selfie = user.getSelfie()+1;
                    userUpdateDto.setSelfie(selfie);
                    break;
            }
        }

        userDetailsService.updateUserTaste(user, userUpdateDto);
    }

    public void updateFrameUserLike(long frameId, User user){
        Long userId = user.getUserId();
        Frame frame = frameRepository.getById(frameId);

        FrameUser existFrameUser = frameUserRepository.findByFrameUser(frameId, userId);

        //FrameUser 테이블에 없는 한 번도 사용하지 않았던 프레임 일 때 새로 저장
        if(existFrameUser == null){
            FrameUser frameUser = new FrameUser();
            frameUser.setFrame(frame);
            frameUser.setUser(user);

            frameUserRepository.save(frameUser);

            //저장 후 좋아요 여부 업데이트
            FrameUser updateFrameLike = frameUserRepository.findByFrameUser(frameId, userId);
            updateFrameLike.setLikeState(true);
        }
        //FrameUser 테이블에 있는 프레임일 때 좋아요 여부만 새로 저장
        else{
            existFrameUser.setLikeState(true);
        }
    }

    public List<FrameDetailDto> getFrameUserLike(User user){

        Long userId = user.getUserId();
        List<FrameUser> frameUsers = frameUserRepository.findByFrameUserLike(userId);

        List<Long> frameIdList = new ArrayList<>();
        List<FrameDetailDto> result = new ArrayList<>();

        for (FrameUser frameUser : frameUsers) {
            frameIdList.add(frameUser.getFrame().getFrameId());
        }

        for(int i=0; i<frameIdList.size(); i++){
            Optional<Frame> frame = frameRepository.findById(frameIdList.get(i));
            FrameDetailDto frameDetailDto = FrameDetailDto.from(frame.get());
            frameDetailDto.setCategories(this.getCategory(frame.get()));
            result.add(frameDetailDto);
        }
        return result;
    }

    public void cancelFrameUserLike(Long frameId, User user){

        Long userId = user.getUserId();
        FrameUser frameUser = frameUserRepository.findByFrameUser(frameId, userId);
        frameUser.setLikeState(false);

    }

    public List<String> getCategory(Frame categoryFrame){
        Optional<Frame> frame = frameRepository.findById(categoryFrame.getFrameId());
        List<String> categories = new ArrayList<>();
        List<FrameCategory> frameCategories = frameCategoryRepository.findByFrame(frame.get());

        for (FrameCategory frameCategory : frameCategories)
            categories.add(frameCategory.getCategory().getCategoryName());

        return categories;
    }

//    public List<String> getUsers(Frame categoryFrame){
//        Optional<Frame> frame = frameRepository.findById(categoryFrame.getFrameId());
//        List<String> categories = new ArrayList<>();
//        List<FrameCategory> frameCategories = frameCategoryRepository.findByFrame(frame.get());
//
//        for(int i=0;i<frameCategories .size();i++)
//            categories.add(frameCategories.get(i).getCategory().getCategoryName());
//
//        return categories;
//    }
}
