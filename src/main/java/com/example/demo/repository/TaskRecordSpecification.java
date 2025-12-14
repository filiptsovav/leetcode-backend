package com.example.demo.repository;

import com.example.demo.dto.TaskSearchCriteria;
import com.example.demo.model.TaskRecord;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class TaskRecordSpecification {

    public static Specification<TaskRecord> getSpec(TaskSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // --- ФИЛЬТР: ТОЛЬКО ЗАДАЧИ ТЕКУЩЕГО ЮЗЕРА ---
            if (criteria.getUsername() != null) {
                // Мы обращаемся к полю "user" в TaskRecord, а у него берем "username"
                predicates.add(cb.equal(root.get("user").get("username"), criteria.getUsername()));
            }
            // ---------------------------------------------

            // Поиск по слову (Название ИЛИ Описание)
            if (criteria.getQuery() != null && !criteria.getQuery().isEmpty()) {
                String searchPattern = "%" + criteria.getQuery().toLowerCase() + "%";
                Predicate titleLike = cb.like(cb.lower(root.get("taskName")), searchPattern);
                Predicate contentLike = cb.like(cb.lower(root.get("content")), searchPattern);
                predicates.add(cb.or(titleLike, contentLike));
            }

            // Остальные фильтры
            if (criteria.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), criteria.getDateFrom()));
            }
            if (criteria.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), criteria.getDateTo()));
            }
            if (criteria.getMinTries() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("tryCounter"), criteria.getMinTries()));
            }
            if (criteria.getMaxTries() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("tryCounter"), criteria.getMaxTries()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}