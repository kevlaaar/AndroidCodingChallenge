package co.teltech.base.shared

import com.bumptech.glide.annotation.Excludes
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule
import com.bumptech.glide.module.AppGlideModule

@Excludes(OkHttpLibraryGlideModule::class)
@GlideModule
class MyAppGlideModule : AppGlideModule()