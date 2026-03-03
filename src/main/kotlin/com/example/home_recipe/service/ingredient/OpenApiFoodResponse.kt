package com.example.home_recipe.service.ingredient

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class OpenApiFoodResponse(

    @JsonProperty("response")
    val response: ResponseWrapper? = null
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ResponseWrapper(
        val header: Header? = null,
        val body: Body? = null
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Header(
        val resultMsg: String? = null
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Body(
        val items: Any? = null
    )
}