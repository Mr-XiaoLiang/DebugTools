import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.jetbrains.compose)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {

    dependencies {
        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.material)
        implementation(compose.ui)
        implementation(compose.components.resources)
        implementation(compose.components.uiToolingPreview)
        implementation(compose.desktop.currentOs)
        // https://mvnrepository.com/artifact/org.json/json
        implementation(libs.json)
    }

}
compose.desktop {
    application {
        mainClass = "DebugToolsDesktopKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.lollipop.debug.desktop"
            packageVersion = "1.0.0"
        }
    }
}