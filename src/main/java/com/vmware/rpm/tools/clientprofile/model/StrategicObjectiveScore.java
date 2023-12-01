package com.vmware.rpm.tools.clientprofile.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StrategicObjectiveScore {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="profile_id", nullable=false)
    private Profile profile;

    @Enumerated(EnumType.STRING)
    private Pillar pillar;

    @Enumerated(EnumType.STRING)
    private StrategicObjective strategicObjective;

    @Column(name = "score")
    private Double score;

    @Column(name = "comments")
    private String comments;

    @Column(name = "score_date")
    private Instant scoreDate;

}
