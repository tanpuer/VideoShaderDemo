package com.example.templechen.videoshaderdemo.filter

import android.content.Context

class FilterListUtil {

    companion object {
        val LIST = listOf(
            "BaseFilter",
            "GrayFilter",
            "FourPartFilter",
            "WaterMarkFilter",
            "BrightnessFilter",
            "GlassSphereFilter",
            "ZoomBlurFilter",
            "VibranceFilter",
            "TransformFilter",
            "SwirlFilter",
            "PixelationFilter",
            "GaussianBlurFilter",
            "SketchFilter",
            "SobelEdgeDetectionFilter"
        )

        fun setFilter(type: String, mOESTextureId: Int, mContext: Context) : BaseFilter{
            val filter: BaseFilter
            when (type) {
                "BaseFilter" -> filter = BaseFilter(mContext, mOESTextureId)
                "GrayFilter" -> filter = GrayFilter(mContext, mOESTextureId)
                "FourPartFilter" -> filter = FourPartFilter(mContext, mOESTextureId)
                "WaterMarkFilter" -> filter = WaterMarkFilter(mContext, mOESTextureId)
                "BrightnessFilter" -> filter = BrightnessFilter(mContext, mOESTextureId)
                "GlassSphereFilter" -> filter = GlassSphereFilter(mContext, mOESTextureId)
                "ZoomBlurFilter" -> filter = ZoomBlurFilter(mContext, mOESTextureId)
                "VibranceFilter" -> filter = VibranceFilter(mContext, mOESTextureId)
                "TransformFilter" -> filter = TransformFilter(mContext, mOESTextureId)
                "SwirlFilter" -> filter = SwirlFilter(mContext, mOESTextureId)
                "PixelationFilter" -> filter = PixelationFilter(mContext, mOESTextureId)
                "GaussianBlurFilter" -> filter = GaussianBlurFilter(mContext, mOESTextureId)
                "SketchFilter" -> filter = SketchFilter(mContext, mOESTextureId)
                "SobelEdgeDetectionFilter" -> filter = SobelEdgeDetectionFilter(mContext, mOESTextureId)
                else -> filter = BaseFilter(mContext, mOESTextureId)
            }
            return filter
        }
    }
}