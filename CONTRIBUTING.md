# How to contribute

The repository is set up in a way that you can simply clone it and then build it with Android Studio. The following
steps are required for full functionality

## Android Studio

Android Studio 4.1 or later

## API Keys

Some API keys are needed for certain features. Create a `release.properties` file in the root directory

#### JcDecaux API Key

  * This API key is needed for requests to the JcDecaux API which provides real time data for Dublin Bikes
  * Follow the [JcDecaux Developer Guide](https://developer.jcdecaux.com/#/opendata/vls?page=getstarted) to get your API key
  * Insert the key into `release.properties`
  * `jcDecauxApiKey=insert_api_key_here`
