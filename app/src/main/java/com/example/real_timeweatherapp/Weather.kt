package com.example.real_timeweatherapp

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)