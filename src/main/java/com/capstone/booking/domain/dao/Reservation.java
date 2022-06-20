package com.capstone.booking.domain.dao;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.common.BaseDAO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "M_RESERVATION")
@SQLDelete(sql = "UPDATE M_RESERVATION SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Reservation extends BaseDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "floor_id")
    private Floor floor;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = AppConstant.DATETIME_JSON_FORMAT)
    @Column(nullable = false)
    private LocalDateTime startReservation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = AppConstant.DATETIME_JSON_FORMAT)
    @Column(nullable = false)
    private LocalDateTime endReservation;

    private String company;

    @Column(nullable = false)
    private Long price;

    @Column(columnDefinition = "text")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppConstant.ReservationStatus status;
}
