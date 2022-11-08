package com.sm.expose.frame.service;

import com.sm.expose.frame.domain.Frame;
import com.sm.expose.frame.domain.FrameCategory;
import com.sm.expose.frame.dto.FrameDetailDto;
import com.sm.expose.frame.respository.FrameCategoryRepository;
import com.sm.expose.frame.respository.FrameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor //final에 있는 애로 생성자 만들어줌 (lombok의 기능)
public class FrameService {

    private final FrameRepository frameRepository;
    private final FrameCategoryRepository frameCategoryRepository;

    public long saveFrame(Frame frame){
        Frame createFrame = frameRepository.save(frame);
        System.out.println(createFrame.getFrameId());
        return createFrame.getFrameId();
    }

//    public FrameDetailDto getFramesByCategory(List<String> categories){
//
//    }

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
