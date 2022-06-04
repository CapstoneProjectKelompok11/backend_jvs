package com.capstone.booking.domain.dao;

import com.capstone.booking.domain.common.BaseDAO;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "M_COMPLEX")
@SQLDelete(sql = "UPDATE M_COMPLEX SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Complex extends BaseDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "complex")
    private List<Building> buildings;


}
