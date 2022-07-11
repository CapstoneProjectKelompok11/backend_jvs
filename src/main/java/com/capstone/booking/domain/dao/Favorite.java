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

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "T_FAVORITE")
@SQLDelete(sql = "UPDATE T_FAVORITE SET is_deleted = true WHERE building_id = ? and user_id = ?")
@Where(clause = "is_deleted = false")
@IdClass(Favorite.FavoriteId.class)
public class Favorite extends BaseDAO {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FavoriteId implements Serializable {
        private static final long serialVersionUID = -1021623691168817706L;
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

}
