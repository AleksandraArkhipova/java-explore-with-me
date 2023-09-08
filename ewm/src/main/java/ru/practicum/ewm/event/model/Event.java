package ru.practicum.ewm.event.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "event")
public class Event {
    @Id
    @Column(name = "event_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    User initiator;

    String description;

    @Column(name = "event_date")
    LocalDateTime eventDate;

    Double latitude;

    Double longitude;

    Boolean paid;

    @Column(name = "participant_limit")
    Integer participantLimit;

    @Column(name = "request_moderation")
    Boolean requestModeration;

    String title;

    @Enumerated(EnumType.STRING)
    EventState state;

    @Column(name = "created_on")
    LocalDateTime createdOn;

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    List<Comment> comments;
}
