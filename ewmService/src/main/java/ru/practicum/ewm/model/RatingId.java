package ru.practicum.ewm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@Embeddable
@AllArgsConstructor
public class RatingId implements Serializable {
    @Column(name = "event_id")
    private int eventId;
    @Column(name = "rating_owner")
    private int ownerId;
}
