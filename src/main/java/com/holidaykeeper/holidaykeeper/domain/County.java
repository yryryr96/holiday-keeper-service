package com.holidaykeeper.holidaykeeper.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "county")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class County {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    @Comment("카운티 명")
    private String name;

    @Builder
    private County(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
