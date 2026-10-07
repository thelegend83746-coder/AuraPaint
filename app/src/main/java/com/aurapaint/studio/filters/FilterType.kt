package com.aurapaint.studio.filters

enum class FilterType(val displayName: String) {
    BRIGHTNESS_CONTRAST("Brightness & Contrast"),
    HUE_SATURATION("Hue & Saturation"),
    EXPOSURE("Exposure & Gamma"),
    GAUSSIAN_BLUR("Gaussian Blur"),
    SHARPEN("Sharpen"),
    PIXELATE("Pixelate / Mosaic"),
    GRAYSCALE("Grayscale"),
    SEPIA("Vintage Sepia"),
    INVERT("Invert Colors"),
    POSTERIZE("Posterize"),
    THRESHOLD("B&W Threshold"),
    TONE_CURVES("Tone Curves")
}
