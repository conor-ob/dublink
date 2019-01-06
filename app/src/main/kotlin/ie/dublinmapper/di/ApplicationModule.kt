package ie.dublinmapper.di

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import ie.dublinmapper.DublinMapperApplication
import ie.dublinmapper.R
import ie.dublinmapper.util.MetadataUtils
import javax.inject.Named

@Module
class ApplicationModule {

    @Provides
    fun provideContext(application: DublinMapperApplication): Context = application.applicationContext

    @Provides
    fun provideResources(context: Context): Resources = context.resources

    @Provides
    @Named("jcdecaux_base_url")
    fun jcdecauxBaseUrl(resources: Resources): String = resources.getString(R.string.jcdecaux_base_url)

    @Provides
    @Named("jcdecaux_contract")
    fun jcdecauxContract(resources: Resources): String = resources.getString(R.string.jcdecaux_contract)

    @Provides
    @Named("api.jcdecaux.API_KEY")
    fun jcdecauxApiKey(context: Context): String {
        return MetadataUtils.getMetadata(context, "api.jcdecaux.API_KEY")
    }

    @Provides
    @Named("rtpi_base_url")
    fun rtpibaseUrl(resources: Resources): String = resources.getString(R.string.rtpi_base_url)

    @Provides
    @Named("api.rtpi.operator.luas")
    fun rtpiOperatoreLuas(resources: Resources): String = resources.getString(R.string.rtpi_operator_luas)

    @Provides
    @Named("api.rtpi.format")
    fun rtpiFormat(resources: Resources): String = resources.getString(R.string.rtpi_format)

}
