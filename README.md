# DubLink

![Android CI](https://github.com/conor-ob/dublink/workflows/Android%20CI/badge.svg)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=26)
[![CodeFactor](https://www.codefactor.io/repository/github/conor-ob/dublink/badge?s=6e0e7443d6d60600f8647fe4f22d5fafcebc385b)](https://www.codefactor.io/repository/github/conor-ob/dublink)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)

DubLink is a **work-in-progress** Android app for getting real time data for Dublin Bus :trolleybus: Irish Rail :railway_car: Luas :tram: Bus Éireann :bus: Dublin Bikes :bike: Aircoach :airplane:

| Favourites | Search |
| ------ | ----- |
| ![Favourites](/assets/screenshots/favourites.jpg) | ![Search](/assets/screenshots/search.jpg) |

| Live Data | Settings |
| ------ | ----- |
| ![Live Data](/assets/screenshots/livedata.jpg) | ![Settings](/assets/screenshots/settings.jpg) |

## Android development

DubLink makes use of the latest Android libraries and best practices
* Written in [Kotlin](https://kotlinlang.org/)
* [Retrofit](https://square.github.io/retrofit/)/[OkHttp](https://square.github.io/okhttp/) for networking. The networking is fully contained in a separate library dublin-rtpi-service
* [SQLDelight](https://github.com/cashapp/sqldelight) for local storage
* [Dagger](https://google.github.io/dagger/) for dependency injection
* Designed and built using Material Design [tools](https://material.io/tools/) and [components](https://material.io/develop/android/)
* Full dark theme support
