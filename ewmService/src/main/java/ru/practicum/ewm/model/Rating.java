package ru.practicum.ewm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "event_rating")
public class Rating {
    @EmbeddedId
    private RatingId ratingId;

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @MapsId("ownerId")
    @JoinColumn(name = "rating_owner")
    private User owner;

    @Column(name = "rating_value")
    private Integer ratingValue;
}
