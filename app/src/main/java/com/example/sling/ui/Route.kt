package com.example.sling.ui

sealed interface SlingNavigation {
    val route: String
}

object Route {

    object List: SlingNavigation {
        override val route: String
            get() = "List"
    }

    object Detail: SlingNavigation {
        override val route: String
            get() = "Detail"
    }

    object Add: SlingNavigation {
        override val route: String
            get() = "Add"
    }

    object Error: SlingNavigation {
        override val route: String
            get() = "Error"

    }
}