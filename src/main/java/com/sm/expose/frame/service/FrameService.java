package com.sm.expose.frame.service;

import com.sm.expose.frame.domain.Category;
import com.sm.expose.frame.domain.Frame;
import com.sm.expose.frame.domain.FrameCategory;
import com.sm.expose.frame.dto.FrameDetailDto;
import com.sm.expose.frame.respository.CategoryRepository;
import com.sm.expose.frame.respository.FrameCategoryRepository;
import com.sm.expose.frame.respository.FrameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor //final에 있는 애로 생성자 만들어줌 (lombok의 기능)
public class FrameService {

    private final FrameRepository frameRepository;
    private final FrameCategoryRepository frameCategoryRepository;
    private final CategoryRepository categoryRepository;

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


    public FrameDetailDto getOneFrame(Frame frame) {
        FrameDetailDto frameDetailDto = FrameDetailDto.from(frame);
        frameDetailDto.setCategories(this.getCategory(frame));
        return frameDetailDto;
    }

    public List<String> getCategory(Frame categoryFrame){
        Optional<Frame> frame = frameRepository.findById(categoryFrame.getFrameId());
        List<String> categories = new ArrayList<>();
        List<FrameCategory> frameCategories = frameCategoryRepository.findByFrame(frame.get());

        for(int i=0;i<frameCategories .size();i++)
            categories.add(frameCategories.get(i).getCategory().getCategoryName());

        return categories;
    }
}
