/*
 * 
 */
package com.wepay.android.models;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import com.roam.roamreaderunifiedapi.constants.Parameter;
import com.wepay.android.enums.ErrorCode;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The Class Error contains information about an error that occurs in the sdk.
 */
public class Error extends Exception {

    /** The error domain. */
    private String errorDomain;

    /** The error category. */
    private String errorCategory;

    /** The error description. */
    private String errorDescription;

    /** The error code. */
    private Integer errorCode;

    /** The inner exception. */
    private Exception innerException;

    /** The constant ERROR_DOMAIN_API */
    final public static String ERROR_DOMAIN_API = "com.wepay.api";

    /** The constant ERROR_DOMAIN_SDK */
    final public static String ERROR_DOMAIN_SDK = "com.wepay.sdk";

    /** The constant string representing the error category Card reader Error. */
    final public static String ERROR_CATEGORY_CARD_READER = "card_reader_error";

    /** The constant string representing the error category API Error. */
    final public static String ERROR_CATEGORY_API = "api_error";

    /** The constant string representing the error category SDK Error. */
    final public static String ERROR_CATEGORY_SDK = "sdk_error";

    /** \internal
     * Instantiates a new error. Use this constructor when an inner exception is available.
     *
     * @param errorCode the error code
     * @param errorDomain the error domain
     * @param errorCategory the error category
     * @param errorDescription the error description
     * @param innerException the inner exception that caused the error
     */
    public Error(Integer errorCode, String errorDomain, String errorCategory, String errorDescription, Exception innerException) {
        super(errorDescription != null ? errorDescription : errorCategory);

        this.errorCode = errorCode;
        this.errorDomain = errorDomain;
        this.errorCategory = errorCategory;
        this.errorDescription = errorDescription;
        this.innerException = innerException;
    }

    /** \internal
     * Instantiates a new error. Use this constructor when there is no inner exception.
     *
     * @param errorCode the error code
     * @param errorDomain the error domain
     * @param errorCategory the error category
     * @param errorDescription the error description
     */
    public Error(Integer errorCode, String errorDomain, String errorCategory, String errorDescription) {
        this(errorCode, errorDomain, errorCategory, errorDescription, null);
    }

    /** \internal
     * Instantiates a new error from a WePay server api response.
     *
     * @param errorResponse the error response from the WePay server
     * @param throwable the throwable generated by the api call
     */
    public Error(JSONObject errorResponse, Throwable throwable) {
        this(errorResponse.optInt("error_code", ErrorCode.UNKNOWN_ERROR.getCode()),
                errorResponse.isNull("error_domain") ? ERROR_DOMAIN_API : errorResponse.optString("error_domain", ERROR_DOMAIN_API),
                errorResponse.isNull("error") ? ERROR_CATEGORY_API : errorResponse.optString("error", ERROR_CATEGORY_API),
                errorResponse.isNull("error_description") ? "" : errorResponse.optString("error_description", ""),
                new Exception(throwable));
    }

    /**
     * Gets the error category.
     *
     * @return the error category
     */
    public String getErrorCategory() {
        return errorCategory;
    }

    /**
     * Gets the error domain.
     *
     * @return the error domain
     */
    public String getErrorDomain() {
        return this.errorDomain;
    }

    /**
     * Gets the error description.
     *
     * @return the error description
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    /**
     * Gets the error code.
     *
     * @return the error code
     */
    public Integer getErrorCode() {
        return errorCode;
    }

