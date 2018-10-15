import com.github.benmanes.gradle.versions.updates.DependencyUpdates
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.spotbugs.SpotBugsExtension
import com.github.spotbugs.SpotBugsTask
import org.gradle.api.JavaVersion
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.withType
import org.springframework.boot.gradle.tasks.run.BootRun

version = "1.0.0-SNAPSHOT"

buildscript {
  project.apply {
    from("$rootDir/gradle/dependencies.gradle.kts")
  }
  val gradlePlugins = extra["gradlePlugins"] as Map<*, *>

  repositories {
    maven { setUrl("https://plugins.gradle.org/m2/") }
    mavenCentral()
    jcenter()
    maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven { setUrl("https://jitpack.io") }
  }
  dependencies {
    classpath(gradlePlugins["spring-boot"] as String)
    classpath(gradlePlugins["versions"] as String)
    classpath(gradlePlugins["git-properties"] as String)
    classpath(gradlePlugins["spotbugs-gradle-plugin"] as String)
  }
}

apply {
  plugin("idea")
  plugin("java")
  plugin("org.springframework.boot")
  plugin("io.spring.dependency-management")
  plugin("com.github.ben-manes.versions")
  plugin("com.gorylenko.gradle-git-properties")
  plugin("com.github.spotbugs")
}

repositories {
  mavenCentral()
  jcenter()
  maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots/") }
  maven { setUrl("https://jitpack.io") }
}

val versions = extra["versions"] as Map<*, *>
val libraries = extra["libraries"] as Map<*, *>
val testLibraries = extra["testLibraries"] as Map<*, *>

ext["groovy.version"] = versions["groovy"]
ext["thymeleaf.version"] = versions["thymeleaf"]
ext["thymeleaf-layout-dialect.version"] = versions["thymeleaf-layout-dialect"]

val compile by configurations
val compileOnly by configurations
val testCompile by configurations
val testCompileOnly by configurations
val spotbugsPlugins by configurations

dependencies {
  compileOnly(libraries["lombok"] as String)
  compileOnly(libraries["spotbugs-annotations"] as String)
  compileOnly(libraries["spring-context-indexer"] as String)

  implementation(libraries["hibernate-validator"] as String)
  implementation(libraries["owasp-java-html-sanitizer"] as String)
  implementation(libraries["spring-boot-starter-breuninger-core"] as String)
  implementation(libraries["spring-boot-starter-breuninger-jobs"] as String)
  implementation(libraries["spring-boot-starter-breuninger-mongo"] as String)
  implementation(libraries["spring-boot-starter-breuninger-togglz"] as String)
  implementation(libraries["edison-hal"] as String)
  implementation(libraries["jongo"] as String)
  implementation(libraries["bson4jackson"] as String)
  implementation(libraries["edison-vault"] as String)
  implementation(libraries["handy-uri-templates"] as String)
  implementation(libraries["httpclient"] as String)

  implementation(libraries["spring-boot-devtools"] as String)

  testCompileOnly(libraries["lombok"] as String)

  testImplementation(testLibraries["embed-mongo"] as String)
  testImplementation(testLibraries["mockito-core"] as String)
  testImplementation(testLibraries["spring-boot-starter-breuninger-testsupport"] as String)
  testImplementation(testLibraries["hamcrest-optional"] as String)
  testImplementation(testLibraries["spring-boot-starter-test"] as String)

  spotbugsPlugins(libraries["spotbugs-plugin"] as String)
}

apply {
  from("$rootDir/gradle/checkstyle.gradle.kts")
  from("$rootDir/gradle/pmd.gradle.kts")
  from("$rootDir/gradle/test.gradle.kts")
  from("$rootDir/gradle/jacoco.gradle.kts")
}

configure<JavaPluginConvention> {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

configure<SpotBugsExtension> {
  toolVersion = versions["spotbugs"] as String
  effort = "max"
  excludeFilter = file("$rootDir/config/spotbugs/spotbugs-exclude.xml")
}

tasks {
  withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(arrayOf("-Xlint:all", "-parameters"))
  }

  getByName<BootRun>("bootRun") {
    systemProperties = System.getProperties().mapKeys { entry -> entry.key.toString() }.toMap()
  }

  withType<SpotBugsTask> {
    group = "Spotbugs"
    reports {
      xml.isEnabled = false
      html.isEnabled = true
    }
  }

  "dependencyUpdates"(DependencyUpdatesTask::class) {
    resolutionStrategy {
      componentSelection {
        all {
          if (listOf("alpha", "beta", "b01", "rc", "cr", "m")
              .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-]*") }
              .any { it.matches(candidate.version) }) {
            reject("Rejected by alpha/beta revision: $candidate")
          }
        }
      }
    }
  }
}
