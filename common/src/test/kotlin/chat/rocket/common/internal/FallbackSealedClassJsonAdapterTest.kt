package chat.rocket.common.internal

import com.squareup.moshi.Moshi
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.hamcrest.CoreMatchers.`is` as isEqualTo

class FallbackSealedClassJsonAdapterTest {

    lateinit var moshi: Moshi

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        moshi = Moshi.Builder()
                .add(FallbackSealedClassJsonAdapter.ADAPTER_FACTORY)
                .build()
    }

    @Test(expected = AssertionError::class)
    fun `should fail with missing fallback field`() {
        val adapter = moshi.adapter<NoFallback>(NoFallback::class.java)
        val value1 = adapter.fromJson(NO_FALLBACK_TEST1)
        assert(value1?.value is NoFallbackClass.value1)
    }

    @Test(expected = AssertionError::class)
    fun `should fail with missing Fallback constructor field`(){
        val adapter = moshi.adapter<MissingField>(MissingField::class.java)
        val value1 = adapter.fromJson(MISSING_FIELD_TEST1)
        assert(value1?.value is MissingFieldClass.value1)
    }

    @Test(expected = AssertionError::class)
    fun `should fail with wrong Constructor field name`(){
        val adapter = moshi.adapter<InvalidFieldName>(InvalidFieldName::class.java)
        val value1 = adapter.fromJson(INVALID_FIELD_TEST1)
        assert(value1?.value is InvalidFieldNameClass.value1)
    }

    @Test
    fun `should parse value1 value`() {
        val adapter = moshi.adapter<ValidFallback>(ValidFallback::class.java)
        val value = adapter.fromJson(VALID_TEST1)
        assert(value?.value is ValidFallbackClass.value1)
    }

    @Test
    fun `should parse value2 value`() {
        val adapter = moshi.adapter<ValidFallback>(ValidFallback::class.java)
        val value = adapter.fromJson(VALID_TEST2)
        assert(value?.value is ValidFallbackClass.value2)
    }

    @Test
    fun `should return Fallback(value)`() {
        val adapter = moshi.adapter<ValidFallback>(ValidFallback::class.java)
        val value = adapter.fromJson(VALID_TEST3)!!
        assert(value.value is ValidFallbackClass.Fallback)
        assertThat((value.value as ValidFallbackClass.Fallback).field, isEqualTo("value"))
    }

    @Test
    fun `should return Fallback(test)`() {
        val adapter = moshi.adapter<ValidFallback>(ValidFallback::class.java)
        val value = adapter.fromJson(VALID_TEST4)!!
        assert(value.value is ValidFallbackClass.Fallback)
        assertThat((value.value as ValidFallbackClass.Fallback).field, isEqualTo("test"))

    }

    @Test
    fun `should parse empty value`() {
        val adapter = moshi.adapter<ValidFallback>(ValidFallback::class.java)
        val value = adapter.fromJson(VALID_TEST5)!!
        assert(value.value is ValidFallbackClass.Fallback)
        assertThat((value.value as ValidFallbackClass.Fallback).field, isEqualTo(""))
    }
}

@FallbackSealedClass(name = "Fallback", fieldName = "field")
sealed class NoFallbackClass {
    class value1 : NoFallbackClass()
    class value2 : NoFallbackClass()
}
data class NoFallback(val value: NoFallbackClass)
const val NO_FALLBACK_TEST1 = "{\"value\":\"value1\"}"

@FallbackSealedClass(name = "Fallback", fieldName = "field")
sealed class MissingFieldClass {
    class value1 : MissingFieldClass()
    class value2 : MissingFieldClass()
    class Fallback : MissingFieldClass()
}
data class MissingField(val value: MissingFieldClass)
const val MISSING_FIELD_TEST1 = "{\"value\":\"value1\"}"

@FallbackSealedClass(name = "Fallback", fieldName = "field")
sealed class InvalidFieldNameClass {
    class value1 : InvalidFieldNameClass()
    class value2 : InvalidFieldNameClass()
    class Fallback(val value: String) : InvalidFieldNameClass()
}
data class InvalidFieldName(val value: InvalidFieldNameClass)
const val INVALID_FIELD_TEST1 = "{\"value\":\"value1\"}"

@FallbackSealedClass(name = "Fallback", fieldName = "field")
sealed class ValidFallbackClass {
    class value1 : ValidFallbackClass()
    class value2 : ValidFallbackClass()
    class Fallback(val field: String) : ValidFallbackClass()
}
data class ValidFallback(val value: ValidFallbackClass)
const val VALID_TEST1 = "{\"value\":\"value1\"}"
const val VALID_TEST2 = "{\"value\":\"value2\"}"
const val VALID_TEST3 = "{\"value\":\"value\"}"
const val VALID_TEST4 = "{\"value\":\"test\"}"
const val VALID_TEST5 = "{\"value\":\"\"}"