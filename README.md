# DubLink

![DubLink CI](https://github.com/conor-ob/dublink/workflows/DubLink%20CI/badge.svg)
[![codecov](https://codecov.io/gh/conor-ob/dublink/branch/master/graph/badge.svg?token=VVTOCCQJNN)](https://codecov.io/gh/conor-ob/dublink)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=26)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)

DubLink is a **work-in-progress** Android app for getting real time data for Dublin Bus :trolleybus: Irish Rail :railway_car: Luas :tram: Bus Éireann :bus: Dublin Bikes :bike: Aircoach :airplane:

| Favourites | Dark Theme Support |
| ------ | ------ |
| ![Favourites](/assets/listing/sc_01_favourites_light.png) | ![Dark Theme](/assets/listing/sc_02_favourites_dark.png) |

| Search | Live Data |
| ------ | ------ |
| ![Search](/assets/listing/sc_04_search.png) | ![Live Data](/assets/listing/sc_03_real_time_info.png) |

| Editor | Settings |
| ------ | ------ |
| ![Editor](/assets/listing/sc_06_edit_favourites.png) | ![Settings](/assets/listing/sc_07_settings.png) |

## Android development

DubLink makes use of the latest Android libraries and best practices
* Written in [Kotlin](https://kotlinlang.org/)
* [Retrofit](https://square.github.io/retrofit/)/[OkHttp](https://square.github.io/okhttp/) for networking. The networking is fully contained in a separate library [dublin-rtpi-service](https://github.com/conor-ob/dublin-rtpi-service)
* [SQLDelight](https://github.com/cashapp/sqldelight) for local storage
* [Dagger](https://google.github.io/dagger/) for dependency injection
* Designed and built using Material Design [tools](https://material.io/tools/) and [components](https://material.io/develop/android/)
* Full dark theme support
