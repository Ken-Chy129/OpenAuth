package cn.ken.auth.util;

import lombok.Getter;

import java.util.Arrays;

/**
 * <pre>
 *
 * </pre>
 *
 * @author <a href="https://github.com/Ken-Chy129">Ken-Chy129</a>
 * @since 2023/3/17 15:38
 */
public class UserInfoUtil {

    @Getter
    public enum UserGender {
        MALE(1, "男"),
        FEMALE(0, "女"),
        UNKNOWN(-1, "未知");

        private int code;
        private String desc;

        UserGender(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    public static UserGender getRealGender(String originalGender) {
        if (null == originalGender || UserGender.UNKNOWN.getCode() == Integer.parseInt(originalGender)) {
            return UserGender.UNKNOWN;
        }
        String[] males = {"m", "男", "1", "male"};
        if (Arrays.asList(males).contains(originalGender.toLowerCase())) {
            return UserGender.MALE;
        }
        return UserGender.FEMALE;
    }
}
