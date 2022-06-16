package com.easemob.app.service;

import com.easemob.app.model.IndexTest;
import com.easemob.app.model.IndexTestRepository;
import com.easemob.app.utils.RandomUidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class IndexTestService {

    @Autowired
    private IndexTestRepository indexTestRepository;

    public void insertData() {
        for (int i = 0; i < 10000000; i++) {
            IndexTest indexTest = IndexTest.builder()
                    .appkey(String.format("demo#%s", RandomUidUtil.getUid()))
//                    .appkey("demo#test")
                    .serverId(UUID.randomUUID().toString())
                    .memberId(String.format("user-%s", RandomUidUtil.getUid()))
                    .name("0809")
                    .age("20")
                    .createdAt(LocalDateTime.now())
                    .build();

            indexTestRepository.save(indexTest);
        }
    }

}
