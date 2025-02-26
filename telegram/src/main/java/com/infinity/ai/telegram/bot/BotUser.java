package com.infinity.ai.telegram.bot;

import com.infinity.ai.telegram.common.SecretUtitls;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BotUser {
    public Long id;
    public String userId;
    public String name;
    public String inviteCode;
    public String pwd;

    public String encryptUserId() {
        return SecretUtitls.encrypt(this.userId);
    }

    public String encryptName() {
        return SecretUtitls.encrypt(this.name);
    }

    public String encryptInviteCode() {
        return SecretUtitls.encrypt(this.inviteCode);
    }

    public String encryptId() {
        return SecretUtitls.encrypt(this.id.toString());
    }

    public String encryptPwd() {
        return SecretUtitls.encrypt(this.pwd);
    }
}
