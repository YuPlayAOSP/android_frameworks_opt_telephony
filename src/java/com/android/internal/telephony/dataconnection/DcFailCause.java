/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.internal.telephony.dataconnection;

import android.content.res.Resources;
import java.util.HashMap;

/**
 * Returned as the reason for a connection failure as defined
 * by RIL_DataCallFailCause in ril.h and some local errors.
 */
public enum DcFailCause {
    NONE(0),

    // This series of errors as specified by the standards
    // specified in ril.h
    OPERATOR_BARRED(0x08),                  /* no retry */
    INSUFFICIENT_RESOURCES(0x1A),
    MISSING_UNKNOWN_APN(0x1B),              /* no retry */
    UNKNOWN_PDP_ADDRESS_TYPE(0x1C),         /* no retry */
    USER_AUTHENTICATION(0x1D),              /* no retry */
    ACTIVATION_REJECT_GGSN(0x1E),           /* no retry */
    ACTIVATION_REJECT_UNSPECIFIED(0x1F),
    SERVICE_OPTION_NOT_SUPPORTED(0x20),     /* no retry */
    SERVICE_OPTION_NOT_SUBSCRIBED(0x21),    /* no retry */
    SERVICE_OPTION_OUT_OF_ORDER(0x22),
    NSAPI_IN_USE(0x23),                     /* no retry */
    REGULAR_DEACTIVATION(0x24),             /* possibly restart radio, based on config */
    ONLY_IPV4_ALLOWED(0x32),                /* no retry */
    ONLY_IPV6_ALLOWED(0x33),                /* no retry */
    ONLY_SINGLE_BEARER_ALLOWED(0x34),
    PROTOCOL_ERRORS(0x6F),                  /* no retry */

    // Local errors generated by Vendor RIL
    // specified in ril.h
    REGISTRATION_FAIL(-1),
    GPRS_REGISTRATION_FAIL(-2),
    SIGNAL_LOST(-3),
    PREF_RADIO_TECH_CHANGED(-4),            /* no retry */
    RADIO_POWER_OFF(-5),                    /* no retry */
    TETHERED_CALL_ACTIVE(-6),               /* no retry */
    ERROR_UNSPECIFIED(0xFFFF),

    // Errors generated by the Framework
    // specified here
    UNKNOWN(0x10000),
    RADIO_NOT_AVAILABLE(0x10001),                   /* no retry */
    UNACCEPTABLE_NETWORK_PARAMETER(0x10002),        /* no retry */
    CONNECTION_TO_DATACONNECTIONAC_BROKEN(0x10003),
    LOST_CONNECTION(0x10004),
    RESET_BY_FRAMEWORK(0x10005);

    private final boolean mRestartRadioOnRegularDeactivation = Resources.getSystem().getBoolean(
            com.android.internal.R.bool.config_restart_radio_on_pdp_fail_regular_deactivation);
    private final int mErrorCode;
    private static final HashMap<Integer, DcFailCause> sErrorCodeToFailCauseMap;
    static {
        sErrorCodeToFailCauseMap = new HashMap<Integer, DcFailCause>();
        for (DcFailCause fc : values()) {
            sErrorCodeToFailCauseMap.put(fc.getErrorCode(), fc);
        }
    }

    DcFailCause(int errorCode) {
        mErrorCode = errorCode;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    /** Radio has failed such that the radio should be restarted */
    public boolean isRestartRadioFail() {
        return (this == REGULAR_DEACTIVATION && mRestartRadioOnRegularDeactivation);
    }

    public boolean isPermanentFail() {
        return (this == OPERATOR_BARRED) || (this == MISSING_UNKNOWN_APN) ||
               (this == UNKNOWN_PDP_ADDRESS_TYPE) || (this == USER_AUTHENTICATION) ||
               (this == ACTIVATION_REJECT_GGSN) || (this == SERVICE_OPTION_NOT_SUPPORTED) ||
               (this == SERVICE_OPTION_NOT_SUBSCRIBED) || (this == NSAPI_IN_USE) ||
               (this == ONLY_IPV4_ALLOWED) || (this == ONLY_IPV6_ALLOWED) ||
               (this == PROTOCOL_ERRORS) ||
               (this == RADIO_POWER_OFF) || (this == TETHERED_CALL_ACTIVE) ||
               (this == RADIO_NOT_AVAILABLE) || (this == UNACCEPTABLE_NETWORK_PARAMETER);
    }

    public boolean isEventLoggable() {
        return (this == OPERATOR_BARRED) || (this == INSUFFICIENT_RESOURCES) ||
                (this == UNKNOWN_PDP_ADDRESS_TYPE) || (this == USER_AUTHENTICATION) ||
                (this == ACTIVATION_REJECT_GGSN) || (this == ACTIVATION_REJECT_UNSPECIFIED) ||
                (this == SERVICE_OPTION_NOT_SUBSCRIBED) ||
                (this == SERVICE_OPTION_NOT_SUPPORTED) ||
                (this == SERVICE_OPTION_OUT_OF_ORDER) || (this == NSAPI_IN_USE) ||
                (this == ONLY_IPV4_ALLOWED) || (this == ONLY_IPV6_ALLOWED) ||
                (this == PROTOCOL_ERRORS) || (this == SIGNAL_LOST) ||
                (this == RADIO_POWER_OFF) || (this == TETHERED_CALL_ACTIVE) ||
                (this == UNACCEPTABLE_NETWORK_PARAMETER);
    }

    public static DcFailCause fromInt(int errorCode) {
        DcFailCause fc = sErrorCodeToFailCauseMap.get(errorCode);
        if (fc == null) {
            fc = UNKNOWN;
        }
        return fc;
    }
}
