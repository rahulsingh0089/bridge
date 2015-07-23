package com.afollestad.bridge;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Aidan Follestad (afollestad)
 */
public class BridgeException extends Exception {

    public static final int REASON_REQUEST_CANCELLED = 1;
    public static final int REASON_REQUEST_FAILED = 2;

    public static final int REASON_RESPONSE_UNSUCCESSFUL = 3;
    public static final int REASON_RESPONSE_UNPARSEABLE = 4;
    public static final int REASON_RESPONSE_IOERROR = 5;

    @IntDef({REASON_REQUEST_CANCELLED, REASON_REQUEST_FAILED, REASON_RESPONSE_UNSUCCESSFUL,
            REASON_RESPONSE_UNPARSEABLE, REASON_RESPONSE_IOERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Reason {
    }

    @Reason
    private int mReason;
    @Nullable
    private Request mRequest;
    @Nullable
    private Response mResponse;

    // Request constructors

    protected BridgeException(@NonNull Request request, Exception wrap) {
        super(String.format("%s %s error: %s", request.method().name(), request.url(), wrap.getMessage()), wrap);
        mRequest = request;
        mReason = REASON_REQUEST_FAILED;
    }

    protected BridgeException(@NonNull Request cancelledRequest) {
        super(String.format("%s request to %s was cancelled.",
                cancelledRequest.method().name(), cancelledRequest.url()));
        mRequest = cancelledRequest;
        mReason = REASON_REQUEST_CANCELLED;
    }

    // Response constructors

    protected BridgeException(@Nullable Response response, @NonNull String message, @Reason int reason) {
        super(message);
        mResponse = response;
        mReason = reason;
    }

    protected BridgeException(@NonNull Response response, @NonNull Exception e, @Reason int reason) {
        super(String.format("%s: %s", response.toString(), e.getLocalizedMessage()), e);
        mResponse = response;
        mReason = reason;
    }

    // Getters

    @Nullable
    public Request request() {
        return mRequest;
    }

    @Nullable
    public Response response() {
        return mResponse;
    }

    @Reason
    public int reason() {
        return mReason;
    }

    @Override
    public String toString() {
        switch (reason()) {
            case BridgeException.REASON_REQUEST_CANCELLED:
                return "Request cancelled";
            case BridgeException.REASON_REQUEST_FAILED:
                return "Request failed: " + toString();
            case BridgeException.REASON_RESPONSE_UNSUCCESSFUL:
                return "Response unsuccessful: " + toString();
            case BridgeException.REASON_RESPONSE_UNPARSEABLE:
                return "Response Unparseable: " + toString();
            case BridgeException.REASON_RESPONSE_IOERROR:
                return "Response I/O Error: " + toString();
        }
        return super.toString();
    }
}
