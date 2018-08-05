import com.github.benmanes.gradle.versions.updates.DependencyUpdates
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
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

  }
}

apply {
  plugin("idea")
  plugin("java")
  plugin("org.springframework.boot")
  plugin("io.spring.dependency-management")
  plugin("com.github.ben-manes.versions")
  plugin("com.gorylenko.gradle-git-properties")
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

dependencies {
  compileOnly(libraries["lombok"] as String)
  compileOnly(libraries["findbugs-annotations"] as String)
  compileOnly(libraries["spring-context-indexer"] as String)

  compile(libraries["hibernate-validator"] as String)
  compile(libraries["owasp-java-html-sanitizer"] as String)
  compile(libraries["spring-boot-starter-breuninger-core"] as String)
  compile(libraries["spring-boot-starter-breuninger-jobs"] as String)
  compile(libraries["spring-boot-starter-breuninger-mongo"] as String)
  compile(libraries["spring-boot-starter-breuninger-togglz"] as String)
  compile(libraries["edison-hal"] as String)
  compile(libraries["jongo"] as String)
  compile(libraries["bson4jackson"] as String)
  compile(libraries["edison-vault"] as String)
  compile(libraries["handy-uri-templates"] as String)
  compile(libraries["httpclient"] as String)

  compile(libraries["spring-boot-devtools"] as String)

  testCompileOnly(libraries["lombok"] as String)

  testCompile(testLibraries["embed-mongo"] as String)
  testCompile(testLibraries["mockito-core"] as String)
  testCompile(testLibraries["spring-boot-starter-breuninger-testsupport"] as String)
  testCompile(testLibraries["hamcrest-optional"] as String)
  testCompile(testLibraries["spring-boot-starter-test"] as String)
}

apply {
  from("$rootDir/gradle/checkstyle.gradle.kts")
  // from("$rootDir/gradle/findbugs.gradle.kts")
  from("$rootDir/gradle/pmd.gradle.kts")
  from("$rootDir/gradle/test.gradle.kts")
  from("$rootDir/gradle/jacoco.gradle.kts")
}

configure<JavaPluginConvention> {
  sourceCompatibility = JavaVersion.VERSION_1_10
  targetCompatibility = JavaVersion.VERSION_1_10
}

tasks {
  withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(arrayOf("-Xlint:all", "-parameters"))
  }

  getByName<BootRun>("bootRun") {
    systemProperties = System.getProperties().mapKeys { entry -> entry.key.toString() }.toMap()
  }

  "dependencyUpdates"(DependencyUpdatesTask::class) {
    resolutionStrategy = closureOf<ResolutionStrategy> {
      componentSelection {
        all {
          val alphaBetaPattern = Regex("^.*[\\.-](alpha|beta|b|rc|cr|m|ea|incubating|atlassian|snap)[\\.\\w\\d-]*$",
            RegexOption.IGNORE_CASE)
          if (candidate.version.matches(alphaBetaPattern)) {
            reject("Rejected by alpha/beta revision: $candidate")
          }
        }
      }
    }
  }
}
