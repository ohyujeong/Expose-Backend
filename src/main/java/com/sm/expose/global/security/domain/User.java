package com.sm.expose.global.security.domain;

import com.sm.expose.global.security.dto.UserUpdateDto;
import com.sm.expose.global.security.oauth.ProviderType;
import lombok.*;

import javax.persistence.*;

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
    private Integer wholeStand;
    @Column
    private Integer wholeSit;
    @Column
    private Integer half;
    @Column
    private Integer selfie;
    @Column
    private Integer longHalf;

    @Column
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

//    @PrePersist
//    public void prePersist(){
//        this.whole = this.whole == null ? 0 : this.whole;
//        this.stand = this.stand == null ? 0 : this.stand;
//        this.half = this.half == null ? 0 : this.half;
//        this.self = this.self == null ? 0 : this.self;
//        this.sit = this.sit == null ? 0 : this.sit;
//    }

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
        this.wholeStand = dto.getWholeStand();
        this.wholeSit = dto.getWholeSit();
        this.half = dto.getHalf();
        this.selfie = dto.getSelfie();
        this.longHalf = dto.getLongHalf();
        return this;
    }
}
