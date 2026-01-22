package com.easemob.app.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "EASEMOB_APP_USER_INFO_NEW")
public class AppUserInfoNew {
    /**
     * 主键 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 环信 AppKey
     */
    @Column(name = "appkey")
    private String appkey;

    /**
     * 手机号
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * 头像 URL
     */
    @Column(name = "avatar_url")
    private String avatarUrl;

    /**
     * 环信用户名
     */
    @Column(name = "chat_user_name")
    private String chatUserName;

    /**
     * 环信用户密码
     */
    @Column(name = "chat_user_password")
    private String chatUserPassword;

    /**
     * 声网 UID
     */
    @Column(name = "agora_uid")
    private String agoraUid;

     /**
     * 创建时间
     */
    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
