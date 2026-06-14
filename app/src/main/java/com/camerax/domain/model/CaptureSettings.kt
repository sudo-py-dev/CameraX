package com.camerax.domain.model

enum class AspectRatioMode {
    RATIO_4_3,
    RATIO_16_9,
    RATIO_1_1,
}

enum class PhotoResolution {
    HIGH,
    MEDIUM,
    LOW,
}

enum class VideoQuality {
    FHD_1080,
    HD_720,
    SD_480,
}

enum class TimerDelay(val seconds: Int) {
    OFF(0),
    THREE(3),
    FIVE(5),
    TEN(10),
}

enum class FlashMode {
    AUTO,
    ON,
    OFF,
    TORCH,
}
