package com.lollipop.debug.floating.creator

class FloatingPanelConfig {

    companion object {
        val DEFAULT = FloatingPanelConfig()
    }

    var defaultHeightWeight = 0.5F
    var maxWidthWeight = 0.9F
    var maxHeightWeight = 1F
    var heightOffsetStep = 0.1F
    var minHeightWeight = 0.1F
    var hideOnBackground = true
    var closeOnlyHide = true
}