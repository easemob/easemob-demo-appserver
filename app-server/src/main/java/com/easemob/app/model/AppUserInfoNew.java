package com.easemob.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "EASEMOB_APP_USER_INFO_NEW")
public class AppUserInfoNew {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appkey")
    private String appkey;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "chat_user_name")
    private String chatUserName;

    @Column(name = "chat_user_password")
    private String chatUserPassword;

    @Column(name = "agora_uid")
    private String agoraUid;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
