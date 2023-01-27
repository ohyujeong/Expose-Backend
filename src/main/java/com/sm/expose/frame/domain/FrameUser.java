package com.sm.expose.frame.domain;

import com.sm.expose.global.security.domain.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
public class FrameUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long frameUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "frame_id")
    private Frame frame;

    //사용 횟수
    @Column(name="use_count")
    private Integer useCount;

    //좋아요 여부
    @Column(name="like_state")
    private Boolean likeState;


    @PrePersist
    public void prePersist(){
        this.useCount = this.useCount == null ? 0 : this.useCount;
        this.likeState = this.likeState != null && this.likeState;
    }

//    //사용 여부
//    @Column(name = "status")
//    private Boolean status;

//    @PrePersist
//    public void prePersist(){
//        this.status = this.status == null ? Boolean.FALSE : Boolean.TRUE;
//    }
}
