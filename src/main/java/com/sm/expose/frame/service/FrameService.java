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
import com.sm.expose.global.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
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

        //제일 많이 나온 카테고리부터 찾아서 리스트에 넣어주기
        for (Map.Entry<String, Integer> en : hm.entrySet()) { categories.add(en.getKey()); }
        String mostCategory = categories.get(0);
        String mostCategory2 = categories.get(1);


        //3개의 추천 포즈 보여 주기 제일 많이 나온 카테고리 + 그 다음 카테고리를 2:1 비율로

        List<Long> frameIdList = new ArrayList<>();

        //랜덤 추출 중복 제거용
        List<Frame> frames = frameRepository.findAll();
        Boolean[] bool = new Boolean[frames.size()+1];
        Arrays.fill(bool,false);

        //mostCategory에 속해 있는 프레임 전체를 가져오기
        Category category = categoryRepository.findByCategoryName(mostCategory);
        List<FrameCategory> frameCategories = frameCategoryRepository.findByCategoryId(category.getCategoryId());

        List<Long> mostFrameIds = new ArrayList<>();


        //mostCategory에 속한 프레임들의 ID를 넣어줌
        for (FrameCategory frameCategory : frameCategories) {
            mostFrameIds.add(frameCategory.getFrame().getFrameId());
        }


        Random random = new Random();

        for(int i=0; i<2; i++){
            int randomIndex = random.nextInt(mostFrameIds.size());
            int frameIndex = mostFrameIds.get(randomIndex).intValue();
            if(!bool[frameIndex]){
                Long frameId = (long) frameIndex;
                frameIdList.add(frameId);
                bool[frameIndex] = true;
            }
            else if(bool[frameIndex] && frameIdList.size() <2){
                i -=1;
            }
        }

        // mostCategory2 랜덤 찾기
        Category category2 = categoryRepository.findByCategoryName(mostCategory2);
        List<FrameCategory> frameCategories2 = frameCategoryRepository.findByCategoryId(category2.getCategoryId());
        List<Long> mostFrameIds2 = new ArrayList<>();

        //mostCategory2에 속한 프레임들의 ID를 넣어줌
        for (FrameCategory frameCategory : frameCategories2) {
            mostFrameIds2.add(frameCategory.getFrame().getFrameId());
        }

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

    public void updateFrameUser(long frameId, Principal principal) {

        User user = userDetailsService.findUser(principal);
        Long userId = user.getUserId();

        Frame frame = frameRepository.getById(frameId);

        FrameUser existFrameUser = frameUserRepository.findByFrameUser(frameId, userId);

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
        else{
            Integer useCount = existFrameUser.getUseCount()+1;
            existFrameUser.setUseCount(useCount);
        }

//        FrameDetailDto frameDetailDto = FrameDetailDto.from(frame);
//        frameDetailDto.setCategories(this.getCategory(frame));

    }

    public List<String> getCategory(Frame categoryFrame){
        Optional<Frame> frame = frameRepository.findById(categoryFrame.getFrameId());
        List<String> categories = new ArrayList<>();
        List<FrameCategory> frameCategories = frameCategoryRepository.findByFrame(frame.get());

        for(int i=0;i<frameCategories .size();i++)
            categories.add(frameCategories.get(i).getCategory().getCategoryName());

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
