import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

abstract class ExpandTemplatesExtension @Inject constructor(private val providers: ProviderFactory) {

    abstract val expansions: MapProperty<String, String>

    init {
        expansions.convention(emptyMap())
    }

    fun expand(key: String, with: String) {
        expansions.put(key, with)
    }

    fun expand(key: String, with: Provider<String>) {
        expansions.put(key, with)
    }

}
