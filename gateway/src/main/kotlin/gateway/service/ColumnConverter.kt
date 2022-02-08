package gateway.service

import io.r2dbc.spi.Row
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.lang.Nullable
import org.springframework.stereotype.Service
import org.springframework.util.ClassUtils

/**
 * This service provides helper function dealing with the low level {@link Row} and Spring's {@link R2dbcCustomConversions}, so type conversions can be applied.
 */
@Service
class ColumnConverter(
    private val conversions: R2dbcCustomConversions,
    private val r2dbcConverter: R2dbcConverter
) {

    private val conversionService = r2dbcConverter.conversionService

    /**
     * Converts the value to the target class with the help of the {@link ConversionService}.
     * @param value to convert.
     * @param target class.
     * @param <T> the parameter for the intended type.
     * @return the value which can be constructed from the input.
     */
    @SuppressWarnings("unchecked")
    fun <T> convert(@Nullable value: Any?, @Nullable target: Class<T>): T? {

        if (value == null || target == null) {
            return null as T
        }

        if (ClassUtils.isAssignableValue(target, value)) {
            return value as T
        }

        if (conversions.hasCustomReadTarget(value::class.java, target)) {
            return conversionService.convert<T>(value, target)
        }

        return if (Enum::class.java.isAssignableFrom(target)) {
            java.lang.Enum.valueOf(target as Class<Enum<*>?>, value.toString()) as T
        } else conversionService.convert<T>(value, target)
    }

    /**
     * Convert a value from the {@link Row} to a type - throws an exception, it it's impossible.
     * @param row which contains the column values.
     * @param target class.
     * @param columnName the name of the column which to convert.
     * @param <T> the parameter for the intended type.
     * @return the value which can be constructed from the input.
     */
    fun <T> fromRow(row: Row, columnName: String, target: Class<T>): T? {
        return try {
            // try, directly the driver
            row.get(columnName, target)
        } catch (e: Exception) {
            val obj = row.get(columnName)
            convert(obj, target)
        }
    }
}
