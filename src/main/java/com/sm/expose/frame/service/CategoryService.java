package com.sm.expose.frame.service;

import com.sm.expose.frame.domain.Category;
import com.sm.expose.frame.domain.Frame;
import com.sm.expose.frame.domain.FrameCategory;
import com.sm.expose.frame.dto.CategoryDto;
import com.sm.expose.frame.respository.CategoryRepository;
import com.sm.expose.frame.respository.FrameCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final FrameCategoryRepository frameCategoryRepository;

    public List<CategoryDto> findAllCategory() {
        final List<Category> all = categoryRepository.findAll();
        return all.stream().map(CategoryDto::new).collect(Collectors.toList());
    }

    public Category findCategory(String categoryName){
        Category category = categoryRepository.findByCategoryName(categoryName);
        return category;
    }

    public void saveCategory(List<String> categories, Frame frame) {

        for (String s : categories) {
            Optional<Category> existedCategory = Optional.ofNullable(categoryRepository.findByCategoryName(s));
            FrameCategory frameCategory = new FrameCategory();
            if (existedCategory.isPresent()) {
                frameCategory .setCategory(existedCategory.get());
            } else {
                Category category = new Category(s);
                categoryRepository.save(category);
                frameCategory.setCategory(category);
            }
            frameCategory.setFrame(frame);
            frameCategoryRepository.save(frameCategory);
        }
    }
}