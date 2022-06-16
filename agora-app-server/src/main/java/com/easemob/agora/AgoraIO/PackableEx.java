package com.easemob.agora.AgoraIO;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
