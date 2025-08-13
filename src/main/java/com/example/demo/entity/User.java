package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(length = 255)
    private String email;

    @Column(length = 20)
    @Setter
    private String nickname;

    @Builder.Default
    private int ticket_num = 0;

    //TODO: 후순위 기능인 rank
    // private Rank rank;

    @Column(length = 20)
    private String provider;

    @Column(length = 20)
    private String role;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Address> addresses;

    @Column(nullable = false)
    @Setter
    private double averageScore;

    @Setter
    private int reviewCount;

    private LocalDateTime withdrawTime;

    @Builder.Default
    private Boolean isBusiness = false;

    @Setter
    private String businessCode;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Like> likes;

    @OneToMany(mappedBy = "user")
    private List<Follow> followings;

    @OneToMany(mappedBy = "storeId")
    private List<Follow> followers;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @OneToMany(mappedBy = "user")
    private List<Raffle> raffles;

    private String refreshToken;



    @Builder.Default
    @Column(name="follower_visible")
    private Boolean followerVisible=true;

    public void addAddress(Address address) {
        addresses.add(address);
        address.setUser(this);
    }

    public void setTicket_num(int ticket_num) { this.ticket_num = ticket_num; }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setFollowerVisible(boolean followerVisible) {
        this.followerVisible = followerVisible;
    }

    public void setIsBusiness(Boolean isBusiness) {
        this.isBusiness = isBusiness;
    }

}
