package ru.practicum.ewm.JPATests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.dao.*;
import ru.practicum.ewm.model.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class JPATests {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private CompilationRepository compilationRepository;
    User user1;
    User user2;
    Category category1;
    Category category2;
    Event event1;
    Event event2;
    Request request1;
    Compilation compilation1;

    @BeforeEach
    void setUp() {
        user1 = entityManager.persist(new User().setName("User name1").setEmail("email@user.f1"));
        user2 = entityManager.persist(new User().setName("User name2").setEmail("email@user.f2"));
        category1 = categoryRepository.save(new Category()
                .setName("Балет"));
        category2 = categoryRepository.save(new Category()
                .setName("Битва детей на кортиках"));
        event1 = eventRepository.save(new Event()
                .setAnnotation("annotation")
                .setCategory(category1)
                .setDescription("Description1")
                .setEventDate(LocalDateTime.of(2025, 12, 12, 1, 1))
                .setInitiator(user1)
                .setTitle("Title1")
                .setLocationLat(11.11F)
                .setLocationLon(12.23F)
                .setPaid(false)
                .setParticipantLimit(1000));
        event2 = eventRepository.save(new Event()
                .setAnnotation("annotation2")
                .setCategory(category1)
                .setDescription("Description2")
                .setEventDate(LocalDateTime.of(2025, 12, 12, 1, 1))
                .setInitiator(user2)
                .setTitle("Title2")
                .setLocationLat(11.11F)
                .setLocationLon(12.23F)
                .setPaid(false)
                .setParticipantLimit(1000));
        request1 = requestRepository.save(new Request()
                .setEvent(event1)
                .setRequester(user2)
                .setStatus(RequestStatus.CONFIRMED));
        compilation1 = compilationRepository.save(new Compilation()
                .setTitle("compilation1")
                .setPinned(false)
                .setEventList(List.of(event1, event2)));
    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(entityManager);
        Assertions.assertNotNull(categoryRepository);
        Assertions.assertNotNull(userRepository);
        Assertions.assertNotNull(eventRepository);
        Assertions.assertNotNull(requestRepository);
        Assertions.assertNotNull(compilationRepository);
    }

    @Test
    public void testCategoryRepository() {
        assertTrue(categoryRepository.isCategoryExists(category1.getId()));
        assertFalse(categoryRepository.isCategoryExists(30000));
    }

    @Test
    public void testUserRepository() {
        assertTrue(userRepository.isUserExists(user1.getId()));
        assertFalse(userRepository.isUserExists(53));

        assertEquals(2, userRepository.findByIdInOrderByIdAsc(List.of(user1.getId(), user2.getId()), PageRequest.of(0, 5)).getContent().size());
    }

    @Test
    public void testEventRepository() {
        assertEquals(event1.getId(), eventRepository.findByIdAndInitiatorId(event1.getId(), user1.getId()).get().getId());
        assertTrue(eventRepository.findByIdAndInitiatorId(event1.getId(), user2.getId()).isEmpty());

        assertEquals(event1.getId(), eventRepository.findByIdAndStateOrderByIdAsc(event1.getId(), EventState.PENDING).get().getId());
        assertTrue(eventRepository.findByIdAndStateOrderByIdAsc(event1.getId(), EventState.PUBLISHED).isEmpty());
    }

    @Test
    public void testRequestRepository() {
        assertEquals(1, requestRepository.findByEventId(event1.getId()).size());

        assertEquals(1, requestRepository.findByEventIdAndStatusOrderByIdAsc(event1.getId(), RequestStatus.CONFIRMED).size());

        assertEquals(1, requestRepository.findByRequesterIdOrderByIdAsc(user2.getId()).size());

        assertTrue(requestRepository.findByIdAndRequesterId(request1.getId(), request1.getRequester().getId()).isPresent());

        assertEquals(1, requestRepository.findConfirmedReqByEvent(List.of(event1.getId())).size());

        assertTrue(requestRepository.isRequestByUserAndEventExists(user2.getId(), event1.getId()));
    }

    @Test
    public void testCompilationRepository() {
        assertEquals(1, compilationRepository.findByPinnedOrderByIdAsc(false, PageRequest.of(0, 1)).getContent().size());

        assertTrue(compilationRepository.isCompilationExists(compilation1.getId()));
    }
}
