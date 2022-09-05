package backend.tdd

import backend.Constants
import org.springframework.boot.SpringApplication


fun testLoader(app: SpringApplication) = with(app) {
    setDefaultProperties(
        hashMapOf<String, Any>().apply {
            set(Constants.SPRING_PROFILE_CONF_DEFAULT_KEY, Constants.SPRING_PROFILE_TEST)
        })
    setAdditionalProfiles(Constants.SPRING_PROFILE_TEST)
}