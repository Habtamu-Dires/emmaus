package com.hab.emmaus.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {

    PHONE_NOT_VERIFIED("ERR_NOT_VERIFIED","Phone Number not verified",NOT_ACCEPTABLE),
    EMAIL_ALREADY_EXISTS("ERR_EMAIL_EXISTS", "Email already exists", CONFLICT),
//    PHONE_ALREADY_EXISTS("ERR_PHONE_EXISTS", "An account with this email number already exists", CONFLICT),
    PASSWORD_MISMATCH("ERR_PASSWORD_MISMATCH", "The password and confirmation do not match", BAD_REQUEST),
    CHANGE_PASSWORD_MISMATCH("ERR_PASSWORD_MISMATCH", "New password and confirmation do not match", BAD_REQUEST),
    ERR_SENDING_ACTIVATION_EMAIL("ERR_SENDING_ACTIVATION_EMAIL",
                                 "An error occurred while sending the activation email",
                                 HttpStatus.INTERNAL_SERVER_ERROR),

    ERR_USER_DISABLED("ERR_USER_DISABLED",
                      "User account is disabled, please activate your account or contact the administrator",
                      UNAUTHORIZED),
    INVALID_CURRENT_PASSWORD("INVALID_CURRENT_PASSWORD", "The current password is incorrect", BAD_REQUEST),
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found", NOT_FOUND),
    FCM_TOKEN_NOT_FOUND("FCM_TOKEN_NOT_FOUND","FCM device token not found",NOT_FOUND),
    ACCOUNT_ALREADY_DEACTIVATED("ACCOUNT_ALREADY_DEACTIVATED", "Account has been deactivated", BAD_REQUEST),
    BAD_CREDENTIALS("BAD_CREDENTIALS", "Username and / or password is incorrect", UNAUTHORIZED),
    INTERNAL_EXCEPTION("INTERNAL_EXCEPTION",
                       "An internal exception occurred, please try again or contact the admin",
                       HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_NOT_FOUND("USERNAME_NOT_FOUND", "Cannot find user with the provided username", NOT_FOUND),


    // Refresh Token
    REFRESH_TOKEN_REVOKED(
            "REFRESH_TOKEN_REVOKED",
            "Refresh token has been revoked",
            UNAUTHORIZED
    ),

    REFRESH_TOKEN_EXPIRED(
            "REFRESH_TOKEN_EXPIRED",
            "Refresh token has expired",
            UNAUTHORIZED
    ),

    REFRESH_TOKEN_ABSOLUTE_EXPIRED(
            "REFRESH_TOKEN_ABSOLUTE_EXPIRED",
            "Session has expired, please login again",
            UNAUTHORIZED
    ),

    REFRESH_TOKEN_NOT_FOUND(
            "REFRESH_TOKEN_NOT_FOUND",
            "Refresh token not found",
            UNAUTHORIZED
    ),

    INVALID_REFRESH_TOKEN(
            "INVALID_REFRESH_TOKEN",
            "Invalid refresh token",
            UNAUTHORIZED
    ),
    INVALID_TOKEN(
            "INVALID_TOKEN",
            "Invalid token",
            UNAUTHORIZED
    ),
    // OTP related
    LOGIN_BLOCKED(
            "LOGIN_BLOCKED",
            "Login requests are blocked until %s",
            TOO_MANY_REQUESTS
    ),
    OTP_BLOCKED(
            "OTP_BLOCKED",
            "OTP requests are blocked until %s",
            TOO_MANY_REQUESTS
    ),

    OTP_ALREADY_VERIFIED(
            "OTP_ALREADY_VERIFIED",
            "OTP has already been verified",
            CONFLICT
    ),

    OTP_EXPIRED(
            "OTP_EXPIRED",
            "OTP has expired",
            UNAUTHORIZED
    ),

    OTP_MAX_ATTEMPTS_EXCEEDED(
            "OTP_MAX_ATTEMPTS_EXCEEDED",
            "Maximum OTP verification attempts exceeded",
            TOO_MANY_REQUESTS
    ),

    OTP_INVALID(
            "OTP_INVALID",
            "Invalid OTP code",
            BAD_REQUEST
    ),

    OTP_NOT_ACTIVE(
            "OTP_NOT_ACTIVE",
            "OTP is not active",
            BAD_REQUEST
    ),

    OTP_RESEND_LIMIT_REACHED(
            "OTP_RESEND_LIMIT_REACHED",
            "please try after %s minute",
            TOO_MANY_REQUESTS
    ),

    OTP_RESEND_TOO_SOON(
            "OTP_RESEND_TOO_SOON",
            "Please wait %s minute before trying resend",
            TOO_MANY_REQUESTS
    ),


    // Identity / Role-related errors
    USER_EXISTS_BUT_NOT_DRIVER(
            "USER_NOT_DRIVER",
            "The user exists but is not registered as a driver",
            FORBIDDEN
    ),

    USER_EXISTS_BUT_NOT_CONSIGNOR(
            "USER_NOT_CONSIGNOR",
            "The user exists but is not registered as a consignor",
            FORBIDDEN
    ),

    USER_NOT_ALLOWED(
            "USER_NOT_ALLOWED",
            "User not allowed for this operation",
            BAD_REQUEST
    ),

    USER_ROLE_ALREADY_ASSIGNED(
            "USER_ROLE_ALREADY_ASSIGNED",
            "The user already has this role assigned",
            CONFLICT
    ),

    USER_ROLE_NOT_ACTIVE(
            "USER_ROLE_NOT_ACTIVE",
            "The user role is not active",
            FORBIDDEN
    ),

    USER_ROLE_NOT_FOUND(
            "USER_ROLE_NOT_FOUND",
            "User role not found",
            NOT_FOUND
    ),

    // Driver-specific errors
    DRIVER_NOT_FOUND(
            "DRIVER_NOT_FOUND",
            "Driver not found",
            NOT_FOUND
    ),

    DRIVER_NOT_VERIFIED(
            "DRIVER_NOT_VERIFIED",
            "Driver account is not verified",
            FORBIDDEN
    ),

    DRIVER_NOT_APPROVED(
            "DRIVER_NOT_APPROVED",
            "Driver account is not approved",
            FORBIDDEN
    ),

    DRIVER_ALREADY_ASSIGNED(
            "DRIVER_ALREADY_ASSIGNED",
            "Driver is already assigned to another shipment",
            CONFLICT
    ),

    DRIVER_NOT_AVAILABLE(
            "DRIVER_NOT_AVAILABLE",
            "Driver is currently not available",
            BAD_REQUEST
    ),

    // vehicle
    VEHICLE_NOT_FOUND(
            "VEHICLE_NOT_FOUND",
            "Vehicle not found",
            NOT_FOUND
    ),

    DRIVER_VEHICLE_NOT_REGISTERED(
            "DRIVER_VEHICLE_NOT_REGISTERED",
            "Driver has no registered vehicle",
            BAD_REQUEST
    ),

    // Consignor / Customer errors
    CONSIGNOR_NOT_FOUND(
            "CONSIGNOR_NOT_FOUND",
            "Consignor not found",
            NOT_FOUND
    ),

    CONSIGNOR_ACCOUNT_SUSPENDED(
            "CONSIGNOR_ACCOUNT_SUSPENDED",
            "Consignor account is suspended",
            FORBIDDEN
    ),

    CONSIGNOR_NOT_APPROVED(
            "CONSIGNOR_NOT_APPROVED",
            "Consignor account not approved",
            FORBIDDEN
    ),

    CONSIGNOR_NOT_VERIFIED(
            "CONSIGNOR_NOT_VERIFIED",
            "Consignor account is not verified",
            FORBIDDEN
    ),

    // Shipment lifecycle errors
    SHIPMENT_NOT_FOUND(
            "SHIPMENT_NOT_FOUND",
            "Shipment not found",
            NOT_FOUND
    ),
    SHIPMENT_NOT_APPROVABLE(
            "SHIPMENT_NOT_APPROVABLE",
            "Shipment not approvable",
            CONFLICT
    ),
    SHIPMENT_NOT_APPROVED(
            "SHIPMENT_NOT_APPROVED",
            "Shipment not approved",
            CONFLICT
    ),
    SHIPMENT_NOT_READY_FOR_DRIVER_ASSIGNMENT(
            "SHIPMENT_NOT_READY_FOR_DRIVER_ASSIGNMENT",
            "Shipment is not ready for driver assignment",
            CONFLICT
    ),
    SHIPMENT_ALREADY_ASSIGNED(
            "SHIPMENT_ALREADY_ASSIGNED",
            "Shipment is already assigned to a driver",
            CONFLICT
    ),
    SHIPMENT_ALREADY_ACCEPTED(
            "SHIPMENT_ALREADY_ACCEPTED",
            "Shipment is already accepted",
            CONFLICT
    ),
    SHIPMENT_OFFER_ALREADY_REJECTED(
            "SHIPMENT_OFFER_ALREADY_REJECTED",
            "Shipment is already rejected",
            CONFLICT
    ),

    SHIPMENT_NOT_ASSIGNED(
            "SHIPMENT_NOT_ASSIGNED",
            "Shipment has not been assigned to any driver",
            BAD_REQUEST
    ),

    SHIPMENT_ALREADY_DELIVERED(
            "SHIPMENT_ALREADY_DELIVERED",
            "Shipment has already been delivered",
            CONFLICT
    ),

    SHIPMENT_CANCELLED(
            "SHIPMENT_CANCELLED",
            "Shipment has been cancelled",
            BAD_REQUEST
    ),

    INVALID_SHIPMENT_STATUS_TRANSITION(
            "INVALID_SHIPMENT_STATUS_TRANSITION",
            "Invalid shipment status transition",
            BAD_REQUEST
    ),
    SHIPMENT_NON_NEGOTIABLE(
            "SHIPMENT_NON_NEGOTIABLE",
            "Shipment can no longer be negotiated",
            CONFLICT
    ),
    INVALID_SHIPMENT_DATES("INVALID_SHIPMENT_DATES",
            "Delivery Date should be after Loading Date",
            CONFLICT
            ),
    OFFER_NOT_FOUND(
            "OFFER_NOT_FOUND",
            "Offer not found",
            NOT_FOUND
    ),

    // Assignment & bidding
    ASSIGNMENT_NOT_FOUND(
            "ASSIGNMENT_NOT_FOUND",
            "Assignment not found",
            NOT_FOUND
    ),

    DRIVER_CANNOT_BID_OWN_SHIPMENT(
            "DRIVER_CANNOT_BID_OWN_SHIPMENT",
            "Driver cannot bid on own shipment",
            BAD_REQUEST
    ),
    DRIVER_NEGOTIATION_NOT_FOUND(
            "DRIVER_NEGOTIATION_NOT_FOUND",
            "Driver Negotiation not found",
            NOT_FOUND
    ),

    BID_ALREADY_EXISTS(
            "BID_ALREADY_EXISTS",
            "A bid already exists for this shipment by the driver",
            CONFLICT
    ),

    BID_NOT_FOUND(
            "BID_NOT_FOUND",
            "Bid not found",
            NOT_FOUND
    ),

    // document
    DOCUMENT_NOT_FOUND(
            "DOCUMENT_NOT_FOUND",
            "Document not found",
            NOT_FOUND
    ),
    GRN_ALREADY_ISSUED(
            "GRN_ALREADY_ISSUED",
            "Void the previous one if you want to issue a new one",
            NOT_FOUND
    ),
    GDN_ALREADY_ISSUED(
            "GDN_ALREADY_ISSUED",
            "Void the previous one if you want to issue a new one",
            NOT_FOUND
    ),

    // tracking
    TRACKING_NOT_ALLOWED(
            "TRACKING_NOT_ALLOWED",
            "Tracking not allowed",
            CONFLICT
    ),
    TRACKING_NOT_FOUND(
            "TRACKING_NOT_FOUND",
            "Tracking not found",
            NOT_FOUND
    ),

    // Payment & pricing errors
    PAYMENT_NOT_FOUND(
            "PAYMENT_NOT_FOUND",
            "Payment not found",
            NOT_FOUND
    ),
    ASSIGNMENT_PAYMENT_NOT_FOUND(
            "ASSIGNMENT_PAYMENT_NOT_FOUND",
            "Assignment Payment not found",
            NOT_FOUND
    ),
    BOOKING_PAYMENT_NOT_FOUND(
            "BOOKING_PAYMENT_NOT_FOUND",
            "Booking Payment not found",
            NOT_FOUND
    ),

    PAYMENT_ALREADY_COMPLETED(
            "PAYMENT_ALREADY_COMPLETED",
            "Payment has already been completed",
            CONFLICT
    ),

    PAYMENT_REQUIRED(
            "PAYMENT_REQUIRED",
            "Payment is required to proceed",
            CONFLICT
    ),

    INVALID_PAYMENT_AMOUNT(
            "INVALID_PAYMENT_AMOUNT",
            "Invalid payment amount",
            BAD_REQUEST
    ),

    REFUND_NOT_ALLOWED(
            "REFUND_NOT_ALLOWED",
            "Refund is not allowed for this shipment",
            BAD_REQUEST
    ),

    // Ownership & authorization
    RESOURCE_NOT_OWNED_BY_USER(
            "RESOURCE_NOT_OWNED_BY_USER",
            "You do not own this resource",
            FORBIDDEN
    ),

    SHIPMENT_NOT_OWNED_BY_CONSIGNOR(
            "SHIPMENT_NOT_OWNED_BY_CONSIGNOR",
            "Shipment does not belong to this consignor",
            FORBIDDEN
    ),

    DRIVER_NOT_ASSIGNED_TO_SHIPMENT(
            "DRIVER_NOT_ASSIGNED_TO_SHIPMENT",
            "Driver is not assigned to this shipment",
            FORBIDDEN
    ),

    // Operational / workflow constraints
    OPERATION_NOT_ALLOWED_IN_CURRENT_STATE(
            "OPERATION_NOT_ALLOWED",
            "Operation is not allowed in the current state",
            BAD_REQUEST
    ),
    REQUIRED_VEHICLE_REACHED(
            "REQUIRED_VEHICLE_REACHED",
            "Required vehicle capacity reached",
            CONFLICT
    ),

    MAX_ACTIVE_SHIPMENTS_REACHED(
            "MAX_ACTIVE_SHIPMENTS_REACHED",
            "Maximum number of active shipments reached",
            BAD_REQUEST
    ),

    // feedback
    INVALID_RATING(
            "INVALID_RATING",
            "Invalid Rating",
            CONFLICT
    ),
    FEEDBACK_ALREADY_GIVEN(
            "FEEDBACK_ALREADY_GIVEN",
            "Feedback alredy given",
            CONFLICT
    ),

    // notification
    NOTIFICATION_NOT_FOUND(
            "NOTIFICATION_NOT_FOUND",
    "Notification not found",
            NOT_FOUND
    );



    private final String code;
    private final String defaultMessage;
    private final HttpStatus status;

    ErrorCode(final String code,
              final String defaultMessage,
              final HttpStatus status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }
}
