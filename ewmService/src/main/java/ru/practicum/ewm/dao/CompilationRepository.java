package ru.practicum.ewm.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Integer> {
    Page<Compilation> findByPinnedOrderByIdAsc(Boolean pinned, Pageable page);

    @Query(value = "select case when count(*) > 0 then true else false end from compilations compilations " +
            "where compilations.id = :compilationId ",
            nativeQuery = true)
    boolean isCompilationExists(@Param("compilationId") Integer compilationId);
}
