package com.aurapaint.studio.vector

data class VectorPoint(
    var x: Float,
    var y: Float,
    var handleInX: Float? = null,
    var handleInY: Float? = null,
    var handleOutX: Float? = null,
    var handleOutY: Float? = null,
    var pressure: Float = 1.0f
)
