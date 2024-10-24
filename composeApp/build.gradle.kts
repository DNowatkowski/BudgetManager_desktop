import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.sql.delight)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlin.csv.jvm)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.transitions)

            implementation(libs.file.kit.core)
            implementation(libs.file.kit.compose)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.composeViewModel)
            implementation(libs.sqlite.driver)
            implementation(libs.stately.common)
            implementation(libs.sqlite.coroutines)
            implementation(libs.jackson.dataformat.csv)
            implementation(libs.date.picker)
            implementation(libs.androidx.compose.material.iconsExtended)
            implementation(libs.compose.charts)
        }
        desktopMain.dependencies {
            implementation(libs.androidx.compose.material3)
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)

        }
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("database")
        }
    }
}

compose.desktop {
    application {
        mainClass = "org.example.project.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.example.project"
            packageVersion = "1.0.0"
        }
    }
}