    /**
     * Gets the inner exception.
     *
     * @return the inner exception
     */
    public Exception getInnerException() {
        return innerException;
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#getMessage()
     */
    public String getMessage() {
        return this.errorDescription;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
        errorMap.put("errorDomain", this.errorDomain);
        errorMap.put("errorCategory", this.errorCategory);
        errorMap.put("errorCode", this.errorCode);
        errorMap.put("errorDescription", this.errorDescription);
        errorMap.put("innerException", this.innerException);

        GsonBuilder builder = new GsonBuilder();
        builder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return (f.getDeclaredClass() == Throwable.class && f.getName().equals("cause"));
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });
        return builder.create().toJson(errorMap, LinkedHashMap.class);
    }

    /** \internal
     * Instantiates a card reader initialization error.
     *
     * @return the card reader timeout error
     */
    public static Error getCardReaderInitializationError() {
        return new Error(ErrorCode.CARD_READER_INITIALIZATION_ERROR.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_CARD_READER, "Failed to initialize card reader");
    }

    /** \internal
     * Instantiates a card reader timeout error.
     *
     * @return the card reader timeout error
     */
    public static Error getCardReaderTimeoutError() {
        return new Error(ErrorCode.CARD_READER_TIME_OUT_ERROR.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_CARD_READER, "Card reader timed out.");
    }

    /** \internal
     * Instantiates a generic card reader error with the provided message.
     * @param errorMessage the message used in the error
     * @return the card reader error
     */
    public static Error getCardReaderErrorWithMessage(String errorMessage) {
        return new Error(ErrorCode.CARD_READER_STATUS_ERROR.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_CARD_READER, errorMessage);
    }

    /** \internal
     * Instantiates a invalid card data error.
     *
     * @return the invalid card data error
     */
    public static Error getInvalidCardDataError() {
        return new Error(ErrorCode.INVALID_CARD_DATA.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_CARD_READER, "Invalid card data");
    }

    /** \internal
     * Instantiates a card not supported error.
     *
     * @return the card not supported error
     */
    public static Error getCardNotSupportedError() {
        return new Error(ErrorCode.CARD_NOT_SUPPORTED.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_CARD_READER, "This card is not supported");
    }

    /** \internal
     * Instantiates an EMV transaction error with the provided message.
     *
     * @param errorMessage the message used in the error
     * @return the EMV transaction error
     */
    public static Error getEmvTransactionErrorWithMessage(String errorMessage) {
        return new Error(ErrorCode.EMV_TRANSACTION_ERROR.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_CARD_READER, errorMessage);
    }

    /** \internal
     * Instantiates invalid application id error.
     *
     * @return the invalid application id error
     */
    public static Error getInvalidApplicationIdError() {
        return new Error(ErrorCode.INVALID_APPLICATION_ID.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_CARD_READER, "Invalid application ID selected");
    }

    /** \internal
     * Instantiates declined by card error.
     *
     * @return the declined by card error
     */
    public static Error getDeclinedByCardError() {
        return new Error(ErrorCode.DECLINED_BY_CARD.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_CARD_READER, "The transaction was declined by the card");
    }

    /** \internal
     * Instantiates card blocked error.
     *
     * @return the card blocked error
     */

    public static Error getCardBlockedError() {
        return new Error(ErrorCode.CARD_BLOCKED.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_CARD_READER, "This card has been blocked");
    }

    /** \internal
     * Instantiates issuer unreachable error.
     *
     * @return the issuer unreachable error
     */

    public static Error getIssuerUnreachableError() {
        return new Error(ErrorCode.ISSUER_UNREACHABLE.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_CARD_READER, "The issuing bank could not be reached");
    }

    /** \internal
     * Instantiates a no data returned error.
     *
     * @return the no data returned error
     */
    public static Error getNoDataReturnedError() {
        return new Error(ErrorCode.NO_DATA_RETURNED_ERROR.getCode(), ERROR_DOMAIN_API, ERROR_CATEGORY_API, "No data returned by the API.");
    }

    /** \internal
     * Instantiates card declined by issuer error.
     *
     * @return the card declined by issuer error
     */

    public static Error getCardDeclinedByIssuerError() {
        return new Error(ErrorCode.CARD_DECLINED_BY_ISSUER.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_SDK, "The transaction was declined by the issuer bank.");
    }

    /** \internal
     * Instantiates invalid transaction amount error.
     *
     * @return the invalid transaction amount error
     */

    public static Error getInvalidTransactionAmountError() {
        return new Error(ErrorCode.INVALID_TRANSACTION_AMOUNT.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_SDK, "The provided transaction amount is invalid.");
    }

    /** \internal
     * Instantiates invalid transaction currency code error.
     *
     * @return the invalid transaction currency code error
     */

    public static Error getInvalidTransactionCurrencyCodeError() {
        return new Error(ErrorCode.INVALID_TRANSACTION_CURRENCY_CODE.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_SDK, "The provided currency code is invalid.");
    }

    /** \internal
     * Instantiates invalid transaction account ID error.
     *
     * @return the invalid transaction account ID  error
     */

    public static Error getInvalidTransactionAccountIDError() {
        return new Error(ErrorCode.INVALID_TRANSACTION_ACCOUNT_ID.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_SDK, "The provided account ID is invalid.");
    }

    /** \internal
     * Instantiates transaction info not provided error.
     *
     * @return the transaction info not provided error
     */

    public static Error getTransactionInfoNotProvidedError() {
        // This will not happen on Android. This error only exists to maintain parity with iOS.
        return new Error(ErrorCode.TRANSACTION_INFO_NOT_PROVIDED.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_SDK, "Transaction info was not provided.");
    }

    /** \internal
     * Instantiates payment method cannot be tokenized error.
     *
     * @return the payment method cannot be tokenized error
     */

    public static Error getPaymentMethodCannotBeTokenizedError() {
        return new Error(ErrorCode.PAYMENT_METHOD_CANNOT_BE_TOKENIZED.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_SDK, "This payment method cannot be tokenized.");
    }

    /** \internal
     * Instantiates failed to get battery level error.
     *
     * @return the failed to get battery level error
     */

    public static Error getFailedToGetBatteryLevelError() {
        return new Error(ErrorCode.FAILED_TO_GET_BATTERY_LEVEL.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_CARD_READER, "Battery level could not be determined.");
    }

    /** \internal
     * Instantiates a card reader not connected error.
     *
     * @return the card reader not connected error
     */
    public static Error getCardReaderNotConnectedError() {
        return new Error(ErrorCode.CARD_READER_NOT_CONNECTED_ERROR.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_CARD_READER, "Card reader is not connected.");
    }

    /** \internal
     * Instantiates a card reader general error.
     *
     * @return the card reader general error
     */
    public static Error getCardReaderGeneralError() {
        return new Error(ErrorCode.CARD_READER_GENERAL_ERROR.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_CARD_READER, "Swipe failed due to: (a) uneven swipe speed, (b) fast swipe, (c) slow swipe, or (d) damaged card.");
    }

    /** \internal
     * Instantiates a card reader unknown error.
     *
     * @return the card reader unknown error
     */
    public static Error getCardReaderUnknownError() {
        return new Error(ErrorCode.UNKNOWN_ERROR.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_CARD_READER, "There was an unexpected error.");
    }

    /** \internal
     * Instantiates a card reader status error.
     *
     * @param message the message
     * @return the card reader status error
     */
    public static Error getCardReaderStatusError(String message) {
        return new Error(ErrorCode.CARD_READER_STATUS_ERROR.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_CARD_READER, message);
    }

    /** \internal
     * Instantiates a invalid signature image error.
     *
     * @param e the exception generated during image validation
     *
     * @return the invalid signature image error
     */
    public static Error getInvalidSignatureImageError(Exception e) {
        return new Error(ErrorCode.INVALID_SIGNATURE_IMAGE_ERROR.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_CARD_READER, "Invalid signature image provided.", e);
    }

    /** \internal
     * Instantiates a name not found error.
     *
     * @return the name not found error
     */
    public static Error getNameNotFoundError() {
        return new Error(ErrorCode.NAME_NOT_FOUND_ERROR.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_CARD_READER, "Name not found.");
    }

    /** \internal
     * Instantiates a card reader selection error.
     *
     * @return the card reader selection error
     */
    public static Error getInvalidCardReaderSelectionError() {
        return new Error(ErrorCode.INVALID_CARD_READER_SELECTION.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_CARD_READER, "Card reader selection is invalid.");
    }

    /** \internal
     * Instantiates a card reader battery too low error.
     *
     * @return the card reader battery too low error
     */
    public static Error getCardReaderBatteryTooLowError() {
        return new Error(ErrorCode.CARD_READER_BATTERY_TOO_LOW.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_CARD_READER, "The card reader battery does not have enough charge. Please charge before using.");
    }

    /** \internal
     * Instantiates an unable to connect to card reader error.
     *
     * @return the unable to connect to card reader error
     */
    public static Error getCardReaderUnableToConnectError() {
        return new Error(ErrorCode.CARD_READER_UNABLE_TO_CONNECT.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_CARD_READER, "Please make sure you’re using a supported card reader and that it is fully charged.");
    }

    public static Error getErrorWithCardReaderResponseData(Map<Parameter, Object> data) {
        if (data != null) {
            Object errorCode = data.get(Parameter.ErrorCode);
            // if the error code is G4X_DECODE_SWIPE_FAIL (equivalent to general error on iOS)
            if(errorCode.toString().equalsIgnoreCase("G4X_DECODE_SWIPE_FAIL")) {
                // return CardReaderGeneralError
                return Error.getCardReaderGeneralError();
            }
        }

        return Error.getCardReaderUnknownError();
    }

    public static Error getCardReaderGeneralErrorWithMessage(String errorDescription) {
        return new Error(ErrorCode.CARD_READER_GENERAL_ERROR.getCode(), ERROR_DOMAIN_SDK, ERROR_CATEGORY_CARD_READER, errorDescription);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = hash * 31 + errorCode;
        hash = hash * 31 + (errorCategory == null ? 0 : errorCategory.hashCode());
        hash = hash * 31 + (errorDescription == null ? 0 : errorDescription.hashCode());
        hash = hash * 31 + (errorDomain == null ? 0 : errorDomain.hashCode());
        hash = hash * 31 + (innerException == null ? 0 : innerException.hashCode());
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (this.getClass() != o.getClass())) {
            return false;
        }
        Error e = (Error) o;
        return e.toString().equals(this.toString());
    }
}
