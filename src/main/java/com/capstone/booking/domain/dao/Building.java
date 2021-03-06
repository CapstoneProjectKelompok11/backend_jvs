package com.capstone.booking.domain.dao;

import com.capstone.booking.constant.AppConstant;
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
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "M_BUILDING")
@SQLDelete(sql = "UPDATE M_BUILDING SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Building extends BaseDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(columnDefinition = "text")
    private String description;

    private int capacity;

    private String buildingSize;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "M_FACILITIES")
    private Set<AppConstant.FacilityType> facilities;

    @ManyToOne
    @JoinColumn(name = "complex_id")
    private Complex complex;

    @JsonIgnore
    @OneToMany(mappedBy = "building")
    private List<Floor> floors;

    @JsonIgnore
    @OneToMany(mappedBy = "building")
    private List<Chat> chats;

    @OneToMany(mappedBy = "building")
    private List<BuildingImage> images;

    @JsonIgnore
    @OneToMany(mappedBy = "building")
    private List<Review> reviews;

    @JsonIgnore
    @OneToMany(mappedBy = "building")
    private List<Favorite> favorites;

}
