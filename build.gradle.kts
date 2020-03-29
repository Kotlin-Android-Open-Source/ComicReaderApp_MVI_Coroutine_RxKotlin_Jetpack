// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
  val kotlinVersion by extra("1.4-M1")
  val navVersion by extra("2.3.0-alpha04")
  extra.run {
    set("coroutinesVersion", "1.3.5")
    set("lifecycleVersion", "2.3.0-alpha01")
    set("pagingVersion", "2.1.2")
    set("koinVersion", "2.1.5")
    set("materialVersion", "1.2.0-alpha05")
    set("glideVersion", "4.11.0")
    set("rxBindingVersion", "3.1.0")
    set("timberVersion", "4.7.1")
    set("rxRelayVersion", "2.1.1")
    set("threetenabpVersion", "1.2.3")
    set("retrofit2Version", "2.8.1")
    set("roomVersion", "2.2.5")
    set("workVersion", "2.4.0-alpha01")
  }


  repositories {
    google()
    jcenter()
    maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
  }
  dependencies {
    classpath("com.android.tools.build:gradle:3.6.1")
    classpath(kotlin("gradle-plugin", kotlinVersion))
    classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
    classpath("com.google.gms:google-services:4.3.3")
  }
}

allprojects {
  repositories {
    google()
    jcenter()
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    maven(url = "https://jitpack.io")
    maven(url = "http://dl.bintray.com/amulyakhare/maven")
    maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
  }
}

tasks.register("clean", Delete::class) { delete(rootProject.buildDir) }

