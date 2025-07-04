package com.example.demo.entity;

import com.example.demo.entity.base.enums.ReasonType;
import com.example.demo.entity.base.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Report extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id")
    private User reportedUser;

    @Enumerated(EnumType.STRING)
    private ReasonType reasonType;

    private String reasonDetail;

    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;

    @ElementCollection
    private List<String> imageUrls;
}
