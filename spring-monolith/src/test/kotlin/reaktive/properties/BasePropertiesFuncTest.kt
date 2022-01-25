package reaktive.properties

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import reaktive.config.Constants.PROP_ITEM
import reaktive.config.Constants.PROP_MESSAGE
import reaktive.tdd.functional.AbstractBaseFunctionalTest
import kotlin.test.Test

class BasePropertiesFuncTest : AbstractBaseFunctionalTest() {

    @Autowired
    lateinit var properties: ApplicationProperties

    @Value("\${$PROP_MESSAGE}")
    lateinit var messagePropValue: String

    @Value("\${$PROP_ITEM}")
    lateinit var itemPropValue: String

    @Test
    fun `Check property reaktive_message`() {
        checkProperty(
            PROP_MESSAGE,
            properties.message,
            messagePropValue
        )
    }

    @Test
    fun `Check property reaktive_item`() {
        checkProperty(
            PROP_ITEM,
            properties.item,
            itemPropValue
        )
    }
}