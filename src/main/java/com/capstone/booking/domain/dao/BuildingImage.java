package com.capstone.booking.domain.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "M_BUILDING_IMAGES")
public class BuildingImage {

    @JsonIgnore
    @Id
    @GeneratedValue
    private Long id;

    private String fileName;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building building;

}
