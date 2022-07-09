package com.capstone.booking.domain.dao;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "T_CHAT")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "buildingId")
    private Building building;

    @ManyToOne
    @JoinColumn(name = "senderId")
    private User user;

    @Column(columnDefinition = "text")
    private String message;

    private Boolean sentByUser;

//    private Boolean isRead;

    private LocalDateTime timestamp;
}
