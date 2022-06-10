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

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "M_FLOOR")
@SQLDelete(sql = "UPDATE M_FLOOR SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Floor extends BaseDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private int maxCapacity;

    @Column(nullable = false)
    private int startingPrice;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

    private String image;


    //List of floor facility with Element Collection

}
