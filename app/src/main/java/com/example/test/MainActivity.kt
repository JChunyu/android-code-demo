package com.example.test

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.test.data.FeatureModel
import com.example.test.lifecycle.ComposeLifecycleActivity
import com.example.test.ui.theme.TestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val features = this.featureList()
        setContent {
            TestTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Home(features)
                }
            }
        }
    }

    private fun featureList(): List<FeatureModel> {
        return listOf(
            FeatureModel("Compose 感知生命周期") {
                startActivity(Intent(this@MainActivity, ComposeLifecycleActivity::class.java))
            }
        )
    }
}

@Composable
fun Home(list: List<FeatureModel>, modifier: Modifier = Modifier) {
    LazyColumn {
        items(list.size) { index ->
            Text(
                text = list[index].text,
                modifier = Modifier.fillMaxWidth()
                    .height(80.dp)
                    .clickable {
                        list[index].onClick.invoke()
                    }
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    TestTheme {
//    }
//}

