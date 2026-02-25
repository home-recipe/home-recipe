package com.example.home_recipe.domain.recipe

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.LocalDateTime

@Document(indexName = "recipe_cache")
class RecipeCache(

    @Id
    val id: String,

    @Field(type = FieldType.Text)
    val recipeContent: String,

    @Field(type = FieldType.Date, format = [DateFormat.date_hour_minute_second_millis])
    val createdAt: LocalDateTime = LocalDateTime.now()
)
