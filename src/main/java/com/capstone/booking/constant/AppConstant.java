package com.capstone.booking.constant;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppConstant {

    private AppConstant() {}

    public static final String DATE_JSON_FORMAT = "dd-MM-yyyy";

    public static final String DATETIME_JSON_FORMAT = "dd-MM-yyyy HH:mm:ss";
    public enum ResponseCode {

        SUCCESS("SUCCESS", "Success!"),
        DATA_NOT_FOUND("DATA_NOT_FOUND", "Data not found!"),
        UNKNOWN_ERROR("UNKNOWN_ERROR", "Happened unknown error!"),

        NOT_LOGGED_IN("NOT_LOGGED_IN", "Login first to access this endpoint"),
        UNAUTHORIZED_ACCESS("UNAUTHORIZED_ACCESS", "You do not have permission to access this"),

        EMAIL_ALREADY_EXIST("EMAIL_ALREADY_EXIST", "An account with this email already exist"),
        BAD_CREDENTIALS("BAD_CREDENTIALS", "Provided Credentials is wrong!");

        private final String code;
        private final String message;

        private ResponseCode(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return this.code;
        }

        public String getMessage() {
            return this.message;
        }

    }

    public enum RoleType {
        ROLE_USER,
        ROLE_ADMIN
    }

    public enum FacilityType {
        PROJECTOR, WIFI, PARKING, BANK, RESTAURANT
    }

    public enum ReservationStatus {
        PENDING, ACTIVE, WAITING, CANCELED, COMPLETED
    }


}
