package com.unipapers.backend.Modules.Auth.Enums;

public enum RevokedReason {
    LOGOUT,
    PASSWORD_CHANGE,
    ADMIN_FORCE_LOGOUT,
    SUSPICIOUS_ACTIVITY,
    TOKEN_REUSE,
    TOKEN_EXPIRED,
    CREATE_ROOM_FOR_OTHER_SESSIONS
}
