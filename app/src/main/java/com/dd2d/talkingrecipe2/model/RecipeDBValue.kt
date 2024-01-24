package com.dd2d.talkingrecipe2.model

import kotlin.time.Duration.Companion.minutes

object RecipeDBValue{
    object Expires{
        val In30M = 30.minutes
    }

    object Table{
        const val RecipeTable = "recipes"
        const val IngredientTable = "recipe_ingredient"
        const val StepInfoTable = "recipe_step_info"
        const val RecipeImageTable = "recipe_image"
        const val StepInfoImageTable = "step_info"
    }

    object Filter{
        const val RecipeIdEqualTo = "recipe_id"
    }

    object Order{
        const val OrderBy = "order"
    }

    object Field{
        val BasicInfoField = listOf("recipe_id", "author_id", "title", "description", "level", "time", "amount", "share_option", "calorie")
    }
}