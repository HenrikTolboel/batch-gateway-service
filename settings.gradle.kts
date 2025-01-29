rootProject.name = "batch-gateway"

plugins {
    id("com.gradle.develocity") version("3.19")
}
develocity {
    buildScan {
        termsOfUseUrl.set("https://gradle.com/help/legal-terms-of-use")
        termsOfUseAgree.set("yes")
    }
}
