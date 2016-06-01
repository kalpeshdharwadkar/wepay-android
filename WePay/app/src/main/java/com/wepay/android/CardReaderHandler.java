/*
 * 
 */
package com.wepay.android;

import com.wepay.android.enums.CardReaderStatus;
import com.wepay.android.enums.CurrencyCode;
import com.wepay.android.models.Error;
import com.wepay.android.models.PaymentInfo;

/** \interface CardReaderHandler
 * The Interface CardReaderHandler defines the methods used to communicate information regarding the card reader.
 */
public interface CardReaderHandler {

    /**
     * Gets called when the card reader reads a card's information successfully.
     *
     * @param paymentInfo the payment info read from a card.
     */
    public void onSuccess(PaymentInfo paymentInfo);

    /**
     * Gets called when the card reader fails to read a card's information.
     *
     * @param error the error due to which card reading failed.
     */
    public void onError(Error error);

    /**
     * Gets called whenever the card reader changes status.
     *
     * @param status the status.
     */
    public void onStatusChange(CardReaderStatus status);

    /**
     * Gets called when the connected card reader is previously configured, to give the app an opportunity to reset the device. The app must respond by executing callback.resetCardReader(). The transaction cannot proceed until this callback is executed. The card reader must be reset here if the merchant manually resets the reader via the hardware reset button on the reader.
     * @param callback the callback object.
     */
    public void onReaderResetRequested(CardReaderResetCallback callback);

    /**
     * Gets called so that the app can provide the amount, currency code and the WePay account Id of the merchant. The app must respond by executing callback.useTransactionInfo(). The transaction cannot proceed until this callback is executed.
     * @param callback the callback object.
     */
    public void onTransactionInfoRequested(CardReaderTransactionInfoCallback callback);

    /**
     * Gets called so that an email address can be provided before a transaction is authorized. The app must respond by executing callback.insertPayerEmail(). The transaction cannot proceed until the callback is executed.
     *
     * @param callback the callback object.
     */
    public void onPayerEmailRequested(CardReaderEmailCallback callback);

    /** \interface CardReaderResetCallback
     * The Interface CardReaderResetCallback defines the method used to provide information to the card reader before a transaction.
     */
    public interface CardReaderResetCallback {

        /**
         * The callback function that must be executed by the app when onReaderResetRequested() is called by the SDK.
         * <p>
         * Examples:
         * callback.resetCardReader(true);
         * callback.resetCardReader(false);
         *
         * @param shouldReset The answer to the question: "Should the card reader be reset?".
         */
        public void resetCardReader(boolean shouldReset);
    }

    /** \interface CardReaderTransactionInfoCallback
     * The Interface CardReaderTransactionInfoCallback defines the method used to provide transaction information to the card reader before a transaction.
     */
    public interface CardReaderTransactionInfoCallback {

        /**
         * The callback function that must be executed when onTransactionInfoRequested() is called by the SDK.
         * Note: In the staging environment, use amounts of 20.61, 120.61, 23.61 and 123.61 to simulate authorization errors. Amounts of 21.61, 121.61, 22.61 and 122.61 will simulate successful auth.
         * <p>
         * Example:
         * callback.useTransactionInfo(21.61, CurrencyCode.USD, 1234567);
         *
         * @param amount       the amount for the transaction. It will be rounded to the nearest two decimal places.
         * @param currencyCode the currency code for the transaction. e.g. CurrencyCode.USD.
         * @param accountId    the WePay account id of the merchant.
         */
        public void useTransactionInfo(double amount, CurrencyCode currencyCode, long accountId);
    }

    /** \interface CardReaderEmailCallback
     * The Interface CardReaderEmailCallback defines the method used to provide email information to the card reader after a transaction.
     */
    public interface CardReaderEmailCallback {

        /**
         * The callback function that must be executed by the app when onPayerEmailRequested() is called by the SDK.
         *
         * Examples:
         *     callback.insertPayerEmail("android-example@wepay.com");
         *     callback.insertPayerEmail(null);
         *
         * @param email the payer's email address.
         */
        public void insertPayerEmail(String email);

    }
}