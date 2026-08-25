import kotlin.math.max

object BuildConfig {

    // Minecraft
    val MINECRAFT_VERSION = "26.2"

    // Fabric
    val FABRIC_LOADER_VERSION = "0.19.3"
    val FABRIC_LOOM_VERSION = "1.17-SNAPSHOT"

    // NeoForge
    val NEOFORGE_VERSION = "26.2.0.0-beta"
    val MODDEV_VERSION = "2.0.141"

    // Dependencies
    val FABRIC_API_VERSION = "0.152.1+26.2"
    val MODMENU_VERSION = "20.0.0-beta.2"

    // Mod Properties
    val MOD_ID = "componentviewer"
    val MOD_VERSION = "1.3.5"
    val MOD_GROUP = "dev.fixyl.componentviewer"

    // Release Information
    val RELEASE_TYPE = "STABLE"  // STABLE, BETA or ALPHA
    val SUPPORTED_VERSIONS = listOf("26.2")

    fun getVersionString(): String {
        return "$MOD_VERSION+${getLatestSupportedVersion()}"
    }

    fun getVersionTitle(): String {
        val latestVersion = getLatestSupportedVersion()
        val oldestVersion = getOldestSupportedVersion()

        return if (latestVersion == oldestVersion) {
            "$MOD_VERSION [$latestVersion]"
        } else {
            "$MOD_VERSION [$oldestVersion-$latestVersion]"
        }
    }

    fun getFabricVersionTitle(): String {
        return "${getVersionTitle()} Fabric"
    }

    fun getNeoforgeVersionTitle(): String {
        return "${getVersionTitle()} NeoForge"
    }

    fun getLatestSupportedVersion(): String {
        return SUPPORTED_VERSIONS.maxWith(VERSION_COMPARATOR)
    }

    fun getOldestSupportedVersion(): String {
        return SUPPORTED_VERSIONS.minWith(VERSION_COMPARATOR)
    }

    private val VERSION_COMPARATOR = Comparator<String> { v1, v2 ->
        val v1Parts = v1.split('.').map(String::toInt)
        val v2Parts = v2.split('.').map(String::toInt)

        val size = max(v1Parts.size, v2Parts.size)

        for (index in 0 until size) {
            val part1 = v1Parts.getOrElse(index) { 0 }
            val part2 = v2Parts.getOrElse(index) { 0 }

            if (part1 != part2) {
                return@Comparator part1.compareTo(part2)
            }
        }

        return@Comparator 0
    }

}
