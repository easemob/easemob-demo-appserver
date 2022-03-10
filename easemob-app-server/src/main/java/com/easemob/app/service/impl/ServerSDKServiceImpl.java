package com.easemob.app.service.impl;

import com.easemob.app.exception.ASGetChatUserIdException;
import com.easemob.app.exception.ASGetChatUserNameException;
import com.easemob.app.exception.ASRegisterChatUserNameException;
import com.easemob.app.exception.ASServerSDKException;
import com.easemob.app.service.ServerSDKService;
import com.easemob.im.server.EMException;
import com.easemob.im.server.EMService;
import com.easemob.im.server.api.token.agora.AccessToken2;
import com.easemob.im.server.exception.EMNotFoundException;
import com.easemob.im.server.model.EMAttachment;
import com.easemob.im.server.model.EMUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ServerSDKServiceImpl implements ServerSDKService {
    private static final String EASEMOB_USER_PASSWORD = "123456";

    @Value("${agora.token.expire.period.seconds}")
    private int expirePeriod;

    @Value("${easemob.chat.group.id}")
    private String groupId;

    @Value("${send.image.path}")
    private String imagePath;

    @Autowired
    private EMService serverSdk;

    private List<String> groupMembers;

    private final String[] contacts = new String[] {"allen", "andrew", "leon", "stevie", "lincoln", "patrick"};

    @Override
    public void registerChatUserName(String chatUserName) {
        try {
            this.serverSdk.user().create(chatUserName, EASEMOB_USER_PASSWORD).block();
            log.info("register chatUserName success :{}", chatUserName);
        } catch (EMException e) {
            log.error("register chatUserName fail. chatUserName : {}, e : {}", chatUserName, e.getMessage());
            throw new ASRegisterChatUserNameException(chatUserName + " register fail.");
        }
    }

    @Override
    public boolean checkIfChatUserNameExists(String chatUserName) {
        try {
            this.serverSdk.user().get(chatUserName).block();
        } catch (EMException e) {
            if (e.getClass() == EMNotFoundException.class) {
                log.info("chatUserName not exists :{}", chatUserName);
                return false;
            }
            log.error("get chatUserName fail. chatUserName : {}, e : {}", chatUserName, e.getMessage());
            throw new ASGetChatUserNameException(chatUserName + " get fail.");
        }
        return true;
    }

    @Override
    public String getChatUserId(String chatUserName) {
        EMUser user;

        try {
            user = this.serverSdk.user().get(chatUserName).block();
        } catch (EMException e) {
            log.error("get chatUserId fail. chatUserName : {}, e : {}", chatUserName, e.getMessage());
            throw new ASGetChatUserIdException(chatUserName + " get chatUserId fail.");
        }
        return user.getUuid();
    }

    @Override
    public void addContacts(String chatUserName) {
        int total = 0;
        List<String> members = new ArrayList<>();

        for (String contact : this.contacts) {
            if (!contact.equals(chatUserName)) {
                if (total == 6) {
                    break;
                }

                try {
                    this.serverSdk.contact().add(chatUserName, contact).block();
                } catch (EMException e) {
                    log.error("add contact fail. chatUserName : {}, contact : {}, e : {}", chatUserName, contact, e.getMessage());
                    throw new ASGetChatUserIdException(String.format("%s add contact %s fail.", chatUserName, contact));
                }

                members.add(contact);

                total++;
            }
        }
        this.groupMembers = members;
    }

    @Override
    public void createChatGroup(String chatUserName) {
        String createGroupId = null;

        try {
            createGroupId = this.serverSdk.group().createPrivateGroup(chatUserName, "Tourism Group", "Travel enthusiasts, welcome to join us!", this.groupMembers, 200, true).block();
        } catch (EMException e) {
            log.error("create private group fail. chatUserName : {}, groupId :{}, e : {}", chatUserName, groupId, e.getMessage());
            if (e.getErrorCode() >= 500) {
                throw new ASServerSDKException(chatUserName + " create group fail.");
            }
        }

        if (createGroupId != null) {
            String member = this.groupMembers.get(1);

            try {
                this.serverSdk.message().send()
                        .fromUser(member).toGroup(createGroupId)
                        .text(msg -> msg.text("The scenery is so great !"))
                        .send()
                        .block();

                String greatPath = imagePath + "great.jpg";
                log.info("greatPath : {}", greatPath);

                EMAttachment greatAttachment = this.serverSdk.attachment().uploadFile(FileSystems.getDefault().getPath(greatPath))
                        .block();

                this.serverSdk.message().send()
                        .fromUser(member).toGroup(createGroupId)
                        .image(msg -> msg.uri(URI.create(greatAttachment.getUrl()))
                                .secret(greatAttachment.getSecret())
                        )
                        .send()
                        .block();

                log.info("great url : {}", URI.create(greatAttachment.getUrl()));

                this.serverSdk.message().send()
                        .fromUser(this.groupMembers.get(3)).toGroup(createGroupId)
                        .text(msg -> msg.text("Yes, and the hotel has a very local style."))
                        .send()
                        .block();

                String stylePath = imagePath + "style.jpg";

                EMAttachment styleAttachment = this.serverSdk.attachment().uploadFile(FileSystems.getDefault().getPath(stylePath))
                        .block();

                this.serverSdk.message().send()
                        .fromUser(this.groupMembers.get(3)).toGroup(createGroupId)
                        .image(msg -> msg.uri(URI.create(styleAttachment.getUrl()))
                                .secret(styleAttachment.getSecret())
                        )
                        .send()
                        .block();

                this.serverSdk.message().send()
                        .fromUser(member).toGroup(createGroupId)
                        .text(msg -> msg.text("This is really an unforgettable memory."))
                        .send()
                        .block();

            } catch (EMException e) {
                log.error("create - send group message fail. chatUserName : {}, groupId : {}, e : {}", member, this.groupId, e.getMessage());
                if (e.getErrorCode() >= 500) {
                    throw new ASServerSDKException(chatUserName + " create - send group message fail.");
                }
            }
        }
    }

    @Override
    public void joinChatGroup(String chatUserName) {
        log.info("join chat group, chatUserName : {}", chatUserName);

        try {
            this.serverSdk.group().addGroupMember(groupId, chatUserName).block();

            this.serverSdk.message().send()
                    .fromUser(this.groupMembers.get(0)).toGroup(groupId)
                    .text(msg -> msg.text(" Welcome to join Agora Chat developer group!"))
                    .send()
                    .block();
        } catch (EMException e) {
            log.error("join group fail. chatUserName : {}, groupId :{}, e : {}", chatUserName, groupId, e.getMessage());
            if (e.getErrorCode() >= 500) {
                throw new ASServerSDKException(chatUserName + " join group fail.");
            }
        }
    }

    @Override
    public void sendMessage(String chatUserName) {
        String sender = this.groupMembers.get(0);
        log.info("send message, sender : {}, chatUserName : {}", sender, chatUserName);

        try {
            this.serverSdk.message().send()
                    .fromUser(sender).toUser(chatUserName)
                    .text(msg -> msg.text("Hey! Nice to meet you in Agora Chat～ You must be a developer, right?"))
                    .send()
                    .block();
        } catch (EMException e) {
            log.error("send chat message fail. contact : {}, chatUserName : {}, e : {}", sender, chatUserName, e.getMessage());
            if (e.getErrorCode() >= 500) {
                throw new ASServerSDKException(chatUserName + " send chat message fail.");
            }
        }
    }

    @Override
    public String generateAgoraChatUserToken(String chatUserName, String chatUserId) {
        EMUser user = new EMUser(chatUserName, chatUserId, true);

        return this.serverSdk.token().getUserToken(user, this.expirePeriod, null, EASEMOB_USER_PASSWORD);
    }

    @Override
    public String generateAgoraRtcToken(String channelName, Integer agorauid) {
        EMUser bob = new EMUser("bob", "da921111-ecf9-11eb-9af3-296ff79acb67", true);
        String bobAgoraChatRtcToken = this.serverSdk.token().getUserToken(bob, this.expirePeriod, token -> {
            AccessToken2.ServiceRtc serviceRtc = new AccessToken2.ServiceRtc(channelName, String.valueOf(agorauid));
            serviceRtc.addPrivilegeRtc(AccessToken2.PrivilegeRtc.PRIVILEGE_JOIN_CHANNEL, this.expirePeriod);
            token.addService(serviceRtc);
        }, null);
        return bobAgoraChatRtcToken;
    }
}
