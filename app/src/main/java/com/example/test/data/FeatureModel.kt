package com.example.test.data

data class FeatureModel (
    val text: String,
    val onClick: () -> Unit = {}
)