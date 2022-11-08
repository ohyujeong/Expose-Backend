package com.sm.expose.frame.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Frame {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="frame_id")
    private Long frameId;

    @Column(name="frame_name")
    private String frameName;

    @Column(name="frame_path", length = 1000)
    private String framePath;

    @Column(name="s3_frame_name")
    private String s3FrameName;

    @OneToMany(mappedBy = "frame", cascade = CascadeType.ALL)
    @Column(name = "categories")
    private List<FrameCategory> categories = new ArrayList<>();

    @Builder
    public Frame(String frameName, String framePath, String s3FrameName) {
        this.frameName = frameName;
        this.framePath = framePath;
        this.s3FrameName = s3FrameName;
    }
}
