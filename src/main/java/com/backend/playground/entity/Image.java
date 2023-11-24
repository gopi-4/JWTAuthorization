package com.backend.playground.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int Id;
    private String URL;
    private String Description;
}
