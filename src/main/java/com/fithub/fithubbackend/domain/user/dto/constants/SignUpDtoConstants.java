package com.fithub.fithubbackend.domain.user.dto.constants;

public class SignUpDtoConstants {
    public static final String PASSWORD_REGEXP = "^(?=.*[@$!%*#?&])[A-Za-z@$!%*#?&\\d]{8,}$";
    public static final String PHONE_NUMBER_REGEXP = "^\\d{3}-\\d{3,4}-\\d{4}$";
    public static final String EMAIL_REGEXP = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
    public static final String NAME_REGEXP = "^[a-zA-Z가-힣]*$";
    public static final String FORM_DATA_ERROR_REGEXP = "[Optional\\[\\]]";
}
