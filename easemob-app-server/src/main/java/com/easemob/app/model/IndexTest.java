package com.easemob.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "INDEX_TEST")
public class IndexTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appkey")
    private String appkey;

    @Column(name = "server_id")
    private String serverId;

    @Column(name = "member_id")
    private String memberId;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private String age;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
