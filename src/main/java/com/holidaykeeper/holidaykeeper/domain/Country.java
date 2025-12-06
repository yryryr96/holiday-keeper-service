package com.holidaykeeper.holidaykeeper.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "country")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    @Comment("국가명")
    private String name;

    @Column(name = "code", unique = true)
    @Comment("국가 코드")
    private String code;

    @Builder
    private Country(Long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }
}
