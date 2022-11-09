package com.sm.expose.frame.respository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sm.expose.frame.domain.FrameCategory;
import com.sm.expose.frame.domain.QFrameCategory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class FrameCategoryRepositoryImpl implements FrameCategoryRepositoryCustom {

    private JPAQueryFactory queryFactory;

    @Override
    public List<FrameCategory> findByCategoryId(long categoryId) {

        //framecategory에서 categoryId인 것들... 찾아서 다 줌
        return queryFactory.selectFrom(QFrameCategory.frameCategory)
                .where(QFrameCategory.frameCategory.category.categoryId.eq(categoryId))
                .fetch();
    }
}
