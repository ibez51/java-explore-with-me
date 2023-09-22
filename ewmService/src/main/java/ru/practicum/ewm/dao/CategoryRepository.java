package ru.practicum.ewm.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query(value = "select case when count(*) > 0 then true else false end from categories categories " +
            "where categories.id = :categoryId ",
            nativeQuery = true)
    boolean isCategoryExists(@Param("categoryId") Integer categoryId);
}
