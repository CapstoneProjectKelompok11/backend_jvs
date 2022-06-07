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
import java.util.List;

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

    private String name;

    private String type;

    private String size;

    private int maxCapacity;

    private String description;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

    @OneToMany(mappedBy = "floor")
    private List<FloorImage> images;


    //List of floor facility with Element Collection

}
