package io.dublink.search

import io.dublink.domain.model.DubLinkServiceLocation
import io.dublink.domain.model.DubLinkStopLocation
import io.rtpi.api.Service

enum class KeyField(val fieldName: String) {
    ID(fieldName = "key_id"),
    SERVICE(fieldName = "key_service");
}

enum class SearchField(val fieldName: String) {
    ID(fieldName = "id") {
        override fun toSearchField(serviceLocation: DubLinkServiceLocation): String? {
            return if (serviceLocation.service == Service.DUBLIN_BUS ||
                serviceLocation.service == Service.BUS_EIREANN ||
                serviceLocation.service == Service.IRISH_RAIL
            ) {
                serviceLocation.id
            } else {
                null
            }
        }
    },
    NAME(fieldName = "name") {
        override fun toSearchField(serviceLocation: DubLinkServiceLocation): String? {
            return serviceLocation.defaultName
        }
    },
    SERVICE(fieldName = "service") {
        override fun toSearchField(serviceLocation: DubLinkServiceLocation): String? {
            return serviceLocation.service.fullName
        }
    },
    ROUTES(fieldName = "routes") {
        override fun toSearchField(serviceLocation: DubLinkServiceLocation): String? {
            return if (serviceLocation is DubLinkStopLocation) {
                if (serviceLocation.service == Service.DUBLIN_BUS || serviceLocation.service == Service.BUS_EIREANN) {
                    serviceLocation.stopLocation.routeGroups.flatMap { routeGroup -> routeGroup.routes }.joinToString(separator = " ")
                } else {
                    null
                }
            } else {
                null
            }
        }
    },
    OPERATORS(fieldName = "operators") {
        override fun toSearchField(serviceLocation: DubLinkServiceLocation): String? {
            return if (serviceLocation is DubLinkStopLocation) {
                serviceLocation.stopLocation.routeGroups.joinToString(separator = " ") { routeGroup -> routeGroup.operator.fullName }
            } else {
                null
            }
        }
    },
    CONTENT(fieldName = "content") {
        override fun toSearchField(serviceLocation: DubLinkServiceLocation): String? {
            return listOfNotNull(
                ID.toSearchField(serviceLocation),
                NAME.toSearchField(serviceLocation),
                SERVICE.toSearchField(serviceLocation)
            ).asSequence().plus(
                if (serviceLocation is DubLinkStopLocation) {
                    if (serviceLocation.service == Service.DUBLIN_BUS || serviceLocation.service == Service.BUS_EIREANN) {
                        serviceLocation.stopLocation.routeGroups.flatMap { routeGroup -> routeGroup.routes }
                    } else {
                        emptyList()
                    }
                } else {
                    emptyList()
                }
            ).plus(
                if (serviceLocation is DubLinkStopLocation) {
                    serviceLocation.stopLocation.routeGroups.map{ routeGroup -> routeGroup.operator.fullName }
                } else {
                    emptyList()
                }
            )
                .toSet()
                .sorted()
                .joinToString(separator = " ")
        }
    };

    abstract fun toSearchField(serviceLocation: DubLinkServiceLocation): String?
}
