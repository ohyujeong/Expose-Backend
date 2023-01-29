package com.sm.expose.global.security.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sm.expose.frame.domain.FrameUser;
import com.sm.expose.global.security.dto.UserUpdateDto;
import com.sm.expose.global.security.oauth.ProviderType;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "email", length = 50, unique = true)
    private String email;

    @Column(name = "password", length = 100)
    private String password;

    @Column
    private String profileImage;

    @Column
    private Integer whole;
    @Column
    private Integer sit;
    @Column
    private Integer half;
    @Column
    private Integer selfie;
    @Column
    private Integer two;
    @Column
    private Integer many;

    @Column
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<FrameUser> frames = new ArrayList<>();

    @PrePersist
    public void prePersist(){
        this.whole = this.whole == null ? 0 : this.whole;
        this.half = this.half == null ? 0 : this.half;
        this.selfie = this.selfie == null ? 0 : this.selfie;
        this.sit = this.sit == null ? 0 : this.sit;
        this.two = this.two == null ? 0 : this.two;
        this.many = this.many == null ? 0 : this.many;
    }

    @Builder
    public User(String email, String nickname, String password, String profileImage, Role role) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.profileImage = profileImage;
        this.role = role;
    }

    public User update(String nickname, String profileImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
        return this;
    }

    public User updateTaste(UserUpdateDto dto){
        this.whole = dto.getWhole();
        this.sit = dto.getSit();
        this.half = dto.getHalf();
        this.selfie = dto.getSelfie();
        this.two = dto.getTwo();
        this.many = dto.getMany();
        return this;
    }
}
