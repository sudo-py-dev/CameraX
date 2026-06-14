package com.camerax.domain.model

import com.google.mlkit.vision.barcode.common.Barcode

object BarcodeMapper {
    fun mapFormat(format: Int): BarcodeFormatType =
        when (format) {
            Barcode.FORMAT_QR_CODE -> BarcodeFormatType.QR_CODE
            Barcode.FORMAT_EAN_13 -> BarcodeFormatType.EAN_13
            Barcode.FORMAT_EAN_8 -> BarcodeFormatType.EAN_8
            Barcode.FORMAT_UPC_A -> BarcodeFormatType.UPC_A
            Barcode.FORMAT_UPC_E -> BarcodeFormatType.UPC_E
            Barcode.FORMAT_CODE_128 -> BarcodeFormatType.CODE_128
            Barcode.FORMAT_CODE_39 -> BarcodeFormatType.CODE_39
            Barcode.FORMAT_CODE_93 -> BarcodeFormatType.CODE_93
            Barcode.FORMAT_ITF -> BarcodeFormatType.ITF
            Barcode.FORMAT_DATA_MATRIX -> BarcodeFormatType.DATA_MATRIX
            Barcode.FORMAT_PDF417 -> BarcodeFormatType.PDF417
            Barcode.FORMAT_AZTEC -> BarcodeFormatType.AZTEC
            Barcode.FORMAT_CODABAR -> BarcodeFormatType.CODABAR
            else -> BarcodeFormatType.UNKNOWN
        }

    fun mapType(valueType: Int): BarcodeContentType =
        when (valueType) {
            Barcode.TYPE_URL -> BarcodeContentType.URL
            Barcode.TYPE_WIFI -> BarcodeContentType.WIFI
            Barcode.TYPE_CONTACT_INFO -> BarcodeContentType.CONTACT
            Barcode.TYPE_EMAIL -> BarcodeContentType.EMAIL
            Barcode.TYPE_PHONE -> BarcodeContentType.PHONE
            Barcode.TYPE_SMS -> BarcodeContentType.SMS
            Barcode.TYPE_GEO -> BarcodeContentType.GEO
            Barcode.TYPE_TEXT -> BarcodeContentType.TEXT
            Barcode.TYPE_CALENDAR_EVENT -> BarcodeContentType.CALENDAR_EVENT
            Barcode.TYPE_PRODUCT -> BarcodeContentType.PRODUCT
            else -> BarcodeContentType.UNKNOWN
        }
}
