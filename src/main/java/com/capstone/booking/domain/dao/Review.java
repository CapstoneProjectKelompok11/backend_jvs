package com.capstone.booking.domain.dao;

import com.capstone.booking.domain.common.BaseDAO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "M_REVIEW")
@SQLDelete(sql = "UPDATE M_REVIEW SET is_deleted = true WHERE user_id = ? and building_id = ?")
@Where(clause = "is_deleted = false")
@IdClass(Review.ReviewId.class)
public class Review extends BaseDAO {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewId implements Serializable {
        private static final long serialVersionUID = 906209331212775822L;
        private Long user;
        private Long building;
    }

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

    @Column(columnDefinition = "text")
    private String review;

    @Column(nullable = false)
    private int rating;

    private LocalDateTime reviewDate;

}
