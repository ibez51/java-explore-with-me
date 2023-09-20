package ru.practicum.ewm.service.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventSortType;
import ru.practicum.ewm.model.EventState;

import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public final class EventQuerySpecification {
    public static Specification<Event> findEventsByAdminParameters(List<Integer> users,
                                                                   List<EventState> states,
                                                                   List<Integer> categories,
                                                                   LocalDateTime rangeStart,
                                                                   LocalDateTime rangeEnd) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (Objects.nonNull(users)) {
                predicate = criteriaBuilder.and(predicate, root.get("initiator").in(users));
            }

            if (Objects.nonNull(states)) {
                predicate = criteriaBuilder.and(predicate, root.get("state").in(states));
            }

            if (Objects.nonNull(categories)) {
                predicate = criteriaBuilder.and(predicate, root.get("category").in(categories));
            }

            if (Objects.nonNull(rangeStart)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), rangeStart));
            }

            if (Objects.nonNull(rangeEnd)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThan(root.get("eventDate").as(LocalDateTime.class), rangeEnd));
            }

            List<Order> orderList = List.of(criteriaBuilder.desc(root.get("id")));
            criteriaQuery.orderBy(orderList);

            return predicate;
        };
    }

    public static Specification<Event> findEventsByPublicParameters(String text,
                                                                    List<Integer> categories,
                                                                    Boolean paid,
                                                                    LocalDateTime rangeStart,
                                                                    LocalDateTime rangeEnd,
                                                                    EventSortType sort) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("state"), EventState.PUBLISHED);

            if (Objects.nonNull(text)
                    && !text.isBlank()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + text.toLowerCase() + "%"))
                );
            }

            if (Objects.nonNull(categories)) {
                predicate = criteriaBuilder.and(predicate, root.get("category").in(categories));
            }

            if (Objects.nonNull(paid)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("paid"), paid));
            }

            if (Objects.nonNull(rangeStart)
                    && Objects.nonNull(rangeEnd)) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.and(
                                criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), rangeStart),
                                criteriaBuilder.lessThan(root.get("eventDate").as(LocalDateTime.class), rangeEnd)
                        ));
            } else {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), LocalDateTime.now()));
            }

            if (Objects.nonNull(sort)
                    && sort == EventSortType.EVENT_DATE) {
                List<Order> orderList = List.of(criteriaBuilder.asc(root.get("eventDate")));
                criteriaQuery.orderBy(orderList);
            }

            return predicate;
        };
    }
}
