package com.fithub.fithubbackend.domain.user.enums;

public enum Gender {
    F, M, UNDEFINED;

    public static Gender toGender(String gender) {
        if (gender != null) {
            switch (gender) {
                case "F": return F;
                case "M": return M;
                default: return UNDEFINED;
            }
        }
        return UNDEFINED;
    }
}