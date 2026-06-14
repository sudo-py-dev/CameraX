package com.camerax.domain.model

enum class BarcodeFormatType {
    QR_CODE,
    EAN_13,
    EAN_8,
    UPC_A,
    UPC_E,
    CODE_128,
    CODE_39,
    CODE_93,
    ITF,
    DATA_MATRIX,
    PDF417,
    AZTEC,
    CODABAR,
    UNKNOWN,
}

enum class BarcodeContentType {
    URL,
    WIFI,
    CONTACT,
    EMAIL,
    PHONE,
    SMS,
    GEO,
    TEXT,
    CALENDAR_EVENT,
    PRODUCT,
    UNKNOWN,
}

data class ScanResult(
    val id: Long = 0L,
    val rawValue: String,
    val format: BarcodeFormatType,
    val contentType: BarcodeContentType,
    val displayValue: String,
    val timestampMs: Long,
)
