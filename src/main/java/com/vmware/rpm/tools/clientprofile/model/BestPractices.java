package com.vmware.rpm.tools.clientprofile.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "best_practices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BestPractices {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private Model model;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private Pillar pillar;

    @Enumerated(EnumType.STRING)
    private StrategicObjective strategicObjective;
}