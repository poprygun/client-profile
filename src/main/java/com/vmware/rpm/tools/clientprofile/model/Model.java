package com.vmware.rpm.tools.clientprofile.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "model")
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    private String name;
    private String description;

    @OneToMany(mappedBy = "model")
    private List<BestPractices> bestPractices = new ArrayList<>();

}