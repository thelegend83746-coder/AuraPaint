package com.aurapaint.studio.symmetry

enum class SymmetryMode(val title: String, val segments: Int) {
    NONE("Off", 1),
    VERTICAL("Vertical Mirror", 2),
    HORIZONTAL("Horizontal Mirror", 2),
    FOUR_WAY("4-Way Quadrant", 4),
    RADIAL_6("Radial (6 Segments)", 6),
    RADIAL_8("Radial (8 Segments)", 8)
}
